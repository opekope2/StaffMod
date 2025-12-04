/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

@file:JvmName("ParticleUtil")

package opekope2.avm_staff.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.client.particle.BlockDustParticle
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShapes

private const val PARTICLE_FREQUENCY = 0.25

@Environment(EnvType.CLIENT)
fun ParticleManager.addBlockBreakParticles(world: ClientWorld, box: Box, state: BlockState) {
    if (state.isAir || !state.hasBlockBreakParticles()) return

    val pos = BlockPos.ofFloored(box.center)
    val voxelShape = VoxelShapes.cuboid(box)
    voxelShape.forEachBox { minX, minY, minZ, maxX, maxY, maxZ ->
        val dx = maxX - minX
        val dy = maxY - minY
        val dz = maxZ - minZ
        val nParticleX = MathHelper.ceil(dx / PARTICLE_FREQUENCY).coerceAtLeast(2)
        val nParticleY = MathHelper.ceil(dy / PARTICLE_FREQUENCY).coerceAtLeast(2)
        val nParticleZ = MathHelper.ceil(dz / PARTICLE_FREQUENCY).coerceAtLeast(2)
        for (ix in 0..<nParticleX) {
            for (iy in 0..<nParticleY) {
                for (iz in 0..<nParticleZ) {
                    val vx = (ix + 0.5) / nParticleX
                    val vy = (iy + 0.5) / nParticleY
                    val vz = (iz + 0.5) / nParticleZ
                    val x = vx * dx + minX
                    val y = vy * dy + minY
                    val z = vz * dz + minZ
                    addParticle(BlockDustParticle(world, x, y, z, vx - 0.5, vy - 0.5, vz - 0.5, state, pos))
                }
            }
        }
    }
}
