// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal.packet.s2c.configure

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerConfigurationNetworkHandler
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.config.Configuration
import opekope2.avm_staff.internal.StaffMod

class ConfigureStaffModS2CPacket(val configuration: Configuration) : FabricPacket {
    constructor(buf: PacketByteBuf) : this(buf.decodeAsJson(Configuration.CODEC))

    override fun write(buf: PacketByteBuf) {
        buf.encodeAsJson(Configuration.CODEC, configuration)
    }

    override fun getType() = TYPE

    fun send(handler: ServerConfigurationNetworkHandler) = ServerConfigurationNetworking.send(handler, this)

    companion object {
        @JvmStatic
        val TYPE: PacketType<ConfigureStaffModS2CPacket> = PacketType.create(
            Identifier(StaffMod.MOD_ID, "configure_staff_mod"),
            ::ConfigureStaffModS2CPacket
        )

        @JvmStatic
        @Environment(EnvType.CLIENT)
        fun registerGlobalReceiver(handler: ClientConfigurationNetworking.ConfigurationPacketHandler<ConfigureStaffModS2CPacket>): Boolean {
            return ClientConfigurationNetworking.registerGlobalReceiver(TYPE, handler)
        }
    }
}
