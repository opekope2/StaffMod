// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal.item

import com.mojang.serialization.Codec
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.config.IConfiguration
import opekope2.avm_staff.api.item.StaffItemHandler

object StaffItemHandlers {
    private val supportedStaffItems = mutableMapOf<Identifier, SupportedStaffItem>()

    @JvmStatic
    fun <T : IConfiguration<*>> register(
        staffItem: Identifier,
        handler: StaffItemHandler,
        configCodec: Codec<T>
    ): Boolean {
        if (staffItem in supportedStaffItems) return false

        supportedStaffItems[staffItem] =
            SupportedStaffItem(handler, configCodec as Codec<IConfiguration<*>>)
        return true
    }

    operator fun contains(staffItem: Identifier): Boolean = staffItem in supportedStaffItems

    operator fun get(staffItem: Identifier): SupportedStaffItem? = supportedStaffItems[staffItem]

    data class SupportedStaffItem(
        val staffHandler: StaffItemHandler,
        val configurationCodec: Codec<IConfiguration<*>>
    )
}
