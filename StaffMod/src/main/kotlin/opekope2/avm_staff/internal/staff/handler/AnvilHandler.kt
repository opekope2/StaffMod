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
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.mixin.IAnvilBlockAccessor
import opekope2.avm_staff.util.*
import kotlin.math.ceil
import kotlin.math.floor

internal class AnvilHandler(private val damagedItem: Item?) : StaffHandler() {
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
        return EventResult.interruptFalse()
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        if (world.isClient) return EventResult.interruptDefault()

        val fallDistance = ceil(attacker.fallDistance - 1f)
        if (fallDistance <= 0) return EventResult.interruptDefault()

        aoeAttack(world, attacker, target, fallDistance)
        world.syncWorldEvent(WorldEvents.SMASH_ATTACK, target.steppingPos, 750)
        attacker.fallDistance = 0f

        val broke = damageAnvil(staffStack, attacker, fallDistance)
        world.syncWorldEvent(
            if (broke) WorldEvents.ANVIL_DESTROYED
            else WorldEvents.ANVIL_LANDS,
            target.blockPos,
            0
        )

        return EventResult.interruptDefault()
    }

    private fun aoeAttack(world: World, attacker: LivingEntity, target: Entity, fallDistance: Float) {
        val cappedFallDistance = floor(fallDistance * IAnvilBlockAccessor.fallingBlockEntityDamageMultiplier())
            .coerceAtMost(IAnvilBlockAccessor.fallingBlockEntityMaxDamage().toFloat())
        val cooldownProgress =
            if (attacker is PlayerEntity) attacker.getAttackCooldownProgress(0f)
            else 1f
        val amount = cappedFallDistance * cooldownProgress
        val radius = cappedFallDistance / 20.0
        val box = Box(target.pos, target.pos).expand(radius)
        val predicate = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR
            .and(EntityPredicates.VALID_LIVING_ENTITY)
            .and(EntityPredicates.maxDistance(target.x, target.y, target.z, radius))

        world.getOtherEntities(attacker, box, predicate).forEach { entity ->
            entity.damage(world.damageSources.fallingAnvil(attacker), amount / (entity.distanceTo(target) + 1))
        }
    }

    private fun damageAnvil(staffStack: ItemStack, attacker: LivingEntity, fallDistance: Float): Boolean {
        if (attacker is PlayerEntity && !attacker.abilities.creativeMode && attacker.random.nextFloat() < 0.05f + fallDistance * 0.05f) {
            if (damagedItem == null) {
                staffStack.mutableItemStackInStaff = null
                return true
            }

            val currentItemStack = staffStack.itemStackInStaff!!
            staffStack.mutableItemStackInStaff =
                currentItemStack.copyComponentsToNewStack(damagedItem, currentItemStack.count)
        }

        return false
    }

    override fun canSwingHand(staffStack: ItemStack, world: World, holder: LivingEntity, hand: Hand): Boolean {
        return ceil(holder.fallDistance - 1f) > 0f
    }

    override fun disablesShield(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) = true

    private companion object {
        private val ATTRIBUTE_MODIFIERS = AttributeModifiersComponent.builder()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(40.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_ATTACK_SPEED, equipTime(4.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, anvilModifier(), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, anvilModifier(), AttributeModifierSlot.OFFHAND)
            .add(EntityAttributes.GENERIC_JUMP_STRENGTH, anvilModifier(), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_JUMP_STRENGTH, anvilModifier(), AttributeModifierSlot.OFFHAND)
            .build()

        private fun anvilModifier() = EntityAttributeModifier(
            Identifier.of(MOD_ID, "anvil_modifier"), -1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
    }
}
