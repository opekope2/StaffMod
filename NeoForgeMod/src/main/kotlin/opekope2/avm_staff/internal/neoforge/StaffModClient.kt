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

import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.TickEvent
import opekope2.avm_staff.api.flamethrowerParticleType
import opekope2.avm_staff.api.particle.FlamethrowerParticle
import opekope2.avm_staff.api.soulFlamethrowerParticleType
import opekope2.avm_staff.internal.event_handler.ADD_REMOVE_KEYBINDING
import opekope2.avm_staff.internal.event_handler.handleKeyBindings
import opekope2.avm_staff.internal.model.registerModelPredicateProviders
import opekope2.avm_staff.internal.staff_item_handler.registerVanillaStaffItemRenderers
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@OnlyIn(Dist.CLIENT)
object StaffModClient {
    fun initializeClient() {
        registerVanillaStaffItemRenderers()
        MOD_BUS.register(this)
        FORGE_BUS.addListener(::handleStaffKeybinding)
    }

    @SubscribeEvent
    fun initializeClient(event: FMLClientSetupEvent) {
        event.enqueueWork {
            registerModelPredicateProviders(ModelPredicateProviderRegistry::registerGeneric)
        }
    }

    @SubscribeEvent
    fun addItemsToItemGroups(event: BuildCreativeModeTabContentsEvent) {
    }

    @SubscribeEvent
    fun registerParticleProviders(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(flamethrowerParticleType.get(), FlamethrowerParticle::Factory)
        event.registerSpriteSet(soulFlamethrowerParticleType.get(), FlamethrowerParticle::Factory)
    }

    @SubscribeEvent
    fun registerKeyBindings(event: RegisterKeyMappingsEvent) {
        event.register(ADD_REMOVE_KEYBINDING)
    }

    @JvmStatic
    fun handleStaffKeybinding(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        handleKeyBindings(MinecraftClient.getInstance())
    }
}
