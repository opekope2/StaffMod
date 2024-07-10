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
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.block.BlockState
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.entity.LivingEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import opekope2.avm_staff.api.item.renderer.StaffRenderer
import java.util.*

/**
 * Data component to override the behavior of a [StaffRenderer].
 *
 * @param renderMode    The display transform of the model to use
 * @param isActive      The item should be treated as if it was [LivingEntity.getActiveItem]
 * @param blockState    The blocks state to render in the staff
 */
data class StaffRendererOverrideComponent(
    val renderMode: Optional<ModelTransformationMode>,
    val isActive: Optional<Boolean>,
    val blockState: Optional<BlockState>
) {
    private constructor(buf: RegistryByteBuf) : this(
        buf.readOptional {
            it.readEnumConstant(ModelTransformationMode::class.java)
        },
        buf.readOptional(PacketByteBuf::readBoolean),
        buf.readOptional {
            it.decodeAsJson(BlockState.CODEC)
        }
    )

    private fun encode(buf: RegistryByteBuf) {
        buf.writeOptional(renderMode, PacketByteBuf::writeEnumConstant)
        buf.writeOptional(isActive, PacketByteBuf::writeBoolean)
        buf.writeOptional(blockState) { buffer, state ->
            buffer.encodeAsJson(BlockState.CODEC, state)
        }
    }

    companion object {
        /**
         * [Codec] for [StaffRendererOverrideComponent].
         */
        @JvmField
        val CODEC: Codec<StaffRendererOverrideComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                ModelTransformationMode.CODEC.optionalFieldOf("renderMode")
                    .forGetter(StaffRendererOverrideComponent::renderMode),
                Codec.BOOL.optionalFieldOf("pointForward").forGetter(StaffRendererOverrideComponent::isActive),
                BlockState.CODEC.optionalFieldOf("blockState").forGetter(StaffRendererOverrideComponent::blockState)
            ).apply(instance, ::StaffRendererOverrideComponent)
        }

        /**
         * [PacketCodec] for [StaffRendererOverrideComponent].
         */
        @JvmField
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, StaffRendererOverrideComponent> =
            PacketCodec.of(StaffRendererOverrideComponent::encode, ::StaffRendererOverrideComponent)
    }
}
