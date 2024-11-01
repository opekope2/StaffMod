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

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.AbstractPiglinEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.LootTableEntry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.LootTableLoadEvent
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem
import net.minecraftforge.eventbus.api.Event
import opekope2.avm_staff.api.cakeEntityType
import opekope2.avm_staff.api.crownOfKingOrangeItem
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.staff.StaffInfusionSmithingRecipeTextures
import opekope2.avm_staff.api.throwableCakesGameRule
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.InsertItemIntoStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.networking.s2c.play.MassDestructionS2CPacket
import opekope2.avm_staff.mixin.IPiglinBrainAccessor
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.*
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

fun registerContent() {
    opekope2.avm_staff.api.registerContent()
}

fun initializeNetworking() {
    AttackC2SPacket
    InsertItemIntoStaffC2SPacket
    RemoveItemFromStaffC2SPacket

    MassDestructionS2CPacket
}

private val MODIFIABLE_LOOT_TABLES = setOf(
    Identifier.ofVanilla("chests/bastion_treasure"),
    Identifier.ofVanilla("chests/trial_chambers/reward_unique")
)

fun subscribeToEvents() {
    FORGE_BUS.addListener(::stopUsingStaffOnPlayerDeath)
    FORGE_BUS.addListener(::dispatchStaffBlockAttack)
    FORGE_BUS.addListener(::tryThrowCake)
    FORGE_BUS.addListener(::modifyLootTables)
    FORGE_BUS.addListener(::tryAngerPiglins)
    FORGE_BUS.addListener(::stopUsingStaffWhenDropped)
}

private fun stopUsingStaffOnPlayerDeath(entity: LivingDeathEvent) {
    val player = entity.entity
    if (player !is PlayerEntity) return

    iterator {
        yieldAll(0 until PlayerInventory.MAIN_SIZE)
        yield(PlayerInventory.OFF_HAND_SLOT)
    }.forEach { slot ->
        if (player.inventory.getStack(slot).isStaff) {
            player.stopUsingItem()
        }
    }
}

private fun dispatchStaffBlockAttack(event: LeftClickBlock) {
    val player = event.entity
    val staffStack = player.getStackInHand(event.hand)
    val staffItem = staffStack.item as? StaffItem ?: return

    val result = staffItem.attackBlock(staffStack, player.entityWorld, player, event.pos, event.face!!, event.hand)
    if (result != ActionResult.PASS) {
        event.isCanceled = true
        event.cancellationResult = result
        event.useBlock = Event.Result.DENY
        event.useItem = Event.Result.DENY
    }
}

private fun tryThrowCake(event: RightClickItem) {
    val player = event.entity
    val world = player.entityWorld
    val cake = player.getStackInHand(event.hand)
    val spawnPos = cakeEntityType.get().getSpawnPosition(world, player.approximateStaffTipPosition)

    if (!cake.isOf(Items.CAKE)) return
    if (spawnPos == null) return
    if (world.isClient) {
        event.isCanceled = true
        event.cancellationResult = ActionResult.SUCCESS
        return
    }
    if (!world.gameRules.getBoolean(throwableCakesGameRule)) return

    CakeEntity.throwCake(world, spawnPos, player.rotationVector * .5 + player.velocity, player)
    cake.decrementUnlessCreative(1, player)

    event.isCanceled = true
    event.cancellationResult = ActionResult.FAIL
}

private fun modifyLootTables(event: LootTableLoadEvent) {
    if (event.name !in MODIFIABLE_LOOT_TABLES) return

    event.table.addPool(
        LootPool.builder().with(
            LootTableEntry.builder(
                RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(MOD_ID, "add_loot_pool/${event.name.path}"))
            )
        ).build()
    )
}

private const val maxAngerDistance = 16.0

private fun tryAngerPiglins(event: AttackEntityEvent) {
    val player = event.entity
    val world = player.entityWorld
    val target = event.target

    if (world.isClient) return
    if (target !is LivingEntity) return
    if (!player.mainHandStack.isStaff) return
    if (!player.armorItems.any { it.isOf(crownOfKingOrangeItem.get()) }) return

    val box = Box.of(player.pos, 2 * maxAngerDistance, 2 * maxAngerDistance, 2 * maxAngerDistance)
    world.getEntitiesByClass(AbstractPiglinEntity::class.java, box) {
        it !== target && it.squaredDistanceTo(player) <= maxAngerDistance * maxAngerDistance
    }.forEach {
        IPiglinBrainAccessor.callBecomeAngryWith(it, target)
    }
}

fun stopUsingStaffWhenDropped(event: ItemTossEvent) {
    stopUsingStaffWhenDropped(event.player, event.entity)
}

fun stopUsingStaffWhenDropped(entity: LivingEntity, item: ItemEntity) {
    val staffItem = item.stack.item as? StaffItem ?: return
    staffItem.onStoppedUsing(item.stack, entity.entityWorld, entity, entity.itemUseTimeLeft)
}

@OnlyIn(Dist.CLIENT)
fun registerSmithingTableTextures() {
    StaffInfusionSmithingRecipeTextures.register(
        Identifier.of(MOD_ID, "item/smithing_table/empty_slot_royal_staff"),
        ISmithingTemplateItemAccessor.emptySlotRedstoneDustTexture()
    )
}

@OnlyIn(Dist.CLIENT)
fun clientAttack(player: PlayerEntity, hand: Hand) {
    val staffStack = player.getStackInHand(hand)
    val staffItem = staffStack.item as? StaffItem ?: return

    staffItem.attack(staffStack, player.entityWorld, player, hand)
    AttackC2SPacket(hand).sendToServer()
}
