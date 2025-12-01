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

package opekope2.avm_staff.internal.loot

import net.minecraft.loot.entry.LootPoolEntry

@Suppress("FunctionName") // Mixin
fun interface ILootPoolBuilder {
    fun staffMod_addEntry(builder: LootPoolEntry.Builder<*>) {
        staffMod_addEntry(builder.build())
    }

    fun staffMod_addEntry(entry: LootPoolEntry?)
}
