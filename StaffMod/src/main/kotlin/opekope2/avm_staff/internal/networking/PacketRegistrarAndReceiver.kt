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

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraftforge.event.network.CustomPayloadEvent
import net.minecraftforge.network.ChannelBuilder
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.SimpleChannel

internal abstract class PacketRegistrarAndReceiver<TPacket : IPacket<TPacket, TByteBuf>, TByteBuf : PacketByteBuf>(
    direction: NetworkDirection<TByteBuf>,
    id: Identifier,
    packetClass: Class<TPacket>,
    packetConstructor: (TByteBuf) -> TPacket,
) {
    protected val channel: SimpleChannel = ChannelBuilder.named(id).simpleChannel()

    init {
        channel.messageBuilder(packetClass, direction)
            .encoder(IPacket<TPacket, TByteBuf>::write)
            .decoder(packetConstructor)
            .consumerMainThread(::receive)
            .add()
    }

    abstract fun receive(packet: TPacket, context: CustomPayloadEvent.Context)
}
