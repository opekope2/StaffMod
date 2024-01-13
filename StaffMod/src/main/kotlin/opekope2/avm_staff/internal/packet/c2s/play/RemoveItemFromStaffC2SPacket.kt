// Copyright (c) 2023-2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal.packet.c2s.play

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPacketHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import opekope2.avm_staff.internal.StaffMod.MOD_ID

class RemoveItemFromStaffC2SPacket() : FabricPacket {
    constructor(@Suppress("UNUSED_PARAMETER") buf: PacketByteBuf) : this()

    override fun write(buf: PacketByteBuf) {
    }

    override fun getType() = TYPE

    @Environment(EnvType.CLIENT)
    fun send() = ClientPlayNetworking.send(this)

    companion object {
        @JvmStatic
        val TYPE: PacketType<RemoveItemFromStaffC2SPacket> = PacketType.create(
            Identifier(MOD_ID, "remove_item_from_staff"),
            ::RemoveItemFromStaffC2SPacket
        )

        @JvmStatic
        fun registerGlobalReceiver(handler: PlayPacketHandler<RemoveItemFromStaffC2SPacket>): Boolean {
            return ServerPlayNetworking.registerGlobalReceiver(TYPE, handler)
        }
    }
}
