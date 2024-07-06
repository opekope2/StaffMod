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

@file: JvmName("DamageUtil")

package opekope2.avm_staff.util

import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.world.World

/**
 * Utility function to create a [DamageSource].
 *
 * @param damageKey The registry key of the damage type
 */
fun World.damageSource(damageKey: RegistryKey<DamageType>) =
    DamageSource(registryManager[RegistryKeys.DAMAGE_TYPE].entryOf(damageKey))

/**
 * Utility function to create a [DamageSource].
 *
 * @param damageKey The registry key of the damage type
 * @param attacker  The causer entity of the damage
 */
fun World.damageSource(damageKey: RegistryKey<DamageType>, attacker: Entity) =
    DamageSource(registryManager[RegistryKeys.DAMAGE_TYPE].entryOf(damageKey), attacker)

/**
 * Utility function to create a [DamageSource].
 *
 * @param damageKey The registry key of the damage type
 * @param source    The causer entity of the damage
 * @param attacker  The owner of [source]
 */
fun World.damageSource(damageKey: RegistryKey<DamageType>, source: Entity, attacker: Entity) =
    DamageSource(registryManager[RegistryKeys.DAMAGE_TYPE].entryOf(damageKey), source, attacker)
