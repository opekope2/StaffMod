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

package opekope2.avm_staff.internal.event_handler

import dev.architectury.networking.NetworkManager.PacketContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import opekope2.avm_staff.api.staffsTag
import opekope2.avm_staff.internal.networking.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.util.handlerOfItem
import opekope2.avm_staff.util.hasHandlerOfItem
import opekope2.avm_staff.util.isItemInStaff
import opekope2.avm_staff.util.itemInStaff

@Suppress("UNUSED_PARAMETER")
fun addBlockToStaff(packet: AddItemToStaffC2SPacket, context: PacketContext) {
    val player = context.player
    val (staffStack, itemStack) = findStaffAndItemStack(player) ?: return

    if (itemStack.isEmpty) return
    if (!itemStack.hasHandlerOfItem) return
    if (staffStack.isItemInStaff) return
    staffStack.itemInStaff = itemStack
}

@Suppress("UNUSED_PARAMETER")
fun removeBlockFromStaff(packet: RemoveItemFromStaffC2SPacket, context: PacketContext) {
    val player = context.player
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

fun attack(packet: AttackC2SPacket, context: PacketContext) {
    val player = context.player
    val staffStack = player.mainHandStack

    if (!staffStack.isIn(staffsTag)) return

    val itemInStaff: ItemStack = staffStack.itemInStaff ?: return
    val staffHandler = itemInStaff.handlerOfItem ?: return

    staffHandler.attack(staffStack, player.entityWorld, player, packet.hand)
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
        mainStack.isIn(staffsTag) && !offStack.isIn(staffsTag) ->
            mainStack to PlayerInventory.OFF_HAND_SLOT

        offStack.isIn(staffsTag) && !mainStack.isIn(staffsTag) ->
            offStack to player.inventory.selectedSlot

        else -> null
    }
}

private fun findStaffAndItemStack(player: PlayerEntity): Pair<ItemStack, ItemStack>? {
    val mainStack = player.mainHandStack
    val offStack = player.offHandStack

    return when {
        mainStack.isIn(staffsTag) && !offStack.isIn(staffsTag) -> mainStack to offStack
        offStack.isIn(staffsTag) && !mainStack.isIn(staffsTag) -> offStack to mainStack
        else -> null
    }
}
