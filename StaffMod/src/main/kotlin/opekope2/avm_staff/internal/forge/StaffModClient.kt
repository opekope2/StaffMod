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

import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import opekope2.avm_staff.api.particle.FlamethrowerParticle
import opekope2.avm_staff.content.ParticleTypes
import opekope2.avm_staff.internal.event_handler.ClientEventHandlers
import opekope2.avm_staff.internal.initializer.ClientInitializer
import opekope2.avm_staff.internal.model.ModelPredicates
import opekope2.avm_staff.internal.staff.handler.registerVanillaStaffItemRenderers
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@OnlyIn(Dist.CLIENT)
object StaffModClient {
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
}
