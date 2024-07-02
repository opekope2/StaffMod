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

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.particle.SimpleParticleType
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import opekope2.avm_staff.api.IStaffModPlatform
import opekope2.avm_staff.internal.initializeNetworking
import opekope2.avm_staff.internal.neoforge.item.NeoForgeCrownItem
import opekope2.avm_staff.internal.neoforge.item.NeoForgeStaffItem
import opekope2.avm_staff.internal.neoforge.item.NeoForgeStaffRendererItem
import opekope2.avm_staff.internal.registerContent
import opekope2.avm_staff.internal.staff_handler.registerVanillaStaffHandlers
import opekope2.avm_staff.internal.stopUsingStaffWhenDropped
import opekope2.avm_staff.internal.subscribeToEvents
import opekope2.avm_staff.util.MOD_ID
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn

@Mod(MOD_ID)
object StaffMod : IStaffModPlatform {
    init {
        registerContent()
        initializeNetworking()
        subscribeToEvents()
        subscribeToNeoForgeEvents()
        registerVanillaStaffHandlers()
        runWhenOn(Dist.CLIENT, StaffModClient::initializeClient)
    }

    private fun subscribeToNeoForgeEvents() {
        FORGE_BUS.addListener(::dropInventory)
    }

    private fun dropInventory(event: LivingDropsEvent) {
        for (item in event.drops) {
            stopUsingStaffWhenDropped(event.entity, item)
        }
    }

    override fun staffItem(settings: Item.Settings) = NeoForgeStaffItem(settings)

    override fun itemWithStaffRenderer(settings: Item.Settings) = NeoForgeStaffRendererItem(settings)

    override fun crownItem(groundBlock: Block, wallBlock: Block, settings: Item.Settings) =
        NeoForgeCrownItem(groundBlock, wallBlock, settings)

    override fun simpleParticleType(alwaysShow: Boolean) = SimpleParticleType(alwaysShow)
}
