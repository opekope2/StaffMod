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

package opekope2.avm_staff.api.component

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

/**
 * Data components to store the shot TNT in a TNT staff.
 *
 * @param tntId The [network ID][net.minecraft.entity.ItemEntity.getId] of the shot TNT entity
 */
data class StaffTntDataComponent(val tntId: Int) {
    companion object {
        /**
         * [PacketCodec] for [StaffTntDataComponent].
         */
        @JvmField
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, StaffTntDataComponent> =
            PacketCodec.tuple(PacketCodecs.VAR_INT, StaffTntDataComponent::tntId, ::StaffTntDataComponent)
    }
}
