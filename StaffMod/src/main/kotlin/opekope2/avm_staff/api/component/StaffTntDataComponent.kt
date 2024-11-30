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

package opekope2.avm_staff.api.component

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import opekope2.avm_staff.api.entity.ImpactTntEntity

/**
 * Data components to store the shot TNT in a TNT staff.
 *
 * @param tnt   The shot TNT entity. Only available server-side
 */
data class StaffTntDataComponent(val tnt: ImpactTntEntity?) {
    companion object {
        /**
         * [PacketCodec] for [StaffTntDataComponent], which doesn't sync its data.
         */
        @JvmField
        val NON_SYNCING_PACKET_CODEC: PacketCodec<RegistryByteBuf, StaffTntDataComponent> =
            PacketCodec.of({ _, _ -> }, { StaffTntDataComponent(null) })
    }
}
