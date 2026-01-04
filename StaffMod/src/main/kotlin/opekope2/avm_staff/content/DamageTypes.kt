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

package opekope2.avm_staff.content

import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKeys
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryKeyUtil

/**
 * Damage types added by AVM Staffs mod.
 */
object DamageTypes : RegistryKeyUtil<DamageType>(MOD_ID, RegistryKeys.DAMAGE_TYPE) {
    /**
     * Pranked damage type.
     */
    @JvmStatic
    val pranked by registryKey

    /**
     * Pranked by player damage type.
     */
    @JvmStatic
    val prankedByPlayer by registryKey
}
