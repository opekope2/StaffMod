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
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

/**
 * A renderer for an item, which can be placed into a staff.
 *
 * @see IStaffItemRenderer.register
 */
@Environment(EnvType.CLIENT)
fun interface IStaffItemRenderer {
    /**
     * Renders an item.
     *
     * @param staffStack        The staff item stack
     * @param mode              The transformation the staff is rendered in. You likely want to pass
     *   [ModelTransformationMode.NONE] to rendering calls
     * @param matrices          Matrix stack for rendering calls
     * @param vertexConsumers   Vertex consumer provider for rendering calls
     * @param light             Light component for rendering calls
     * @param overlay           Overlay component for rendering calls
     */
    fun renderItemInStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    )

    @Environment(EnvType.CLIENT)
    companion object {
        private val staffItemRenderers = mutableMapOf<Identifier, IStaffItemRenderer>()

        /**
         * Registers a renderer for a given [item ID][staffItem].
         *
         * @param staffItem The item ID to register a renderer for
         * @param renderer  The item's renderer
         * @return `true`, if the registration was successful, `false`, if a renderer for the item was already registered
         */
        @JvmStatic
        fun register(staffItem: Identifier, renderer: IStaffItemRenderer): Boolean {
            if (staffItem in staffItemRenderers) return false

            staffItemRenderers[staffItem] = renderer
            return true
        }

        /**
         * Checks if a renderer for the [given item][staffItem] is registered.
         *
         * @param staffItem The item ID, which can be inserted into the staff
         */
        @JvmStatic
        operator fun contains(staffItem: Identifier): Boolean = staffItem in staffItemRenderers

        /**
         * Gets the registered renderer for the [given item][staffItem] or `null`, if no renderer was registered.
         *
         * @param staffItem The item ID, which can be inserted into the staff
         */
        @JvmStatic
        operator fun get(staffItem: Identifier): IStaffItemRenderer? = staffItemRenderers[staffItem]
    }
}
