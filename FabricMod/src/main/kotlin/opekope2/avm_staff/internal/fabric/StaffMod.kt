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
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.avm_staff.internal.event_handler.attackBlock
import opekope2.avm_staff.internal.event_handler.attackEntity

@Suppress("unused")
object StaffMod : ModInitializer {
    override fun onInitialize() {
        AttackBlockCallback.EVENT.register(::attackBlock)
        AttackEntityCallback.EVENT.register(::handleEntityAttackEvent)
    }

    private fun handleEntityAttackEvent(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        target: Entity,
        @Suppress("UNUSED_PARAMETER") hit: EntityHitResult?
    ): ActionResult {
        if (world.isClient) return ActionResult.PASS // Handled with mixin

        return attackEntity(player, world, hand, target)
    }
}
