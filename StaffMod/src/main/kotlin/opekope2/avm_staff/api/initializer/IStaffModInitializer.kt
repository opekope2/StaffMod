// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.api.initializer

import net.fabricmc.api.ModInitializer
import opekope2.avm_staff.api.item.StaffItemHandler

/**
 * Staff Mod initializer called by Staff Mod from its [ModInitializer.onInitialize].
 * In `fabric.mod.json`, the entrypoint key is `avm-staff`.
 */
fun interface IStaffModInitializer {
    /**
     * Implement this method to add support for [an item to be used in a staff][StaffItemHandler].
     */
    fun onInitializeStaffMod(context: IStaffModInitializationContext)
}
