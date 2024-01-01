package opekope2.avm_staff

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import opekope2.avm_staff.item.StaffItem
import opekope2.avm_staff.packet.c2s.AddBlockToStaffC2SPacket
import opekope2.avm_staff.packet.c2s.RemoveBlockFromStaffC2SPacket
import opekope2.avm_staff.server.ServerStaffHandler

@Suppress("unused")
object StaffMod : ModInitializer {
    const val MOD_ID = "avm_staff"

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
        AddBlockToStaffC2SPacket.registerGlobalReceiver(ServerStaffHandler::addBlockToStaff)
        RemoveBlockFromStaffC2SPacket.registerGlobalReceiver(ServerStaffHandler::removeBlockFromStaff)
    }
}
