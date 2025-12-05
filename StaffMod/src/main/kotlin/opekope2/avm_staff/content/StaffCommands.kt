/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

import net.minecraft.registry.Registry
import opekope2.avm_staff.api.staff.StaffCommand
import opekope2.avm_staff.internal.staff.command.GreetCommand
import opekope2.avm_staff.internal.staff.command.NoOpCommand
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryKeyUtil

internal object StaffCommands : RegistryKeyUtil<StaffCommand.Type<*>>(MOD_ID, StaffCommand.Type.REGISTRY_KEY) {
    private fun <T : StaffCommand<*>> register(path: String, type: StaffCommand.Type<T>): StaffCommand.Type<T> {
        Registry.register(StaffCommand.Type.REGISTRY, id(path), type)
        return type
    }

    @JvmField
    val NO_OP = register("no_op", NoOpCommand.type)

    @JvmField
    val GREET = register("greet", GreetCommand.TYPE)
}
