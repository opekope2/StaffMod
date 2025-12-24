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

package opekope2.avm_staff.api

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.model.BakedModel
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.IStaffModClientPlatform.Instance.renderAsStaffModel
import opekope2.avm_staff.api.IStaffModClientPlatform.Instance.staffModelItems
import opekope2.avm_staff.internal.staffModClientPlatform

/**
 * Loader-specific functionality
 *
 * @see Instance
 */
@Environment(EnvType.CLIENT)
interface IStaffModClientPlatform {
    /**
     * Gets the items requested to be rendered as a staff model.
     * Do not modify, call [renderAsStaffModel] instead.
     *
     * @see renderAsStaffModel
     */
    val staffModelItems: Collection<Item>

    /**
     * Tells Minecraft to render the given item as a staff model.
     * When called on the server, it does nothing
     *
     * @param item  The item to render as a staff
     * @see staffModelItems
     */
    fun renderAsStaffModel(item: Item)

    /**
     * Returns a model registered through loader-specific API.
     *
     * @param modelId   The [net.minecraft.util.Identifier] of the model to get
     */
    fun getStandaloneModel(modelId: Identifier): BakedModel

    /**
     * Wrapper around the current loader's [IStaffModClientPlatform] implementation.
     */
    companion object Instance : IStaffModClientPlatform by staffModClientPlatform
}
