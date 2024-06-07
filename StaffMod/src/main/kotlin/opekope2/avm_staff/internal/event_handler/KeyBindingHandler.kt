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

@file: Environment(EnvType.CLIENT)

package opekope2.avm_staff.internal.event_handler

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import opekope2.avm_staff.internal.networking.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.util.MOD_ID
import org.lwjgl.glfw.GLFW

val addRemoveStaffItemKeyBinding = KeyBinding(
    "key.$MOD_ID.add_remove_staff_item",
    InputUtil.Type.KEYSYM,
    GLFW.GLFW_KEY_R,
    "key.categories.$MOD_ID"
)

fun handleKeyBindings(client: MinecraftClient) {
    if (!addRemoveStaffItemKeyBinding.isPressed) return
    addRemoveStaffItemKeyBinding.isPressed = false

    val player = client.player ?: return

    player.canInsertIntoStaff().ifSuccess {
        AddItemToStaffC2SPacket().sendToServer()
        player.resetLastAttackedTicks()
    }.ifError {
        player.canRemoveFromStaff().ifSuccess {
            RemoveItemFromStaffC2SPacket().sendToServer()
            player.resetLastAttackedTicks()
        }
    }
}
