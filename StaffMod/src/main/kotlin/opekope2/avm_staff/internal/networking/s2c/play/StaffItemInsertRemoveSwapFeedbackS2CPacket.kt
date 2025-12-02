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

package opekope2.avm_staff.internal.networking.s2c.play

import dev.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import net.minecraft.util.Identifier
import opekope2.avm_staff.internal.networking.IS2CPacket
import opekope2.avm_staff.internal.networking.PacketRegistrarAndReceiver
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.inGameHud

internal class StaffItemInsertRemoveSwapFeedbackS2CPacket(val success: Boolean, val feedback: Text?) : IS2CPacket {
    constructor(buf: PacketByteBuf) : this(
        buf.readBoolean(),
        buf.readNullable(TextCodecs.PACKET_CODEC::decode)
    )

    override fun getId() = payloadId

    override fun write(buf: PacketByteBuf) {
        buf.writeBoolean(success)
        buf.writeNullable(feedback, TextCodecs.PACKET_CODEC::encode)
    }

    companion object : PacketRegistrarAndReceiver<StaffItemInsertRemoveSwapFeedbackS2CPacket>(
        NetworkManager.s2c(),
        Identifier.of(MOD_ID, "staff_item_insert_remove_swap_feedback"),
        ::StaffItemInsertRemoveSwapFeedbackS2CPacket
    ) {
        override fun receive(
            packet: StaffItemInsertRemoveSwapFeedbackS2CPacket,
            context: NetworkManager.PacketContext
        ) {
            if (packet.success) context.player.resetLastAttackedTicks()
            if (packet.feedback != null) inGameHud.setOverlayMessage(packet.feedback, false)
        }
    }
}
