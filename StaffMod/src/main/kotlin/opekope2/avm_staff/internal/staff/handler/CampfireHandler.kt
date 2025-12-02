/*
 * AvM Staff Mod
 * Copyright (c) 2024-2025 opekope2
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

package opekope2.avm_staff.internal.staff.handler

import dev.architectury.event.EventResult
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.SimpleParticleType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import opekope2.avm_staff.api.entity.CampfireFlameEntity
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.SoundEvents
import opekope2.avm_staff.util.*

internal class CampfireHandler(private val parameters: Parameters) : StaffHandler() {
    override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity) = 72000

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (!user.canUseStaff(RaycastContext.FluidHandling.ANY)) return

        val forward = user.rotationVector
        val origin = user.approximateStaffTipPosition
        val relativeRight = user.getRotationVector(0f, MathHelper.wrapDegrees(user.yaw + 90f)).normalize()
        val relativeUp = relativeRight.crossProduct(forward).normalize()
        val applyThrust = !user.isOnGround && !user.isFallFlying && (user !is PlayerEntity || !user.abilities.flying)

        if (applyThrust) {
            user.addVelocity(forward * -calculateThrust(user))
            user.limitFallDistance()
        }

        world.playSound(
            null,
            user.x, user.y, user.z,
            SoundEvents.flamethrowerFire, user.soundCategory,
            1f, 0f
        )

        if (world.isClient) return

        world.spawnEntity(
            CampfireFlameEntity(
                world,
                CampfireFlameEntity.ServerParameters(
                    origin, // Added after the player -> ticked after the player -> starts to backfire over ~42.5b/s
                    forward * FLAMETHROWER_STEP_RESOLUTION.toDouble(),
                    relativeRight * FLAMETHROWER_CONE_END_WIDTH,
                    relativeUp * FLAMETHROWER_CONE_END_HEIGHT,
                    16,
                    parameters.particle,
                    FLAMETHROWER_CONE_RAY_RESOLUTION,
                    parameters.flammableBlockFireChance,
                    parameters.nonFlammableBlockFireChance,
                    parameters.fireSeconds,
                    parameters.fireDamage,
                ),
                user
            )
        )

        staffStack.damage(1, user)
    }

    private fun calculateThrust(user: LivingEntity) = when {
        !user.isSneaking -> parameters.rocketThrust
        user.velocity.y > 0.0 -> 0.0
        user.velocity.y < -parameters.rocketThrust -> parameters.rocketThrust
        else -> {
            var thrust = user.finalGravity / MathHelper.cos(parameters.rocketThrust.toFloat())
            thrust = thrust.coerceAtMost(parameters.rocketThrust)
            thrust
        }
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        (user as? ServerPlayerEntity)?.incrementItemUseStat(staffStack.item)
        (user as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
    }

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        onStoppedUsing(staffStack, world, user, 0)
        return staffStack
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        target.setOnFireFor(parameters.fireSeconds)

        (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)

        return EventResult.pass()
    }

    override fun allowComponentsUpdateAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        player: PlayerEntity,
        hand: Hand
    ) = false

    override fun allowReequipAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        selectedSlotChanged: Boolean
    ) = selectedSlotChanged

    data class Parameters(
        val flammableBlockFireChance: Double,
        val nonFlammableBlockFireChance: Double,
        val fireSeconds: Float,
        val fireDamage: Float,
        val rocketThrust: Double,
        val particle: SimpleParticleType
    )

    private companion object {
        private const val FLAMETHROWER_STEP_RESOLUTION = 16
        private const val FLAMETHROWER_CONE_END_WIDTH = 0.25 * FLAMETHROWER_STEP_RESOLUTION
        private const val FLAMETHROWER_CONE_END_HEIGHT = 0.25 * FLAMETHROWER_STEP_RESOLUTION
        private const val FLAMETHROWER_CONE_RAY_RESOLUTION = 16
    }
}
