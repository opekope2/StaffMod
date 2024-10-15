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

package opekope2.avm_staff.internal.networking

import dev.architectury.networking.NetworkManager
import dev.architectury.platform.Platform
import dev.architectury.utils.Env
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

internal abstract class PacketRegistrar<TPacket : IPacket>(
    private val side: NetworkManager.Side,
    id: Identifier,
    packetConstructor: (PacketByteBuf) -> TPacket
) {
    protected val payloadId = CustomPayload.Id<TPacket>(id)
    private val codec = PacketCodec.of(IPacket::write, packetConstructor)

    fun registerReceiver(receiver: NetworkManager.NetworkReceiver<TPacket>) {
        if (side == NetworkManager.Side.S2C && Platform.getEnvironment() == Env.SERVER) {
            NetworkManager.registerS2CPayloadType(payloadId, codec)
        } else {
            NetworkManager.registerReceiver(side, payloadId, codec, receiver)
        }
    }
}
