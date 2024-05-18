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
import opekope2.avm_staff.internal.model.HEAD_SEED
import opekope2.avm_staff.internal.model.ITEM_SEED
import opekope2.avm_staff.internal.model.ROD_BOTTOM_SEED
import opekope2.avm_staff.internal.model.ROD_TOP_SEED
import opekope2.avm_staff.util.itemInStaff
import opekope2.avm_staff.util.push

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
        when (mode) {
            ModelTransformationMode.GUI -> renderInventoryStaff(
                mode, staffStack, matrices, vertexConsumers, light, overlay
            )

            ModelTransformationMode.FIXED -> renderItemFrameStaff(
                mode, staffStack, matrices, vertexConsumers, light, overlay
            )

            else -> renderFullStaff(
                mode, staffStack, matrices, vertexConsumers, light, overlay
            )
        }
    }

    private fun renderFullStaff(
        mode: ModelTransformationMode,
        staffStack: ItemStack,
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
                renderPart(staffStack, this, vertexConsumers, light, overlay, HEAD_SEED)

                // Item
                staffStack.itemInStaff?.let { itemInStaff ->
                    renderItem(mode, this, staffStack, itemInStaff, light, overlay, vertexConsumers)
                }
            }

            // Rod (top)
            push {
                translate(0f, 2f / 16f, 0f)
                renderPart(staffStack, this, vertexConsumers, light, overlay, ROD_TOP_SEED)
            }

            // Rod (bottom)
            push {
                translate(0f, -12f / 16f, 0f)
                renderPart(staffStack, this, vertexConsumers, light, overlay, ROD_BOTTOM_SEED)
            }
        }
    }

    private fun renderInventoryStaff(
        mode: ModelTransformationMode,
        staffStack: ItemStack,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push {
            translate(0.5f, 0.5f, 0.5f)

            // Head
            push {
                renderPart(staffStack, this, vertexConsumers, light, overlay, HEAD_SEED)

                // Item
                staffStack.itemInStaff?.let { itemInStaff ->
                    renderItem(mode, this, staffStack, itemInStaff, light, overlay, vertexConsumers)
                }
            }
        }
    }

    private fun renderItemFrameStaff(
        mode: ModelTransformationMode,
        staffStack: ItemStack,
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
                renderPart(staffStack, this, vertexConsumers, light, overlay, HEAD_SEED)

                // Item
                staffStack.itemInStaff?.let { itemInStaff ->
                    renderItem(mode, this, staffStack, itemInStaff, light, overlay, vertexConsumers)
                }
            }

            // Rod (top)
            push {
                translate(0f, -5f / 16f, 0f)
                renderPart(staffStack, this, vertexConsumers, light, overlay, ROD_TOP_SEED)
            }
        }
    }

    private fun renderItem(
        mode: ModelTransformationMode,
        matrices: MatrixStack,
        staffStack: ItemStack,
        itemStackInStaff: ItemStack,
        light: Int,
        overlay: Int,
        vertexConsumers: VertexConsumerProvider
    ) {
        matrices.push {
            safeGetModel(staffStack, ITEM_SEED).transformation.fixed.apply(false, this)

            val staffItemRenderer = IStaffItemRenderer[Registries.ITEM.getId(itemStackInStaff.item)]
            if (staffItemRenderer != null) {
                staffItemRenderer.renderItemInStaff(staffStack, mode, this, vertexConsumers, light, overlay)
            } else {
                val itemRenderer = MinecraftClient.getInstance().itemRenderer

                val model = MinecraftClient.getInstance().bakedModelManager.missingModel
                itemRenderer.renderItem(itemStackInStaff, mode, false, this, vertexConsumers, light, overlay, model)
            }
        }
    }

    private fun renderPart(
        staffStack: ItemStack,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        partSeed: Int
    ) {
        val itemRenderer = MinecraftClient.getInstance().itemRenderer

        val model = safeGetModel(staffStack, partSeed)

        itemRenderer.renderItem(
            staffStack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, model
        )
    }

    private fun safeGetModel(staffStack: ItemStack, partSeed: Int): BakedModel {
        val itemRenderer = MinecraftClient.getInstance().itemRenderer

        val model = itemRenderer.getModel(staffStack, null, null, partSeed)

        // Prevent StackOverflowError if an override is missing
        return if (!model.isBuiltin) model
        else MinecraftClient.getInstance().bakedModelManager.missingModel
    }
}
