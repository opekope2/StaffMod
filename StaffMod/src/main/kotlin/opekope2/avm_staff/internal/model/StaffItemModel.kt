/*
 * AvM Staff Mod
 * Copyright (c) 2023-2024 opekope2
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

package opekope2.avm_staff.internal.model

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.client.render.model.BakedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.math.random.Random
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.util.handlerOfItem
import opekope2.avm_staff.util.isItemInStaff
import opekope2.avm_staff.util.itemInStaff
import java.util.function.Supplier

@Environment(EnvType.CLIENT)
class StaffItemModel(model: BakedModel) : ForwardingBakedModel() {
    init {
        super.wrapped = model
    }

    override fun emitItemQuads(stack: ItemStack, randomSupplier: Supplier<Random>, context: RenderContext) {
        super.emitItemQuads(stack, randomSupplier, context)

        if (!stack.isItemInStaff) return

        val itemStack = stack.itemInStaff
        val handler: StaffItemHandler =
            if (itemStack == null) StaffItemHandler.EmptyStaffHandler
            else itemStack.handlerOfItem ?: StaffItemHandler.FallbackStaffHandler

        handler.staffItemRenderer.emitItemQuads(stack, randomSupplier, context)
    }

    override fun isVanillaAdapter() = false
}
