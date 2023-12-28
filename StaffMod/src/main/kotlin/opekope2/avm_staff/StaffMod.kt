package opekope2.avm_staff

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import opekope2.avm_staff.item.StaffItem

@Suppress("unused")
object StaffMod : ModInitializer {
    const val MOD_ID = "avm_staff"

    val STAFF_ITEM: StaffItem = Registry.register(
        Registries.ITEM,
        Identifier(MOD_ID, "staff"),
        StaffItem(FabricItemSettings().maxCount(1))
    )

    override fun onInitialize() {
    }
}
