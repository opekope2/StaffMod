/*
 * AvM Staff Mod
 * Copyright (c) 2024-2025 opekope2
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

@file: JvmName("ItemStackUtil")

package opekope2.avm_staff.util

import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import opekope2.avm_staff.api.item.StaffItem

/**
 * Checks if the given item stack is a staff.
 */
inline val ItemStack.isStaff
    get() = item is StaffItem

/**
 * @see StaffItem.damage
 * @see ItemStack.damage
 */
fun ItemStack.damage(amount: Int = 1, entity: LivingEntity, hand: Hand = entity.activeHand) {
    val item = item
    if (item is StaffItem) item.damage(this, amount, entity, LivingEntity.getSlotForHand(hand))
    else damage(amount, entity, LivingEntity.getSlotForHand(hand))
}
