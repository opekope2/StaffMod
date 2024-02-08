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

package opekope2.avm_staff.internal.fabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.client.render.model.BakedModel
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import opekope2.avm_staff.internal.event_handler.ADD_REMOVE_KEYBINDING
import opekope2.avm_staff.internal.fabric.item.model.StaffItemModel
import opekope2.avm_staff.internal.event_handler.handleKeyBindings
import opekope2.avm_staff.util.MOD_ID

@Suppress("unused")
@Environment(EnvType.CLIENT)
object StaffModClient : ClientModInitializer {
    override fun onInitializeClient() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register { entries ->
            entries.addAfter(Items.NETHERITE_HOE, StaffMod.staffItem)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register { entries ->
            entries.addAfter(Items.TRIDENT, StaffMod.staffItem)
        }

        ModelLoadingPlugin.register(::modelLoadingPlugin)

        KeyBindingHelper.registerKeyBinding(ADD_REMOVE_KEYBINDING)

        ClientTickEvents.END_CLIENT_TICK.register(::handleKeyBindings)
    }

    private fun modelLoadingPlugin(pluginContext: ModelLoadingPlugin.Context) {
        pluginContext.modifyModelAfterBake().register(::modifyModelAfterBake)
    }

    private fun modifyModelAfterBake(model: BakedModel?, context: ModelModifier.AfterBake.Context): BakedModel? {
        val id = context.id()

        return if (model == null || id.namespace != MOD_ID || id.path != "staff") model
        else StaffItemModel(model)
    }
}
