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

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.stat.StatType
import net.minecraft.text.Text
import opekope2.avm_staff.content.StatTypes.USED_ITEM_IN_STAFF
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.Registrar

/**
 * Stats added by AVM Staffs mod.
 */
object StatTypes : Registrar<StatType<*>>(MOD_ID, RegistryKeys.STAT_TYPE) {
    /**
     * Stat type registered as `avm_staff:used_item_in_staff`
     */
    @JvmField
    val USED_ITEM_IN_STAFF = register("used_item_in_staff") { key ->
        StatType(Registries.ITEM, Text.translatable("stat_type.${key.value.namespace}.${key.value.path}"))
    }

    /**
     * @see USED_ITEM_IN_STAFF
     */
    val usedItemInStaff: StatType<Item>
        @JvmName("usedItemInStaff")
        get() = USED_ITEM_IN_STAFF.get()
}
