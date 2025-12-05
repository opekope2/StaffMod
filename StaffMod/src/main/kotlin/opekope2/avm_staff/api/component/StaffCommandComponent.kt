/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

package opekope2.avm_staff.api.component

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.entry.RegistryEntry
import opekope2.avm_staff.api.component.StaffCommandComponent.Companion.CODEC
import opekope2.avm_staff.api.component.StaffCommandComponent.Companion.MAP_CODEC
import opekope2.avm_staff.api.staff.StaffCommand

/**
 * Data component to store the selected command and its arguments in a command block staff.
 *
 * @param TArgs     The type of the arguments
 * @param command   The dynamic registry entry of the staff command
 * @param args      The configured arguments of the staff command
 */
data class StaffCommandComponent<TArgs : StaffCommand.IArgs>(
    val command: RegistryEntry<StaffCommand<TArgs>>,
    val args: TArgs
) {
    companion object {
        /**
         * Key of [StaffCommandComponent.command] in the encoded representation.
         */
        const val COMMAND_KEY = "command"

        /**
         * Key of [StaffCommandComponent.args] in the encoded representation.
         */
        const val ARGS_KEY = "args"

        /**
         * Map codec for [StaffCommandComponent].
         */
        @JvmField
        val MAP_CODEC: MapCodec<StaffCommandComponent<*>> = StaffCommand.entryCodec<StaffCommand.IArgs>()
            .dispatchMap(COMMAND_KEY, StaffCommandComponent<StaffCommand.IArgs>::command) { command ->
                command.value().argsCodec.codec().fieldOf(ARGS_KEY).xmap(
                    { StaffCommandComponent(command, it) },
                    StaffCommandComponent<StaffCommand.IArgs>::args
                )
            } as MapCodec<StaffCommandComponent<*>>

        /**
         * Codec for [StaffCommandComponent].
         */
        @JvmField
        val CODEC: Codec<StaffCommandComponent<*>> = MAP_CODEC.codec()

        /**
         * Packet codec for [StaffCommandComponent].
         */
        @JvmField
        val PACKET_CODEC: PacketCodec<in RegistryByteBuf, StaffCommandComponent<*>> =
            PacketCodecs.unlimitedRegistryCodec(CODEC)

        /**
         * Typed [MAP_CODEC].
         */
        @JvmStatic
        fun <T : StaffCommand.IArgs> mapCodec() = MAP_CODEC as MapCodec<StaffCommandComponent<T>>

        /**
         * Typed [CODEC].
         */
        @JvmStatic
        fun <T : StaffCommand.IArgs> codec() = CODEC as Codec<StaffCommandComponent<T>>
    }
}
