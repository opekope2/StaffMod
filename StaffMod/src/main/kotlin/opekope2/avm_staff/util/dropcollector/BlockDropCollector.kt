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
 * A block drop collector, which merges compatible items passed in, and drops them at their (non-weighed) average
 * position.
 */
class BlockDropCollector : IBlockDropCollector {
    private val drops = mutableMapOf<ItemStack, MutableList<BlockDrop>>()

    override fun collect(
        world: ServerWorld,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        destroyer: Entity,
        tool: ItemStack
    ) {
        val droppedStacks = Block.getDroppedStacks(state, world, pos, blockEntity, destroyer, tool)

        for (stack in droppedStacks) {
            val reference = drops.keys.firstOrNull { ItemStack.areItemsAndComponentsEqual(it, stack) } ?: stack
            drops.getOrPut(reference, ::mutableListOf) += BlockDrop(world, pos, state, stack, tool)
        }
    }

    override fun dropAll(world: ServerWorld) {
        val sortedDrops = drops.mapValues { (_, droppers) -> droppers.sortedBy { it.pos } }

        for ((referenceItem, drops) in sortedDrops) {
            val max = referenceItem.maxCount
            val total = drops.sumOf { it.drop.count }
            val stacks = total / max
            val remaining = total % max
            val x = drops.map { it.pos.x }.average()
            val y = drops.map { it.pos.y }.average()
            val z = drops.map { it.pos.z }.average()
            val pos = BlockPos.ofFloored(x, y, z)

            repeat(stacks) {
                Block.dropStack(world, pos, referenceItem.copyWithCount(max))
            }
            if (remaining > 0) {
                Block.dropStack(world, pos, referenceItem.copyWithCount(remaining))
            }

            drops.forEach {
                it.state.onStacksDropped(it.world, pos, it.tool, true)
            }
        }

        drops.clear()
    }
}
