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

package opekope2.avm_staff.api.staff

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec

/**
 * Data components to store the state of a furnace staff.
 *
 * @param serverBurnTicks   The ticks the furnace has been on for minus the items smelted. This data is not synced to
 *   the client
 */
class StaffFurnaceDataComponent(var serverBurnTicks: Int) {
    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        else -> true
    }

    override fun hashCode() = javaClass.hashCode()

    companion object {
        /**
         * [PacketCodec] for [StaffFurnaceDataComponent], which doesn't sync [serverBurnTicks].
         */
        @JvmField
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, StaffFurnaceDataComponent> =
            PacketCodec.of({ _, _ -> }, { StaffFurnaceDataComponent(0) })
    }
}
