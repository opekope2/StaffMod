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

package opekope2.avm_staff.api.component

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.component.ComponentType
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import opekope2.avm_staff.api.staff.StaffHandler

/**
 * [ItemStack] wrapper to make them compatible with [ComponentType]s.
 *
 * @param item  The item stored in this component. Must be [copied][ItemStack.copy] before modifying it
 */
class StaffItemComponent(val item: ItemStack) {
    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other is StaffItemComponent -> ItemStack.areEqual(item, other.item)
            else -> false
        }
    }

    override fun hashCode(): Int {
        return ItemStack.hashCode(item)
    }

    private fun validate(): DataResult<StaffItemComponent> {
        return if (item.item in StaffHandler.Registry) DataResult.success(this)
        else DataResult.error { "There is no staff handler registered for item ${item.item}" }
    }

    companion object {
        /**
         * [Codec] for [StaffItemComponent].
         */
        @JvmField
        val CODEC: Codec<StaffItemComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.VALIDATED_CODEC.fieldOf("item").forGetter(StaffItemComponent::item)
            ).apply(instance, ::StaffItemComponent)
        }

        /**
         * Validated [Codec] for [StaffItemComponent]. This only allows [item]s registered in [StaffHandler.Registry].
         */
        @JvmField
        val VALIDATED_CODEC: Codec<StaffItemComponent> = CODEC.validate(StaffItemComponent::validate)

        /**
         * [PacketCodec] for [StaffItemComponent].
         */
        @JvmField
        val PACKET_CODEC: PacketCodec<in RegistryByteBuf, StaffItemComponent> = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, StaffItemComponent::item, ::StaffItemComponent
        )
    }
}
