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

import dev.architectury.networking.NetworkManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import opekope2.avm_staff.internal.networking.IC2SPacket
import opekope2.avm_staff.internal.networking.PacketRegistrarAndReceiver
import opekope2.avm_staff.util.*

internal class InsertItemIntoStaffC2SPacket() : IC2SPacket {
    @Suppress("UNUSED_PARAMETER")
    constructor(buf: PacketByteBuf) : this()

    override fun getId() = payloadId

    override fun write(buf: PacketByteBuf) {
    }

    companion object : PacketRegistrarAndReceiver<InsertItemIntoStaffC2SPacket>(
        NetworkManager.c2s(),
        Identifier(MOD_ID, "add_item"),
        ::InsertItemIntoStaffC2SPacket
    ) {
        override fun receive(packet: InsertItemIntoStaffC2SPacket, context: NetworkManager.PacketContext) {
            context.player.tryInsertItemIntoStaff { player, staffStack, toInsert ->
                staffStack.mutableItemStackInStaff = toInsert.split(1)
                player.resetLastAttackedTicks()
            }
        }

        inline fun PlayerEntity.tryInsertItemIntoStaff(insertAction: (PlayerEntity, ItemStack, ItemStack) -> Unit): Boolean {
            val staffStack: ItemStack
            val itemStackToAdd: ItemStack

            when {
                mainHandStack.isStaff && !offHandStack.isStaff -> {
                    staffStack = mainHandStack
                    itemStackToAdd = offHandStack
                }

                offHandStack.isStaff && !mainHandStack.isStaff -> {
                    staffStack = offHandStack
                    itemStackToAdd = mainHandStack
                }

                else -> return false
            }

            if (itemStackToAdd.isEmpty) return false
            if (staffStack.isItemInStaff) return false
            if (isItemCoolingDown(staffStack.item)) return false
            if (!itemStackToAdd.item.hasStaffHandler) return false

            insertAction(this, staffStack, itemStackToAdd)
            return true
        }
    }
}
