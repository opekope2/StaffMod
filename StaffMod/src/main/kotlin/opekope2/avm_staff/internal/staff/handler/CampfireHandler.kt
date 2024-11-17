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

package opekope2.avm_staff.internal.staff.handler

import dev.architectury.event.EventResult
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import opekope2.avm_staff.api.entity.CampfireFlameEntity
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.ComponentTypes
import opekope2.avm_staff.internal.minecraftUnit
import opekope2.avm_staff.util.approximateStaffTipPosition
import opekope2.avm_staff.util.canUseStaff
import opekope2.avm_staff.util.times

internal class CampfireHandler(private val parameters: Parameters) : StaffHandler() {
    override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity) = 72000

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (user.isSneaking && !user.isOnGround) {
            staffStack[ComponentTypes.rocketMode] = minecraftUnit
        }

        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (!user.canUseStaff) return

        val forward = user.rotationVector
        val origin = user.approximateStaffTipPosition
        val relativeRight = user.getRotationVector(0f, MathHelper.wrapDegrees(user.yaw + 90f)).normalize()
        val relativeUp = relativeRight.crossProduct(forward).normalize()
        val rocketMode = ComponentTypes.rocketMode in staffStack

        if (rocketMode) {
            user.addVelocity(forward * -parameters.rocketThrust)
            user.limitFallDistance()
        }

        if (world.isClient) return

        world.spawnEntity(
            CampfireFlameEntity(
                world,
                CampfireFlameEntity.ServerParameters(
                    origin,
                    forward * FLAMETHROWER_STEP_RESOLUTION.toDouble(),
                    relativeRight * FLAMETHROWER_CONE_END_WIDTH,
                    relativeUp * FLAMETHROWER_CONE_END_HEIGHT,
                    16,
                    parameters.particleEffectSupplier.key,
                    FLAMETHROWER_CONE_RAY_RESOLUTION,
                    parameters.flammableBlockFireChance,
                    parameters.nonFlammableBlockFireChance,
                    parameters.flameFireTicks,
                    !rocketMode
                ),
                user
            )
        )
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        staffStack.remove(ComponentTypes.rocketMode)
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
        target.setOnFireFor(parameters.attackFireSeconds)
        return EventResult.pass()
    }

    data class Parameters(
        val flammableBlockFireChance: Double,
        val nonFlammableBlockFireChance: Double,
        val attackFireSeconds: Float,
        val flameFireTicks: Int,
        val rocketThrust: Double,
        val particleEffectSupplier: RegistrySupplier<SimpleParticleType>
    )

    private companion object {
        private const val FLAMETHROWER_STEP_RESOLUTION = 16
        private const val FLAMETHROWER_CONE_END_WIDTH = 0.25 * FLAMETHROWER_STEP_RESOLUTION
        private const val FLAMETHROWER_CONE_END_HEIGHT = 0.25 * FLAMETHROWER_STEP_RESOLUTION
        private const val FLAMETHROWER_CONE_RAY_RESOLUTION = 16
    }
}
