/*
 * AvM Staff Mod
 * Copyright (c) 2024-2025 opekope2
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

import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.util.ModelIdentifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import opekope2.avm_staff.api.IStaffModClientPlatform
import opekope2.avm_staff.internal.AbstractStaffModClient
import opekope2.avm_staff.internal.neoforge.renderer.StaffRenderer
import opekope2.avm_staff.util.MOD_ID
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(MOD_ID, dist = [Dist.CLIENT])
@OnlyIn(Dist.CLIENT)
object StaffModClient : AbstractStaffModClient() {
    init {
        super.initialize()

        MOD_BUS.register(this)
    }

    @SubscribeEvent
    fun initializeClient(event: FMLClientSetupEvent) {
        event.enqueueWork { registerModelPredicateProviders(ModelPredicateProviderRegistry::registerGeneric) }
    }

    @SubscribeEvent
    fun registerStaffItemModels(event: ModelEvent.RegisterAdditional) {
        registerStaffItemModels { event.register(ModelIdentifier.standalone(it)) }
    }

    @SubscribeEvent
    fun registerStaffRenderers(event: RegisterClientExtensionsEvent) {
        for (item in IStaffModClientPlatform.staffModelItems) event.registerItem(StaffRenderer, item)
    }
}
