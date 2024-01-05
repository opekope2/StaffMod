// Copyright (c) 2023-2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

@file: JvmName("StaffUtil")

package opekope2.avm_staff.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

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
