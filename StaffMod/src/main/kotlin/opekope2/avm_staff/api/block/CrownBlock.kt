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

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RotationPropertyHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

/**
 * A crown placed on the ground.
 */
class CrownBlock(settings: Settings) : Block(settings) {
    init {
        defaultState = defaultState.with(Properties.ROTATION, 0)
    }

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?) = false

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState =
        state.with(Properties.ROTATION, rotation.rotate(state[Properties.ROTATION], MAX_ROTATIONS))

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
        state.with(Properties.ROTATION, mirror.mirror(state[Properties.ROTATION], MAX_ROTATIONS))

    override fun getCullingShape(state: BlockState?, world: BlockView?, pos: BlockPos?): VoxelShape =
        VoxelShapes.empty()

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ) = SHAPE

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState =
        defaultState.with(Properties.ROTATION, RotationPropertyHelper.fromYaw(ctx.playerYaw))

    override fun getTranslationKey(): String {
        return super.getTranslationKey()
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.ROTATION)
    }

    private companion object {
        val SHAPE: VoxelShape = VoxelShapes.cuboid(4 / 16.0, 0.0, 4 / 16.0, 12 / 16.0, 12 / 16.0, 12 / 16.0)
        val MAX_ROTATIONS = RotationPropertyHelper.getMax() + 1
    }
}
