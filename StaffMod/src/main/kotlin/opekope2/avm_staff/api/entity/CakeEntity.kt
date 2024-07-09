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

package opekope2.avm_staff.api.entity

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.GraphicsMode
import net.minecraft.client.particle.BlockDustParticle
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import opekope2.avm_staff.api.cakeDamageType
import opekope2.avm_staff.api.cakeEntityType
import opekope2.avm_staff.api.cakeSplashSoundEvent
import opekope2.avm_staff.api.playerCakeDamageType
import opekope2.avm_staff.util.damageSource
import opekope2.avm_staff.util.times

/**
 * A flying cake entity, which splashes on collision damaging target(s).
 */
class CakeEntity(entityType: EntityType<CakeEntity>, world: World) : Entity(entityType, world) {
    private var thrower: LivingEntity? = null
    private var timeFalling = 0

    constructor(world: World, position: Vec3d, velocity: Vec3d, thrower: LivingEntity?) :
            this(cakeEntityType.get(), world) {
        intersectionChecked = true
        setPosition(position)
        this.velocity = velocity
        prevX = position.x
        prevY = position.y
        prevZ = position.z
        startPos = blockPos
        this.thrower = thrower
    }

    override fun handleAttack(attacker: Entity?): Boolean {
        if (!world.isClient) {
            discard()
        }
        return true
    }

    /**
     * The position, where the cake was spawned.
     */
    var startPos: BlockPos
        get() = dataTracker[BLOCK_POS]
        set(pos) {
            dataTracker[BLOCK_POS] = pos
        }

    override fun getMoveEffect(): MoveEffect {
        return MoveEffect.NONE
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
        builder.add(BLOCK_POS, BlockPos.ORIGIN)
    }

    override fun onRemoved() {
        world.playSound(
            x, y, z,
            cakeSplashSoundEvent.get(), SoundCategory.BLOCKS,
            (CAKE_STATE.soundGroup.volume + 1f) / 2f, CAKE_STATE.soundGroup.pitch * .8f,
            false
        )
        addCakeSplashParticles()
    }

    @Environment(EnvType.CLIENT)
    private fun addCakeSplashParticles() {
        val rng = Random.create(CAKE_STATE.getRenderingSeed(startPos))
        val particlePerSide = particlePerSide - 1
        val width = type.dimensions.width
        val height = type.dimensions.height

        for (i in 0..particlePerSide) {
            for (j in 0..particlePerSide) {
                for (k in 0..particlePerSide) {
                    val offsetX = i * width / particlePerSide - width / 2
                    val offsetY = j * height / particlePerSide
                    val offsetZ = k * width / particlePerSide - width / 2
                    val velocityScale = rng.nextDouble() * .25 + .25
                    val velocityX = offsetX * velocityScale
                    val velocityY = (offsetY - height / 2) * velocityScale
                    val velocityZ = offsetZ * velocityScale

                    particleManager.addParticle(
                        BlockDustParticle(
                            world as ClientWorld,
                            x + offsetX, y + offsetY, z + offsetZ,
                            velocityX, velocityY, velocityZ,
                            CAKE_STATE, blockPos
                        ).apply {
                            setVelocity(velocityX, velocityY, velocityZ)
                        }
                    )
                }
            }
        }
    }

    override fun canHit() = !isRemoved

    override fun getGravity() = 0.04

    override fun tick() {
        ++timeFalling
        applyGravity()
        move(MovementType.SELF, velocity)
        if (!world.isClient) {
            if (timeFalling > 100 && blockPos.y !in world.topY downTo (world.bottomY + 1) || timeFalling > 600) {
                discard()
            } else {
                splashOnImpact()
            }
        }
        velocity *= 0.98
    }

    private fun splashOnImpact() {
        if (horizontalCollision || verticalCollision) {
            damageCollidingEntities()
            discard()
            return
        }

        val colliders = EntityPredicates.EXCEPT_SPECTATOR
            .and(EntityPredicates.VALID_ENTITY)
            .and { it !is CakeEntity }
        val collisions = world.getOtherEntities(this, boundingBox, colliders)

        if (collisions.isNotEmpty()) {
            damageCollidingEntities()
            discard()
        }
    }

    private fun damageCollidingEntities() {
        val damageables = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.and(EntityPredicates.VALID_LIVING_ENTITY)
        val thrower = thrower
        val damageSource =
            if (thrower == null) world.damageSource(cakeDamageType)
            else world.damageSource(playerCakeDamageType, this, thrower)

        world.getOtherEntities(this, boundingBox, damageables).forEach {
            it.damage(damageSource, 1f)
        }
    }

    override fun handleFallDamage(fallDistance: Float, damageMultiplier: Float, damageSource: DamageSource) = false

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        nbt.putInt("Time", timeFalling)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        timeFalling = nbt.getInt("Time")
    }

    override fun doesRenderOnFire() = false

    override fun entityDataRequiresOperator() = true

    override fun createSpawnPacket() = EntitySpawnS2CPacket(this)

    override fun onSpawnPacket(packet: EntitySpawnS2CPacket) {
        super.onSpawnPacket(packet)
        intersectionChecked = true
        setPosition(packet.x, packet.y, packet.z)
        startPos = blockPos
    }

    private companion object {
        private val CAKE_STATE = Blocks.CAKE.defaultState
        private val BLOCK_POS = DataTracker.registerData(
            CakeEntity::class.java, TrackedDataHandlerRegistry.BLOCK_POS
        )
        private val particleManager by lazy { MinecraftClient.getInstance().particleManager }
        private val graphicsModeOption by lazy { MinecraftClient.getInstance().options.graphicsMode }
        private val particlePerSide: Int
            @Environment(EnvType.CLIENT)
            get() = when (graphicsModeOption.value!!) {
                GraphicsMode.FAST -> 4
                GraphicsMode.FANCY -> 5
                GraphicsMode.FABULOUS -> 6
            }
    }
}
