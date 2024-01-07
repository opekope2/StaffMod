// Copyright (c) 2023-2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.packet.c2s.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.packet.c2s.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.server.StaffPacketHandler

@Suppress("unused")
object StaffMod : ModInitializer {
    const val MOD_ID = "avm_staff"

    @JvmField
    val STAFF_ITEM: StaffItem = Registry.register(
        Registries.ITEM,
        Identifier(MOD_ID, "staff"),
        StaffItem(FabricItemSettings().maxCount(1))
    )

    override fun onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register { entries ->
            entries.addAfter(Items.NETHERITE_HOE, STAFF_ITEM)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register { entries ->
            entries.addAfter(Items.TRIDENT, STAFF_ITEM)
        }
        AddItemToStaffC2SPacket.registerGlobalReceiver(StaffPacketHandler::addBlockToStaff)
        RemoveItemFromStaffC2SPacket.registerGlobalReceiver(StaffPacketHandler::removeBlockFromStaff)
    }
}
