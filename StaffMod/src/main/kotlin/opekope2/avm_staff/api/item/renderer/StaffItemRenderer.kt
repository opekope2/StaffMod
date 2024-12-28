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
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.registry.RegistryBase

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
     * @param mode              The transformation the staff is rendered in. You likely want to pass
     *   [ModelTransformationMode.NONE] to rendering calls
     * @param matrices          Matrix stack for rendering calls
     * @param vertexConsumers   Vertex consumer provider for rendering calls
     * @param light             Light component for rendering calls
     * @param overlay           Overlay component for rendering calls
     */
    abstract fun renderItemInStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    )

    @Environment(EnvType.CLIENT)
    companion object Registry : RegistryBase<Identifier, StaffItemRenderer>() {
        private inline val Item.registryId: Identifier
            get() = Registries.ITEM.getId(this)

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
    }
}
