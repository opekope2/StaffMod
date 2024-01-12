// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.api.initializer

import com.mojang.serialization.Codec
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.config.Configuration
import opekope2.avm_staff.api.config.IConfiguration
import opekope2.avm_staff.api.item.StaffItemHandler

/**
 * Initialization context for [IStaffModInitializer].
 */
interface IStaffModInitializationContext {
    /**
     * Registers a [StaffItemHandler].
     *
     * @param itemInStaff           The item ID to register a handler for
     * @param handler               The handler, which processes staff interactions, while the [registered item][itemInStaff] is in it
     * @param configurationCodec    The codec for the configuration in [Configuration.itemConfigurations]
     * @return `true`, if the registration was successful, `false`, if the item was already registered
     */
    fun <TConfig : IConfiguration<TProfile>, TProfile : Any> registerStaffItemHandler(
        itemInStaff: Identifier,
        handler: StaffItemHandler,
        configurationCodec: Codec<TConfig>
    ): Boolean
}
