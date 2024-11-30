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

package opekope2.avm_staff.api.entity.renderer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Blocks
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.mixin.ICakeBlockAccessor
import opekope2.avm_staff.util.push
import org.joml.Quaternionf

/**
 * Renderer of [CakeEntity].
 */
@Environment(EnvType.CLIENT)
class CakeEntityRenderer(context: EntityRendererFactory.Context) : EntityRenderer<CakeEntity>(context) {
    private val blockRenderManager = context.blockRenderManager

    init {
        shadowRadius = 0.5f
    }

    override fun render(
        cake: CakeEntity,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int
    ) {
        val cakeYaw = MathHelper.lerpAngleDegrees(tickDelta, cake.prevYaw, cake.yaw)
        val cakePitch = MathHelper.lerpAngleDegrees(tickDelta, cake.prevPitch, cake.pitch)

        matrices.push {
            translate(0f, cake.getDimensions(cake.pose).height / 2, 0f)
            multiply(Quaternionf().rotationYXZ(cakeYaw, cakePitch, 0f))
            translate(-.5f, NEGATIVE_HALF_CAKE_HEIGHT, -.5f)

            blockRenderManager.renderBlockAsEntity(CAKE_STATE, this, vertexConsumers, light, OverlayTexture.DEFAULT_UV)
        }

        super.render(cake, yaw, tickDelta, matrices, vertexConsumers, light)
    }

    override fun getTexture(cake: CakeEntity): Identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE

    private companion object {
        private val CAKE_STATE = Blocks.CAKE.defaultState
        private val NEGATIVE_HALF_CAKE_HEIGHT = ICakeBlockAccessor.bitesToShape()[0].boundingBox.lengthY.toFloat() / -2
    }
}
