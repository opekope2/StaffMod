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
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.WitherSkullEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.world.Difficulty
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.util.approximateStaffTipPosition
import opekope2.avm_staff.util.getSpawnPosition
import opekope2.avm_staff.util.incrementStaffItemUseStat
import opekope2.avm_staff.util.itemInStaff

internal class WitherSkeletonSkullHandler : AbstractProjectileShootingStaffHandler() {
    override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity) = 20

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if ((remainingUseTicks and 1) == 0) super.usageTick(staffStack, world, user, remainingUseTicks)
    }

    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) {
        if (attacker is PlayerEntity && attacker.itemCooldownManager.isCoolingDown(staffStack.item)) return
        super.attack(staffStack, world, attacker, hand)
        if (attacker is PlayerEntity) addCooldown(staffStack, world, attacker, 0)
    }

    override fun tryShootProjectile(world: World, shooter: LivingEntity, reason: ProjectileShootReason): Boolean {
        if (!super.tryShootProjectile(world, shooter, reason)) return false

        val spawnPos = EntityType.WITHER_SKULL.getSpawnPosition(world, shooter.approximateStaffTipPosition)
            ?: return false

        world.spawnEntity(WitherSkullEntity(world, shooter, shooter.rotationVector).apply {
            owner = shooter
            isCharged = reason == ProjectileShootReason.ATTACK
            setPosition(spawnPos)
        })
        world.syncWorldEvent(WorldEvents.WITHER_SHOOTS, shooter.blockPos, 0)

        return true
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        if (world.isClient) return EventResult.pass()
        if (target is LivingEntity && !target.isInvulnerableTo(world.damageSources.wither())) {
            val amplifier = if (world.difficulty == Difficulty.HARD) 1 else 0
            target.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, 10 * 20, amplifier))
            (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
        }

        return EventResult.pass()
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (user is PlayerEntity) addCooldown(staffStack, world, user, remainingUseTicks)
    }

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        onStoppedUsing(staffStack, world, user, 0)
        return staffStack
    }

    private fun addCooldown(staffStack: ItemStack, world: World, player: PlayerEntity, remainingUseTicks: Int) {
        if (player.abilities.creativeMode) return
        player.itemCooldownManager.set(
            staffStack.item,
            4 * (getMaxUseTime(staffStack, world, player) - remainingUseTicks)
        )
    }
}
