// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

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
    "Weapon modifier",
    totalAttackDamage - PLAYER_BASE_ATTACK_DAMAGE,
    EntityAttributeModifier.Operation.ADDITION
)

/**
 * Creates an [EntityAttributeModifier], which changes the attack speed to [totalAttackSpeed] after subtracting the
 * player's base attack speed.
 *
 * @param totalAttackSpeed  The desired attack speed in attack/second
 */
fun attackSpeed(totalAttackSpeed: Double): EntityAttributeModifier = EntityAttributeModifier(
    Item.ATTACK_SPEED_MODIFIER_ID,
    "Weapon modifier",
    totalAttackSpeed - PLAYER_BASE_ATTACK_SPEED,
    EntityAttributeModifier.Operation.ADDITION
)

/**
 * Creates an [EntityAttributeModifier], which changes the attack speed to the reciprocal of [totalEquipTime] after
 * subtracting the player's base attack speed.
 *
 * @param totalEquipTime    The desired equip time in seconds
 */
fun equipTime(totalEquipTime: Double): EntityAttributeModifier = attackSpeed(1.0 / totalEquipTime)
