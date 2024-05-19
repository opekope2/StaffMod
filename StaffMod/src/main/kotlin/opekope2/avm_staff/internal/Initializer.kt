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

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.LootEvent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import opekope2.avm_staff.api.crownOfKingOrangeItem
import opekope2.avm_staff.api.staffsTag
import opekope2.avm_staff.internal.event_handler.addBlockToStaff
import opekope2.avm_staff.internal.event_handler.attack
import opekope2.avm_staff.internal.event_handler.removeBlockFromStaff
import opekope2.avm_staff.internal.networking.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.util.handlerOfItem
import opekope2.avm_staff.util.itemInStaff

fun registerContent() {
    opekope2.avm_staff.api.registerContent()
}

fun initializeNetworking() {
    AddItemToStaffC2SPacket.registerHandler(::addBlockToStaff)
    RemoveItemFromStaffC2SPacket.registerHandler(::removeBlockFromStaff)
    AttackC2SPacket.registerHandler(::attack)
}

private fun attackBlock(player: PlayerEntity, hand: Hand, target: BlockPos, direction: Direction): EventResult {
    val staffStack = player.getStackInHand(hand)
    if (!staffStack.isIn(staffsTag)) return EventResult.pass()

    val itemInStaff = staffStack.itemInStaff ?: return EventResult.pass()
    val staffHandler = itemInStaff.handlerOfItem ?: return EventResult.pass()

    val result = staffHandler.attackBlock(staffStack, player.entityWorld, player, target, direction, hand)
    return if (result.isFalse) EventResult.interruptTrue() // Force Fabric to send packet for Neo/Forge parity
    else result
}

private fun clientAttack(player: PlayerEntity, hand: Hand) {
    val staffStack = player.getStackInHand(hand)
    if (!staffStack.isIn(staffsTag)) return

    val itemInStaff: ItemStack = staffStack.itemInStaff ?: return
    val staffHandler = itemInStaff.handlerOfItem ?: return

    staffHandler.attack(staffStack, player.entityWorld, player, hand)
    AttackC2SPacket(hand).send()
}

private val TREASURE_BASTION_CHEST_LOOT = Identifier("chests/bastion_treasure")

@Suppress("UNUSED_PARAMETER")
fun modifyLootTables(
    lootManager: LootManager?,
    lootTableId: Identifier,
    context: LootEvent.LootTableModificationContext,
    builtin: Boolean
) {
    if (builtin && lootTableId == TREASURE_BASTION_CHEST_LOOT) {
        context.addPool(LootPool.builder().with(ItemEntry.builder(crownOfKingOrangeItem.get())))
    }
}

fun subscribeToEvents() {
    InteractionEvent.LEFT_CLICK_BLOCK.register(::attackBlock)
    InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(::clientAttack)
    LootEvent.MODIFY_LOOT_TABLE.register(::modifyLootTables)
}
