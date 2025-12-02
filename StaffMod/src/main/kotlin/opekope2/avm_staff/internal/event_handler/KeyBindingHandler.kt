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

package opekope2.avm_staff.internal.event_handler

import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import opekope2.avm_staff.internal.networking.c2s.play.StaffItemInsertRemoveSwapC2SPacket
import opekope2.avm_staff.util.MOD_ID
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
internal object KeyBindingHandler : ClientTickEvent.Client {
    private val ADD_REMOVE_STAFF_ITEM = KeyBinding(
        "key.$MOD_ID.add_remove_staff_item",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.categories.$MOD_ID"
    )

    init {
        ClientTickEvent.CLIENT_POST.register(this)
        KeyMappingRegistry.register(ADD_REMOVE_STAFF_ITEM)
    }

    override fun tick(client: MinecraftClient) {
        if (!ADD_REMOVE_STAFF_ITEM.isPressed) return
        ADD_REMOVE_STAFF_ITEM.isPressed = false

        if (client.player == null) return
        StaffItemInsertRemoveSwapC2SPacket().sendToServer()
    }
}
