/*
 * AvM Staff Mod
 * Copyright (c) 2023-2024 opekope2
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

package opekope2.avm_staff.internal.server

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import opekope2.avm_staff.internal.StaffMod.STAFF_ITEM
import opekope2.avm_staff.internal.packet.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.packet.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.packet.c2s.play.StaffAttackC2SPacket
import opekope2.avm_staff.util.handlerOfItem
import opekope2.avm_staff.util.hasHandlerOfItem
import opekope2.avm_staff.util.isItemInStaff
import opekope2.avm_staff.util.itemInStaff

object StaffPacketHandler {
    @Suppress("UNUSED_PARAMETER")
    fun addBlockToStaff(
        packet: AddItemToStaffC2SPacket,
        player: ServerPlayerEntity,
        responseSender: PacketSender
    ) {
        val (staffStack, itemStack) = findStaffAndItemStack(player) ?: return

        if (itemStack.isEmpty) return
        if (!itemStack.hasHandlerOfItem) return
        if (staffStack.isItemInStaff) return
        staffStack.itemInStaff = itemStack
    }

    @Suppress("UNUSED_PARAMETER")
    fun removeBlockFromStaff(
        packet: RemoveItemFromStaffC2SPacket,
        player: ServerPlayerEntity,
        responseSender: PacketSender
    ) {
        if (player.isUsingItem) return
        val (staffStack, itemSlot) = findStaffStackAndItemSlot(player) ?: return
        val inventory = player.inventory
        val itemStack = inventory.getStack(itemSlot)
        val staffItem = staffStack.itemInStaff ?: return

        if (itemStack.canAccept(staffItem, inventory.maxCountPerStack)) {
            inventory.insertStack(itemSlot, staffItem)
            staffStack.itemInStaff = null
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun attack(packet: StaffAttackC2SPacket, player: ServerPlayerEntity, responseSender: PacketSender): ActionResult {
        val staffStack = player.mainHandStack
        if (!staffStack.isOf(STAFF_ITEM)) return ActionResult.PASS

        return staffStack.itemInStaff?.handlerOfItem?.attack(
            staffStack,
            player.world,
            player,
            Hand.MAIN_HAND
        ) ?: ActionResult.PASS
    }

    private fun ItemStack.canAccept(other: ItemStack, maxCountPerStack: Int): Boolean {
        val canCombine = isEmpty || ItemStack.canCombine(this, other)
        val totalCount = count + other.count

        return canCombine && totalCount <= item.maxCount && totalCount <= maxCountPerStack
    }

    private fun findStaffStackAndItemSlot(player: PlayerEntity): Pair<ItemStack, Int>? {
        val mainStack = player.mainHandStack
        val offStack = player.offHandStack

        return when {
            mainStack.isOf(STAFF_ITEM) && !offStack.isOf(STAFF_ITEM) ->
                mainStack to PlayerInventory.OFF_HAND_SLOT

            offStack.isOf(STAFF_ITEM) && !mainStack.isOf(STAFF_ITEM) ->
                offStack to player.inventory.selectedSlot

            else -> null
        }
    }

    private fun findStaffAndItemStack(player: PlayerEntity): Pair<ItemStack, ItemStack>? {
        val mainStack = player.mainHandStack
        val offStack = player.offHandStack

        return when {
            mainStack.isOf(STAFF_ITEM) && !offStack.isOf(STAFF_ITEM) -> mainStack to offStack
            offStack.isOf(STAFF_ITEM) && !mainStack.isOf(STAFF_ITEM) -> offStack to mainStack
            else -> null
        }
    }
}