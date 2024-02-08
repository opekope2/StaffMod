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

package opekope2.avm_staff.api.item.model

import net.minecraft.client.render.model.BakedModel
import net.minecraft.item.ItemStack

/**
 * [BakedModel] provider for a given staff item stack.
 */
interface IReloadableBakedModelProvider {
    /**
     * Gets the model of the item in the Staff.
     *
     * @param staffStack    The staff item stack (not the item in the staff)
     */
    fun getModel(staffStack: ItemStack): BakedModel

    /**
     * Reloads the model(s) returned by [getModel].
     * Called after the game initially loads or reloads the resources.
     */
    fun reload()
}
