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

@file: JvmName("ModelUtil")

package opekope2.avm_staff.util

import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.BasicBakedModel
import net.minecraft.client.render.model.json.Transformation
import net.minecraft.client.texture.MissingSprite
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import opekope2.avm_staff.internal.platform.getQuadBakerVertexConsumer
import org.joml.Vector3f
import java.util.function.Consumer

/**
 * The transform of an item in the default position of the staff.
 */
@JvmField
val TRANSFORM_INTO_STAFF = Transformation(
    Vector3f(),
    Vector3f((16f - 7f) / 16f / 2f, 22f / 16f, (16f - 7f) / 16f / 2f),
    Vector3f(7f / 16f)
)

/**
 * Gets the missing sprite from the block atlas.
 */
val missingSprite: Sprite
    get() {
        val atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
        return atlas.apply(MissingSprite.getMissingSpriteId())
    }

private val BlockState.model
    get() = MinecraftClient.getInstance().blockRenderManager.getModel(this)

/**
 * Creates a transformed model based on the model of the block state.
 *
 * @param transformation    The transformation to apply to the model
 */
fun BlockState.getTransformedModel(transformation: Transformation): BakedModel =
    model.transform(this, transformation)

private val cullFaces = arrayOf(*Direction.values(), null)

/**
 * Creates a new transformed model.
 *
 * @param blockState        Passed to [BakedModel.getQuads]
 * @param transformation    The transformation to apply to the model
 */
fun BakedModel.transform(blockState: BlockState?, transformation: Transformation): BakedModel {
    val matrices = MatrixStack()
    transformation.apply(false, matrices)
    val random = Random.create()

    val quads = mutableMapOf<Direction?, MutableList<BakedQuad>>()
    for (cullFace in cullFaces) {
        val cullQuads = quads.getOrPut(cullFace, ::mutableListOf)
        val vertexConsumer = getQuadBakerVertexConsumer(missingSprite, cullQuads::add)

        random.setSeed(42L)
        for (quad in getQuads(blockState, cullFace, random)) {
            vertexConsumer.sprite = quad.sprite
            vertexConsumer.quad(
                matrices.peek(),
                quad,
                1f,
                1f,
                1f,
                LightmapTextureManager.pack(blockState?.luminance ?: 0, 0),
                0 // Overlay is ignored
            )
        }
    }

    return BasicBakedModel(
        quads.remove(null)!!,
        quads,
        useAmbientOcclusion(),
        isSideLit,
        hasDepth(),
        particleSprite,
        this.transformation,
        overrides
    )
}

/**
 * Renders the model to [BakedQuad]s.
 *
 * @param sprite            The sprite of the model part
 * @param luminance         The luminance of the model. Must be in range `[0,15]`
 * @param transformation    The transformation to apply to the model part while rendering
 */
@JvmOverloads
fun ModelPart.getBakedQuads(
    sprite: Sprite,
    transformation: Transformation = Transformation.IDENTITY,
    luminance: Int = 0
): MutableList<BakedQuad> {
    val quads = mutableListOf<BakedQuad>()
    getBakedQuads(quads::add, sprite, transformation, luminance)
    return quads
}

/**
 * Renders the model to [BakedQuad]s.
 *
 * @param bakedQuadConsumer The consumer of the output quads
 * @param sprite            The sprite of the model part
 * @param transformation    The transformation to apply to the model part while rendering
 * @param luminance         The luminance of the model. Must be in range `[0,15]`
 */
@JvmOverloads
fun ModelPart.getBakedQuads(
    bakedQuadConsumer: Consumer<BakedQuad>,
    sprite: Sprite,
    transformation: Transformation = Transformation.IDENTITY,
    luminance: Int = 0
) {
    val vertexConsumer = sprite.getTextureSpecificVertexConsumer(getQuadBakerVertexConsumer(sprite, bakedQuadConsumer))
    val matrices = MatrixStack()
    transformation.apply(false, matrices)
    render(matrices, vertexConsumer, LightmapTextureManager.pack(luminance, 0), 0) // Overlay is ignored
}
