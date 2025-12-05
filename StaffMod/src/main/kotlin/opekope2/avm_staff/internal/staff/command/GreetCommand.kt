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

package opekope2.avm_staff.internal.staff.command

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffCommand

internal data class GreetCommand(val format: String) : StaffCommand<GreetCommand.Args>() {
    override val type get() = TYPE
    override val argsCodec get() = ARGS_CODEC

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (user is ServerPlayerEntity) {
            val message = format.format(getArgs(staffStack).name)
            user.sendMessage(Text.literal(message))
        }
        return TypedActionResult.success(staffStack)
    }

    data class Args(val name: String) : IArgs

    companion object {
        @JvmField
        val ARGS_CODEC: MapCodec<Args> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(Args::name)
            ).apply(instance, ::Args)
        }

        @JvmField
        val MAP_CODEC: MapCodec<GreetCommand> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("format").forGetter(GreetCommand::format)
            ).apply(instance, ::GreetCommand)
        }

        @JvmField
        val TYPE = Type(MAP_CODEC)
    }
}
