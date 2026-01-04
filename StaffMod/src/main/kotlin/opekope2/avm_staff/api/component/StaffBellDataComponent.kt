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

package opekope2.avm_staff.api.component

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import opekope2.avm_staff.mixin.IBellBlockEntityAccessor

/**
 * Data component to store bell data.
 *
 * @param lastRingTime  [World time][net.minecraft.world.World.getTime] when the bell staff was rang.
 */
data class StaffBellDataComponent(val lastRingTime: Long) {
    /**
     * Gets the [world time][net.minecraft.world.World.getTime] when the glow effect should be applied to nearby entities
     */
    val applyGlowTime: Long
        get() = lastRingTime + IBellBlockEntityAccessor.maxResonatingTicks()

    companion object {
        /**
         * [PacketCodec] for [StaffBellDataComponent].
         */
        @JvmField
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, StaffBellDataComponent> =
            PacketCodec.tuple(PacketCodecs.VAR_LONG, StaffBellDataComponent::lastRingTime, ::StaffBellDataComponent)
    }
}
