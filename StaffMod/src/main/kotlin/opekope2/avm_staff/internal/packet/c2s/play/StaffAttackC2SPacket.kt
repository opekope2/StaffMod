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

package opekope2.avm_staff.internal.packet.c2s.play

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import opekope2.avm_staff.util.MOD_ID

class StaffAttackC2SPacket() : FabricPacket {
    constructor(buf: PacketByteBuf) : this()

    override fun write(buf: PacketByteBuf) {
    }

    override fun getType() = TYPE

    @Environment(EnvType.CLIENT)
    fun send() = ClientPlayNetworking.send(this)

    companion object {
        @JvmStatic
        val TYPE: PacketType<StaffAttackC2SPacket> = PacketType.create(
            Identifier(MOD_ID, "staff_attack"),
            ::StaffAttackC2SPacket
        )

        @JvmStatic
        fun registerGlobalReceiver(handler: ServerPlayNetworking.PlayPacketHandler<StaffAttackC2SPacket>): Boolean {
            return ServerPlayNetworking.registerGlobalReceiver(TYPE, handler)
        }
    }
}
