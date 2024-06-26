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
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.client.item.ModelPredicateProviderRegistry
import opekope2.avm_staff.api.flamethrowerParticleType
import opekope2.avm_staff.api.particle.FlamethrowerParticle
import opekope2.avm_staff.api.soulFlamethrowerParticleType
import opekope2.avm_staff.internal.model.registerModelPredicateProviders

@Suppress("unused")
@Environment(EnvType.CLIENT)
object StaffModClient : ClientModInitializer {
    override fun onInitializeClient() {
        ParticleFactoryRegistry.getInstance().apply {
            register(flamethrowerParticleType.get(), FlamethrowerParticle::Factory)
            register(soulFlamethrowerParticleType.get(), FlamethrowerParticle::Factory)
        }

        registerModelPredicateProviders(ModelPredicateProviderRegistry::register)
    }
}
