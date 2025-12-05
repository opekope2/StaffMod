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
import net.minecraft.util.Hand
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.networking.c2s.play.StaffItemInsertRemoveSwapC2SPacket
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.isStaff
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
internal object KeyBindingHandler : ClientTickEvent.Client {
    private val ADD_REMOVE_STAFF_ITEM = KeyBinding(
        "key.$MOD_ID.add_remove_staff_item",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.categories.$MOD_ID"
    )
    private val STAFF_MENU = KeyBinding(
        "key.$MOD_ID.staff_menu",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_Z,
        "key.categories.$MOD_ID"
    )

    init {
        ClientTickEvent.CLIENT_POST.register(this)
        KeyMappingRegistry.register(ADD_REMOVE_STAFF_ITEM)
        KeyMappingRegistry.register(STAFF_MENU)
    }

    override fun tick(client: MinecraftClient) {
        if (ADD_REMOVE_STAFF_ITEM.isPressed) handleAddRemoveStaffItem(client)
        if (STAFF_MENU.isPressed) handleStaffMenu(client)
    }

    private fun handleAddRemoveStaffItem(client: MinecraftClient) {
        ADD_REMOVE_STAFF_ITEM.isPressed = false

        if (client.player == null) return
        StaffItemInsertRemoveSwapC2SPacket().sendToServer()
    }

    private fun handleStaffMenu(client: MinecraftClient) {
        val player = client.player ?: return
        val hand = when {
            player.mainHandStack.isStaff -> Hand.MAIN_HAND
            player.offHandStack.isStaff -> Hand.OFF_HAND
            else -> return
        }
        val stack = player.getStackInHand(hand)
        (stack.item as StaffItem).openMenu(stack, player.world, player, hand)
    }
}
