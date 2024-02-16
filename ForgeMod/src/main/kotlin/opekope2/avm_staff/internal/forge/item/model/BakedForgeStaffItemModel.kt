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

package opekope2.avm_staff.internal.forge.item.model

import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.model.BakedModelWrapper
import net.minecraftforge.registries.ForgeRegistries
import opekope2.avm_staff.api.item.model.IStaffItemBakedModel
import opekope2.avm_staff.util.isItemInStaff
import opekope2.avm_staff.util.itemInStaff

@OnlyIn(Dist.CLIENT)
class BakedForgeStaffItemModel(
    original: BakedModel,
    private val itemModels: Map<Identifier, IStaffItemBakedModel>,
    private val missingModel: BakedModel
) : BakedModelWrapper<BakedModel>(original) {
    override fun applyTransform(
        cameraTransformType: ModelTransformationMode,
        poseStack: MatrixStack,
        applyLeftHandTransform: Boolean
    ): BakedModel {
        // BakedModelWrapper delegates this to the original model, which returns the original model instead of this
        super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform)
        return this
    }

    override fun getRenderPasses(stack: ItemStack, fabulous: Boolean): MutableList<BakedModel> {
        if (!stack.isItemInStaff) return mutableListOf(this)

        val itemStack = stack.itemInStaff ?: return mutableListOf(this)

        val model = itemModels[ForgeRegistries.ITEMS.getKey(itemStack.item)]?.getModel(stack) ?: missingModel
        return mutableListOf(this, model)
    }
}
