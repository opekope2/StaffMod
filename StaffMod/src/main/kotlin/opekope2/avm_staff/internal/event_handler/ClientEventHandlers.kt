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

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

@OnlyIn(Dist.CLIENT)
object ClientEventHandlers {
    init {
        FORGE_BUS.addListener(::click)
    }

    private fun click(event: LeftClickEmpty) {
        val player = event.entity
        val hand = event.hand
        val staffStack = player.getStackInHand(hand)
        val staffItem = staffStack.item as? StaffItem ?: return

        staffItem.attack(staffStack, player.entityWorld, player, hand)
        AttackC2SPacket(hand).sendToServer()
    }
}
