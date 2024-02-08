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

import net.minecraft.block.Blocks
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.thrown.SnowballEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.model.ReloadableSingleBakedModelProvider
import opekope2.avm_staff.util.TRANSFORM_INTO_STAFF
import opekope2.avm_staff.util.getTransformedModel

class SnowBlockHandler : StaffItemHandler() {
    override val maxUseTime = 72000

    override val itemModelProvider = ReloadableSingleBakedModelProvider {
        Blocks.SNOW_BLOCK.defaultState.getTransformedModel(TRANSFORM_INTO_STAFF)
    }

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.pass(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        throwSnowball(world, user)
    }

    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand): ActionResult {
        throwSnowball(world, attacker)
        return ActionResult.SUCCESS
    }

    private fun throwSnowball(world: World, user: LivingEntity) {
        world.playSound(
            user,
            user.blockPos,
            SoundEvents.ENTITY_SNOWBALL_THROW,
            user.soundCategory,
            0.5f,
            0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        )

        if (world.isClient) return
        world.spawnEntity(SnowballEntity(world, user).apply {
            // TODO speed
            setVelocity(user, user.pitch, user.yaw, 0f, 4f, 1f)
        })
    }
}
