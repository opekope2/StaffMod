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
import net.minecraft.util.math.Vec3i
import opekope2.avm_staff.util.encompassPositions
import opekope2.avm_staff.util.plus
import opekope2.avm_staff.util.times

/**
 * A [BlockDestructionPredicate], which only allow breaking blocks in the Netherite Block Staff's shape.
 *
 * @param origin        The starting point of the destruction
 * @param forwardVector Vector pointing "forward" relative to the block destroyer's POV
 * @param upVector      Vector pointing "upward" relative to the block destroyer's POV
 */
class NetheriteBlockStaffShapePredicate(origin: BlockPos, forwardVector: Vec3i, upVector: Vec3i) :
    BlockDestructionPredicate {
    private val rightVector = forwardVector.crossProduct(upVector)

    private val farBottomLeft = origin + forwardVector * 10 + upVector * -3 + rightVector * -6
    private val furtherBottomLeft = origin + forwardVector * 11 + upVector * -3 + rightVector * -6
    private val nearTopRight = origin + upVector * 9 + rightVector * 6

    /**
     * The bounding volume of the destroyable blocks.
     */
    val volume = encompassPositions(furtherBottomLeft, nearTopRight)!!

    override fun test(world: ServerWorld, pos: BlockPos): Boolean {
        if (pos !in volume) return false

        val nearCoordMatch = getMatchingCoordinates(nearTopRight, pos)
        val farCoordMatch = getMatchingCoordinates(farBottomLeft, pos)
        val furtherCoordMatch = getMatchingCoordinates(furtherBottomLeft, pos)

        return nearCoordMatch + farCoordMatch < 2 && nearCoordMatch + furtherCoordMatch < 2
    }

    private fun getMatchingCoordinates(a: Vec3i, b: Vec3i): Int {
        var match = 0

        if (a.x == b.x) match++
        if (a.y == b.y) match++
        if (a.z == b.z) match++

        return match
    }
}
