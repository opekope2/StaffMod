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

@file: JvmName("EntityUtil")

package opekope2.avm_staff.util

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Calculates the camera's upward direction based on [Entity.getFacing] and [Entity.getHorizontalFacing].
 */
val Entity.cameraUp: Direction
    get() = when (facing) {
        Direction.DOWN -> horizontalFacing
        Direction.UP -> horizontalFacing.opposite
        else -> Direction.UP
    }

/**
 * Gets the spawn position if the given entity has enough space to spawn at the given position, or `null`, if the
 * position is obstructed by blocks.
 *
 * @param world     The world to spawn the entity in
 * @param center    The center position of the entity
 */
fun EntityType<out Entity>.getSpawnPosition(world: World, center: Vec3d): Vec3d? {
    val spawnPos = center.add(0.0, dimensions.height / -2.0, 0.0)
    return if (world.isSpaceEmpty(getSpawnBox(spawnPos.x, spawnPos.y, spawnPos.z))) spawnPos
    else null
}
