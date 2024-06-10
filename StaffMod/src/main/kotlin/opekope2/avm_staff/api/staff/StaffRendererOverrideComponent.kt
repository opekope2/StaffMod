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

package opekope2.avm_staff.api.staff

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.entity.LivingEntity
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import opekope2.avm_staff.api.item.renderer.StaffRenderer

/**
 * Data component to override the behavior of a [StaffRenderer].
 *
 * @param renderMode    The display transform of the model to use
 * @param isActive      The item should be treated as if it was [LivingEntity.getActiveItem]
 */
data class StaffRendererOverrideComponent(val renderMode: ModelTransformationMode, val isActive: Boolean) {
    private constructor(buf: RegistryByteBuf) : this(
        buf.readEnumConstant(ModelTransformationMode::class.java),
        buf.readBoolean()
    )

    private fun encode(buf: RegistryByteBuf) {
        buf.writeEnumConstant(renderMode)
        buf.writeBoolean(isActive)
    }

    companion object {
        @JvmField
        val CODEC: Codec<StaffRendererOverrideComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                ModelTransformationMode.CODEC.fieldOf("renderMode")
                    .forGetter(StaffRendererOverrideComponent::renderMode),
                Codec.BOOL.fieldOf("pointForward")
                    .forGetter(StaffRendererOverrideComponent::isActive)
            ).apply(instance, ::StaffRendererOverrideComponent)
        }

        @JvmField
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, StaffRendererOverrideComponent> =
            PacketCodec.of(StaffRendererOverrideComponent::encode, ::StaffRendererOverrideComponent)
    }
}
