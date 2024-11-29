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

import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.util.approximateStaffTipPosition
import opekope2.avm_staff.util.getSpawnPosition
import opekope2.avm_staff.util.plus
import opekope2.avm_staff.util.times

internal class CakeHandler : AbstractProjectileShootingStaffHandler() {
    override fun getFireRateDenominator(rapidFireLevel: Int) = if (rapidFireLevel >= 2) 1 else 2

    private val ProjectileShootReason.velocity: Double
        get() = when (this) {
            ProjectileShootReason.ATTACK -> 0.5
            ProjectileShootReason.USE -> 1.0
        }

    override fun tryShootProjectile(
        staffStack: ItemStack,
        world: World,
        shooter: LivingEntity,
        reason: ProjectileShootReason
    ): Boolean {
        if (!super.tryShootProjectile(staffStack, world, shooter, reason)) return false

        val spawnPos = EntityTypes.cake.getSpawnPosition(world, shooter.approximateStaffTipPosition) ?: return false
        CakeEntity.throwCake(world, spawnPos, shooter.rotationVector * reason.velocity + shooter.velocity, shooter)

        return true
    }
}
