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

package opekope2.avm_staff.api.item.renderer

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.MultipartBakedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.math.random.Random
import opekope2.avm_staff.util.plusAssign
import opekope2.avm_staff.util.timesAssign
import org.joml.Vector3f
import org.joml.Vector3fc
import java.util.function.Supplier

/**
 * A base class for rendering a [BlockState] at a specific position and scale.
 */
@Environment(EnvType.CLIENT)
abstract class StaffBlockStateRenderer : IStaffItemRenderer {
    private val transform: RenderContext.QuadTransform by lazy { Transformation(scale, offset) }

    /**
     * Gets the scale of the [block state][getBlockState] to render. Applied before [offset].
     */
    abstract val scale: Float

    /**
     * Gets the offset of the [block state][getBlockState] to render. Applied after [scale].
     */
    abstract val offset: Vector3fc

    /**
     * Gets the block state to render into the staff.
     */
    abstract fun getBlockState(staffStack: ItemStack): BlockState

    override fun emitItemQuads(staffStack: ItemStack, randomSupplier: Supplier<Random>, context: RenderContext) {
        context.pushTransform(transform)

        val state = getBlockState(staffStack)
        if (state.renderType != BlockRenderType.MODEL) {
            throwInvalidRenderTypeException(state)
        }

        val model: FabricBakedModel = BLOCK_RENDERING_MANAGER.getModel(state)
        if (model is MultipartBakedModel) {
            // Fabric API implements emitItemQuads in MultipartBakedModel as NO-OP, so I add back the "removed" code
            val emitter = context.emitter

            for (i in 0..ModelHelper.NULL_FACE_ID) {
                val cullFace = ModelHelper.faceFromIndex(i)
                val quads = model.getQuads(state, cullFace, randomSupplier.get())

                for (quad in quads) {
                    emitter.fromVanilla(quad, MATERIAL_STANDARD, cullFace).emit()
                }
            }
        } else {
            model.emitItemQuads(staffStack, randomSupplier, context)
        }

        context.popTransform()
    }

    /**
     * A [RenderContext.QuadTransform], which scales each quad to [scale], and then offsets them by [offset].
     *
     * @param scale     The size multiplier of each quad
     * @param offset    The position modifier of each quad
     */
    @Environment(EnvType.CLIENT)
    class Transformation(private val scale: Float, private val offset: Vector3fc) : RenderContext.QuadTransform {
        override fun transform(quadView: MutableQuadView): Boolean {
            val vec = Vector3f()
            for (i in 0 until 4) {
                val pos = quadView.copyPos(i, vec)
                pos *= scale
                pos += offset
                quadView.pos(i, pos)
            }
            return true
        }
    }

    @Environment(EnvType.CLIENT)
    companion object {
        private val MATERIAL_STANDARD by lazy { RendererAccess.INSTANCE.renderer!!.materialFinder().find() }
        private val BLOCK_RENDERING_MANAGER by lazy { MinecraftClient.getInstance().blockRenderManager }

        @Suppress("NOTHING_TO_INLINE")
        @JvmStatic
        protected inline fun throwInvalidRenderTypeException(state: BlockState): Nothing =
            throw UnsupportedOperationException(
                "Unsupported block state `$state`. Only `MODEL` block render type is supported, not `${state.renderType}`."
            )
    }
}
