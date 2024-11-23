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
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.util.*

internal class MagmaBlockHandler : AbstractProjectileShootingStaffHandler() {
    override val attributeModifiers = StaffAttributeModifiersComponentBuilder()
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(10.0), AttributeModifierSlot.MAINHAND)
        .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(1.25), AttributeModifierSlot.MAINHAND)
        .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
        .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
        .build()

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if ((remainingUseTicks and 1) == 0) super.usageTick(staffStack, world, user, remainingUseTicks)
    }

    override fun tryShootProjectile(world: World, shooter: LivingEntity, reason: ProjectileShootReason): Boolean {
        if (!super.tryShootProjectile(world, shooter, reason)) return false

        val spawnPos = EntityType.SMALL_FIREBALL.getSpawnPosition(world, shooter.approximateStaffTipPosition)
            ?: return false

        world.spawnEntity(SmallFireballEntity(world, shooter, shooter.rotationVector).apply {
            owner = shooter
            setPosition(spawnPos)
        })
        world.syncWorldEvent(WorldEvents.BLAZE_SHOOTS, shooter.blockPos, 0)

        return true
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        target.setOnFireFor(8f)

        (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)

        return EventResult.pass()
    }
}
