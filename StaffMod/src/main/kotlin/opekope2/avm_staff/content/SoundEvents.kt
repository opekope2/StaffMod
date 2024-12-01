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

import net.minecraft.sound.SoundEvent
import net.minecraftforge.registries.ForgeRegistries
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryUtil

/**
 * Sound events added by AVM Staffs mod.
 */
object SoundEvents : RegistryUtil<SoundEvent>(MOD_ID, ForgeRegistries.SOUND_EVENTS) {
    /**
     * Sound event registered as `avm_staff:entity.cake.splash`.
     */
    @JvmField
    val CAKE_SPLASH = register("entity.cake.splash") { SoundEvent.of(it.value) }

    /**
     * @see CAKE_SPLASH
     */
    val cakeSplash: SoundEvent
        @JvmName("cakeSplash")
        get() = CAKE_SPLASH.get()

    /**
     * Sound event registered as `avm_staff:entity.cake.throw`.
     */
    @JvmField
    val CAKE_THROW = register("entity.cake.throw") { SoundEvent.of(it.value) }

    /**
     * @see CAKE_THROW
     */
    val cakeThrow: SoundEvent
        @JvmName("cakeThrow")
        get() = CAKE_THROW.get()
}
