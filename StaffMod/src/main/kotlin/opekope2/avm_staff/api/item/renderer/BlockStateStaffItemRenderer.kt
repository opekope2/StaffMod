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

import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.BlockModels
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack

/**
 * A [IStaffItemRenderer], always which renders a single block state.
 *
 * @param blockState    The block state to render
 */
class BlockStateStaffItemRenderer(blockState: BlockState) : IStaffItemRenderer {
    private val blockStateId = BlockModels.getModelId(blockState)
    private val blockItem = blockState.block.asItem().defaultStack

    override fun renderItemInStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val itemRenderer = MinecraftClient.getInstance().itemRenderer
        val modelManager = MinecraftClient.getInstance().bakedModelManager
        val model = modelManager.getModel(blockStateId)
        itemRenderer.renderItem(
            blockItem, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, model
        )
    }
}
