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

package opekope2.avm_staff.internal.event_handler

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.event.TickEvent.ClientTickEvent
import opekope2.avm_staff.internal.networking.c2s.play.InsertItemIntoStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.InsertItemIntoStaffC2SPacket.Companion.tryInsertItemIntoStaff
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket.Companion.tryRemoveItemFromStaff
import opekope2.avm_staff.util.MOD_ID
import org.lwjgl.glfw.GLFW
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

@OnlyIn(Dist.CLIENT)
internal object KeyBindingHandler {
    private val ADD_REMOVE_STAFF_ITEM = KeyBinding(
        "key.$MOD_ID.add_remove_staff_item",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.categories.$MOD_ID"
    )

    init {
        FORGE_BUS.addListener(::register)
        FORGE_BUS.addListener(::tick)
    }

    private fun register(event: RegisterKeyMappingsEvent) {
        event.register(ADD_REMOVE_STAFF_ITEM)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun tick(event: ClientTickEvent.Post) {
        val client = MinecraftClient.getInstance()
        if (!ADD_REMOVE_STAFF_ITEM.isPressed) return
        ADD_REMOVE_STAFF_ITEM.isPressed = false

        val player = client.player ?: return

        if (!player.tryInsertItemIntoStaff(KeyBindingHandler::sendInsertPacket)) {
            player.tryRemoveItemFromStaff(KeyBindingHandler::sendRemovePacket)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun sendRemovePacket(player: PlayerEntity, staffStack: ItemStack, targetSlot: Int) {
        RemoveItemFromStaffC2SPacket().sendToServer()
        player.resetLastAttackedTicks()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun sendInsertPacket(player: PlayerEntity, staffStack: ItemStack, itemStackToAdd: ItemStack) {
        InsertItemIntoStaffC2SPacket().sendToServer()
        player.resetLastAttackedTicks()
    }
}
