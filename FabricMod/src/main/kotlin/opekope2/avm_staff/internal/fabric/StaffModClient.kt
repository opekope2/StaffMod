/*
 * AvM Staff Mod
 * Copyright (c) 2023-2024 opekope2
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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.item.ModelPredicateProviderRegistry
import opekope2.avm_staff.api.faintRoyalStaffItem
import opekope2.avm_staff.api.flamethrowerParticleType
import opekope2.avm_staff.api.item.renderer.StaffRenderer
import opekope2.avm_staff.api.particle.FlamethrowerParticle
import opekope2.avm_staff.api.royalStaffItem
import opekope2.avm_staff.api.soulFlamethrowerParticleType
import opekope2.avm_staff.internal.event_handler.ADD_REMOVE_KEYBINDING
import opekope2.avm_staff.internal.event_handler.handleKeyBindings
import opekope2.avm_staff.internal.model.registerModelPredicateProviders

@Suppress("unused")
@Environment(EnvType.CLIENT)
object StaffModClient : ClientModInitializer {
    override fun onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(ADD_REMOVE_KEYBINDING)

        ClientTickEvents.END_CLIENT_TICK.register(::handleKeyBindings)

        ParticleFactoryRegistry.getInstance().register(
            flamethrowerParticleType.get(),
            FlamethrowerParticle::Factory
        )
        ParticleFactoryRegistry.getInstance().register(
            soulFlamethrowerParticleType.get(),
            FlamethrowerParticle::Factory
        )

        registerModelPredicateProviders(ModelPredicateProviderRegistry::register)

        BuiltinItemRendererRegistry.INSTANCE.register(faintRoyalStaffItem.get(), StaffRenderer::renderStaff)
        BuiltinItemRendererRegistry.INSTANCE.register(royalStaffItem.get(), StaffRenderer::renderStaff)
    }
}
