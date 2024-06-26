/*
 * AvM Staff Mod
 * Copyright (c) 2023-2024 opekope2
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

package opekope2.avm_staff.internal.fabric

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.avm_staff.api.staffsTag
import opekope2.avm_staff.util.contains
import opekope2.avm_staff.util.itemInStaff
import opekope2.avm_staff.util.staffHandler

@Suppress("unused")
object StaffMod : ModInitializer {
    override fun onInitialize() {
        AttackEntityCallback.EVENT.register(::handleEntityAttackEvent)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleEntityAttackEvent(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        target: Entity,
        hit: EntityHitResult?
    ): ActionResult {
        val itemStack = player.getStackInHand(hand)
        if (itemStack !in staffsTag) return ActionResult.PASS

        val itemInStaff = itemStack.itemInStaff ?: return ActionResult.PASS
        val staffHandler = itemInStaff.staffHandler ?: return ActionResult.PASS

        val result = staffHandler.attackEntity(itemStack, world, player, target, hand)
        return if (result.interruptsFurtherEvaluation()) ActionResult.SUCCESS
        else ActionResult.PASS
    }
}
