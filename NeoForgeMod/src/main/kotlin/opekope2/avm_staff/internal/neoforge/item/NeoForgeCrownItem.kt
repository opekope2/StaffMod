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

package opekope2.avm_staff.internal.neoforge.item

import net.minecraft.block.Block
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.neoforged.neoforge.common.extensions.IItemExtension
import opekope2.avm_staff.api.item.CrownItem

class NeoForgeCrownItem(groundBlock: Block, wallBlock: Block, settings: Item.Settings) :
    CrownItem(groundBlock, wallBlock, settings), IItemExtension {
    override fun isRepairable(arg: ItemStack) = false

    override fun makesPiglinsNeutral(stack: ItemStack, wearer: LivingEntity) = stack.item is CrownItem
}
