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

package opekope2.avm_staff.util.destruction

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.OperatorBlock
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.stat.Stats
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.util.dropcollector.IBlockDropCollector
import java.util.function.BiPredicate

/**
 * A predicate specifying if a block should be broken.
 */
typealias BlockDestructionPredicate = BiPredicate<ServerWorld, BlockPos>

/**
 * Destroys a volume of blocks.
 *
 * @param world                 The world to destroy blocks in
 * @param box                   The volume to destroy blocks in
 * @param dropCollector         The collector of the dropped items
 * @param destroyer             The entity destroying the blocks
 * @param tool                  The tool [destroyer] destroys blocks with
 * @param destructionPredicate  A predicate specifying if a block should be broken
 */
fun destroyBox(
    world: ServerWorld,
    box: BlockBox,
    dropCollector: IBlockDropCollector,
    destroyer: Entity,
    tool: ItemStack,
    destructionPredicate: BlockDestructionPredicate
) {
    if (destroyer is ServerPlayerEntity) {
        destroyBox(world, box, dropCollector, destroyer, tool, destructionPredicate)
        return
    }

    for (pos in BlockPos.iterate(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
        if (!destructionPredicate.test(world, pos)) continue
        destroyBlock(world, pos, world.getBlockState(pos), dropCollector, destroyer, tool)
    }
}

private fun destroyBlock(
    world: ServerWorld,
    pos: BlockPos,
    state: BlockState,
    dropCollector: IBlockDropCollector,
    destroyer: Entity,
    tool: ItemStack
): Boolean {
    if (state.block is OperatorBlock) {
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL)
        return false
    }

    if (world.removeBlock(pos, false)) {
        state.block.onBroken(world, pos, state)
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state))
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(destroyer, state))
    } else return false

    dropCollector.collect(world, pos.toImmutable(), state, world.getBlockEntity(pos), destroyer, tool.copy())
    return true
}

/**
 * Destroys a volume of blocks.
 *
 * @param world                 The world to destroy blocks in
 * @param box                   The volume to destroy blocks in
 * @param dropCollector         The collector of the dropped items
 * @param destroyer             The player destroying the blocks
 * @param tool                  The tool [destroyer] destroys blocks with
 * @param destructionPredicate  A predicate specifying if a block should be broken
 */
fun destroyBox(
    world: ServerWorld,
    box: BlockBox,
    dropCollector: IBlockDropCollector,
    destroyer: ServerPlayerEntity,
    tool: ItemStack,
    destructionPredicate: BlockDestructionPredicate
) {
    var exhaustion = 0

    for (pos in BlockPos.iterate(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
        if (!destructionPredicate.test(world, pos)) continue
        if (destroyBlock(world, pos, world.getBlockState(pos), dropCollector, destroyer, tool)) exhaustion++
    }

    destroyer.addExhaustion(exhaustion * .005f)
}

private fun destroyBlock(
    world: ServerWorld,
    pos: BlockPos,
    state: BlockState,
    dropCollector: IBlockDropCollector,
    destroyer: ServerPlayerEntity,
    tool: ItemStack
): Boolean {
    if (state.block is OperatorBlock && !destroyer.isCreativeLevelTwoOp) {
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL)
        return false
    }
    if (destroyer.isBlockBreakingRestricted(world, pos, destroyer.interactionManager.gameMode)) return false

    val breakState = state.block.onBreak(world, pos, state, destroyer)
    val broke = world.removeBlock(pos, false)
    if (broke) {
        state.block.onBroken(world, pos, breakState)
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state))
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(destroyer, state))
    }

    tool.postMine(world, breakState, pos, destroyer)

    if (!broke) return false

    destroyer.incrementStat(Stats.MINED.getOrCreateStat(state.block))

    dropCollector.collect(world, pos.toImmutable(), breakState, world.getBlockEntity(pos), destroyer, tool.copy())
    return true
}
