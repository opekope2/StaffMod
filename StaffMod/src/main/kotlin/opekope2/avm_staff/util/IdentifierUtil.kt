/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

@file: JvmName("IdentifierUtil")

package opekope2.avm_staff.util

import net.minecraft.item.Item
import net.minecraft.registry.DefaultedRegistry
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

/**
 * @see Registries.ITEM
 * @see DefaultedRegistry.getId
 */
inline val Item.registryId: Identifier
    get() = Registries.ITEM.getId(this)
