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
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.stat.Stats
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.block.IBlockAfterDestroyHandler
import opekope2.avm_staff.internal.networking.s2c.play.MassDestructionS2CPacket
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
    destroyer: LivingEntity,
    tool: ItemStack,
    destructionPredicate: BlockDestructionPredicate
) {
    var exhaustion = 0
    val destroyerPlayer = destroyer as? ServerPlayerEntity
    val destroyedBlocks = mutableListOf<BlockPos>()
    val destroyedBlockStates = mutableListOf<Int>()

    for (pos in BlockPos.iterate(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
        if (!destructionPredicate.test(world, pos)) continue

        val state = world.getBlockState(pos)
        val blockDestroyed =
            if (destroyerPlayer != null) destroyBlock(world, pos, state, dropCollector, destroyerPlayer, tool)
            else destroyBlock(world, pos, state, dropCollector, destroyer, tool)
        if (!blockDestroyed) continue

        exhaustion++
        destroyedBlocks += pos.toImmutable()
        destroyedBlockStates += Block.getRawIdFromState(state)
    }

    destroyerPlayer?.addExhaustion(exhaustion * .005f)
    if (destroyedBlocks.isNotEmpty()) {
        MassDestructionS2CPacket(destroyedBlocks, destroyedBlockStates)
            .sendToAround(world.server, destroyer.world.registryKey)
    }
}

private fun destroyBlock(
    world: ServerWorld,
    pos: BlockPos,
    state: BlockState,
    dropCollector: IBlockDropCollector,
    destroyer: LivingEntity,
    tool: ItemStack
): Boolean {
    val block = state.block
    val blockEntity = world.getBlockEntity(pos)
    if (block is OperatorBlock) {
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL)
        return false
    }

    if (world.removeBlock(pos, false)) {
        block.onBroken(world, pos, state)
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(destroyer, state))
    } else return false

    dropCollector.collect(world, pos.toImmutable(), state, blockEntity, destroyer, tool.copy())
    if (block is IBlockAfterDestroyHandler) {
        block.staffMod_afterBlockDestroyed(
            world,
            pos.toImmutable(),
            state,
            blockEntity,
            dropCollector,
            destroyer,
            tool.copy()
        )
    }

    return true
}

private fun destroyBlock(
    world: ServerWorld,
    pos: BlockPos,
    state: BlockState,
    dropCollector: IBlockDropCollector,
    destroyer: ServerPlayerEntity,
    tool: ItemStack
): Boolean {
    val block = state.block
    val blockEntity = world.getBlockEntity(pos)
    if (block is OperatorBlock && !destroyer.isCreativeLevelTwoOp) {
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL)
        return false
    }
    if (destroyer.isBlockBreakingRestricted(world, pos, destroyer.interactionManager.gameMode)) return false

    val breakState = block.onBreak(world, pos, state, destroyer)
    val broke = world.removeBlock(pos, false)
    if (broke) {
        block.onBroken(world, pos, breakState)
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(destroyer, state))
    }

    tool.postMine(world, breakState, pos, destroyer)

    if (!broke) return false

    destroyer.incrementStat(Stats.MINED.getOrCreateStat(block))
    dropCollector.collect(world, pos.toImmutable(), breakState, blockEntity, destroyer, tool.copy())
    if (block is IBlockAfterDestroyHandler) {
        block.staffMod_afterBlockDestroyed(
            world,
            pos.toImmutable(),
            state,
            blockEntity,
            dropCollector,
            destroyer,
            tool.copy()
        )
    }

    return true
}
