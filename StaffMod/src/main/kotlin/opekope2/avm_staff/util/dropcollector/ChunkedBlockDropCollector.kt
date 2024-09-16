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

package opekope2.avm_staff.util.dropcollector

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import kotlin.math.ceil
import kotlin.math.floor

/**
 * A block drop collector that merges the dropped items if they're in the same sub-cuboid.
 *
 * @param box           The volume the blocks will be broken inside. Calling [collect] with a position outside it will throw
 *  an error
 * @param maxChunkSize  The maximum side length of a sub-cuboid
 */
class ChunkedBlockDropCollector(private val box: BlockBox, maxChunkSize: Int) : IBlockDropCollector {
    private val clumpsX = ceil(box.blockCountX / maxChunkSize.toFloat()).toInt()
    private val clumpsY = ceil(box.blockCountY / maxChunkSize.toFloat()).toInt()
    private val clumpsZ = ceil(box.blockCountZ / maxChunkSize.toFloat()).toInt()
    private val clumps = Array(clumpsX * clumpsY * clumpsZ) { BlockDropCollector() }

    override fun collect(
        world: ServerWorld,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        destroyer: Entity,
        tool: ItemStack
    ) {
        require(pos in box) { "Position is outside the specified box" }

        this[
            floor((pos.x - box.minX).toFloat() * clumpsX / box.blockCountX).toInt(),
            floor((pos.y - box.minY).toFloat() * clumpsY / box.blockCountY).toInt(),
            floor((pos.z - box.minZ).toFloat() * clumpsZ / box.blockCountZ).toInt()
        ].collect(world, pos, state, blockEntity, destroyer, tool)
    }

    private operator fun get(x: Int, y: Int, z: Int): IBlockDropCollector {
        return clumps[x * clumpsY * clumpsZ + y * clumpsZ + z]
    }

    override fun dropAll(world: ServerWorld) {
        clumps.forEach { it.dropAll(world) }
    }
}
