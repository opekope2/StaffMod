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
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import opekope2.avm_staff.api.staffsTag
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.util.itemInStaff
import opekope2.avm_staff.util.staffHandler

fun attackBlock(player: PlayerEntity, hand: Hand, target: BlockPos, direction: Direction): EventResult {
    val staffStack = player.getStackInHand(hand)
    if (!staffStack.isIn(staffsTag)) return EventResult.pass()

    val itemInStaff = staffStack.itemInStaff ?: return EventResult.pass()
    val staffHandler = itemInStaff.staffHandler ?: return EventResult.pass()

    val result = staffHandler.attackBlock(staffStack, player.entityWorld, player, target, direction, hand)
    return if (result.isFalse) EventResult.interruptTrue() // Force Fabric to send packet for Neo/Forge parity
    else result
}

@Environment(EnvType.CLIENT)
fun clientAttack(player: PlayerEntity, hand: Hand) {
    val staffStack = player.getStackInHand(hand)
    if (!staffStack.isIn(staffsTag)) return

    val itemInStaff = staffStack.itemInStaff ?: return
    val staffHandler = itemInStaff.staffHandler ?: return

    staffHandler.attack(staffStack, player.entityWorld, player, hand)
    AttackC2SPacket(hand).sendToServer()
}
