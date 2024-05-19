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

import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.LootEvent
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.crownOfKingOrangeItem
import opekope2.avm_staff.internal.event_handler.addBlockToStaff
import opekope2.avm_staff.internal.event_handler.attack
import opekope2.avm_staff.internal.event_handler.attackBlock
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

private val TREASURE_BASTION_CHEST_LOOT = Identifier("chests/bastion_treasure")

fun subscribeToEvents() {
    InteractionEvent.LEFT_CLICK_BLOCK.register(::attackBlock)
    LootEvent.MODIFY_LOOT_TABLE.register(LootEvent.ModifyLootTable { _, lootTableId, context, builtin ->
        if (builtin && lootTableId == TREASURE_BASTION_CHEST_LOOT) {
            context.addPool(LootPool.builder().with(ItemEntry.builder(crownOfKingOrangeItem.get())))
        }
    })
}
