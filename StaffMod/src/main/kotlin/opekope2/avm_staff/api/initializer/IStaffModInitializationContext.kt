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

package opekope2.avm_staff.api.initializer

import net.minecraft.util.Identifier
import opekope2.avm_staff.api.item.StaffItemHandler

/**
 * Initialization context for [IStaffModInitializer].
 */
interface IStaffModInitializationContext {
    /**
     * Registers a [StaffItemHandler].
     *
     * @param itemInStaff           The item ID to register a handler for
     * @param handler               The handler, which processes staff interactions, while the [registered item][itemInStaff] is in it
     * @return `true`, if the registration was successful, `false`, if the item was already registered
     */
    fun registerStaffItemHandler(itemInStaff: Identifier, handler: StaffItemHandler): Boolean
}
