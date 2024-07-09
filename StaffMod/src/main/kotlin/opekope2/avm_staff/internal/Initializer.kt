/*
 * AvM Staff Mod
 * Copyright (c) 2024 opekope2
 *
 * This mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this mod. If not, see <https://www.gnu.org/licenses/>.
 */

package opekope2.avm_staff.internal

import dev.architectury.event.CompoundEventResult
import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.event.events.common.*
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.client.rendering.RenderTypeRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.entity.TntEntityRenderer
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.LootTableEntry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.*
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.entity.renderer.CakeEntityRenderer
import opekope2.avm_staff.api.staff.StaffInfusionSmithingRecipeTextures
import opekope2.avm_staff.internal.event_handler.*
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.InsertItemIntoStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.*

fun registerContent() {
    opekope2.avm_staff.api.registerContent()
}

fun initializeNetworking() {
    InsertItemIntoStaffC2SPacket.registerHandler(::addItemToStaff)
    RemoveItemFromStaffC2SPacket.registerHandler(::removeItemFromStaff)
    AttackC2SPacket.registerHandler(::attack)
}

private val MODIFIABLE_LOOT_TABLES = setOf(
    Identifier("chests/bastion_treasure"),
    Identifier("chests/trial_chambers/reward_unique")
)

fun modifyLootTables(
    lootTable: RegistryKey<LootTable>,
    context: LootEvent.LootTableModificationContext,
    builtin: Boolean
) {
    // FIXME builtin check after updating to 1.21 because Fabric detects experiments as data pack
    if (lootTable.value !in MODIFIABLE_LOOT_TABLES) return

    context.addPool(
        LootPool.builder().with(
            LootTableEntry.builder(
                RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier(MOD_ID, "add_loot_pool/${lootTable.value.path}"))
            )
        )
    )
}

fun subscribeToEvents() {
    InteractionEvent.LEFT_CLICK_BLOCK.register(::attackBlock)
    InteractionEvent.RIGHT_CLICK_ITEM.register(::tryThrowCake)
    LootEvent.MODIFY_LOOT_TABLE.register(::modifyLootTables)
    EntityEvent.LIVING_DEATH.register(::stopUsingStaffOnPlayerDeath)
    PlayerEvent.DROP_ITEM.register(::stopUsingStaffWhenDropped)
    PlayerEvent.ATTACK_ENTITY.register(::tryAngerPiglins)
}

@Suppress("UNUSED_PARAMETER")
fun stopUsingStaffOnPlayerDeath(entity: LivingEntity, damageSource: DamageSource): EventResult {
    if (entity !is PlayerEntity) return EventResult.pass()

    iterator {
        yieldAll(0 until PlayerInventory.MAIN_SIZE)
        yield(PlayerInventory.OFF_HAND_SLOT)
    }.forEach { slot ->
        if (entity.inventory.getStack(slot) in staffsTag) {
            entity.stopUsingItem()
        }
    }

    return EventResult.pass()
}

fun tryThrowCake(player: PlayerEntity, hand: Hand): CompoundEventResult<ItemStack> {
    val world = player.entityWorld
    val cake = player.getStackInHand(hand)

    if (!world.gameRules.getBoolean(throwableCakesGameRule)) return CompoundEventResult.pass()
    if (!cake.isOf(Items.CAKE)) return CompoundEventResult.pass()

    if (!world.isClient) {
        CakeEntity.throwCake(
            world, player.eyePos + player.rotationVector, player.rotationVector * .5 + player.velocity, player
        )
    }

    cake.decrementUnlessCreative(1, player)

    return CompoundEventResult.interrupt(world.isClient, player.getStackInHand(hand))
}

fun stopUsingStaffWhenDropped(entity: LivingEntity, item: ItemEntity): EventResult {
    if (item.stack in staffsTag) {
        item.stack.itemInStaff.staffHandlerOrDefault.onStoppedUsing(
            item.stack, entity.entityWorld, entity, entity.itemUseTimeLeft
        )
    }
    return EventResult.pass()
}

@Environment(EnvType.CLIENT)
fun registerClientContent() {
    KeyMappingRegistry.register(addRemoveStaffItemKeyBinding)
    EntityRendererRegistry.register(impactTntEntityType, ::TntEntityRenderer)
    EntityRendererRegistry.register(cakeEntityType, ::CakeEntityRenderer)
}

@Environment(EnvType.CLIENT)
fun registerSmithingTableTextures() {
    StaffInfusionSmithingRecipeTextures.register(
        Identifier(MOD_ID, "item/smithing_table/empty_slot_royal_staff"),
        ISmithingTemplateItemAccessor.emptySlotRedstoneDustTexture()
    )
}

@Environment(EnvType.CLIENT)
fun subscribeToClientEvents() {
    ClientLifecycleEvent.CLIENT_SETUP.register(::setupClient)
    InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(::clientAttack)
    ClientTickEvent.CLIENT_POST.register(::handleKeyBindings)
}

@Suppress("UNUSED_PARAMETER")
@Environment(EnvType.CLIENT)
private fun setupClient(client: MinecraftClient) {
    RenderTypeRegistry.register(RenderLayer.getCutout(), crownOfKingOrangeBlock.get(), wallCrownOfKingOrangeBlock.get())
}
