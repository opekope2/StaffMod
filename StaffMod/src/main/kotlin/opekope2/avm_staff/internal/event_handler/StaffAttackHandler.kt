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

package opekope2.avm_staff.internal.event_handler

import dev.architectury.event.EventResult
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.staffsTag
import opekope2.avm_staff.util.handlerOfItem
import opekope2.avm_staff.util.itemInStaff

fun attackBlock(player: PlayerEntity, hand: Hand, target: BlockPos, direction: Direction): EventResult {
    val staffStack = player.getStackInHand(hand)
    if (!staffStack.isIn(staffsTag)) return EventResult.pass()

    val itemInStaff = staffStack.itemInStaff ?: return EventResult.pass()
    val staffHandler = itemInStaff.handlerOfItem ?: return EventResult.pass()

    val result = staffHandler.attackBlock(staffStack, player.entityWorld, player, target, direction, hand)
    return if (result.isFalse) EventResult.interruptTrue() // Force Fabric to send packet for Neo/Forge parity
    else result
}

fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand): ActionResult {
    if (!staffStack.isIn(staffsTag)) return ActionResult.PASS

    val itemInStaff: ItemStack = staffStack.itemInStaff ?: return ActionResult.PASS
    val staffHandler = itemInStaff.handlerOfItem ?: return ActionResult.PASS

    return staffHandler.attack(staffStack, world, attacker, hand)
}
