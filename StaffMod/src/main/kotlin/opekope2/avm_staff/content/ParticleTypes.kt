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

package opekope2.avm_staff.content

import net.minecraft.client.particle.ParticleManager
import net.minecraft.particle.ParticleType
import net.minecraft.particle.SimpleParticleType
import net.minecraftforge.registries.ForgeRegistries
import opekope2.avm_staff.api.IStaffModPlatform
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryUtil

/**
 * Particle types added by AVM Staffs mod.
 */
object ParticleTypes : RegistryUtil<ParticleType<*>>(MOD_ID, ForgeRegistries.PARTICLE_TYPES) {
    /**
     * Particle registered as `avm_staff:flame`.
     *
     * @see ParticleManager.addParticle
     */
    @JvmField
    val FLAME = register("flame") { IStaffModPlatform.simpleParticleType(false) }

    /**
     * @see FLAME
     */
    val flame: SimpleParticleType
        @JvmName("flame")
        get() = FLAME.get()

    /**
     * Particle registered as `avm_staff:soul_fire_flame`.
     *
     * @see ParticleManager.addParticle
     */
    @JvmField
    val SOUL_FIRE_FLAME = register("soul_fire_flame") { IStaffModPlatform.simpleParticleType(false) }

    /**
     * @see SOUL_FIRE_FLAME
     */
    val soulFireFlame: SimpleParticleType
        @JvmName("soulFireFlame")
        get() = SOUL_FIRE_FLAME.get()
}
