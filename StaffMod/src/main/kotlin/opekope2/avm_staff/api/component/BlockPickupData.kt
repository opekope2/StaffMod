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

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.util.math.BlockPos

/**
 * Data component to store the block about to be picked up by an empty staff.
 *
 * @param pos   The position of the block to be picked up from. Only available server-side
 * @param state The block state to be picked up. Only available server-side
 */
data class BlockPickupData(val pos: BlockPos, val state: BlockState) {
    companion object {
        @JvmField
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, BlockPickupData> =
            PacketCodec.of({ _, _ -> }, { BlockPickupData(BlockPos.ORIGIN, Blocks.AIR.defaultState) })
    }
}
