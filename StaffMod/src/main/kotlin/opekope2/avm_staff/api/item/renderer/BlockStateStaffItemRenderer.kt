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
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.BlockModels
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import opekope2.avm_staff.util.mc
import opekope2.avm_staff.util.push

/**
 * A [StaffItemRenderer], always which renders a single block state.
 *
 * @param blockState    The block state to render
 */
@Environment(EnvType.CLIENT)
class BlockStateStaffItemRenderer(blockState: BlockState) : StaffItemRenderer() {
    private val blockStateId = BlockModels.getModelId(blockState)
    private val blockItem = blockState.block.asItem().defaultStack
    private val luminance = blockState.luminance

    /**
     * Creates a new [BlockStateStaffItemRenderer] with the [default state][Block.defaultState] of the given block.
     *
     * @param block The block to render its default state
     */
    constructor(block: Block) : this(block.defaultState)

    override fun renderItemInStaff(
        staffStack: ItemStack,
        itemTransform: ModelTransformation,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push {
            transform(itemTransform, ModelTransformationMode.FIXED)
            mc.itemRenderer.renderItem(
                blockItem,
                ModelTransformationMode.NONE,
                false,
                this,
                vertexConsumers,
                getLight(light, luminance),
                overlay,
                mc.bakedModelManager.getModel(blockStateId)
            )
        }
    }
}
