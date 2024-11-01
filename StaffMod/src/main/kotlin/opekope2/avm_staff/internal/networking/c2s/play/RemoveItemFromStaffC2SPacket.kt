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

package opekope2.avm_staff.internal.networking.c2s.play

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.util.Identifier
import net.minecraftforge.event.network.CustomPayloadEvent
import net.minecraftforge.network.NetworkDirection
import opekope2.avm_staff.internal.networking.IC2SPacket
import opekope2.avm_staff.internal.networking.PacketRegistrarAndReceiver
import opekope2.avm_staff.util.*

internal class RemoveItemFromStaffC2SPacket() : IC2SPacket<RemoveItemFromStaffC2SPacket, RegistryByteBuf> {
    @Suppress("UNUSED_PARAMETER")
    constructor(buf: RegistryByteBuf) : this()

    override fun write(buf: RegistryByteBuf) {
    }

    override fun sendToServer() = sendToServer(channel)

    companion object : PacketRegistrarAndReceiver<RemoveItemFromStaffC2SPacket, RegistryByteBuf>(
        NetworkDirection.PLAY_TO_SERVER,
        Identifier.of(MOD_ID, "remove_item"),
        RemoveItemFromStaffC2SPacket::class.java,
        ::RemoveItemFromStaffC2SPacket
    ) {
        override fun receive(packet: RemoveItemFromStaffC2SPacket, context: CustomPayloadEvent.Context) {
            context.sender!!.tryRemoveItemFromStaff { player, staffStack, targetSlot ->
                player.inventory.insertStack(targetSlot, staffStack.mutableItemStackInStaff)
                staffStack.mutableItemStackInStaff = null
                player.resetLastAttackedTicks()
            }
        }

        inline fun PlayerEntity.tryRemoveItemFromStaff(removeAction: (PlayerEntity, ItemStack, Int) -> Unit): Boolean {
            if (isUsingItem) return false

            val staffStack: ItemStack
            val targetSlot: Int

            when {
                mainHandStack.isStaff && !offHandStack.isStaff -> {
                    staffStack = mainHandStack
                    targetSlot = PlayerInventory.OFF_HAND_SLOT
                }

                offHandStack.isStaff && !mainHandStack.isStaff -> {
                    staffStack = offHandStack
                    targetSlot = inventory.selectedSlot
                }

                else -> return false
            }

            val targetStack = inventory.getStack(targetSlot)
            val itemStackInStaff = staffStack.itemStackInStaff ?: return false

            if (isItemCoolingDown(staffStack.item)) return false
            // ItemStack merge-ability check
            if (targetStack.count + itemStackInStaff.count > itemStackInStaff.maxCount) return false
            if (targetStack.isEmpty || ItemStack.areItemsAndComponentsEqual(targetStack, itemStackInStaff)) {
                removeAction(this, staffStack, targetSlot)
                return true
            }
            return false
        }
    }
}
