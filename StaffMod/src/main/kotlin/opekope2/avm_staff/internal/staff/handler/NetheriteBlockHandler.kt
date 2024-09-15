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
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.math.*
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*
import opekope2.avm_staff.util.destruction.InTruncatedPyramidPredicate
import opekope2.avm_staff.util.destruction.MaxHardnessPredicate
import opekope2.avm_staff.util.destruction.destroyBox
import opekope2.avm_staff.util.dropcollector.ChunkedBlockDropCollector
import opekope2.avm_staff.util.dropcollector.NoOpBlockDropCollector

class NetheriteBlockHandler : StaffHandler() {
    override val attributeModifiers: AttributeModifiersComponent
        get() = ATTRIBUTE_MODIFIERS

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        if (world.isClient) return EventResult.pass()
        if (attacker is PlayerEntity && attacker.isAttackCoolingDown) return EventResult.pass()
        require(world is ServerWorld)

        val knockbackMultiplier = 1.0 -
                if (target !is LivingEntity) 0.0
                else target.attributes.getValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)

        val knockbackSpeed = attacker.rotationVector.multiply(3.0, 1.5, 3.0) * knockbackMultiplier
        val hitPos = target.pos.add(0.0, target.type.height / 2.0, 0.0)
        val right = attacker.getRotationVector(0f, MathHelper.wrapDegrees(attacker.yaw + 90f)).normalize()
        val pyramidPredicate = InTruncatedPyramidPredicate(
            hitPos - attacker.rotationVector * 4.0,
            Vec2f(4f, 4f),
            hitPos + attacker.rotationVector * 14.0 * knockbackMultiplier,
            Vec2f(14f, 14f) * knockbackMultiplier.toFloat(),
            right
        )
        val dropCollector =
            if (attacker is PlayerEntity && attacker.abilities.creativeMode) NoOpBlockDropCollector()
            else ChunkedBlockDropCollector(pyramidPredicate.volume, MAX_CHUNK_SIZE)

        destroyBox(
            world,
            pyramidPredicate.volume,
            dropCollector,
            attacker,
            staffStack,
            maxNetheriteHard.and(pyramidPredicate)
        )
        dropCollector.dropAll(world)

        attacker.addVelocity(attacker.rotationVector * -0.5)
        attacker.velocityModified = true

        target.addVelocity(knockbackSpeed)
        target.isOnGround = false

        world.syncWorldEvent(WorldEvents.SMASH_ATTACK, target.steppingPos, 750)

        return EventResult.pass()
    }

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

        destroyBox(world, frontMidBackBox, dropCollector, attacker, staffStack, maxNetheriteHard)
        destroyBox(world, topBox, dropCollector, attacker, staffStack, maxNetheriteHard)
        destroyBox(world, bottomBox, dropCollector, attacker, staffStack, maxNetheriteHard)
        destroyBox(world, leftBox, dropCollector, attacker, staffStack, maxNetheriteHard)
        destroyBox(world, rightBox, dropCollector, attacker, staffStack, maxNetheriteHard)

        dropCollector.dropAll(world)

        // "Mismatch in destroy block pos" in server logs if I interrupt on server but not on client side. Nothing bad should happen, right?
        return EventResult.pass()
    }

    private companion object {
        private val ATTRIBUTE_MODIFIERS = StaffAttributeModifiersComponentBuilder()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(20.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(1.0), AttributeModifierSlot.MAINHAND)
            .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
            .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
            .build()

        private const val MAX_CHUNK_SIZE = 3

        private val maxNetheriteHard = MaxHardnessPredicate(Blocks.NETHERITE_BLOCK)
    }
}
