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
import net.minecraft.block.Blocks
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import opekope2.avm_staff.api.item.renderer.BlockStateStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.StaffItemRenderer
import opekope2.avm_staff.util.push

@Environment(EnvType.CLIENT)
class LightningRodStaffItemRenderer : StaffItemRenderer() {
    private val lightningRodRenderer = BlockStateStaffItemRenderer(Blocks.LIGHTNING_ROD.defaultState)

    override fun renderItemInStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push {
            if (mode != ModelTransformationMode.GUI && mode != ModelTransformationMode.FIXED) {
                translate(0f, 22f / 16f, 0f)
            }
            lightningRodRenderer.renderItemInStaff(staffStack, mode, matrices, vertexConsumers, light, overlay)
        }
    }
}
