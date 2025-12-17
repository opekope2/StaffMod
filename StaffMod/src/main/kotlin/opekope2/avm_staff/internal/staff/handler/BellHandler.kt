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
import net.minecraft.registry.tag.EntityTypeTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.component.StaffBellDataComponent
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.DataComponentTypes
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

    override fun beforeRemove(staffStack: ItemStack) {
        staffStack.remove(DataComponentTypes.bellData)
    }

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        ring(world, user)

        if (!world.isClient) BellBehavior.get(world).ring(staffStack, world, user)

        return TypedActionResult.success(staffStack)
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        ring(world, target)
        if (!world.isClient && target is LivingEntity) applyGlow(target)

        (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)

        return EventResult.pass()
    }

    override fun tick(staffStack: ItemStack, world: World, holder: Entity, slot: Int, selected: Boolean) {
        if (world.isClient) return
        if (holder !is LivingEntity) return

        val bellData = staffStack[DataComponentTypes.bellData] ?: return

        if (world.time >= bellData.applyGlowTime) {
            BellBehavior.get(world).applyGlow(staffStack, world, holder)
            staffStack.remove(DataComponentTypes.bellData)
        }
    }

    override fun allowComponentsUpdateAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        player: PlayerEntity,
        hand: Hand
    ) = false

    override fun allowReequipAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        selectedSlotChanged: Boolean
    ) = selectedSlotChanged

    private enum class BellBehavior(private val baseRadius: Double) {
        RAID(8.0) {
            override fun canApplyGlow(user: LivingEntity, target: LivingEntity) = target.type in EntityTypeTags.RAIDERS
        },
        ESP(2.0);

        private fun getRadius(efficiency: Int) = baseRadius * (1 + efficiency)

        private fun getHearingEntities(
            staffStack: ItemStack,
            world: World,
            user: LivingEntity
        ): MutableList<LivingEntity> {
            val efficiency = staffStack.getEnchantmentLevel(Enchantments.EFFICIENCY, world.registryManager)
            val radius = getRadius(efficiency)
            val box = Box(user.blockPos).expand(radius)
            val predicate = entityPredicate.and(EntityPredicates.maxDistance(user.x, user.y, user.z, radius))
            val hearingEntities = world.getEntitiesByClass(LivingEntity::class.java, box, predicate)
            return hearingEntities
        }

        fun ring(staffStack: ItemStack, world: World, user: LivingEntity) {
            val hearingEntities = getHearingEntities(staffStack, world, user)

            var resonate = false
            val hearDistance = IBellBlockEntityAccessor.maxBellHearingDistance().toDouble()
            for (entity in hearingEntities) {
                if (user.blockPos.isWithinDistance(entity.pos, hearDistance))
                    entity.brain.remember(MemoryModuleType.HEARD_BELL_TIME, world.time)
                resonate = resonate || canApplyGlow(user, entity)
            }
            if (resonate) {
                if (DataComponentTypes.bellData !in staffStack) world.playSound(
                    null,
                    user.blockPos,
                    SoundEvents.BLOCK_BELL_RESONATE,
                    user.soundCategory,
                    1.0f,
                    1.0f
                )
                staffStack[DataComponentTypes.bellData] = StaffBellDataComponent(world.time)
            }

            world.emitGameEvent(user, GameEvent.RESONATE_10, user.pos)
        }

        protected open fun canApplyGlow(user: LivingEntity, target: LivingEntity): Boolean {
            return true
        }

        fun applyGlow(staffStack: ItemStack, world: World, user: LivingEntity) {
            val hearingEntities = getHearingEntities(staffStack, world, user)
            hearingEntities.removeIf { !canApplyGlow(user, it) }

            for (entity in hearingEntities) applyGlow(entity)
        }

        companion object {
            private val entityPredicate: Predicate<Entity> = EntityPredicates.VALID_LIVING_ENTITY
                .and(EntityPredicates.EXCEPT_SPECTATOR)
                .and { !it.isRemoved }

            fun get(world: World) = if (world.gameRules.getBoolean(GameRules.BELL_STAFF_ESP)) ESP else RAID
        }
    }

    private companion object {
        private fun applyGlow(entity: LivingEntity) {
            entity.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, IBellBlockEntityAccessor.glowDuration()))
        }

        private fun ring(world: World, user: Entity) {
            world.playSound(user, user.blockPos, SoundEvents.BLOCK_BELL_USE, user.soundCategory, 2f, 1f)
        }
    }
}
