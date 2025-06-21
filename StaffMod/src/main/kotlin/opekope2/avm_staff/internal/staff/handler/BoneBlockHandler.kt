/*
 * AvM Staff Mod
 * Copyright (c) 2024-2025 opekope2
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

import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.BoneMealItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*

internal class BoneBlockHandler : StaffHandler() {
    override val attributeModifiers = StaffAttributeModifiersComponentBuilder()
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(5.0), AttributeModifierSlot.MAINHAND)
        .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(2.0), AttributeModifierSlot.MAINHAND)
        .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
        .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
        .build()

    override fun useOnBlock(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        target: BlockPos,
        side: Direction,
        hand: Hand
    ): ActionResult {
        if (!useOnFertilizable(world, user, target)) {
            return if (useOnGround(world, user, target, side)) {
                (user as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
                staffStack.damage(1, user, hand)
                ActionResult.SUCCESS
            } else ActionResult.PASS
        }

        val range =
            0.5 + staffStack.getEnchantmentLevel(Enchantments.EFFICIENCY, world.registryManager).coerceAtMost(5) / 2.0
        val rangeSquare = range * range
        var uses = 0

        for (pos in BlockPos.iterateOutwards(target, range.toInt(), range.toInt(), range.toInt())) {
            if (pos == target) continue
            val offset = pos - target
            if (offset.x * offset.x + offset.y * offset.y + offset.z * offset.z > rangeSquare) continue

            if (useOnFertilizable(world, user, pos)) uses++
        }

        (user as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
        staffStack.damage(uses, user, hand)

        return ActionResult.SUCCESS
    }

    private fun useOnFertilizable(world: World, user: LivingEntity, target: BlockPos): Boolean {
        if (!BoneMealItem.useOnFertilizable(Items.BONE_MEAL.defaultStack, world, target)) return false

        if (!world.isClient) {
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH)
            world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, target, 15)
        }
        return true
    }

    private fun useOnGround(world: World, user: LivingEntity, target: BlockPos, side: Direction): Boolean {
        val targetState = world.getBlockState(target)
        if (!targetState.isSideSolidFullSquare(world, target, side)) return false

        val neighborOnUsedSide = target.offset(side)
        if (!BoneMealItem.useOnGround(Items.BONE_MEAL.defaultStack, world, neighborOnUsedSide, side)) return false

        if (!world.isClient) {
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH)
            world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, neighborOnUsedSide, 15)
        }

        return true
    }
}
