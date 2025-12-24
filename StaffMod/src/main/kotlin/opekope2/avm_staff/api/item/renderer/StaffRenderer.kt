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
import opekope2.avm_staff.api.IStaffModClientPlatform
import opekope2.avm_staff.util.itemStackInStaff
import opekope2.avm_staff.util.mc
import opekope2.avm_staff.util.push
import opekope2.avm_staff.util.registryId

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
        fun StaffModelPart.render() {
            mc.itemRenderer.renderItem(
                staffStack,
                ModelTransformationMode.NONE,
                false,
                matrices,
                vertexConsumers,
                light,
                overlay,
                getModel(staffStack.item)
            )
        }

        matrices.push {
            when (mode) {
                ModelTransformationMode.GUI -> translate(8f / 16f, -8f / 16f, 8f / 16f)
                ModelTransformationMode.FIXED -> translate(8f / 16f, 1f / 16f, 8f / 16f)
                else -> translate(8f / 16f, 8f / 16f, 8f / 16f)
            }

            // Head
            push {
                translate(0f, 16f / 16f, 0f)
                StaffModelPart.HEAD.render()

                // Item
                renderItem(staffStack, mode, this, light, overlay, vertexConsumers)
            }

            if (mode != ModelTransformationMode.GUI) { // Inventory
                // Rod (top)
                push {
                    translate(0f, 2f / 16f, 0f)
                    StaffModelPart.ROD_TOP.render()
                }
            }

            if (mode != ModelTransformationMode.GUI && mode != ModelTransformationMode.FIXED) { // Inventory, item frame
                // Rod (bottom)
                push {
                    translate(0f, -12f / 16f, 0f)
                    StaffModelPart.ROD_BOTTOM.render()
                }
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
            staffStack.itemStackInStaff?.let { itemInStaff ->
                val staffItemRenderer =
                    StaffItemRenderer.REGISTRY[itemInStaff.item.registryId] ?: MissingModelStaffItemRenderer

                staffItemRenderer.renderItemInStaff(
                    staffStack,
                    StaffModelPart.ITEM_TRANSFORM.getModel(staffStack.item).transformation,
                    mode,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay
                )
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private enum class StaffModelPart(private val suffix: String) {
        HEAD("/head"),
        ITEM_TRANSFORM("/item_transform"),
        ROD_TOP("/rod_top"),
        ROD_BOTTOM("/rod_bottom");

        fun getModel(item: Item) = IStaffModClientPlatform.getStandaloneModel(
            item.registryId.withPrefixedPath("item/").withSuffixedPath(suffix)
        )
    }
}
