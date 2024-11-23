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
import opekope2.avm_staff.util.TagKeyUtil

/**
 * Enchantments added by AVM Staffs mod.
 */
object Enchantments {
    /**
     * Enchantment tags added by AVM Staffs mod.
     */
    object Tags : TagKeyUtil<Enchantment>(MOD_ID, RegistryKeys.ENCHANTMENT) {
        /**
         * Enchantment registered as `avm_staff:allows_projectile_rapid_fire`.
         */
        @JvmField
        val ALLOWS_PROJECTILE_RAPID_FIRE = tagKey("allows_projectile_rapid_fire")
    }
}
