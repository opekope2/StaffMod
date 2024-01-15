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

package opekope2.avm_staff.api.initializer

import net.fabricmc.api.ModInitializer
import opekope2.avm_staff.api.item.StaffItemHandler

/**
 * Staff Mod initializer called by Staff Mod from its [ModInitializer.onInitialize].
 * In `fabric.mod.json`, the entrypoint key is `avm-staff`.
 */
fun interface IStaffModInitializer {
    /**
     * Implement this method to add support for [an item to be used in a staff][StaffItemHandler].
     */
    fun onInitializeStaffMod(context: IStaffModInitializationContext)
}
