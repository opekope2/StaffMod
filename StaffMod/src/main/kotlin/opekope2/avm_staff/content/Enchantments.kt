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

import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.RegistryKeys
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryKeyUtil

/**
 * Enchantments added by AVM Staffs mod.
 */
object Enchantments : RegistryKeyUtil<Enchantment>(MOD_ID, RegistryKeys.ENCHANTMENT) {
    /**
     * Cohesion enchantment.
     * Increases the number of scraps when a staff breaks into pieces.
     */
    @JvmStatic
    val cohesion by registryKey

    /**
     * Distant detonation enchantment.
     * Allows detonating thrown impact TNT with the TNT staff.
     */
    @JvmStatic
    val distantDetonation by registryKey

    /**
     * Power charge enchantment.
     * Makes certain explosive projectiles fired from the staff stronger.
     */
    @JvmStatic
    val powerCharge by registryKey

    /**
     * Quick draw enchantment.
     * Reduces certain staff cooldowns.
     */
    @JvmStatic
    val quickDraw by registryKey

    /**
     * Rapid fire enchantment.
     * Allows rapid firing of projectiles from certain staffs.
     */
    @JvmStatic
    val rapidFire by registryKey

    /**
     * Enchantment tags added by AVM Staffs mod.
     */
    object Tags : RegistryKeyUtil<Enchantment>(MOD_ID, RegistryKeys.ENCHANTMENT) {
        /**
         * Enchantments that can change impact TNT velocity on attack without blowing it up.
         */
        @JvmStatic
        val redirectsImpactTnt by tagKey
    }
}
