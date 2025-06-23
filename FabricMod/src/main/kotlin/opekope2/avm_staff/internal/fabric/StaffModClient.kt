/*
 * AvM Staff Mod
 * Copyright (c) 2023-2025 opekope2
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

package opekope2.avm_staff.internal.fabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.render.RenderLayer
import opekope2.avm_staff.api.particle.FlamethrowerParticle
import opekope2.avm_staff.content.Blocks
import opekope2.avm_staff.content.ParticleTypes
import opekope2.avm_staff.internal.initializer.ClientInitializer
import opekope2.avm_staff.internal.model.ModelPredicates
import opekope2.avm_staff.internal.staff.handler.registerVanillaStaffItemRenderers

@Suppress("unused")
@Environment(EnvType.CLIENT)
object StaffModClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientInitializer
        registerVanillaStaffItemRenderers()

        registerParticleFactories(ParticleFactoryRegistry.getInstance())
        registerBlockRenderLayers()
        registerModelPredicateProviders()
        StaffItemModelLoadingPlugin
    }

    private fun registerParticleFactories(particleFactoryRegistry: ParticleFactoryRegistry) {
        particleFactoryRegistry.register(ParticleTypes.flame, FlamethrowerParticle::Factory)
        particleFactoryRegistry.register(ParticleTypes.soulFireFlame, FlamethrowerParticle::Factory)
    }

    private fun registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderLayer.getCutout(),
            Blocks.crownOfKingOrange,
            Blocks.wallCrownOfKingOrange
        )
    }

    private fun registerModelPredicateProviders() {
        for ((key, value) in ModelPredicates) {
            ModelPredicateProviderRegistry.register(key, value)
        }
    }
}
