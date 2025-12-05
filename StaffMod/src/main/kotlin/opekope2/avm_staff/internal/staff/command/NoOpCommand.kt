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

package opekope2.avm_staff.internal.staff.command

import com.mojang.serialization.MapCodec
import net.minecraft.registry.entry.RegistryEntry
import opekope2.avm_staff.api.component.StaffCommandComponent
import opekope2.avm_staff.api.staff.StaffCommand

internal data object NoOpCommand : StaffCommand<NoOpCommand.Args>() {
    override val type = Type(MapCodec.unit(this))
    override val argsCodec: MapCodec<Args> = MapCodec.unit(Args)
    val component = StaffCommandComponent(RegistryEntry.of(this), Args)

    data object Args : IArgs
}
