// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal.staff_item_handler

import opekope2.avm_staff.api.initializer.IStaffModInitializationContext

@Suppress("unused")
fun register(context: IStaffModInitializationContext) {
    BoneBlockItemHandler.registerStaffItemHandler(context)

    SnowBlockItemHandler.registerStaffItemHandler(context)
}
