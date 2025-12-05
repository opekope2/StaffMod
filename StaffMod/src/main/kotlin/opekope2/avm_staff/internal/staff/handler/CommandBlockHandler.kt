/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.DataComponentTypes
import opekope2.avm_staff.internal.staff.command.NoOpCommand

internal class CommandBlockHandler : StaffHandler() {
    override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity) =
        getCommand(staffStack).getMaxUseTime(staffStack, world, user)

    override fun use(staffStack: ItemStack, world: World, user: LivingEntity, hand: Hand) =
        getCommand(staffStack).use(staffStack, world, user, hand)

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) =
        getCommand(staffStack).usageTick(staffStack, world, user, remainingUseTicks)

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) =
        getCommand(staffStack).onStoppedUsing(staffStack, world, user, remainingUseTicks)

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity) =
        getCommand(staffStack).finishUsing(staffStack, world, user)

    override fun useOnBlock(
        staffStack: ItemStack, world: World, user: LivingEntity, target: BlockPos, side: Direction, hand: Hand
    ) = getCommand(staffStack).useOnBlock(staffStack, world, user, target, side, hand)

    override fun useOnEntity(
        staffStack: ItemStack, world: World, user: LivingEntity, target: LivingEntity, hand: Hand
    ) = getCommand(staffStack).useOnEntity(staffStack, world, user, target, hand)

    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) =
        getCommand(staffStack).attack(staffStack, world, attacker, hand)

    override fun attackBlock(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: BlockPos, side: Direction, hand: Hand
    ) = getCommand(staffStack).attackBlock(staffStack, world, attacker, target, side, hand)

    override fun attackEntity(staffStack: ItemStack, world: World, attacker: LivingEntity, target: Entity, hand: Hand) =
        getCommand(staffStack).attackEntity(staffStack, world, attacker, target, hand)

    override fun openMenu(staffStack: ItemStack, world: World, user: PlayerEntity, hand: Hand) {
        // TODO
        super.openMenu(staffStack, world, user, hand)
    }

    private fun getCommand(staffStack: ItemStack) =
        staffStack.getOrDefault(DataComponentTypes.STAFF_COMMAND.get(), NoOpCommand.component).command.value()
}
