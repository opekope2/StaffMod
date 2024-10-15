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

package opekope2.avm_staff.internal.networking.s2c.play

import dev.architectury.networking.NetworkManager
import net.minecraft.block.Block
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.RegistryKey
import net.minecraft.server.MinecraftServer
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import opekope2.avm_staff.internal.networking.IS2CPacket
import opekope2.avm_staff.internal.networking.PacketRegistrarAndReceiver
import opekope2.avm_staff.mixin.IParticleManagerAccessor
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.countingSort
import java.util.function.IntSupplier
import kotlin.math.sqrt

internal class MassDestructionS2CPacket(val positions: List<BlockPos>, val rawIds: List<Int>) : IS2CPacket {
    init {
        require(positions.isNotEmpty()) { "positions must not be empty" }
        require(positions.size < MAX_DATA_IN_PACKET) { "too much data (max. $MAX_DATA_IN_PACKET)" }
        require(positions.size == rawIds.size) { "positions and rawIds must contain the same amount of elements" }
    }

    private val volume = BlockBox.encompassPositions(positions).get()
    private val volumeCenter = volume.center.toCenterPos()
    private val volumeHalfDiagonal =
        sqrt((MathHelper.square(volume.blockCountX) + MathHelper.square(volume.blockCountY) + MathHelper.square(volume.blockCountZ)).toDouble()) / 2.0

    constructor(buf: PacketByteBuf) : this(buf.readList(BlockPos.PACKET_CODEC), buf.readList(PacketCodecs.INTEGER))

    override fun getId() = payloadId

    override fun write(buf: PacketByteBuf) {
        buf.writeCollection(positions, BlockPos.PACKET_CODEC)
        buf.writeCollection(rawIds, PacketCodecs.INTEGER)
    }

    fun sendToAround(server: MinecraftServer, worldKey: RegistryKey<World>) {
        positions.sortedBy { it.x }
        intArrayOf().sort()

        val maxDistanceSquare = MathHelper.square(64 + volumeHalfDiagonal)
        val players = server.playerManager.playerList.filter { player ->
            if (player.world.registryKey !== worldKey) false
            else player.squaredDistanceTo(volumeCenter) <= maxDistanceSquare
        }

        NetworkManager.sendToPlayers(players, this)
    }

    private data class BlockBrokenWorldEvent(
        val pos: BlockPos,
        val blockStateRawId: Int,
        var squaredDistanceFromPlayer: Int
    ) : IntSupplier {
        override fun getAsInt() = squaredDistanceFromPlayer
    }

    companion object : PacketRegistrarAndReceiver<MassDestructionS2CPacket>(
        NetworkManager.s2c(),
        Identifier.of(MOD_ID, "mass_destruction"),
        ::MassDestructionS2CPacket
    ) {
        const val MAX_DATA_IN_PACKET = 1024 * 1024

        override fun receive(packet: MassDestructionS2CPacket, context: NetworkManager.PacketContext) {
            val maxParticles = IParticleManagerAccessor.maxParticleCount() / (4 * 4 * 4)
            val maxSounds = 128
            val world = context.player.entityWorld
            val playerPos = context.player.pos
            val blockBrokenEvents = Array(packet.positions.size) { i ->
                val pos = packet.positions[i]
                BlockBrokenWorldEvent(pos, packet.rawIds[i], pos.getSquaredDistance(playerPos).toInt())
            }
            val min = blockBrokenEvents.minOf { it.squaredDistanceFromPlayer }
            val max = blockBrokenEvents.maxOf { it.squaredDistanceFromPlayer } - min

            for (event in blockBrokenEvents) {
                event.squaredDistanceFromPlayer -= min
            }

            val sortedBlockBrokenEvents = countingSort(blockBrokenEvents, max)
            sortedBlockBrokenEvents.take(maxSounds).forEach { (pos, blockStateRawId) ->
                val blockState = Block.getStateFromRawId(blockStateRawId)
                if (blockState.isAir) return@forEach

                val soundGroup = blockState.soundGroup
                world.playSoundAtBlockCenter(
                    pos,
                    soundGroup.breakSound,
                    SoundCategory.BLOCKS,
                    (soundGroup.volume + 1.0f) / 2.0f,
                    soundGroup.pitch * 0.8f,
                    false
                )
            }
            sortedBlockBrokenEvents.take(maxParticles).asReversed().forEach { (pos, blockStateRawId) ->
                val blockState = Block.getStateFromRawId(blockStateRawId)
                world.addBlockBreakParticles(pos, blockState)
            }
        }
    }
}
