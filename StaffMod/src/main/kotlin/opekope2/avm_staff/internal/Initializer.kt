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

import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.LootEvent
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.entity.TntEntityRenderer
import net.minecraft.item.SmithingTemplateItem
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.crownOfKingOrangeItem
import opekope2.avm_staff.api.impactTntEntityType
import opekope2.avm_staff.api.staff.StaffInfusionSmithingRecipeTextures
import opekope2.avm_staff.internal.event_handler.*
import opekope2.avm_staff.internal.networking.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.util.MOD_ID

fun registerContent() {
    opekope2.avm_staff.api.registerContent()
}

fun initializeNetworking() {
    AddItemToStaffC2SPacket.registerHandler(::addItemToStaff)
    RemoveItemFromStaffC2SPacket.registerHandler(::removeItemFromStaff)
    AttackC2SPacket.registerHandler(::attack)
}

private val TREASURE_BASTION_CHEST_LOOT = Identifier("chests/bastion_treasure")

fun modifyLootTables(
    lootTable: RegistryKey<LootTable>,
    context: LootEvent.LootTableModificationContext,
    builtin: Boolean
) {
    if (builtin && lootTable.value == TREASURE_BASTION_CHEST_LOOT) {
        context.addPool(LootPool.builder().with(ItemEntry.builder(crownOfKingOrangeItem.get())))
    }
}

fun subscribeToEvents() {
    InteractionEvent.LEFT_CLICK_BLOCK.register(::attackBlock)
    LootEvent.MODIFY_LOOT_TABLE.register(::modifyLootTables)
}

@Environment(EnvType.CLIENT)
fun registerClientContent() {
    KeyMappingRegistry.register(addRemoveStaffItemKeyBinding)
    EntityRendererRegistry.register(impactTntEntityType, ::TntEntityRenderer)
}

@Environment(EnvType.CLIENT)
fun registerSmithingTableTextures() {
    StaffInfusionSmithingRecipeTextures.register(
        Identifier(MOD_ID, "item/smithing_table/empty_slot_royal_staff"),
        SmithingTemplateItem.EMPTY_SLOT_REDSTONE_DUST_TEXTURE
    )
}

@Environment(EnvType.CLIENT)
fun subscribeToClientEvents() {
    InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(::clientAttack)
    ClientTickEvent.CLIENT_POST.register(::handleKeyBindings)
}
