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
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*
import opekope2.avm_staff.util.dropcollector.ChunkedBlockDropCollector
import opekope2.avm_staff.util.dropcollector.NoOpBlockDropCollector

class NetheriteBlockHandler : StaffHandler() {
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

        val forwardVector = attacker.facing.vector
        val upVector = attacker.cameraUp.vector
        val rightVector = forwardVector.crossProduct(upVector)

        val frontMidBackBox = BlockBox.create(
            target.add(rightVector * -5 + upVector * -2),
            target.add(rightVector * 5 + upVector * 8 + forwardVector * 11)
        )
        val topBox = BlockBox.create(
            target.add(rightVector * -5 + upVector * 9 + forwardVector),
            target.add(rightVector * 5 + upVector * 9 + forwardVector * 9)
        )
        val bottomBox = BlockBox.create(
            target.add(rightVector * -5 + upVector * -3 + forwardVector),
            target.add(rightVector * 5 + upVector * -3 + forwardVector * 9)
        )
        val leftBox = BlockBox.create(
            target.add(rightVector * -6 + upVector * 8 + forwardVector),
            target.add(rightVector * -6 + upVector * -2 + forwardVector * 9)
        )
        val rightBox = BlockBox.create(
            target.add(rightVector * 6 + upVector * 8 + forwardVector),
            target.add(rightVector * 6 + upVector * -2 + forwardVector * 9)
        )
        val dropCollector =
            if (attacker is PlayerEntity && attacker.abilities.creativeMode) NoOpBlockDropCollector()
            else ChunkedBlockDropCollector(
                BlockBox.encompass(listOf(frontMidBackBox, topBox, bottomBox, leftBox, rightBox)).get(),
                MAX_CHUNK_SIZE
            )

        world as ServerWorld
        destroyBox(world, frontMidBackBox, dropCollector, attacker, staffStack, Blocks.NETHERITE_BLOCK.hardness)
        destroyBox(world, topBox, dropCollector, attacker, staffStack, Blocks.NETHERITE_BLOCK.hardness)
        destroyBox(world, bottomBox, dropCollector, attacker, staffStack, Blocks.NETHERITE_BLOCK.hardness)
        destroyBox(world, leftBox, dropCollector, attacker, staffStack, Blocks.NETHERITE_BLOCK.hardness)
        destroyBox(world, rightBox, dropCollector, attacker, staffStack, Blocks.NETHERITE_BLOCK.hardness)

        dropCollector.dropAll(world)

        // "Mismatch in destroy block pos" in server logs if I interrupt on server but not on client side. Nothing bad should happen, right?
        return EventResult.pass()
    }

    private companion object {
        private var ATTRIBUTE_MODIFIERS = StaffAttributeModifiersComponentBuilder()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(20.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(1.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, knockbackModifier(), AttributeModifierSlot.MAINHAND)
            .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
            .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
            .build()

        private const val MAX_CHUNK_SIZE = 3

        private fun knockbackModifier() = EntityAttributeModifier(
            Identifier.of(MOD_ID, "netherite_block_modifier"), 5.0, EntityAttributeModifier.Operation.ADD_VALUE
        )
    }
}
