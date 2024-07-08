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
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.thrown.SnowballEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*

class SnowBlockHandler : StaffHandler() {
    override val maxUseTime = 72000

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        tryThrowSnowball(world, user)
    }

    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) {
        tryThrowSnowball(world, attacker)
        (attacker as? PlayerEntity)?.resetLastAttackedTicks()
    }

    private fun tryThrowSnowball(world: World, thrower: LivingEntity) {
        if (world.isClient) return
        if (!thrower.canUseStaff) return
        if (thrower is PlayerEntity && thrower.isAttackCoolingDown) return

        val spawnPos = EntityType.SNOWBALL.getSpawnPosition(world, thrower.approximateStaffTipPosition) ?: return
        val (x, y, z) = spawnPos

        world.spawnEntity(SnowballEntity(world, x, y, z).apply {
            owner = thrower
            // TODO speed
            setVelocity(thrower, thrower.pitch, thrower.yaw, 0f, 4f, 1f)
        })
        world.playSound(
            null,
            thrower.blockPos,
            SoundEvents.ENTITY_SNOWBALL_THROW,
            thrower.soundCategory,
            0.5f,
            0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        )
    }
}
