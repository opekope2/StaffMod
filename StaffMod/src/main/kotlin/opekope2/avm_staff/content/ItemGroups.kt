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

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.item.ItemGroup
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import opekope2.avm_staff.content.ItemGroups.AVM_STAFF_MOD_ITEMS
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.Registrar
import opekope2.avm_staff.util.mutableItemStackInStaff

/**
 * Item groups added by AVM Staffs mod.
 */
object ItemGroups : Registrar<ItemGroup>(MOD_ID, RegistryKeys.ITEM_GROUP) {
    /**
     * Item group containing items added by Staff Mod.
     */
    @JvmField
    val AVM_STAFF_MOD_ITEMS: RegistrySupplier<ItemGroup> = register("${MOD_ID}_items") {
        CreativeTabRegistry.create(Text.translatable("itemGroup.${MOD_ID}_items")) {
            Items.royalStaff.defaultStack.apply {
                mutableItemStackInStaff = net.minecraft.item.Items.COMMAND_BLOCK.defaultStack
            }
        }
    }

    /**
     * @see AVM_STAFF_MOD_ITEMS
     */
    val avmStaffModItems: ItemGroup
        @JvmName("avmStaffModItems")
        get() = AVM_STAFF_MOD_ITEMS.get()
}
