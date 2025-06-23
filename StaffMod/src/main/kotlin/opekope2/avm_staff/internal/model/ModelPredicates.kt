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

package opekope2.avm_staff.internal.model

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.item.ClampedModelPredicateProvider
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.registry.RegistryBase
import opekope2.avm_staff.util.MOD_ID

@Environment(EnvType.CLIENT)
object ModelPredicates : RegistryBase<Identifier, ClampedModelPredicateProvider>() {
    init {
        register(Identifier.of(MOD_ID, "using_item")) { stack, _, entity, _ ->
            if (entity == null || !entity.isUsingItem) return@register 0f
            // When the item's components get changed server-side, Minecraft client is just janky with references
            when {
                ItemStack.areEqual(entity.activeItem, stack) -> 1f
                ItemStack.areEqual(entity.getStackInHand(entity.activeHand), stack) -> 1f
                else -> 0f
            }
        }
    }
}
