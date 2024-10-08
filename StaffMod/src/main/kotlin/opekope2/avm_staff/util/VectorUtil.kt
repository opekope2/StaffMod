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

@file: JvmSynthetic
@file: Suppress("NOTHING_TO_INLINE")

package opekope2.avm_staff.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import org.joml.Vector3f
import org.joml.Vector3fc

inline operator fun Vec2f.component1(): Float = x

inline operator fun Vec2f.component2(): Float = y

inline operator fun Vec2f.plus(other: Vec2f): Vec2f = add(other)

inline operator fun Vec2f.minus(other: Vec2f): Vec2f = add(-other)

inline operator fun Vec2f.unaryMinus(): Vec2f = negate()

inline operator fun Vec2f.times(scalar: Float): Vec2f = multiply(scalar)

inline operator fun Vec3i.component1(): Int = x

inline operator fun Vec3i.component2(): Int = y

inline operator fun Vec3i.component3(): Int = z

inline operator fun Vec3i.plus(other: Vec3i): Vec3i = add(other)

inline operator fun Vec3i.minus(other: Vec3i): Vec3i = subtract(other)

inline operator fun Vec3i.unaryMinus(): Vec3i = multiply(-1)

inline operator fun Vec3i.times(scalar: Int): Vec3i = multiply(scalar)

inline operator fun Vec3d.component1(): Double = x

inline operator fun Vec3d.component2(): Double = y

inline operator fun Vec3d.component3(): Double = z

inline operator fun Vec3d.plus(other: Vec3d): Vec3d = add(other)

inline operator fun Vec3d.minus(other: Vec3d): Vec3d = subtract(other)

inline operator fun Vec3d.unaryMinus(): Vec3d = negate()

inline operator fun Vec3d.times(scalar: Double): Vec3d = multiply(scalar)

inline operator fun Vector3f.component1(): Float = x

inline operator fun Vector3f.component2(): Float = y

inline operator fun Vector3f.component3(): Float = z

inline operator fun Vector3f.plusAssign(other: Vector3fc) {
    add(other)
}

inline operator fun Vector3f.minusAssign(other: Vector3fc) {
    sub(other)
}

inline operator fun Vector3f.timesAssign(scalar: Float) {
    mul(scalar)
}

inline operator fun BlockPos.plus(other: Vec3i): BlockPos = add(other)

inline operator fun BlockPos.minus(other: Vec3i): BlockPos = subtract(other)

inline operator fun BlockPos.unaryMinus(): BlockPos = multiply(-1)

inline operator fun BlockPos.times(scalar: Int): BlockPos = multiply(scalar)
