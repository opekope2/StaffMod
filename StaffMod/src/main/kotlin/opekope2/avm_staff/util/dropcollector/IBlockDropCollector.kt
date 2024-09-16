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

package opekope2.avm_staff.util.dropcollector

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

/**
 * Collects drops from destruction of blocks.
 */
interface IBlockDropCollector {
    /**
     * Stores the dropped items of a broken block.
     *
     * @param world         The world the block was broken in
     * @param pos           The position of the broken block
     * @param state         The block state broken
     * @param blockEntity   The block entity of the block if any
     * @param destroyer     The entity breaking the block
     * @param tool          The item used to break the block
     *
     * @see Block.getDroppedStacks
     */
    fun collect(
        world: ServerWorld,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        destroyer: Entity,
        tool: ItemStack
    ) = collect(pos, state, tool, Block.getDroppedStacks(state, world, pos, blockEntity, destroyer, tool))

    /**
     * Stores the dropped items of a broken block.
     *
     * @param pos           The position of the broken block
     * @param state         The block state broken
     * @param tool          The item used to break the block
     * @param droppedStacks The dropped items of the block
     */
    fun collect(pos: BlockPos, state: BlockState, tool: ItemStack, droppedStacks: List<ItemStack>)

    /**
     * Drops all collected items into the world.
     *
     * @param world The world to spawn items in
     *
     * @see Block.dropStack
     * @see BlockState.onStacksDropped
     */
    fun dropAll(world: ServerWorld)
}
