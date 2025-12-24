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

package opekope2.avm_staff.util

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.TagKey

/**
 * @see BlockState.isIn
 */
operator fun TagKey<Block>.contains(state: BlockState) = state.isIn(this)

/**
 * @see ItemStack.isIn
 */
operator fun TagKey<Item>.contains(stack: ItemStack) = stack.isIn(this)

/**
 * @see ItemStack.isIn
 */
operator fun TagKey<Item>.contains(item: Item) = item.registryEntry.isIn(this)

/**
 * @see DamageSource.isIn
 */
operator fun TagKey<DamageType>.contains(source: DamageSource) = source.isIn(this)

/**
 * @see EntityType.isIn
 */
operator fun TagKey<EntityType<*>>.contains(type: EntityType<*>) = type.isIn(this)
