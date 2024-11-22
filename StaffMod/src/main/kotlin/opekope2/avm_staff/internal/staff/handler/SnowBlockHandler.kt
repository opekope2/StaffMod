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

import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.thrown.SnowballEntity
import net.minecraft.sound.SoundEvents
import net.minecraft.world.World
import opekope2.avm_staff.util.*

internal class SnowBlockHandler : AbstractProjectileShootingStaffHandler() {
    private val ProjectileShootReason.velocity: Float
        get() = when (this) {
            ProjectileShootReason.ATTACK -> 1.5f
            ProjectileShootReason.USE -> 3f
        }

    override fun tryShootProjectile(world: World, shooter: LivingEntity, reason: ProjectileShootReason): Boolean {
        if (!super.tryShootProjectile(world, shooter, reason)) return false

        val spawnPos = EntityType.SNOWBALL.getSpawnPosition(world, shooter.approximateStaffTipPosition) ?: return false
        val (x, y, z) = spawnPos

        world.spawnEntity(SnowballEntity(world, x, y, z).apply {
            owner = shooter
            setVelocity(shooter, shooter.pitch, shooter.yaw, 0f, reason.velocity, 1f)
        })
        world.playSound(
            null,
            shooter.blockPos,
            SoundEvents.ENTITY_SNOWBALL_THROW,
            shooter.soundCategory,
            .5f,
            .4f / (world.random.nextFloat() * .4f + .8f)
        )

        return true
    }
}
