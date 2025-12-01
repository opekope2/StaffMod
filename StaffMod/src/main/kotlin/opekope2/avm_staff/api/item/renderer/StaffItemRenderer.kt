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

package opekope2.avm_staff.api.item.renderer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.registry.RegistryBase
import opekope2.avm_staff.util.registryId
import kotlin.math.max

/**
 * A renderer for an item, which can be placed into a staff.
 *
 * @see StaffItemRenderer.register
 */
@Environment(EnvType.CLIENT)
abstract class StaffItemRenderer {
    /**
     * Renders an item.
     *
     * @param staffStack        The staff item stack
     * @param itemTransform     The transformation of the item in the staff. Use [ModelTransformationMode.FIXED] to
     *   render the item inside the staff or [ModelTransformationMode.HEAD] to render the item on top of the staff
     * @param mode              The transformation the staff is rendered in. You likely want to pass
     *   [ModelTransformationMode.NONE] to rendering calls
     * @param matrices          Matrix stack for rendering calls
     * @param vertexConsumers   Vertex consumer provider for rendering calls
     * @param light             Light component for rendering calls
     * @param overlay           Overlay component for rendering calls
     */
    abstract fun renderItemInStaff(
        staffStack: ItemStack,
        itemTransform: ModelTransformation,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    )

    /**
     * Transforms a matrix stack
     *
     * @param itemTransform     The `itemTransform` from [renderItemInStaff]
     * @param itemTransformMode [ModelTransformationMode.FIXED] to render the item inside the staff or
     *   [ModelTransformationMode.HEAD] to render the item on top of the staff
     */
    protected fun MatrixStack.transform(
        itemTransform: ModelTransformation,
        itemTransformMode: ModelTransformationMode
    ) {
        itemTransform.getTransformation(itemTransformMode).apply(false, this)
    }

    @Environment(EnvType.CLIENT)
    companion object Registry : RegistryBase<Identifier, StaffItemRenderer>() {
        /**
         * Registers an entry to this registry.
         *
         * @param key The key to associate a value with
         * @param value The value to register
         */
        fun register(key: Item, value: StaffItemRenderer) = register(key.registryId, value)

        /**
         * Checks if the given key is present in the registry
         *
         * @param key The key to check
         */
        operator fun contains(key: Item) = key.registryId in this

        /**
         * Gets the value associated with the given key or throws an exception, if the key is not present in this registry.
         *
         * @param key The key to check
         */
        operator fun get(key: Item) = getValue(key.registryId)

        /**
         * Calculates a new light parameter value.
         *
         * @param light     The [packed][LightmapTextureManager.pack] light value
         * @param luminance The [luminance][net.minecraft.block.BlockState.getLuminance] of a block state
         * @return A new light value where the block light is [luminance] if it's greater than the previous block light
         *   value
         */
        @JvmStatic
        protected fun getLight(light: Int, luminance: Int): Int {
            val blockLight = LightmapTextureManager.getBlockLightCoordinates(light)
            val skyLight = LightmapTextureManager.getSkyLightCoordinates(light)

            return LightmapTextureManager.pack(max(blockLight, luminance and 0xFFFF), skyLight)
        }
    }
}
