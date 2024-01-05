// Copyright (c) 2023-2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal.packet.c2s

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

class AddBlockToStaffC2SPacket() : FabricPacket {
    constructor(@Suppress("UNUSED_PARAMETER") buf: PacketByteBuf) : this()

    override fun write(buf: PacketByteBuf) {
    }

    override fun getType() = TYPE

    @Environment(EnvType.CLIENT)
    fun send() = ClientPlayNetworking.send(this)

    companion object {
        @JvmStatic
        val TYPE: PacketType<AddBlockToStaffC2SPacket> = PacketType.create(
            Identifier(MOD_ID, "add_block_to_staff"),
            ::AddBlockToStaffC2SPacket
        )

        @JvmStatic
        fun registerGlobalReceiver(handler: PlayPacketHandler<AddBlockToStaffC2SPacket>): Boolean {
            return ServerPlayNetworking.registerGlobalReceiver(TYPE, handler)
        }
    }
}
