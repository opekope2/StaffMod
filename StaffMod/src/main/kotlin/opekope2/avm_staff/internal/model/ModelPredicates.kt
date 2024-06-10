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
import opekope2.avm_staff.api.staffRendererOverrideComponentType
import opekope2.avm_staff.util.MOD_ID

const val HEAD_SEED = -0b10011_10100_00001_00110_00110_00000
const val ITEM_SEED = -0b10011_10100_00001_00110_00110_00001
const val ROD_TOP_SEED = -0b10011_10100_00001_00110_00110_00010
const val ROD_BOTTOM_SEED = -0b10011_10100_00001_00110_00110_00011

// ModelPredicateProviderRegistry.register is private in common project
@Environment(EnvType.CLIENT)
fun registerModelPredicateProviders(register: (Identifier, ClampedModelPredicateProvider) -> Unit) {
    register(Identifier(MOD_ID, "using_item")) { stack, _, entity, _ ->
        if (entity != null && entity.isUsingItem && ItemStack.areEqual(entity.activeItem, stack)) 1f
        else if (stack[staffRendererOverrideComponentType.get()]?.isActive == true) 1f
        else 0f
    }
    register(Identifier(MOD_ID, "head")) { _, _, _, seed ->
        if (seed == HEAD_SEED) 1f
        else 0f
    }
    register(Identifier(MOD_ID, "item")) { _, _, _, seed ->
        if (seed == ITEM_SEED) 1f
        else 0f
    }
    register(Identifier(MOD_ID, "rod_top")) { _, _, _, seed ->
        if (seed == ROD_TOP_SEED) 1f
        else 0f
    }
    register(Identifier(MOD_ID, "rod_bottom")) { _, _, _, seed ->
        if (seed == ROD_BOTTOM_SEED) 1f
        else 0f
    }
}
