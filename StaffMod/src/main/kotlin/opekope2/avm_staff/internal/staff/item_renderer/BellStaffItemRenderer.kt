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

package opekope2.avm_staff.internal.staff.item_renderer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import opekope2.avm_staff.api.item.renderer.StaffItemRenderer
import opekope2.avm_staff.util.push

@Environment(EnvType.CLIENT)
class BellStaffItemRenderer : StaffItemRenderer() {
    private val bellModel = BellBlockEntityRenderer.getTexturedModelData().createModel().apply {
        setPivot(-8f, -12f, -8f)
    }

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
            scale(16f / 9f, 16f / 9f, 16f / 9f)
            translate(0f, 2f / 9f, 0f)

            bellModel.render(
                matrices,
                BellBlockEntityRenderer.BELL_BODY_TEXTURE.getVertexConsumer(
                    vertexConsumers,
                    RenderLayer::getEntitySolid
                ),
                light,
                overlay
            )
        }
    }
}
