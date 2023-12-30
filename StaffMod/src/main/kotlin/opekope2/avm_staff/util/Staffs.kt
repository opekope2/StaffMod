package opekope2.avm_staff.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

private const val BLOCK_KEY = "Block"

val ItemStack.hasBlock: Boolean
    get() = nbt?.contains(BLOCK_KEY) ?: false

fun ItemStack.readBlock(): ItemStack {
    val nbt = nbt ?: return ItemStack.EMPTY
    if (!nbt.contains(BLOCK_KEY)) return ItemStack.EMPTY

    return ItemStack.fromNbt(nbt.getCompound(BLOCK_KEY))
}

fun ItemStack.addBlock(blockStack: ItemStack) {
    val staffBlockStack = blockStack.split(1)

    val nbt = getOrCreateNbt()
    nbt.put(BLOCK_KEY, NbtCompound().also(staffBlockStack::writeNbt))
}

fun ItemStack.removeBlock() = removeSubNbt(BLOCK_KEY)
