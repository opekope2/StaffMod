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

package opekope2.avm_staff.internal

import opekope2.avm_staff.internal.event_handler.addBlockToStaff
import opekope2.avm_staff.internal.event_handler.attack
import opekope2.avm_staff.internal.event_handler.removeBlockFromStaff
import opekope2.avm_staff.internal.networking.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.StaffAttackC2SPacket

fun registerContent() {
    opekope2.avm_staff.api.registerContent()
}

fun initializeNetworking() {
    AddItemToStaffC2SPacket.registerHandler(::addBlockToStaff)
    RemoveItemFromStaffC2SPacket.registerHandler(::removeBlockFromStaff)
    StaffAttackC2SPacket.registerHandler(::attack)
}
