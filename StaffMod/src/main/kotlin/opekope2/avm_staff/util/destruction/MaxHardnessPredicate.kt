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
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

/**
 * A [BlockDestructionPredicate], which only allows breaking blocks with a maximum hardness of [maxHardness].
 * This disallows breaking unbreakable blocks.
 *
 * @param maxHardness   The maximum allowed hardness
 */
class MaxHardnessPredicate(private val maxHardness: Float) : BlockDestructionPredicate {
    /**
     * Creates a new [MaxHardnessPredicate] instance.
     *
     * @param block A reference block specifying the maximum hardness
     */
    constructor(block: Block) : this(block.hardness)

    override fun test(world: ServerWorld, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        val hardness = state.getHardness(world, pos)
        if (hardness == -1f) return false // Unbreakable
        return hardness <= maxHardness
    }
}
