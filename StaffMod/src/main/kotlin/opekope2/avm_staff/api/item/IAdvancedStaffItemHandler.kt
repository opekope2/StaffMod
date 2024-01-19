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

package opekope2.avm_staff.api.item

/**
 * Provides additional functionality for a staff, when an item is inserted into it. To make use of the extra
 * functionalities, implement this interface alongside [StaffItemHandler].
 *
 * @see StaffItemHandler
 */
interface IAdvancedStaffItemHandler {
    /**
     * Returns if the current item should disable the shield of the attacked entity.
     */
    fun disablesShield(): Boolean = false
}
