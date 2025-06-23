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
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import opekope2.avm_staff.api.particle.FlamethrowerParticle
import opekope2.avm_staff.content.ParticleTypes
import opekope2.avm_staff.internal.event_handler.ClientEventHandlers
import opekope2.avm_staff.internal.initializer.ClientInitializer
import opekope2.avm_staff.internal.model.ModelPredicates
import opekope2.avm_staff.internal.staff.handler.registerVanillaStaffItemRenderers
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@OnlyIn(Dist.CLIENT)
object StaffModClient {
    val staffItems = mutableSetOf<Item>()

    fun initializeClient() {
        ClientInitializer
        ClientEventHandlers
        registerVanillaStaffItemRenderers()
        MOD_BUS.register(this)
    }

    @SubscribeEvent
    fun initializeClient(event: FMLClientSetupEvent) {
        event.enqueueWork {
            for ((key, value) in ModelPredicates) {
                ModelPredicateProviderRegistry.registerGeneric(key, value)
            }
        }
    }

    @SubscribeEvent
    fun registerParticleProviders(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(ParticleTypes.flame, FlamethrowerParticle::Factory)
        event.registerSpriteSet(ParticleTypes.soulFireFlame, FlamethrowerParticle::Factory)
    }

    @SubscribeEvent
    fun registerStaffItemModels(event: ModelEvent.RegisterAdditional) {
        for (item in staffItems) {
            val itemId = Registries.ITEM.getId(item).withPrefixedPath("item/")
            event.register(ModelIdentifier.standalone(itemId.withSuffixedPath("/head")))
            event.register(ModelIdentifier.standalone(itemId.withSuffixedPath("/item_transform")))
            event.register(ModelIdentifier.standalone(itemId.withSuffixedPath("/rod_top")))
            event.register(ModelIdentifier.standalone(itemId.withSuffixedPath("/rod_bottom")))
        }
    }
}
