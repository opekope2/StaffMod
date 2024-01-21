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

package opekope2.avm_staff.api.item.renderer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.item.ItemStack
import net.minecraft.util.math.random.Random
import java.util.function.Supplier

/**
 * Interface for adding a model into the staff while rendering the staff model.
 *
 * @see StaffBlockStateRenderer
 * @see InsideStaffBlockStateRenderer
 */
@Environment(EnvType.CLIENT)
fun interface IStaffItemRenderer {
    /**
     * Adds the model of the block added to the staff to the staff model's mesh.
     *
     * @param staffStack        The item stack to render
     * @param randomSupplier    A random value supplier
     * @param context           The Fabric Rendering API context to emit the mode's mesh with
     * @see FabricBakedModel.emitItemQuads
     */
    fun emitItemQuads(staffStack: ItemStack, randomSupplier: Supplier<Random>, context: RenderContext)
}
