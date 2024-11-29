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
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.Enchantments
import opekope2.avm_staff.util.*

internal abstract class AbstractProjectileShootingStaffHandler : StaffHandler() {
    protected abstract fun getFireRateDenominator(rapidFireLevel: Int): Int

    override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity) = 72000

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        val allowsProjectileRapidFire = staffStack.isEnchantedWith(Enchantments.RAPID_FIRE, world.registryManager)
        if (!allowsProjectileRapidFire) return TypedActionResult.pass(staffStack)

        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        val rapidFire = staffStack.getEnchantmentLevel(Enchantments.RAPID_FIRE, world.registryManager)
        if (remainingUseTicks % getFireRateDenominator(rapidFire) != 0) return
        if (!tryShootProjectile(world, user, ProjectileShootReason.USE)) return

        staffStack.damage(entity = user)
        (user as? ServerPlayerEntity)?.incrementItemUseStat(staffStack.item)
        (user as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
    }

    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) {
        if (tryShootProjectile(world, attacker, ProjectileShootReason.ATTACK)) {
            staffStack.damage(entity = attacker)
            (attacker as? ServerPlayerEntity)?.incrementItemUseStat(staffStack.item)
            (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
        }
        (attacker as? PlayerEntity)?.resetLastAttackedTicks()
    }

    protected open fun tryShootProjectile(world: World, shooter: LivingEntity, reason: ProjectileShootReason): Boolean {
        if (world.isClient) return false
        if (!shooter.canUseStaff) return false
        if (shooter is PlayerEntity && shooter.isAttackCoolingDown) return false

        return true
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

    protected enum class ProjectileShootReason {
        ATTACK,
        USE
    }
}
