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

package opekope2.avm_staff.api

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.particle.SimpleParticleType
import opekope2.avm_staff.api.IStaffModPlatform.Instance
import opekope2.avm_staff.api.item.CrownItem
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.staffModPlatform

/**
 * Loader-specific functionality
 *
 * @see Instance
 */
interface IStaffModPlatform {
    /**
     * Creates a loader-specific instance of [StaffItem].
     *
     * @param settings  The item settings to pass to the constructor
     */
    fun staffItem(settings: Item.Settings): StaffItem

    /**
     * Creates an item, which is rendered like a staff.
     *
     * @param settings  The item settings to pass to the constructor
     */
    fun itemWithStaffRenderer(settings: Item.Settings): Item

    /**
     * Creates a loader-specific instance of [CrownItem].
     *
     * @param groundBlock   The crown block placed on the ground
     * @param wallBlock     The crown block placed on the wall
     * @param settings      The item settings to pass to the constructor
     */
    fun crownItem(groundBlock: Block, wallBlock: Block, settings: Item.Settings): CrownItem

    /**
     * Creates an instance of [SimpleParticleType].
     *
     * @param alwaysShow    Passed to [SimpleParticleType] constructor
     */
    fun simpleParticleType(alwaysShow: Boolean): SimpleParticleType

    /**
     * Wrapper around the current loader's [IStaffModPlatform] implementation.
     */
    companion object Instance : IStaffModPlatform by staffModPlatform
}
