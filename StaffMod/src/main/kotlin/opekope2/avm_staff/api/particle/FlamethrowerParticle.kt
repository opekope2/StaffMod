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

package opekope2.avm_staff.api.particle

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.math.BlockPos
import opekope2.avm_staff.api.flamethrowerParticleType
import opekope2.avm_staff.api.soulFlamethrowerParticleType
import opekope2.avm_staff.mixin.IParticleMixin

/**
 * Particle emitted by the campfire staff.
 *
 * @param world     The world the particle is in
 * @param x         The X component of the particle's position
 * @param y         The Y component of the particle's position
 * @param y         The Z component of the particle's position
 * @param velocityX The X component of the particle's velocity
 * @param velocityY The Y component of the particle's velocity
 * @param velocityZ The Z component of the particle's velocity
 * @see FlamethrowerParticle.Factory
 */
@Environment(EnvType.CLIENT)
class FlamethrowerParticle(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double
) : AbstractSlowingParticle(world, x, y, z, velocityX, velocityY, velocityZ) {
    private var stopped = 0

    private val isSubmerged: Boolean
        get() {
            val blockPos = BlockPos.ofFloored(x, y, z)
            val fluid = world.getFluidState(blockPos)
            if (fluid.isEmpty) return false

            val fluidShape = fluid.getShape(world, blockPos)

            val rx = x - blockPos.x
            val ry = y - blockPos.y
            val rz = z - blockPos.z
            // Check if source block, because the bounding box is shorter than 1 block, and some particles leak through
            return fluid.isStill || fluidShape.boundingBoxes.any { it.contains(rx, ry, rz) }
        }

    init {
        gravityStrength = 0f
        velocityMultiplier = 1f

        if (isSubmerged) {
            markDead()
        }
    }

    override fun getType(): ParticleTextureSheet = ParticleTextureSheet.PARTICLE_SHEET_OPAQUE

    override fun move(dx: Double, dy: Double, dz: Double) {
        val oldVX = velocityX
        val oldVZ = velocityZ

        super.move(dx, dy, dz)

        if (isSubmerged) {
            markDead()
            return
        }

        @Suppress("CAST_NEVER_SUCCEEDS")
        if ((this as IParticleMixin).isStopped || oldVX != velocityX || oldVZ != velocityZ) {
            velocityX = 0.0
            velocityY = 0.0
            velocityZ = 0.0
            stopped++
        }

        if (stopped == 1) {
            maxAge -= (maxAge - age) / 2
            stopped++
        }
    }

    /**
     * Factory class for [FlamethrowerParticle], intended to register in Minecraft instead of direct consumption.
     *
     * @param spriteProvider    Flame sprite provider
     * @see flamethrowerParticleType
     * @see soulFlamethrowerParticleType
     * @see ParticleManager.addParticle
     */
    class Factory(private val spriteProvider: SpriteProvider) : ParticleFactory<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType,
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double
        ) = FlamethrowerParticle(world, x, y, z, velocityX, velocityY, velocityZ).apply {
            setSprite(spriteProvider)
        }
    }
}
