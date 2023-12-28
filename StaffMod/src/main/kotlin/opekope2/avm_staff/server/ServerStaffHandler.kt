package opekope2.avm_staff.server

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import opekope2.avm_staff.StaffMod
import opekope2.avm_staff.packet.AddBlockToStaffC2SPacket
import opekope2.avm_staff.packet.RemoveBlockFromStaffC2SPacket
import opekope2.avm_staff.util.addBlock
import opekope2.avm_staff.util.hasBlock
import opekope2.avm_staff.util.readBlock
import opekope2.avm_staff.util.removeBlock

object ServerStaffHandler {
    fun addBlockToStaff(
        packet: AddBlockToStaffC2SPacket,
        player: ServerPlayerEntity,
        responseSender: PacketSender
    ) {
        val inventory = player.inventory

        val (staffStack, blockStack) = findStaffAndBlockStack(inventory) ?: return

        if (blockStack.isEmpty) return
        if (staffStack.hasBlock) return
        staffStack.addBlock(blockStack)
    }

    fun removeBlockFromStaff(
        packet: RemoveBlockFromStaffC2SPacket,
        player: ServerPlayerEntity,
        responseSender: PacketSender
    ) {
        val inventory = player.inventory

        val (staffStack, blockSlot) = findStaffStackAndBlockSlot(inventory) ?: return
        val blockStack = inventory.getStack(blockSlot)

        if (!staffStack.hasBlock) return

        val staffBlockStack = staffStack.readBlock()

        if (blockStack.canAccept(staffBlockStack, inventory.maxCountPerStack)) {
            inventory.insertStack(blockSlot, staffBlockStack)
            staffStack.removeBlock()
        }
    }

    private fun ItemStack.canAccept(other: ItemStack, maxCountPerStack: Int): Boolean {
        val canCombine = isEmpty || ItemStack.canCombine(this, other)
        val totalCount = count + other.count

        return canCombine && totalCount <= item.maxCount && totalCount <= maxCountPerStack
    }

    private fun findStaffStackAndBlockSlot(inventory: PlayerInventory): Pair<ItemStack, Int>? {
        val mainStack = inventory.mainHandStack
        val offStack = inventory.offHand[0]

        return when {
            mainStack.isOf(StaffMod.STAFF_ITEM) && !offStack.isOf(StaffMod.STAFF_ITEM) ->
                mainStack to PlayerInventory.OFF_HAND_SLOT

            offStack.isOf(StaffMod.STAFF_ITEM) && !mainStack.isOf(StaffMod.STAFF_ITEM) ->
                offStack to inventory.selectedSlot

            else -> null
        }
    }

    private fun findStaffAndBlockStack(inventory: PlayerInventory): Pair<ItemStack, ItemStack>? {
        val mainStack = inventory.mainHandStack
        val offStack = inventory.offHand[0]

        return when {
            mainStack.isOf(StaffMod.STAFF_ITEM) && !offStack.isOf(StaffMod.STAFF_ITEM) -> mainStack to offStack
            offStack.isOf(StaffMod.STAFF_ITEM) && !mainStack.isOf(StaffMod.STAFF_ITEM) -> offStack to mainStack
            else -> null
        }
    }
}
