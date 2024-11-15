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
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.event.events.common.*
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.DispenserBlock
import net.minecraft.client.render.entity.EmptyEntityRenderer
import net.minecraft.client.render.entity.TntEntityRenderer
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.AbstractPiglinEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.LootTableEntry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.block.dispenser.CakeDispenserBehavior
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.entity.renderer.CakeEntityRenderer
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.staff.StaffInfusionSmithingRecipeTextures
import opekope2.avm_staff.content.*
import opekope2.avm_staff.internal.event_handler.handleKeyBindings
import opekope2.avm_staff.internal.event_handler.registerKeyBindings
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.InsertItemIntoStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.networking.s2c.play.MassDestructionS2CPacket
import opekope2.avm_staff.mixin.IPiglinBrainAccessor
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.*
import net.minecraft.item.Items as MinecraftItems

fun registerContent() {
    Blocks.register()
    ComponentTypes.register()
    Criteria.register()
    EntityTypes.register()
    ItemGroups.register()
    Items.register()
    ParticleTypes.register()
    SoundEvents.register()
}

fun initializeNetworking() {
    AttackC2SPacket.registerReceiver()
    InsertItemIntoStaffC2SPacket.registerReceiver()
    RemoveItemFromStaffC2SPacket.registerReceiver()

    MassDestructionS2CPacket.registerReceiver()
}

fun subscribeToEvents() {
    EntityEvent.LIVING_DEATH.register(::stopUsingStaffOnPlayerDeath)
    InteractionEvent.LEFT_CLICK_BLOCK.register(::dispatchStaffBlockAttack)
    InteractionEvent.RIGHT_CLICK_ITEM.register(::tryThrowCake)
    LifecycleEvent.SETUP.register(::setup)
    LootEvent.MODIFY_LOOT_TABLE.register(::modifyLootTables)
    PlayerEvent.ATTACK_ENTITY.register(::tryAngerPiglins)
    PlayerEvent.DROP_ITEM.register(::stopUsingStaffWhenDropped)
    EntityEvent.LIVING_HURT.register(::triggerDamageWhileUsingItemCriterion)
}

@Suppress("UNUSED_PARAMETER")
private fun stopUsingStaffOnPlayerDeath(entity: LivingEntity, damageSource: DamageSource): EventResult {
    if (entity !is PlayerEntity) return EventResult.pass()

    if (entity.activeItem.isStaff) {
        entity.stopUsingItem()
    }

    return EventResult.pass()
}

private fun dispatchStaffBlockAttack(
    player: PlayerEntity, hand: Hand, target: BlockPos, direction: Direction
): EventResult {
    val staffStack = player.getStackInHand(hand)
    val staffItem = staffStack.item as? StaffItem ?: return EventResult.pass()

    return staffItem.attackBlock(staffStack, player.entityWorld, player, target, direction, hand)
}

private fun tryThrowCake(player: PlayerEntity, hand: Hand): CompoundEventResult<ItemStack> {
    val world = player.entityWorld
    val cake = player.getStackInHand(hand)
    val spawnPos = EntityTypes.cake.getSpawnPosition(world, player.approximateStaffTipPosition)

    if (!cake.isOf(MinecraftItems.CAKE)) return CompoundEventResult.pass()
    if (spawnPos == null) return CompoundEventResult.pass()
    if (world.isClient) return CompoundEventResult.interruptTrue(cake)
    if (!world.gameRules.getBoolean(GameRules.THROWABLE_CAKES)) return CompoundEventResult.pass()

    CakeEntity.throwCake(world, spawnPos, player.rotationVector * .5 + player.velocity, player)
    cake.decrementUnlessCreative(1, player)

    return CompoundEventResult.interruptFalse(cake)
}

private fun setup() {
    DispenserBlock.registerBehavior(MinecraftItems.CAKE, CakeDispenserBehavior())
}

private val MODIFIABLE_LOOT_TABLES = setOf(
    Identifier.ofVanilla("chests/bastion_treasure"),
    Identifier.ofVanilla("chests/trial_chambers/reward_unique")
)

private fun modifyLootTables(
    lootTable: RegistryKey<LootTable>,
    context: LootEvent.LootTableModificationContext,
    builtin: Boolean
) {
    if (!builtin) return
    if (lootTable.value !in MODIFIABLE_LOOT_TABLES) return

    context.addPool(
        LootPool.builder().with(
            LootTableEntry.builder(
                RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID, "add_loot_pool/${lootTable.value.path}"))
            )
        )
    )
}

private const val MAX_ANGER_DISTANCE = 16.0

@Suppress("UNUSED_PARAMETER")
private fun tryAngerPiglins(
    player: PlayerEntity, world: World, target: Entity, hand: Hand, hit: EntityHitResult?
): EventResult {
    if (world.isClient) return EventResult.pass()
    if (target !is LivingEntity) return EventResult.pass()
    if (!player.getStackInHand(hand).isStaff) return EventResult.pass()
    if (!player.armorItems.any { it.isOf(Items.crownOfKingOrange) }) return EventResult.pass()

    val box = Box.of(player.pos, 2 * MAX_ANGER_DISTANCE, 2 * MAX_ANGER_DISTANCE, 2 * MAX_ANGER_DISTANCE)
    world.getEntitiesByClass(AbstractPiglinEntity::class.java, box) {
        it !== target && it.squaredDistanceTo(player) <= MAX_ANGER_DISTANCE * MAX_ANGER_DISTANCE
    }.forEach {
        IPiglinBrainAccessor.callBecomeAngryWith(it, target)
    }

    return EventResult.pass()
}

fun stopUsingStaffWhenDropped(entity: LivingEntity, item: ItemEntity): EventResult {
    val staffItem = item.stack.item as? StaffItem ?: return EventResult.pass()
    staffItem.onStoppedUsing(item.stack, entity.entityWorld, entity, entity.itemUseTimeLeft)
    return EventResult.pass()
}

@Suppress("UNUSED_PARAMETER")
fun triggerDamageWhileUsingItemCriterion(entity: LivingEntity, damage: DamageSource, amount: Float): EventResult {
    if (entity is ServerPlayerEntity && entity.isUsingItem) {
        Criteria.takeDamageWhileUsingItem.trigger(entity, entity.activeItem, damage)
    }
    return EventResult.pass()
}

@Environment(EnvType.CLIENT)
fun registerClientContent() {
    registerKeyBindings()
    EntityRendererRegistry.register(EntityTypes.IMPACT_TNT, ::TntEntityRenderer)
    EntityRendererRegistry.register(EntityTypes.CAKE, ::CakeEntityRenderer)
    EntityRendererRegistry.register(EntityTypes.CAMPFIRE_FLAME, ::EmptyEntityRenderer)
}

@Environment(EnvType.CLIENT)
fun registerSmithingTableTextures() {
    StaffInfusionSmithingRecipeTextures.register(
        Identifier.of(MOD_ID, "item/smithing_table/empty_slot_royal_staff"),
        ISmithingTemplateItemAccessor.emptySlotRedstoneDustTexture()
    )
}

@Environment(EnvType.CLIENT)
fun subscribeToClientEvents() {
    ClientTickEvent.CLIENT_POST.register(::handleKeyBindings)
    InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(::clientAttack)
}

@Environment(EnvType.CLIENT)
private fun clientAttack(player: PlayerEntity, hand: Hand) {
    val staffStack = player.getStackInHand(hand)
    val staffItem = staffStack.item as? StaffItem ?: return

    staffItem.attack(staffStack, player.entityWorld, player, hand)
    AttackC2SPacket(hand).sendToServer()
}
