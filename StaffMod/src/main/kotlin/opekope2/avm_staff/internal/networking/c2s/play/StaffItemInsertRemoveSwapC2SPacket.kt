/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.event.GameEvent.RESONATE_5
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.internal.networking.IC2SPacket
import opekope2.avm_staff.internal.networking.PacketRegistrarAndReceiver
import opekope2.avm_staff.internal.networking.s2c.play.StaffItemInsertRemoveSwapFeedbackS2CPacket
import opekope2.avm_staff.util.*

internal class StaffItemInsertRemoveSwapC2SPacket() : IC2SPacket {
    @Suppress("UNUSED_PARAMETER")
    constructor(buf: PacketByteBuf) : this()

    override fun getId() = payloadId

    override fun write(buf: PacketByteBuf) {
    }

    companion object : PacketRegistrarAndReceiver<StaffItemInsertRemoveSwapC2SPacket>(
        NetworkManager.c2s(),
        Identifier.of(MOD_ID, "staff_item_insert_remove_swap"),
        ::StaffItemInsertRemoveSwapC2SPacket
    ) {
        override fun receive(value: StaffItemInsertRemoveSwapC2SPacket, context: NetworkManager.PacketContext) {
            val player = context.player as ServerPlayerEntity
            val inventory = player.inventory
            var feedback: Text? = null

            fun failed(feedback: Text? = null) {
                StaffItemInsertRemoveSwapFeedbackS2CPacket(false, feedback).sendToPlayer(player)
            }

            val staffStack: ItemStack
            val otherStack: ItemStack
            val otherSlot: Int

            when {
                player.mainHandStack.isStaff && !player.offHandStack.isStaff -> {
                    staffStack = player.mainHandStack
                    otherStack = player.offHandStack
                    otherSlot = PlayerInventory.OFF_HAND_SLOT
                }

                player.offHandStack.isStaff && !player.mainHandStack.isStaff -> {
                    staffStack = player.offHandStack
                    otherStack = player.mainHandStack
                    otherSlot = inventory.selectedSlot
                }

                else -> return failed()
            }

            if (player.isItemCoolingDown(staffStack.item))
                return failed(I18n.FEEDBACK_AVM_STAFF_COOLDOWN.getText(staffStack.name))

            val stackInStaff = staffStack.itemStackInStaff ?: ItemStack.EMPTY

            when {
                stackInStaff.isEmpty && otherStack.isEmpty -> return failed()

                // Remove
                !stackInStaff.isEmpty && !otherStack.isEmpty && otherStack.count != 1 &&
                        ItemStack.areItemsEqual(otherStack, stackInStaff) -> {
                    val compatible = ItemStack.areItemsAndComponentsEqual(otherStack, stackInStaff)
                    val overstack = otherStack.count + stackInStaff.count > stackInStaff.maxCount

                    when {
                        compatible && !overstack -> {
                            otherStack.increment(stackInStaff.count)
                            staffStack.mutableItemStackInStaff = null
                        }

                        inventory.emptySlot != PlayerInventory.NOT_FOUND -> {
                            inventory.insertStack(stackInStaff.copy())
                            staffStack.mutableItemStackInStaff = null
                            feedback = I18n.FEEDBACK_AVM_STAFF_INVENTORY_STASHED.getText(stackInStaff.name)
                        }

                        else -> return failed(I18n.FEEDBACK_AVM_STAFF_INVENTORY_FULL.getText())
                    }
                }

                // Swap
                !stackInStaff.isEmpty && !otherStack.isEmpty -> {
                    if (otherStack.item.registryId !in StaffHandler.REGISTRY)
                        return failed(I18n.VALIDATION_ERROR_AVM_STAFF_NO_HANDLER.getText(otherStack.name))
                    if (otherStack !in staffStack.enabledItemsInStaffTag) return failed(
                        I18n.FEEDBACK_AVM_STAFF_HANDLER_NOT_ENABLED.getText(otherStack.name, staffStack.name)
                    )
                    if (otherStack.count != 1 && inventory.emptySlot == PlayerInventory.NOT_FOUND)
                        return failed(I18n.FEEDBACK_AVM_STAFF_INVENTORY_FULL.getText())

                    staffStack.mutableItemStackInStaff = otherStack.split(1)
                    if (otherStack.isEmpty) inventory.setStack(otherSlot, stackInStaff.copy())
                    else {
                        inventory.insertStack(stackInStaff.copy())
                        feedback = I18n.FEEDBACK_AVM_STAFF_INVENTORY_STASHED.getText(stackInStaff.name)
                    }
                }

                // Remove
                !stackInStaff.isEmpty -> {
                    assert(otherStack.isEmpty)
                    inventory.insertStack(otherSlot, stackInStaff.copy())
                    staffStack.mutableItemStackInStaff = null
                }

                // Insert
                !otherStack.isEmpty -> {
                    assert(stackInStaff.isEmpty)
                    if (otherStack.item.registryId !in StaffHandler.REGISTRY)
                        return failed(I18n.VALIDATION_ERROR_AVM_STAFF_NO_HANDLER.getText(otherStack.name))
                    if (otherStack !in staffStack.enabledItemsInStaffTag) return failed(
                        I18n.FEEDBACK_AVM_STAFF_HANDLER_NOT_ENABLED.getText(otherStack.name, staffStack.name)
                    )

                    staffStack.mutableItemStackInStaff = otherStack.split(1)
                }
            }

            player.resetLastAttackedTicks()
            player.entityWorld.emitGameEvent(player, RESONATE_5, player.pos)
            StaffItemInsertRemoveSwapFeedbackS2CPacket(true, feedback).sendToPlayer(player)
        }
    }
}
