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

package opekope2.avm_staff.content

import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.SoundEvent
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.Registrar

/**
 * Sound events added by AVM Staffs mod.
 */
@Suppress("unused")
object SoundEvents : Registrar<SoundEvent>(MOD_ID, RegistryKeys.SOUND_EVENT) {
    @JvmStatic
    private fun registering(path: String) = registering(path) { key -> SoundEvent.of(key.value)!! }

    /**
     * Cake splashing sound event.
     */
    @JvmStatic
    val cakeSplash by registering("entity.cake.splash")

    /**
     * Cake thrown sound event.
     */
    @JvmStatic
    val cakeThrow by registering("entity.cake.throw")

    /**
     * Player celebrates prank sound event.
     */
    @JvmStatic
    val celebratePrank by registering("entity.player.celebrate_prank")

    /**
     * Flamethrower fire sound event.
     */
    @JvmStatic
    val flamethrowerFire by registering("item.flamethrower.fire")
}
