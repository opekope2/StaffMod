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

import net.minecraft.block.BeehiveBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.TurtleEggBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import opekope2.avm_staff.util.destruction.destroyBox
import opekope2.avm_staff.util.dropcollector.IBlockDropCollector

/**
 * Replacement for [Block.afterBreak] during [destroyBox]. Mixed into [TurtleEggBlock] and [BeehiveBlock].
 * Implement this on a [Block] too if you override [Block.afterBreak].
 *
 * @see Block.afterBreak
 */
interface IBlockAfterDestroyHandler {
    /**
     * Called after a staff destroys a block. Staff mod collects the dropped items, increments stats, and applies
     * exhaustion, so these should not be called here.
     *
     * @param world         The world to destroy blocks in
     * @param pos           The position of the broken block
     * @param state         The block state broken
     * @param blockEntity   The block entity of the block if any
     * @param dropCollector The collector of the dropped items. Additional drops can be added here
     * @param destroyer     The entity destroying the blocks
     * @param tool          The tool [destroyer] destroys blocks with
     *
     * @see Block.afterBreak
     */
    @Suppress("FunctionName") // Mixin
    fun staffMod_afterBlockDestroyed(
        world: ServerWorld,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        dropCollector: IBlockDropCollector,
        destroyer: LivingEntity,
        tool: ItemStack
    )
}
