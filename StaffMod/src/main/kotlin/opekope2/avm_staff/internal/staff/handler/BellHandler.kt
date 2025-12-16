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

import dev.architectury.event.EventResult
import net.minecraft.SharedConstants
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.GameRules
import opekope2.avm_staff.mixin.IBellBlockEntityAccessor
import opekope2.avm_staff.util.*
import java.util.function.Predicate

internal class BellHandler : StaffHandler() {
    override val attributeModifiers = StaffAttributeModifiersComponentBuilder()
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(8.0), AttributeModifierSlot.MAINHAND)
        .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(1.5), AttributeModifierSlot.MAINHAND)
        .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
        .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
        .build()

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        world.playSound(user, user.blockPos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2f, 1f)

        if (!world.isClient) BellBehavior.get(world).use(staffStack, world, user)

        return TypedActionResult.success(staffStack)
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        world.playSound(
            target as? PlayerEntity,
            target.blockPos,
            SoundEvents.BLOCK_BELL_USE,
            attacker.soundCategory,
            2f,
            1f
        )
        if (!world.isClient && target is LivingEntity) BellBehavior.ESP.applyGlow(attacker, world, target)

        (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)

        return EventResult.pass()
    }

    private enum class BellBehavior {
        RAID {
            override fun getVolumeRadius(efficiency: Int) = 8.0 * (1 + efficiency)

            override fun applyGlow(user: LivingEntity, world: World, entity: LivingEntity) =
                if (!IBellBlockEntityAccessor.callIsRaiderEntity(user.blockPos, entity)) false
                else super.applyGlow(user, world, entity)
        },
        ESP {
            override fun getVolumeRadius(efficiency: Int) = 2.0 * (1 + efficiency)
        };

        abstract fun getVolumeRadius(efficiency: Int): Double

        open fun applyGlow(user: LivingEntity, world: World, entity: LivingEntity): Boolean {
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, 3 * SharedConstants.TICKS_PER_SECOND))
            return true
        }

        fun use(staffStack: ItemStack, world: World, user: LivingEntity) {
            val efficiency = staffStack.getEnchantmentLevel(Enchantments.EFFICIENCY, world.registryManager)
            val box = Box(user.blockPos).expand(getVolumeRadius(efficiency))
            val hearingEntities = world.getEntitiesByClass(LivingEntity::class.java, box, entityPredicate)

            var resonate = false
            for (entity in hearingEntities) {
                if (user.blockPos.isWithinDistance(entity.pos, 32.0))
                    entity.brain.remember(MemoryModuleType.HEARD_BELL_TIME, world.time)
                resonate = resonate or applyGlow(user, world, entity)
            }
            if (resonate) // TODO delay
                world.playSound(null, user.blockPos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f, 1.0f)

            world.emitGameEvent(user, GameEvent.RESONATE_10, user.pos)
        }

        companion object {
            private val entityPredicate: Predicate<Entity> = EntityPredicates.VALID_LIVING_ENTITY
                .and(EntityPredicates.EXCEPT_SPECTATOR)
                .and { !it.isRemoved }

            fun get(world: World) =
                if (world.gameRules.getBoolean(GameRules.BELL_STAFF_ESP)) ESP else RAID
        }
    }
}
