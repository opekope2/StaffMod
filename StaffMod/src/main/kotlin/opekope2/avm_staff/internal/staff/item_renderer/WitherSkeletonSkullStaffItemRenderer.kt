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
import net.minecraft.block.AbstractSkullBlock
import net.minecraft.block.Blocks
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer
import net.minecraft.client.render.entity.model.SkullEntityModel
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import opekope2.avm_staff.api.item.renderer.StaffItemRenderer
import opekope2.avm_staff.util.push

@Environment(EnvType.CLIENT)
class WitherSkeletonSkullStaffItemRenderer : StaffItemRenderer() {
    private val skullModel = SkullEntityModel.getSkullTexturedModelData().createModel()

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
            scale(-1f, -1f, 1f)
            translate(0f, 8f / 16f, 0f)
            scale(2f, 2f, 2f)
            skullModel.render(
                matrices,
                vertexConsumers.getBuffer(
                    SkullBlockEntityRenderer.getRenderLayer(
                        (Blocks.WITHER_SKELETON_SKULL as AbstractSkullBlock).skullType, null
                    )
                ),
                light,
                overlay
            )
        }
    }
}
