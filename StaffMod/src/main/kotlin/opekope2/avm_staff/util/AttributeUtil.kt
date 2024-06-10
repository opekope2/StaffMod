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

@file: JvmName("AttributeUtil")

package opekope2.avm_staff.util

import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.item.Item

private const val PLAYER_BASE_ATTACK_DAMAGE = 1.0
private const val PLAYER_BASE_ATTACK_SPEED = 4.0

/**
 * Creates an [EntityAttributeModifier], which changes the attack damage to [totalAttackDamage] after subtracting the
 * player's base attack damage.
 *
 * @param totalAttackDamage The desired amount of damage in half hearts
 */
fun attackDamage(totalAttackDamage: Double): EntityAttributeModifier = EntityAttributeModifier(
    Item.ATTACK_DAMAGE_MODIFIER_ID,
    "Staff modifier",
    totalAttackDamage - PLAYER_BASE_ATTACK_DAMAGE,
    EntityAttributeModifier.Operation.ADD_VALUE
)

/**
 * Creates an [EntityAttributeModifier], which changes the attack speed to [totalAttackSpeed] after subtracting the
 * player's base attack speed.
 *
 * @param totalAttackSpeed  The desired attack speed in attack/second
 */
fun attackSpeed(totalAttackSpeed: Double): EntityAttributeModifier = EntityAttributeModifier(
    Item.ATTACK_SPEED_MODIFIER_ID,
    "Staff modifier",
    totalAttackSpeed - PLAYER_BASE_ATTACK_SPEED,
    EntityAttributeModifier.Operation.ADD_VALUE
)

/**
 * Creates an [EntityAttributeModifier], which changes the attack speed to the reciprocal of [totalEquipTime] after
 * subtracting the player's base attack speed.
 *
 * @param totalEquipTime    The desired equip time in seconds
 */
fun equipTime(totalEquipTime: Double): EntityAttributeModifier = attackSpeed(1.0 / totalEquipTime)

/**
 * Creates an [EntityAttributeModifier], which increases the interaction range by [additionalRange].
 *
 * @param additionalRange   The number of blocks to add to the interaction range
 */
fun interactionRange(additionalRange: Double) = EntityAttributeModifier(
    "Staff modifier",
    additionalRange,
    EntityAttributeModifier.Operation.ADD_VALUE
)
