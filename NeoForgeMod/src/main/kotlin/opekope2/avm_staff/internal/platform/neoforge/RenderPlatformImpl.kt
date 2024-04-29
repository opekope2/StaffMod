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

package opekope2.avm_staff.internal.platform.neoforge

import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.texture.Sprite
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer
import opekope2.avm_staff.api.render.IQuadBakerVertexConsumer
import java.util.function.Consumer

fun getQuadBakerVertexConsumer(sprite: Sprite, bakedQuadConsumer: Consumer<BakedQuad>): IQuadBakerVertexConsumer {
    return ForgeQuadBakerVertexConsumer(QuadBakingVertexConsumer(bakedQuadConsumer), sprite)
}

private class ForgeQuadBakerVertexConsumer(private val wrapped: QuadBakingVertexConsumer, sprite: Sprite) :
    VertexConsumer by wrapped, IQuadBakerVertexConsumer {
    override var sprite: Sprite = sprite
        set(value) {
            field = value
            wrapped.setSprite(value)
        }

    init {
        this.sprite = sprite // Call the setter instead of setting the field
    }

    override fun overlay(u: Int, v: Int): VertexConsumer {
        // Not supported by Fabric Rendering API, override as NO-OP for parity
        return this
    }
}
