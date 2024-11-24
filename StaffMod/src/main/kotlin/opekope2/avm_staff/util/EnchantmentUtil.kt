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

@file: JvmName("EnchantmentUtil")

package opekope2.avm_staff.util

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.RegistryKey
import opekope2.avm_staff.content.Enchantments.getEntry

/**
 * Gets an enchantment level on an item
 *
 * @param enchantment       The registry key of the enchantment to check
 * @param registryManager   The registry manager of a world
 * @see EnchantmentHelper.getLevel
 */
fun ItemStack.getEnchantmentLevel(enchantment: RegistryKey<Enchantment>, registryManager: DynamicRegistryManager) =
    EnchantmentHelper.getLevel(enchantment.getEntry(registryManager), this)

/**
 * Checks is an item is enchanted with [enchantment]
 *
 * @param enchantment       The registry key of the enchantment to check
 * @param registryManager   The registry manager of a world
 * @see EnchantmentHelper.getLevel
 */
fun ItemStack.isEnchantedWith(enchantment: RegistryKey<Enchantment>, registryManager: DynamicRegistryManager) =
    getEnchantmentLevel(enchantment, registryManager) > 0
