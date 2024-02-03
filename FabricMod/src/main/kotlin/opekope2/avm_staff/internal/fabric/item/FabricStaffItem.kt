/*
 * AvM Staff Mod
 * Copyright (c) 2024 opekope2
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

package opekope2.avm_staff.internal.fabric.item

import com.google.common.collect.Multimap
import net.fabricmc.fabric.api.item.v1.FabricItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.util.handlerOfItemOrFallback
import opekope2.avm_staff.util.itemInStaff

class FabricStaffItem(settings: Item.Settings) : StaffItem(settings), FabricItem {
    override fun allowNbtUpdateAnimation(
        player: PlayerEntity,
        hand: Hand,
        oldStack: ItemStack,
        newStack: ItemStack
    ): Boolean {
        val oldHandler = oldStack.itemInStaff.handlerOfItemOrFallback
        val newHandler = newStack.itemInStaff.handlerOfItemOrFallback

        return if (oldHandler !== newHandler) true
        else oldHandler.allowNbtUpdateAnimation(oldStack, newStack, player, hand)
    }

    override fun getAttributeModifiers(
        stack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return stack.itemInStaff.handlerOfItemOrFallback.getAttributeModifiers(stack, slot)
    }
}
