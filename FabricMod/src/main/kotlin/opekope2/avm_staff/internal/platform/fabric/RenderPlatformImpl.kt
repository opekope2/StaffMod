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

@file: JvmName("RenderPlatformImpl")

package opekope2.avm_staff.internal.platform.fabric

import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.texture.Sprite
import opekope2.avm_staff.api.render.IQuadBakerVertexConsumer
import java.util.function.Consumer

fun getQuadBakerVertexConsumer(sprite: Sprite, bakedQuadConsumer: Consumer<BakedQuad>): IQuadBakerVertexConsumer {
    return FabricQuadBakerVertexConsumer(sprite, bakedQuadConsumer)
}

private class FabricQuadBakerVertexConsumer(
    override var sprite: Sprite,
    private val bakedQuadConsumer: Consumer<BakedQuad>
) : IQuadBakerVertexConsumer {
    private val meshBuilder = renderer.meshBuilder()
    private var emitter = meshBuilder.emitter
    private var vertex = 0

    override fun vertex(x: Double, y: Double, z: Double): VertexConsumer {
        emitter.pos(vertex, x.toFloat(), y.toFloat(), z.toFloat())
        return this
    }

    override fun color(red: Int, green: Int, blue: Int, alpha: Int): VertexConsumer {
        emitter.color(vertex, (alpha shl 24) or (red shl 16) or (green shl 8) or blue)
        return this
    }

    override fun texture(u: Float, v: Float): VertexConsumer {
        emitter.uv(vertex, u, v)
        return this
    }

    override fun overlay(u: Int, v: Int): VertexConsumer {
        // Not supported by Fabric Rendering API
        return this
    }

    override fun light(u: Int, v: Int): VertexConsumer {
        emitter.lightmap(vertex, u or (v shl 16))
        return this
    }

    override fun normal(x: Float, y: Float, z: Float): VertexConsumer {
        emitter.normal(vertex, x, y, z)
        return this
    }

    override fun next() {
        if (++vertex != 4) return

        bakedQuadConsumer.accept(emitter.toBakedQuad(sprite))

        vertex = 0
        emitter = meshBuilder.emitter
    }

    override fun fixedColor(red: Int, green: Int, blue: Int, alpha: Int) {
    }

    override fun unfixColor() {
    }

    private companion object {
        private val renderer = RendererAccess.INSTANCE.renderer
            ?: throw IllegalStateException("No Fabric renderer is available")
    }
}
