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
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

/**
 * Utility class to create [Identifier]s and [TagKey]s using a specified [namespace][Identifier.namespace] and registry.
 *
 * @param TContent  The type of the content to register
 * @param modId     The [namespace][Identifier.namespace] of the content to register.
 * @param registry  The registry the content is registered in
 */
open class TagKeyUtil<TContent>(
    protected val modId: String,
    protected val registry: RegistryKey<Registry<TContent>>
) {
    init {
        require(Identifier.isNamespaceValid(modId)) { "Mod ID is not a valid namespace" }
    }

    /**
     * Creates an [Identifier] from the namespace specified in the constructor and a given path.
     *
     * @param path  The path of the [Identifier] to create
     */
    fun id(path: String): Identifier = Identifier.of(modId, path)

    /**
     * Creates a [TagKey] from the registry and namespace specified in the constructor and a given path.
     *
     * @param path  The path of the [Identifier] to create a tag key from
     */
    fun tagKey(path: String): TagKey<TContent> = TagKey.of(registry, id(path))
}

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
