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
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

/**
 * A block drop collector that drops the items without merging, just like vanilla Minecraft.
 */
class VanillaBlockDropCollector : IBlockDropCollector {
    private val drops = mutableListOf<BlockDrop>()
    private val brokenBlocks = mutableListOf<BrokenBlock>()

    override fun collect(pos: BlockPos, state: BlockState, tool: ItemStack, droppedStacks: List<ItemStack>) {
        for (stack in droppedStacks) {
            drops += BlockDrop(pos, stack)
        }

        brokenBlocks += BrokenBlock(pos, state, tool)
    }

    override fun dropAll(world: ServerWorld) {
        for ((pos, drop) in drops) {
            Block.dropStack(world, pos, drop)
        }
        for ((pos, state, tool) in brokenBlocks) {
            state.onStacksDropped(world, pos, tool, true)
        }

        drops.clear()
        brokenBlocks.clear()
    }
}
