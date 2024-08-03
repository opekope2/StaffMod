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
import net.minecraft.block.Blocks
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*

class GoldBlockHandler : StaffHandler() {
    override val attributeModifiers: AttributeModifiersComponent
        get() = ATTRIBUTE_MODIFIERS

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

        val facing = attacker.facing.vector
        val (x, y, z) = facing
        val signedOne = x + y + z
        val perpendiculars = Vec3i(x - signedOne, y - signedOne, z - signedOne)

        for (pos in BlockPos.iterate(target.subtract(perpendiculars), target.add(perpendiculars).add(facing))) {
            if (!pos.isWithinDistance(target, 1.5)) continue // 3x3x3 except for corners

            val hardness = world.getBlockState(pos).getHardness(world, pos)
            if (hardness == -1f || hardness > Blocks.OBSIDIAN.hardness) continue

            world.breakBlock(pos, attacker !is PlayerEntity || !attacker.abilities.creativeMode, attacker)
        }

        return EventResult.interruptTrue()
    }

    private companion object {
        private val ATTRIBUTE_MODIFIERS = StaffAttributeModifiersComponentBuilder()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(14.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(1.0), AttributeModifierSlot.MAINHAND)
            .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
            .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
            .build()
    }
}
