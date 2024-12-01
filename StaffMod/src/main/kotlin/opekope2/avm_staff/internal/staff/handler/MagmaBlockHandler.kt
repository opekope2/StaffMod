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

import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.projectile.AbstractFireballEntity
import net.minecraft.entity.projectile.FireballEntity
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.content.Enchantments
import opekope2.avm_staff.util.*

internal class MagmaBlockHandler : AbstractProjectileShootingStaffHandler() {
    override val attributeModifiers = StaffAttributeModifiersComponentBuilder()
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(10.0), AttributeModifierSlot.MAINHAND)
        .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(1.25), AttributeModifierSlot.MAINHAND)
        .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
        .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
        .build()

    override fun getFireRateDenominator(rapidFireLevel: Int) = if (rapidFireLevel >= 2) 2 else 4

    override fun tryShootProjectile(
        staffStack: ItemStack,
        world: World,
        shooter: LivingEntity,
        reason: ProjectileShootReason
    ): Boolean {
        if (!super.tryShootProjectile(staffStack, world, shooter, reason)) return false

        return if (reason.isAttack && staffStack.isEnchantedWith(Enchantments.POWER_CHARGE, world.registryManager)) {
            shootFireball(world, shooter, WorldEvents.GHAST_SHOOTS, EntityType.FIREBALL) {
                FireballEntity(world, shooter, shooter.rotationVector, 1)
            }
        } else {
            shootFireball(world, shooter, WorldEvents.BLAZE_SHOOTS, EntityType.SMALL_FIREBALL) {
                SmallFireballEntity(world, shooter, shooter.rotationVector)
            }
        }
    }

    private inline fun <T : AbstractFireballEntity> shootFireball(
        world: World,
        shooter: LivingEntity,
        soundWorldEvent: Int,
        fireballType: EntityType<T>,
        fireballFactory: () -> T
    ): Boolean {
        val spawnPos = fireballType.getSpawnPosition(world, shooter.approximateStaffTipPosition) ?: return false
        val fireball = fireballFactory()
        fireball.setPosition(spawnPos)
        fireball.owner = shooter

        world.spawnEntity(fireball)
        world.syncWorldEvent(soundWorldEvent, shooter.blockPos, 0)

        return true
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): ActionResult {
        target.setOnFireFor(8f)

        (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)

        return ActionResult.PASS
    }
}
