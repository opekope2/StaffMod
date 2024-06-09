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

import com.mojang.serialization.DataResult
import dev.architectury.networking.NetworkManager.PacketContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import opekope2.avm_staff.api.staffsTag
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.InsertItemIntoStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.util.*

@Suppress("UNUSED_PARAMETER")
fun addItemToStaff(packet: InsertItemIntoStaffC2SPacket, context: PacketContext) {
    context.player.canInsertIntoStaff().ifSuccess { (staffStack, itemStackToAdd) ->
        staffStack.mutableItemStackInStaff = itemStackToAdd.split(1)
        context.player.resetLastAttackedTicks()
    }
}

@Suppress("UNUSED_PARAMETER")
fun removeItemFromStaff(packet: RemoveItemFromStaffC2SPacket, context: PacketContext) {
    context.player.canRemoveFromStaff().ifSuccess { (staffStack, targetSlot) ->
        context.player.inventory.insertStack(targetSlot, staffStack.mutableItemStackInStaff)
        staffStack.mutableItemStackInStaff = null
        context.player.resetLastAttackedTicks()
    }
}

fun attack(packet: AttackC2SPacket, context: PacketContext) {
    val player = context.player
    val staffStack = player.mainHandStack

    if (staffStack !in staffsTag) return

    val itemInStaff = staffStack.itemInStaff ?: return
    val staffHandler = itemInStaff.staffHandler ?: return

    staffHandler.attack(staffStack, player.entityWorld, player, packet.hand)
}

fun PlayerEntity.canInsertIntoStaff(): DataResult<Pair<ItemStack, ItemStack>> {
    val staffStack: ItemStack
    val itemStackToAdd: ItemStack

    when {
        mainHandStack in staffsTag && offHandStack !in staffsTag -> {
            staffStack = mainHandStack
            itemStackToAdd = offHandStack
        }

        offHandStack in staffsTag && mainHandStack !in staffsTag -> {
            staffStack = offHandStack
            itemStackToAdd = mainHandStack
        }

        else -> return DataResult.error { "The player doesn't hold exactly 1 staff" }
    }

    if (itemStackToAdd.isEmpty) return DataResult.error { "Can't insert empty ItemStack into staff" }
    if (staffStack.isItemInStaff) return DataResult.error { "An item is already inserted into the staff" }
    if (isItemCoolingDown(staffStack.item)) return DataResult.error { "Staff is cooling down" }
    if (!itemStackToAdd.item.hasStaffHandler) return DataResult.error { "Can't insert item without a StaffHandler into the staff" }

    return DataResult.success(staffStack to itemStackToAdd)
}

fun PlayerEntity.canRemoveFromStaff(): DataResult<Pair<ItemStack, Int>> {
    if (isUsingItem) return DataResult.error { "The player is using an item" }

    val staffStack: ItemStack
    val targetSlot: Int

    when {
        mainHandStack in staffsTag && offHandStack !in staffsTag -> {
            staffStack = mainHandStack
            targetSlot = PlayerInventory.OFF_HAND_SLOT
        }

        offHandStack in staffsTag && mainHandStack !in staffsTag -> {
            staffStack = offHandStack
            targetSlot = inventory.selectedSlot
        }

        else -> return DataResult.error { "The player doesn't hold exactly 1 staff" }
    }

    val targetStack = inventory.getStack(targetSlot)
    val itemStackInStaff = staffStack.itemStackInStaff ?: return DataResult.error { "Staff is empty" }

    if (isItemCoolingDown(staffStack.item)) return DataResult.error { "Staff is cooling down" }
    if (!targetStack.canAccept(itemStackInStaff, inventory.maxCountPerStack)) return DataResult.error {
        "Target stack is incompatible with staff item"
    }

    return DataResult.success(staffStack to targetSlot)
}

private fun ItemStack.canAccept(other: ItemStack, maxCountPerStack: Int): Boolean {
    val canCombine = isEmpty || ItemStack.areItemsAndComponentsEqual(this, other)
    val totalCount = count + other.count

    return canCombine && totalCount <= item.maxCount && totalCount <= maxCountPerStack
}
