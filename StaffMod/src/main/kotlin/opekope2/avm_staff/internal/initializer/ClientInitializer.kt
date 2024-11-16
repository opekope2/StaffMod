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

package opekope2.avm_staff.internal.initializer

import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.entity.EmptyEntityRenderer
import net.minecraft.client.render.entity.TntEntityRenderer
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.entity.renderer.CakeEntityRenderer
import opekope2.avm_staff.api.staff.StaffInfusionSmithingRecipeTextures
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.internal.event_handler.KeyBindingHandler
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.MOD_ID

@Environment(EnvType.CLIENT)
object ClientInitializer {
    init {
        KeyBindingHandler
        registerEntityRenderers()
        registerSmithingTableTextures()
    }

    private fun registerEntityRenderers() {
        EntityRendererRegistry.register(EntityTypes.IMPACT_TNT, ::TntEntityRenderer)
        EntityRendererRegistry.register(EntityTypes.CAKE, ::CakeEntityRenderer)
        EntityRendererRegistry.register(EntityTypes.CAMPFIRE_FLAME, ::EmptyEntityRenderer)
    }

    private fun registerSmithingTableTextures() {
        StaffInfusionSmithingRecipeTextures.register(
            Identifier.of(MOD_ID, "item/smithing_table/empty_slot_royal_staff"),
            ISmithingTemplateItemAccessor.emptySlotRedstoneDustTexture()
        )
    }
}
