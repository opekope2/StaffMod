package opekope2.avm_staff.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

private const val ITEM_KEY = "Item"

val ItemStack.staffHasItem: Boolean
    @JvmName("staffHasItem")
    get() = nbt?.contains(ITEM_KEY) ?: false

var ItemStack.staffItem: ItemStack?
    get() {
        return if (!staffHasItem) null
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
