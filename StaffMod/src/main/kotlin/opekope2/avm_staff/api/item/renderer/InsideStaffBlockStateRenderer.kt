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
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import org.joml.Vector3f

/**
 * A base class for rendering a [BlockState] at the default position and scale (that is, "inside" the staff).
 */
@Environment(EnvType.CLIENT)
abstract class InsideStaffBlockStateRenderer : StaffBlockStateRenderer() {
    override val scale: Float
        get() = SCALE

    override val offset: Vector3f
        get() = OFFSET

    @Environment(EnvType.CLIENT)
    private class ConstantBlockStateRenderer(private val blockState: BlockState) :
        InsideStaffBlockStateRenderer() {
        init {
            if (blockState.renderType != BlockRenderType.MODEL) {
                throwInvalidRenderTypeException(blockState)
            }
        }

        override fun getBlockState(staffStack: ItemStack) = blockState
    }

    @Environment(EnvType.CLIENT)
    companion object {
        /**
         * Scale matching the staff's block holding space.
         */
        const val SCALE = 7f / 16f

        /**
         * Offset of the staff's block holding space.
         */
        @JvmField
        val OFFSET = Vector3f(9f / 16f / 2f, 22f / 16f, (16f - 7f) / 16f / 2f)

        /**
         * Returns an [InsideStaffBlockStateRenderer], which always renders the given [BlockState].
         *
         * @param blockState    The block state to render
         */
        @JvmStatic
        fun forBlockState(blockState: BlockState): InsideStaffBlockStateRenderer =
            ConstantBlockStateRenderer(blockState)

        /**
         * Returns an [InsideStaffBlockStateRenderer], which always renders the default block state of the given [BlockItem].
         *
         * @param blockItem The block item to render
         */
        @JvmStatic
        fun forBlockItem(blockItem: BlockItem): InsideStaffBlockStateRenderer =
            ConstantBlockStateRenderer(blockItem.block.defaultState)
    }
}
