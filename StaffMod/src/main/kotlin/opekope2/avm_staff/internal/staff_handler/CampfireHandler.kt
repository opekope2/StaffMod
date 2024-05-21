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

package opekope2.avm_staff.internal.staff_handler

import dev.architectury.event.events.common.TickEvent
import dev.architectury.registry.registries.RegistrySupplier
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.GraphicsMode
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.item.ItemStack
import net.minecraft.particle.DefaultParticleType
import net.minecraft.server.MinecraftServer
import net.minecraft.state.property.Properties.LIT
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.rocketModeComponentType
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*

class CampfireHandler(
    private val particleEffectSupplier: RegistrySupplier<DefaultParticleType>,
    private val properties: Properties
) : StaffHandler() {
    override val maxUseTime: Int
        get() = 72000

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (user.isSneaking && !user.isOnGround) {
            staffStack[rocketModeComponentType.get()] = UnitComponent
        }

        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (!user.canUseStaff) return

        val forward = user.rotationVector
        val origin = user.approximateStaffTipPosition
        val target = origin + forward * FLAME_MAX_DISTANCE
        val relativeRight = user.getRotationVector(0f, MathHelper.wrapDegrees(user.yaw + 90f)).normalize()
        val relativeUp = relativeRight.crossProduct(forward).normalize()

        if (world.isClient) {
            throwFlameParticles(user, target, relativeRight, relativeUp)
            return
        }

        for (i in 0 until FLAMETHROWER_CONE_RAYS) {
            for (j in 0 until FLAMETHROWER_CONE_RAYS) {
                val xScale = i / (FLAMETHROWER_CONE_RAYS - 1.0) - 0.5
                val yScale = j / (FLAMETHROWER_CONE_RAYS - 1.0) - 0.5

                val offsetTarget = target +
                        relativeRight * (xScale * FLAMETHROWER_CONE_END_WIDTH) +
                        relativeUp * (yScale * FLAMETHROWER_CONE_END_HEIGHT)

                shootFire(
                    FirePellet(
                        user,
                        world,
                        origin,
                        (offsetTarget - origin).normalize() * FLAME_SPEED,
                        FLAME_MAX_AGE
                    )
                )
            }
        }

        if (rocketModeComponentType.get() in staffStack) {
            user.addVelocity(forward * -properties.rocketThrust)
            user.velocityModified = true
            if (forward.y < 0.0) {
                // If the user is looking directly upwards, then the Y velocity becomes -0.078 from a much lower value
                // a game tick before, for some reason. Force looking somewhat down to dampen fall damage, so the player
                // can't exploit this.
                user.limitFallDistance()
            }
        }
    }

    @Environment(EnvType.CLIENT)
    fun throwFlameParticles(user: LivingEntity, target: Vec3d, relativeRight: Vec3d, relativeUp: Vec3d) {
        val random = Random.create()
        val particleManager = MinecraftClient.getInstance().particleManager

        val origin = user.approximateStaffTipPosition

        for (i in 0..flameParticleCount) {
            val xScale = random.nextDouble() - 0.5
            val yScale = random.nextDouble() - 0.5

            val offsetTarget = target +
                    relativeRight * (xScale * FLAMETHROWER_CONE_END_WIDTH) +
                    relativeUp * (yScale * FLAMETHROWER_CONE_END_HEIGHT)
            val targetDirection = (offsetTarget - origin).normalize()
            val particleSpeed = targetDirection.normalize() * FLAME_SPEED * (0.9 + Math.random() * 0.2)

            particleManager.addParticle(
                particleEffectSupplier.get(),
                origin.x,
                origin.y,
                origin.z,
                particleSpeed.x,
                particleSpeed.y,
                particleSpeed.z
            )!!.maxAge = (0.25 * FLAME_MAX_AGE / (Math.random() * 0.8 + 0.2) - 0.05 * FLAME_MAX_AGE).toInt()
        }
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        staffStack.remove(rocketModeComponentType.get())
    }

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        onStoppedUsing(staffStack, world, user, 0)
        return staffStack
    }

    data class Properties(
        val nonFlammableBlockFireChance: Double,
        val flammableBlockFireChance: Double,
        val fireTicks: Int,
        val rocketThrust: Double
    )

    private inner class FirePellet(
        private val shooter: LivingEntity,
        private val world: World,
        private var position: Vec3d,
        private val velocity: Vec3d,
        private val maxAge: Int
    ) {
        private var age = 0

        fun tick(attackedEntities: MutableSet<Entity>): Boolean {
            val newPosition = position + velocity
            val blockHit = world.raycast(
                RaycastContext(
                    position,
                    newPosition,
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.ANY,
                    ShapeContext.absent()
                )
            )
            val entityHit = ProjectileUtil.raycast(
                shooter,
                position,
                newPosition,
                Box(position, newPosition),
                { true },
                velocity.lengthSquared()
            )

            val blockDistance = blockHit.pos.squaredDistanceTo(position)
            val entityDistance = entityHit?.pos?.squaredDistanceTo(position)
            if (entityHit != null && entityDistance!! < blockDistance) {
                tryBurnEntity(entityHit.entity, attackedEntities)
            } else if (blockHit.type == HitResult.Type.BLOCK) {
                tryCauseFire(blockHit)
                return false
            }

            position = newPosition
            return ++age < maxAge
        }

        private fun tryBurnEntity(target: Entity, ignoredEntities: MutableSet<Entity>) {
            if (target in ignoredEntities) return

            if (target.isOnFire) {
                // Technically inFire, but use onFire, because it has a more fitting death message
                target.damage(target.damageSources.onFire(), properties.fireTicks.toFloat())
            }
            target.fireTicks = target.fireTicks.coerceAtLeast(0) + properties.fireTicks + 1
            (target as? LivingEntity)?.attacker = shooter
            ignoredEntities += target
        }

        private fun tryCauseFire(blockHit: BlockHitResult) {
            val firePos = blockHit.blockPos.offset(blockHit.side)
            val blockToLight = world.getBlockState(blockHit.blockPos)
            if (CampfireBlock.canBeLit(blockToLight) ||
                CandleBlock.canBeLit(blockToLight) ||
                CandleCakeBlock.canBeLit(blockToLight)
            ) {
                world.setBlockState(blockHit.blockPos, blockToLight.with(LIT, true), Block.NOTIFY_ALL_AND_REDRAW)
                world.emitGameEvent(shooter, GameEvent.BLOCK_CHANGE, firePos)
                return
            }

            if (!world.canSetBlock(firePos)) return
            if (!AbstractFireBlock.canPlaceAt(world, firePos, shooter.horizontalFacing)) return

            var fireCauseChance =
                if (world.getBlockState(blockHit.blockPos).isBurnable) properties.flammableBlockFireChance
                else properties.nonFlammableBlockFireChance
            fireCauseChance /= FLAMETHROWER_CONE_RAYS_TOTAL
            if (Math.random() >= fireCauseChance) return

            world.setBlockState(firePos, AbstractFireBlock.getState(world, firePos), Block.NOTIFY_ALL_AND_REDRAW)
            world.emitGameEvent(shooter, GameEvent.BLOCK_PLACE, firePos)
        }
    }

    private companion object : TickEvent.Server {
        private const val FLAME_SPEED = 1.0
        private const val FLAME_MAX_AGE = 16
        private const val FLAME_MAX_DISTANCE = FLAME_SPEED * FLAME_MAX_AGE

        private const val FLAMETHROWER_CONE_END_WIDTH = 0.25 * FLAME_MAX_DISTANCE
        private const val FLAMETHROWER_CONE_END_HEIGHT = 0.25 * FLAME_MAX_DISTANCE
        private const val FLAMETHROWER_CONE_RAYS = 16
        private const val FLAMETHROWER_CONE_RAYS_TOTAL = FLAMETHROWER_CONE_RAYS * FLAMETHROWER_CONE_RAYS

        private val flameParticleCount: Int
            @Environment(EnvType.CLIENT)
            get() = when (MinecraftClient.getInstance().options.graphicsMode.value!!) {
                GraphicsMode.FAST -> 4 * 4
                GraphicsMode.FANCY -> 8 * 8
                GraphicsMode.FABULOUS -> 16 * 16
            }
        private val firePellets = mutableListOf<FirePellet>()

        init {
            TickEvent.SERVER_PRE.register(this)
        }

        private fun shootFire(firePellet: FirePellet) {
            firePellets += firePellet
        }

        override fun tick(server: MinecraftServer) {
            val damagedEntities = mutableSetOf<Entity>()
            val iterator = firePellets.iterator()
            while (iterator.hasNext()) {
                val pellet = iterator.next()
                if (!pellet.tick(damagedEntities)) {
                    iterator.remove()
                }
            }
        }
    }
}
