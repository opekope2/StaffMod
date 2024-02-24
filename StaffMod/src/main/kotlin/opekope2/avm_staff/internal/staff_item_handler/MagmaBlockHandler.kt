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

package opekope2.avm_staff.internal.staff_item_handler

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.SmallFireballEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.util.*

class MagmaBlockHandler : StaffItemHandler() {
    override val maxUseTime = 72000

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.pass(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if ((remainingUseTicks and 1) == 0) {
            shootFireball(world, user)
        }
    }

    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand): ActionResult {
        shootFireball(world, attacker)
        return ActionResult.SUCCESS
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): ActionResult {
        if (!world.isClient) {
            target.setOnFireFor(8) // TODO duration
        }

        return ActionResult.PASS
    }

    private fun shootFireball(world: World, user: LivingEntity) {
        if (!user.canUseStaff) return

        world.syncWorldEvent(user as? PlayerEntity, WorldEvents.BLAZE_SHOOTS, user.blockPos, 0)

        if (world.isClient) return

        val (x, y, z) = user.rotationVector
        world.spawnEntity(SmallFireballEntity(world, user, x, y, z).apply {
            setPosition(user.approximateStaffTipPosition)
        })
    }

    override fun getAttributeModifiers(
        staffStack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return if (slot == EquipmentSlot.MAINHAND) ATTRIBUTE_MODIFIERS
        else super.getAttributeModifiers(staffStack, slot)
    }

    private companion object {
        private val ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            attackDamage(5.0),
            EntityAttributes.GENERIC_ATTACK_SPEED,
            attackSpeed(2.0)
        )
    }
}
