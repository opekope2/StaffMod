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

package opekope2.avm_staff.internal.neoforge

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import opekope2.avm_staff.internal.initializeNetworking
import opekope2.avm_staff.internal.registerContent
import opekope2.avm_staff.internal.staff_handler.registerVanillaStaffHandlers
import opekope2.avm_staff.internal.subscribeToEvents
import opekope2.avm_staff.util.MOD_ID
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn

@Mod(MOD_ID)
object StaffMod {
    init {
        registerContent()
        initializeNetworking()
        subscribeToEvents()
        registerVanillaStaffHandlers()
        runWhenOn(Dist.CLIENT, StaffModClient::initializeClient)
    }
}
