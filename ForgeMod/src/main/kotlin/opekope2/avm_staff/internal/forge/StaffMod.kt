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

package opekope2.avm_staff.internal.forge

import net.minecraft.item.Item
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import opekope2.avm_staff.IStaffMod
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.forge.item.ForgeStaffItem
import opekope2.avm_staff.internal.initializeNetworking
import opekope2.avm_staff.internal.staff_item_handler.registerVanillaStaffItemHandlers
import opekope2.avm_staff.util.MOD_ID
import thedarkcolour.kotlinforforge.forge.DIST
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runWhenOn

@Mod(MOD_ID)
object StaffMod : IStaffMod {
    private val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)

    private val STAFF_ITEM = ITEMS.register("staff") { ForgeStaffItem(Item.Settings().maxCount(1)) }

    init {
        initialize()
        initializeNetworking()
        registerVanillaStaffItemHandlers()
        runWhenOn(Dist.CLIENT, StaffModClient::initializeClient)
    }

    override val staffItem: StaffItem
        get() = STAFF_ITEM.get()

    override val isPhysicalClient: Boolean
        get() = DIST.isClient

    private fun initialize() {
        ITEMS.register(MOD_BUS)
    }
}
