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

package opekope2.avm_staff.internal.staff_item_handler

import net.minecraft.entity.LivingEntity
import net.minecraft.item.BoneMealItem
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.item.StaffItemHandler

class BoneBlockHandler : StaffItemHandler() {
    override fun useOnBlock(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        target: BlockPos,
        side: Direction,
        hand: Hand
    ): ActionResult {
        if (BoneMealItem.useOnFertilizable(staffStack.copy(), world, target)) {
            // TODO fertilize area when enchanted
            if (!world.isClient) {
                user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH) // FIXME user originally PlayerEntity
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, target, 0)
            }

            return ActionResult.success(world.isClient)
        }

        val targetState = world.getBlockState(target)
        if (!targetState.isSideSolidFullSquare(world, target, side)) return ActionResult.PASS

        val neighborOnUsedSide = target.offset(side)
        if (!BoneMealItem.useOnGround(staffStack.copy(), world, neighborOnUsedSide, side)) return ActionResult.PASS

        if (!world.isClient) {
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH) // FIXME user originally PlayerEntity
            world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, neighborOnUsedSide, 0)
        }

        return ActionResult.success(world.isClient)
    }
}