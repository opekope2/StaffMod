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

import com.mojang.serialization.Lifecycle
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.item.renderer.StaffItemRenderer.Companion.REGISTRY
import opekope2.avm_staff.util.MOD_ID
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
    companion object {
        /**
         * Registry key of [REGISTRY].
         */
        @JvmField
        val REGISTRY_KEY: RegistryKey<Registry<StaffItemRenderer>> =
            RegistryKey.ofRegistry(Identifier.of(MOD_ID, "staff_item_renderer"))

        /**
         * Registry of staff item renderers.
         */
        @JvmField
        val REGISTRY: Registry<StaffItemRenderer> = SimpleRegistry(REGISTRY_KEY, Lifecycle.stable())

        /**
         * Registers an entry to [REGISTRY].
         *
         * @param T     The type of the staff item renderer
         * @param key   The key to associate a value with
         * @param value The value to register
         */
        fun <T : StaffItemRenderer> register(key: Item, value: T): T =
            Registry.register(REGISTRY, key.registryId, value)

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
