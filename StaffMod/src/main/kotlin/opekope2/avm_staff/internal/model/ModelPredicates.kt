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

package opekope2.avm_staff.internal.model

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.item.ClampedModelPredicateProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.staff.StaffRendererPartComponent
import opekope2.avm_staff.api.staffRendererOverrideComponentType
import opekope2.avm_staff.api.staffRendererPartComponentType
import opekope2.avm_staff.util.MOD_ID

// ModelPredicateProviderRegistry.register is private in common project
@Environment(EnvType.CLIENT)
fun registerModelPredicateProviders(register: (Identifier, ClampedModelPredicateProvider) -> Unit) {
    register(Identifier(MOD_ID, "using_item")) { stack, _, entity, _ ->
        val isActiveOverride = stack[staffRendererOverrideComponentType.get()]?.isActive
        when {
            isActiveOverride == true -> 1f
            isActiveOverride == false -> 0f
            entity != null && entity.isUsingItem && ItemStack.areEqual(entity.activeItem, stack) -> 1f
            else -> 0f
        }
    }
    register(Identifier(MOD_ID, "head"), matchStaffRendererPart(StaffRendererPartComponent.HEAD))
    register(Identifier(MOD_ID, "item"), matchStaffRendererPart(StaffRendererPartComponent.ITEM))
    register(Identifier(MOD_ID, "rod_top"), matchStaffRendererPart(StaffRendererPartComponent.ROD_TOP))
    register(Identifier(MOD_ID, "rod_bottom"), matchStaffRendererPart(StaffRendererPartComponent.ROD_BOTTOM))
}

private fun matchStaffRendererPart(part: StaffRendererPartComponent) = ClampedModelPredicateProvider { stack, _, _, _ ->
    if (stack[staffRendererPartComponentType.get()] == part) 1f
    else 0f
}
