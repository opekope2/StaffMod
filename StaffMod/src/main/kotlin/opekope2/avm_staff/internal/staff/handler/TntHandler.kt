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
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.entity.ImpactTntEntity
import opekope2.avm_staff.api.impactTntEntityType
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*

class TntHandler : StaffHandler() {
    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) {
        tryShootTnt(world, attacker)
        (attacker as? PlayerEntity)?.resetLastAttackedTicks()
    }

    private fun tryShootTnt(world: World, shooter: LivingEntity) {
        if (world.isClient) return
        if (!shooter.canUseStaff) return
        if (shooter is PlayerEntity && shooter.isAttackCoolingDown) return

        val spawnPos = impactTntEntityType.get().getSpawnPosition(world, shooter.approximateStaffTipPosition) ?: return
        val (x, y, z) = spawnPos

        world.spawnEntity(ImpactTntEntity(world, x, y, z, shooter.rotationVector + shooter.velocity, shooter))
        world.playSound(null, x, y, z, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1f, 1f)
        world.emitGameEvent(shooter, GameEvent.PRIME_FUSE, spawnPos)
    }
}
