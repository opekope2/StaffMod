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

package opekope2.avm_staff.api.block

import net.minecraft.block.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

/**
 * A crown placed on the wall.
 */
class WallCrownBlock(settings: Settings) : HorizontalFacingBlock(settings) {
    init {
        defaultState = defaultState.with(FACING, Direction.NORTH)
    }

    override fun getCodec() = CODEC

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?) = false

    override fun getCullingShape(state: BlockState?, world: BlockView?, pos: BlockPos?): VoxelShape =
        VoxelShapes.empty()

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ) = when (state[FACING]) {
        Direction.SOUTH -> SOUTH_SHAPE
        Direction.WEST -> WEST_SHAPE
        Direction.EAST -> EAST_SHAPE
        else -> NORTH_SHAPE
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        for (direction in ctx.placementDirections) {
            if (!direction.axis.isHorizontal) continue
            val placementState = defaultState.with(WallSkullBlock.FACING, direction.opposite)
            if (ctx.world.getBlockState(ctx.blockPos.offset(direction)).canReplace(ctx)) continue
            return placementState
        }
        return null
    }

    override fun getTranslationKey(): String = asItem().translationKey

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    private companion object {
        private val CODEC = createCodec(::WallCrownBlock)
        private val NORTH_SHAPE = VoxelShapes.cuboid(4 / 16.0, 4 / 16.0, 1 - 12 / 16.0, 12 / 16.0, 12 / 16.0, 1.0)
        private val SOUTH_SHAPE = VoxelShapes.cuboid(4 / 16.0, 4 / 16.0, 0.0, 12 / 16.0, 12 / 16.0, 12 / 16.0)
        private val WEST_SHAPE = VoxelShapes.cuboid(1 - 12 / 16.0, 4 / 16.0, 4 / 16.0, 1.0, 12 / 16.0, 12 / 16.0)
        private val EAST_SHAPE = VoxelShapes.cuboid(0.0, 4 / 16.0, 4 / 16.0, 12 / 16.0, 12 / 16.0, 12 / 16.0)
    }
}
