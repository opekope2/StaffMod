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

package opekope2.avm_staff.util.destruction

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.util.math.random.Random
import opekope2.avm_staff.util.encompassPositions
import opekope2.avm_staff.util.minus
import opekope2.avm_staff.util.plus
import opekope2.avm_staff.util.times
import java.util.*
import kotlin.math.abs

/**
 * A [BlockDestructionPredicate], which only allow breaking blocks in the Diamond Block Staff's shape.
 *
 * @param origin        The starting point of the destruction
 * @param forwardVector Vector pointing "forward" relative to the block destroyer's POV
 * @param upVector      Vector pointing "upward" relative to the block destroyer's POV
 * @param rng           Random number generator deciding the pattern of destruction
 */
class DiamondBlockStaffShapePredicate(origin: BlockPos, forwardVector: Vec3i, upVector: Vec3i, rng: Random) :
    BlockDestructionPredicate {
    private val rightVector: Vec3i = forwardVector.crossProduct(upVector)

    private val farBottomLeft = origin + forwardVector * 8 + upVector * -1 + rightVector * -4
    private val nearTopRight = origin + upVector * 9 + rightVector * 4
    private val nearerTopRight = nearTopRight - forwardVector * OUTER_LAYER_BLOCK_KEEP_CHANCES.size

    private val nonDestroyablePositions: Set<BlockPos>

    /**
     * The bounding volume of the destroyable blocks.
     */
    val volume = encompassPositions(farBottomLeft, nearTopRight)!!

    init {
        nonDestroyablePositions = mutableSetOf()

        val checkedPositions = mutableSetOf<BlockPos>()
        val remainingPositions: Queue<BlockPos> = LinkedList()
        remainingPositions.offer(volume.center)

        while (remainingPositions.isNotEmpty()) {
            val pos = remainingPositions.remove()
            if (pos in nonDestroyablePositions) continue
            if (pos in checkedPositions) continue
            if (pos !in volume) continue

            val distance = pos.distanceFromNearestFace
            if (distance < OUTER_LAYER_BLOCK_KEEP_CHANCES.size) {
                when (tryFindParent(pos)) {
                    null -> {
                        val neighbors = iterateNeighbors(pos)
                        if (neighbors.filter { it in checkedPositions }.all { it in nonDestroyablePositions }) {
                            nonDestroyablePositions += pos
                        }
                    }

                    in nonDestroyablePositions -> nonDestroyablePositions += pos
                }
                if (rng.nextFloat() < OUTER_LAYER_BLOCK_KEEP_CHANCES[distance]) {
                    nonDestroyablePositions += pos
                }
            }

            checkedPositions += pos

            for (d in Direction.entries) {
                remainingPositions.offer(pos.offset(d))
            }
        }

        nonDestroyablePositions -= origin
    }

    override fun test(world: ServerWorld, pos: BlockPos) = pos in volume && pos !in nonDestroyablePositions

    private fun tryFindParent(blockPos: BlockPos): BlockPos? {
        val pos = blockPos.mutableCopy()
        val distance = pos.distanceFromNearestFace

        for (dir in Direction.entries) {
            pos.move(dir)
            if (pos.distanceFromNearestFace > distance && pos in volume) return pos
            pos.move(dir.opposite)
        }

        return null
    }

    private fun iterateNeighbors(blockPos: BlockPos) = sequence {
        val pos = blockPos.mutableCopy()
        val distance = pos.distanceFromNearestFace

        for (dir in Direction.entries) {
            pos.move(dir)
            if (pos.distanceFromNearestFace == distance && pos in volume) yield(pos)
            pos.move(dir.opposite)
        }
    }

    private val Vec3i.distanceFromNearestFace: Int
        get() {
            val d1 = farBottomLeft - this
            val d2 = nearerTopRight - this
            return minOf(abs(d1.x), abs(d1.y), abs(d1.z), abs(d2.x), abs(d2.y), abs(d2.z))
        }

    private companion object {
        val OUTER_LAYER_BLOCK_KEEP_CHANCES = arrayOf(0.6f, 0.05f)
    }
}
