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

@file:JvmName("Constants")

package opekope2.avm_staff.util

import opekope2.avm_staff.content.SoundEvents

/**
 * The identifier of Staff Mod.
 */
const val MOD_ID = "avm_staff"

/**
 * Entity defused world event.
 * Also used when a cake collides and splashes.
 * Data: [net.minecraft.entity.Entity.getId]
 */
const val ENTITY_DEFUSED_WORLD_EVENT: Int = 286782330 // AVMSTAFF0

/**
 * Celebrate prank world event.
 * Used to tell clients to play the [SoundEvents.celebratePrank] sound effect when a player pranks another one.
 * Data: [net.minecraft.entity.Entity.getId]
 */
const val CELEBRATE_PRANK_WORLD_EVENT: Int = 286782331 // AVMSTAFF1
