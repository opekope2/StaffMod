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
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.util.InputUtil
import opekope2.avm_staff.internal.model.StaffItemModel
import opekope2.avm_staff.internal.networking.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.isItemInStaff
import org.lwjgl.glfw.GLFW

@Suppress("unused")
@Environment(EnvType.CLIENT)
object StaffModClient : ClientModInitializer {
    @JvmStatic
    val ADD_REMOVE_KEYBIND = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.avm_staff.add_remove_staff_block",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.avm_staff"
        )
    )

    override fun onInitializeClient() {
        ModelLoadingPlugin.register(::modelLoadingPlugin)
        ClientTickEvents.END_CLIENT_TICK.register(::handleStaffKeybind)
    }

    private fun modelLoadingPlugin(pluginContext: ModelLoadingPlugin.Context) {
        pluginContext.modifyModelAfterBake().register(::modifyModelAfterBake)
    }

    private fun modifyModelAfterBake(model: BakedModel?, context: ModelModifier.AfterBake.Context): BakedModel? {
        val id = context.id()

        return if (model == null || id.namespace != MOD_ID || id.path != "staff") model
        else StaffItemModel(model)
    }

    private fun handleStaffKeybind(client: MinecraftClient) {
        if (ADD_REMOVE_KEYBIND.isPressed) {
            ADD_REMOVE_KEYBIND.isPressed = false

            val player = client.player ?: return

            if (player.mainHandStack.isItemInStaff || player.offHandStack.isItemInStaff) {
                RemoveItemFromStaffC2SPacket().send()
            } else {
                AddItemToStaffC2SPacket().send()
            }
        }
    }
}
