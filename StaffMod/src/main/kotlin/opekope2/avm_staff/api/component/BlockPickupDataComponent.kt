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

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.math.BlockPos

/**
 * Data component to store the block about to be picked up by an empty staff.
 *
 * @param pos   The position of the block to be picked up from. Only available server-side
 * @param state The block state to be picked up. Only available server-side
 */
data class BlockPickupDataComponent(val pos: BlockPos, val state: BlockState) {
    companion object {
        /**
         * [PacketCodec] for [BlockPickupDataComponent].
         */
        @JvmField
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, BlockPickupDataComponent> = PacketCodec.tuple(
            BlockPos.PACKET_CODEC,
            BlockPickupDataComponent::pos,
            PacketCodecs.entryOf(Block.STATE_IDS),
            BlockPickupDataComponent::state,
            ::BlockPickupDataComponent
        )
    }
}
