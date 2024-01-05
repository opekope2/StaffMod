// Copyright (c) 2023-2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.api.item

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import opekope2.avm_staff.util.isItemInStaff
import opekope2.avm_staff.util.itemInStaff

/**
 * Staff item.
 */
class StaffItem(settings: Settings) : Item(settings) {
    override fun getName(stack: ItemStack): Text {
        val staffItem = stack.itemInStaff ?: return super.getName(stack)
        val staffItemText = Text.translatable(staffItem.item.getTranslationKey(staffItem))
        return Text.translatable(getTranslationKey(stack), staffItemText)
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return if (stack.isItemInStaff) "$translationKey.with_item"
        else super.getTranslationKey(stack)
    }
}
