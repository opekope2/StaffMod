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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package opekope2.avm_staff.internal.item

import net.minecraft.util.Identifier
import opekope2.avm_staff.api.item.StaffItemHandler

object StaffItemHandlers {
    private val supportedStaffItems = mutableMapOf<Identifier, StaffItemHandler>()

    @JvmStatic
    fun register(staffItem: Identifier, handler: StaffItemHandler): Boolean {
        if (staffItem in supportedStaffItems) return false

        supportedStaffItems[staffItem] = handler
        return true
    }

    operator fun contains(staffItem: Identifier): Boolean = staffItem in supportedStaffItems

    operator fun get(staffItem: Identifier): StaffItemHandler? = supportedStaffItems[staffItem]
}
