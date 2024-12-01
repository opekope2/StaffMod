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

package opekope2.avm_staff.internal.initializer

import opekope2.avm_staff.content.*
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.InsertItemIntoStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.networking.s2c.play.MassDestructionS2CPacket

object Initializer {
    init {
        registerContent()
        initializeNetworking()
    }

    private fun registerContent() {
        Blocks.register()
        Criteria.register()
        DamageTypes
        DataComponentTypes.register()
        Enchantments
        Enchantments.Tags
        EntityTypes.register()
        GameRules
        ItemGroups.register()
        Items.register()
        Items.Tags
        ParticleTypes.register()
        SoundEvents.register()
        StatTypes.register()
    }

    private fun initializeNetworking() {
        AttackC2SPacket.registerReceiver()
        InsertItemIntoStaffC2SPacket.registerReceiver()
        RemoveItemFromStaffC2SPacket.registerReceiver()

        MassDestructionS2CPacket.registerReceiver()
    }
}
