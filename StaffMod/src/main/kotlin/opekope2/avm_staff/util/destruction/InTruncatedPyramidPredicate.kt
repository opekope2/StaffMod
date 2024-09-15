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
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import opekope2.avm_staff.util.*

/**
 * A [BlockDestructionPredicate], which only allows breaking blocks in a truncated pyramid.
 * The near and far sides of the truncated pyramid are the top and bottom sides.
 *
 * @param near      The center point of the near side of the truncated pyramid
 * @param nearSize  The width and height of the near side of the truncated pyramid
 * @param far       The center point on the far side of the truncated pyramid
 * @param farSize   The width and height of the far side if the truncated pyramid
 * @param up        The direction pointing "upwards" (perpendicular to `far-near` vector)
 */
class InTruncatedPyramidPredicate(near: Vec3d, nearSize: Vec2f, far: Vec3d, farSize: Vec2f, up: Vec3d) :
    BlockDestructionPredicate {
    private val forward = far - near
    private val right = forward.crossProduct(up).normalize()

    private val nearTopLeft = near - right * (nearSize.x / 2.0) + up * (nearSize.y / 2.0)
    private val nearTopRight = near + right * (nearSize.x / 2.0) + up * (nearSize.y / 2.0)
    private val nearBottomLeft = near - right * (nearSize.x / 2.0) - up * (nearSize.y / 2.0)
    private val nearBottomRight = near + right * (nearSize.x / 2.0) - up * (nearSize.y / 2.0)
    private val farTopLeft = far - right * (farSize.x / 2.0) + up * (farSize.y / 2.0)
    private val farTopRight = far + right * (farSize.x / 2.0) + up * (farSize.y / 2.0)
    private val farBottomLeft = far - right * (farSize.x / 2.0) - up * (farSize.y / 2.0)
    private val farBottomRight = far + right * (farSize.x / 2.0) - up * (farSize.y / 2.0)

    private val planes = listOf(
        createPlane(nearBottomLeft, nearBottomLeft - nearTopLeft, nearBottomLeft - nearBottomRight), // Near
        createPlane(farTopRight, farTopRight - farTopLeft, farTopRight - farBottomRight), // Far
        createPlane(farTopLeft, farTopLeft - farTopRight, farTopLeft - nearTopLeft), // Top
        createPlane(nearBottomRight, nearBottomRight - farBottomRight, nearBottomRight - nearBottomLeft), // Bottom
        createPlane(nearTopLeft, nearTopLeft - nearBottomLeft, nearTopLeft - farTopLeft), // Left
        createPlane(farBottomRight, farBottomRight - nearBottomRight, farBottomRight - farTopRight) // Right
    )

    val volume = encompassVectors(
        nearTopLeft,
        nearTopRight,
        nearBottomLeft,
        nearBottomRight,
        farTopLeft,
        farTopRight,
        farBottomLeft,
        farBottomRight
    )!!

    override fun test(world: ServerWorld, pos: BlockPos) = planes.all { it.isPointAbove(pos.toCenterPos()) }

    private fun createPlane(point: Vec3d, tangent1: Vec3d, tangent2: Vec3d) =
        Plane(point, tangent1.crossProduct(tangent2).normalize())
}
