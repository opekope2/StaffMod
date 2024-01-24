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

@file: JvmName("StaffUtil")

package opekope2.avm_staff.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import opekope2.avm_staff.api.item.StaffItemHandler

/**
 * NBT key
 */
private const val ITEM_KEY = "Item"

/**
 * Checks if an item is added the given staff item stack.
 */
val ItemStack.isItemInStaff: Boolean
    @JvmName("isItemInStaff")
    get() = nbt?.contains(ITEM_KEY) ?: false

/**
 * Gets or sets the item added to the given staff item stack.
 */
var ItemStack.itemInStaff: ItemStack?
    get() {
        return if (!isItemInStaff) null
        else ItemStack.fromNbt(nbt?.getCompound(ITEM_KEY) ?: return null)
    }
    set(value) {
        if (value == null) {
            removeSubNbt(ITEM_KEY)
            return
        }

        val staffItemStack = value.split(1)
        val nbt = getOrCreateNbt()
        nbt.put(ITEM_KEY, NbtCompound().also(staffItemStack::writeNbt))
    }

/**
 * Returns if the given staff item stack has a registered handler.
 * This item stack is not the staff item stack, but the one can be inserted into the staff.
 */
val ItemStack.hasHandlerOfItem: Boolean
    @JvmName("hasHandlerOfStaff")
    get() {
        val itemId = Registries.ITEM.getId(item)
        return itemId in StaffItemHandler
    }

/**
 * Returns the handler of the given item stack, if available.
 * This item stack is not the staff item stack, but the one can be inserted into the staff.
 */
val ItemStack.handlerOfItem: StaffItemHandler?
    get() {
        val itemId = Registries.ITEM.getId(item)
        return StaffItemHandler[itemId]
    }
