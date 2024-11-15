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

package opekope2.avm_staff.internal.staff.item_renderer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.AbstractFurnaceBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import opekope2.avm_staff.api.item.renderer.BlockStateStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.StaffItemRenderer
import opekope2.avm_staff.content.ComponentTypes

@Environment(EnvType.CLIENT)
class FurnaceStaffItemRenderer(unlitState: BlockState, litState: BlockState) : StaffItemRenderer() {
    constructor(furnaceBlock: Block) : this(
        furnaceBlock.defaultState,
        furnaceBlock.defaultState.with(AbstractFurnaceBlock.LIT, true)
    )

    private val unlitRenderer = BlockStateStaffItemRenderer(unlitState)
    private val litRenderer = BlockStateStaffItemRenderer(litState)

    override fun renderItemInStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val renderer =
            if (ComponentTypes.furnaceData in staffStack) litRenderer
            else unlitRenderer

        renderer.renderItemInStaff(staffStack, mode, matrices, vertexConsumers, light, overlay)
    }
}
