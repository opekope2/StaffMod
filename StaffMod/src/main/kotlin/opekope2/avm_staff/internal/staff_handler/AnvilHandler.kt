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

package opekope2.avm_staff.internal.staff_handler

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import dev.architectury.event.EventResult
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.equipTime
import opekope2.avm_staff.util.itemStackInStaff
import java.util.*

class AnvilHandler(private val damagedStackFactory: () -> ItemStack?) : StaffHandler() {
    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        if (world.isClient) return EventResult.pass()

        var broke = false
        if (attacker is PlayerEntity && !attacker.abilities.creativeMode && attacker.random.nextFloat() < 0.12F) {
            val damagedStack = damagedStackFactory()
            staffStack.itemStackInStaff = damagedStack
            broke = damagedStack == null
        }

        world.syncWorldEvent(
            if (broke) WorldEvents.ANVIL_DESTROYED
            else WorldEvents.ANVIL_LANDS,
            target.blockPos,
            0
        )

        return EventResult.pass()
    }

    override fun disablesShield() = true

    override fun getAttributeModifiers(
        staffStack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return when (slot) {
            EquipmentSlot.MAINHAND -> MAIN_HAND_ATTRIBUTE_MODIFIERS
            EquipmentSlot.OFFHAND -> OFF_HAND_ATTRIBUTE_MODIFIERS
            else -> super.getAttributeModifiers(staffStack, slot)
        }
    }

    private companion object {
        private val MAIN_HAND_ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            attackDamage(40.0),
            EntityAttributes.GENERIC_ATTACK_SPEED,
            equipTime(4.0),
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            EntityAttributeModifier(
                UUID.fromString("c0374b4f-d600-4b6a-9984-3ee35d37750d"),
                "Weapon modifier",
                -1.0,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            )
        )

        private val OFF_HAND_ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            EntityAttributeModifier(
                UUID.fromString("c0374b4f-d600-4b6a-9984-3ee35d37750e"),
                "Weapon modifier",
                -1.0,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            )
        )
    }
}
