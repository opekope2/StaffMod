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

package opekope2.avm_staff.util

import net.minecraft.util.math.Vec3d

/**
 * Represents a 2D plane in a 3D world.
 *
 * @param point     A point on the plane
 * @param normal    The normal vector of the plane pointing "above" the plane
 */
class Plane(point: Vec3d, normal: Vec3d) {
    private val a = normal.x
    private val b = normal.y
    private val c = normal.z
    private val d = a * point.x + b * point.y + c * point.z

    /**
     * Checks if the given point is "above" the plane.
     *
     * @param point The point to check its position relative to the plane
     */
    fun isPointAbove(point: Vec3d) = a * point.x + b * point.y + c * point.z >= d
}
