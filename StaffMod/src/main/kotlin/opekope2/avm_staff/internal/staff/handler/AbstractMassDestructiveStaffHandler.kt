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

package opekope2.avm_staff.internal.staff.handler

import dev.architectury.event.EventResult
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.destruction.BlockDestructionPredicate
import opekope2.avm_staff.util.destruction.IShapedBlockDestructionPredicate
import opekope2.avm_staff.util.destruction.destroyBox
import opekope2.avm_staff.util.dropcollector.ChunkedBlockDropCollector
import opekope2.avm_staff.util.dropcollector.IBlockDropCollector
import opekope2.avm_staff.util.dropcollector.NoOpBlockDropCollector
import opekope2.avm_staff.util.incrementItemUseStat
import opekope2.avm_staff.util.incrementStaffItemUseStat
import opekope2.avm_staff.util.isAttackCoolingDown
import opekope2.avm_staff.util.itemInStaff

internal abstract class AbstractMassDestructiveStaffHandler : StaffHandler() {
    override fun attackBlock(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: BlockPos,
        side: Direction,
        hand: Hand
    ): EventResult {
        if (world.isClient) return EventResult.pass()
        if (attacker is PlayerEntity && attacker.isAttackCoolingDown) return EventResult.pass()
        require(world is ServerWorld)

        val shapePredicate = createBlockDestructionShapePredicate(world, attacker, target)
        val dropCollector =
            if (attacker is PlayerEntity && attacker.abilities.creativeMode) NoOpBlockDropCollector()
            else createBlockDropCollector(shapePredicate)

        destroyBox(
            world,
            shapePredicate.volume,
            dropCollector,
            attacker,
            staffStack,
            createDestructionPredicate(shapePredicate)
        )
        dropCollector.dropAll(world)

        (attacker as? ServerPlayerEntity)?.incrementItemUseStat(staffStack.item)
        (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)

        // "Mismatch in destroy block pos" in server logs if I interrupt on server but not on client side. Nothing bad should happen, right?
        return EventResult.pass()
    }

    protected abstract fun createBlockDestructionShapePredicate(
        world: World,
        attacker: LivingEntity,
        target: BlockPos
    ): IShapedBlockDestructionPredicate

    protected abstract fun createDestructionPredicate(shapePredicate: IShapedBlockDestructionPredicate): BlockDestructionPredicate

    protected open fun createBlockDropCollector(shapePredicate: IShapedBlockDestructionPredicate): IBlockDropCollector =
        ChunkedBlockDropCollector(shapePredicate.volume, MAX_CHUNK_SIZE)

    companion object {
        const val MAX_CHUNK_SIZE = 3
    }
}
