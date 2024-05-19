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

package opekope2.avm_staff.internal.networking.c2s.play

import dev.architectury.networking.NetworkChannel
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import opekope2.avm_staff.internal.networking.IPacket
import opekope2.avm_staff.internal.networking.PacketRegistrar
import opekope2.avm_staff.util.MOD_ID

class AttackC2SPacket(val hand: Hand) : IPacket {
    constructor(buf: PacketByteBuf) : this(buf.readEnumConstant(Hand::class.java))

    override fun write(buf: PacketByteBuf) {
        buf.writeEnumConstant(hand)
    }

    @Environment(EnvType.CLIENT)
    fun send() = channel.sendToServer(this)

    companion object : PacketRegistrar<AttackC2SPacket>(
        NetworkChannel.create(Identifier(MOD_ID, "attack")),
        AttackC2SPacket::class.java,
        ::AttackC2SPacket
    )
}
