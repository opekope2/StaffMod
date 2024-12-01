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

package opekope2.avm_staff.api.block

import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.util.Clearable

/**
 * Performs additional clearing after [Clearable.clear]. Mixed into [AbstractFurnaceBlockEntity] to prevent XP
 * duplication.
 *
 * @see Clearable.clear
 */
interface IClearableBeforeInsertedIntoStaff {
    /**
     * Called before a block is inserted into a staff and removed from the world.
     *
     * @see Clearable.clear
     */
    @Suppress("FunctionName") // Mixin
    fun staffMod_clearBeforeRemovedFromWorld()
}
