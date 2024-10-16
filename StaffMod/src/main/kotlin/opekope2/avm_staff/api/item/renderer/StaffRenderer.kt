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
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import opekope2.avm_staff.api.component.StaffRendererPartComponent
import opekope2.avm_staff.api.staffRendererOverrideComponentType
import opekope2.avm_staff.api.staffRendererPartComponentType
import opekope2.avm_staff.util.itemStackInStaff
import opekope2.avm_staff.util.push
import kotlin.jvm.optionals.getOrNull

/**
 * Builtin model item renderer for staffs.
 */
@Environment(EnvType.CLIENT)
object StaffRenderer {
    /**
     * Renders the staff.
     *
     * @param staffStack        The staff item stack
     * @param mode              The transformation the staff is rendered in
     * @param matrices          Matrix stack for rendering calls
     * @param vertexConsumers   Vertex consumer provider for rendering calls
     * @param light             Light component for rendering calls
     * @param overlay           Overlay component for rendering calls
     */
    fun renderStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val renderMode = staffStack[staffRendererOverrideComponentType.get()]?.renderMode?.getOrNull() ?: mode

        when (renderMode) {
            ModelTransformationMode.GUI -> renderInventoryStaff(
                staffStack, renderMode, matrices, vertexConsumers, light, overlay
            )

            ModelTransformationMode.FIXED -> renderItemFrameStaff(
                staffStack, renderMode, matrices, vertexConsumers, light, overlay
            )

            else -> renderFullStaff(
                staffStack, renderMode, matrices, vertexConsumers, light, overlay
            )
        }
    }

    private fun renderFullStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push {
            translate(0.5f, 0.5f, 0.5f)

            // Head
            push {
                translate(0f, 16f / 16f, 0f)
                renderPart(staffStack, this, vertexConsumers, light, overlay, StaffRendererPartComponent.HEAD)

                // Item
                renderItem(staffStack, mode, this, light, overlay, vertexConsumers)
            }

            // Rod (top)
            push {
                translate(0f, 2f / 16f, 0f)
                renderPart(staffStack, this, vertexConsumers, light, overlay, StaffRendererPartComponent.ROD_TOP)
            }

            // Rod (bottom)
            push {
                translate(0f, -12f / 16f, 0f)
                renderPart(staffStack, this, vertexConsumers, light, overlay, StaffRendererPartComponent.ROD_BOTTOM)
            }
        }
    }

    private fun renderInventoryStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push {
            translate(0.5f, 0.5f, 0.5f)

            // Head
            push {
                renderPart(staffStack, this, vertexConsumers, light, overlay, StaffRendererPartComponent.HEAD)

                // Item
                renderItem(staffStack, mode, this, light, overlay, vertexConsumers)
            }
        }
    }

    private fun renderItemFrameStaff(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push {
            translate(0.5f, 0.5f, 0.5f)

            // Head
            push {
                translate(0f, 9f / 16f, 0f)
                renderPart(staffStack, this, vertexConsumers, light, overlay, StaffRendererPartComponent.HEAD)

                // Item
                renderItem(staffStack, mode, this, light, overlay, vertexConsumers)
            }

            // Rod (top)
            push {
                translate(0f, -5f / 16f, 0f)
                renderPart(staffStack, this, vertexConsumers, light, overlay, StaffRendererPartComponent.ROD_TOP)
            }
        }
    }

    private fun renderItem(
        staffStack: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        light: Int,
        overlay: Int,
        vertexConsumers: VertexConsumerProvider
    ) {
        matrices.push {
            safeGetModel(staffStack, StaffRendererPartComponent.ITEM).transformation.fixed.apply(false, this)

            val blockStateOverride = staffStack[staffRendererOverrideComponentType.get()]?.blockState?.getOrNull()
            if (blockStateOverride != null) {
                BlockStateStaffItemRenderer.renderBlockState(
                    blockStateOverride, matrices, vertexConsumers, light, overlay
                )
            } else {
                staffStack.itemStackInStaff?.let { itemInStaff ->
                    renderItem(staffStack, itemInStaff, mode, matrices, light, overlay, vertexConsumers)
                }
            }
        }
    }

    private fun renderItem(
        staffStack: ItemStack,
        itemStackInStaff: ItemStack,
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        light: Int,
        overlay: Int,
        vertexConsumers: VertexConsumerProvider
    ) {
        val staffItemRenderer = IStaffItemRenderer[Registries.ITEM.getId(itemStackInStaff.item)]
        if (staffItemRenderer != null) {
            staffItemRenderer.renderItemInStaff(staffStack, mode, matrices, vertexConsumers, light, overlay)
        } else {
            val itemRenderer = MinecraftClient.getInstance().itemRenderer
            val model = MinecraftClient.getInstance().bakedModelManager.missingModel

            itemRenderer.renderItem(
                itemStackInStaff, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, model
            )
        }
    }

    private fun renderPart(
        staffStack: ItemStack,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        part: StaffRendererPartComponent
    ) {
        val itemRenderer = MinecraftClient.getInstance().itemRenderer
        val model = safeGetModel(staffStack, part)

        itemRenderer.renderItem(
            staffStack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, model
        )
    }

    private fun safeGetModel(staffStack: ItemStack, part: StaffRendererPartComponent): BakedModel {
        val itemRenderer = MinecraftClient.getInstance().itemRenderer

        staffStack[staffRendererPartComponentType.get()] = part
        val model = itemRenderer.getModel(staffStack, null, null, 0)
        staffStack.remove(staffRendererPartComponentType.get())

        // Prevent StackOverflowError if an override is missing
        return if (!model.isBuiltin) model
        else MinecraftClient.getInstance().bakedModelManager.missingModel
    }
}
