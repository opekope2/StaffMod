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
 * A [BlockDestructionPredicate], which only allow breaking blocks in the Gold Block Staff's shape.
 *
 * @param origin        The starting point of the destruction
 * @param forwardVector Vector pointing "forward" relative to the block destroyer's POV
 * @param upVector      Vector pointing "upward" relative to the block destroyer's POV
 */
class GoldBlockStaffShapePredicate(private val origin: BlockPos, forwardVector: Vec3i, upVector: Vec3i) :
    BlockDestructionPredicate {
    private val rightVector = forwardVector.crossProduct(upVector)

    /**
     * The bounding volume of the destroyable blocks.
     */
    val volume = encompassPositions(
        origin + forwardVector + upVector + rightVector * -1,
        origin + upVector * -1 + rightVector
    )!!

    override fun test(world: ServerWorld, pos: BlockPos) = pos.isWithinDistance(origin, 1.5)
}
