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

package opekope2.avm_staff.content

import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.RegistryKeys
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryKeyUtil
import opekope2.avm_staff.util.TagKeyUtil

/**
 * Enchantments added by AVM Staffs mod.
 */
object Enchantments : RegistryKeyUtil<Enchantment>(MOD_ID, RegistryKeys.ENCHANTMENT) {
    @JvmField
    val RAPID_FIRE = registryKey("rapid_fire")

    @JvmField
    val SPECTRE = registryKey("spectre")

    /**
     * Enchantment tags added by AVM Staffs mod.
     */
    object Tags : TagKeyUtil<Enchantment>(MOD_ID, RegistryKeys.ENCHANTMENT) {
        /**
         * Enchantment registered as `avm_staff:redirects_impact_tnt`.
         */
        @JvmField
        val REDIRECTS_IMPACT_TNT = tagKey("redirects_impact_tnt")
    }
}
