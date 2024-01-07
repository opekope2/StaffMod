// Copyright (c) 2023-2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal.server

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import opekope2.avm_staff.internal.StaffMod.STAFF_ITEM
import opekope2.avm_staff.internal.packet.c2s.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.packet.c2s.RemoveItemFromStaffC2SPacket
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
        if (staffStack.isItemInStaff) return
        staffStack.itemInStaff = itemStack
    }

    @Suppress("UNUSED_PARAMETER")
    fun removeBlockFromStaff(
        packet: RemoveItemFromStaffC2SPacket,
        player: ServerPlayerEntity,
        responseSender: PacketSender
    ) {
        val (staffStack, itemSlot) = findStaffStackAndItemSlot(player) ?: return
        val inventory = player.inventory
        val itemStack = inventory.getStack(itemSlot)
        val staffItem = staffStack.itemInStaff ?: return

        if (itemStack.canAccept(staffItem, inventory.maxCountPerStack)) {
            inventory.insertStack(itemSlot, staffItem)
            staffStack.itemInStaff = null
        }
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
