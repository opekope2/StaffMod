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

package opekope2.avm_staff.internal.neoforge.item

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.neoforged.api.distmarker.Dist
import net.neoforged.neoforge.common.ItemAbilities
import net.neoforged.neoforge.common.ItemAbility
import net.neoforged.neoforge.common.extensions.IItemExtension
import opekope2.avm_staff.api.IStaffModClientPlatform
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.util.isStaff
import opekope2.avm_staff.util.staffHandlerOrFallback
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn
import java.util.function.Supplier

class NeoForgeStaffItem(settings: Settings, repairIngredientSupplier: Supplier<out Item>?) :
    StaffItem(settings, repairIngredientSupplier), IItemExtension {
    init {
        runWhenOn(Dist.CLIENT) { IStaffModClientPlatform.renderAsStaffModel(this) }
    }

    override fun canDisableShield(stack: ItemStack, shield: ItemStack, entity: LivingEntity, attacker: LivingEntity) =
        disablesShield(stack, attacker.entityWorld, attacker, Hand.MAIN_HAND) ||
                super<IItemExtension>.canDisableShield(stack, shield, entity, attacker)

    override fun isRepairable(arg: ItemStack) = true

    override fun onEntitySwing(stack: ItemStack, entity: LivingEntity) = !canSwingHand(
        stack,
        entity.entityWorld,
        entity,
        if (stack === entity.getStackInHand(Hand.MAIN_HAND)) Hand.MAIN_HAND
        else Hand.OFF_HAND
    )

    override fun canPerformAction(stack: ItemStack, itemAbility: ItemAbility) =
        itemAbility == ItemAbilities.SHIELD_BLOCK && stack.isStaff && (stack.item as StaffItem).getUseAction(stack) == UseAction.BLOCK

    override fun onLeftClickEntity(stack: ItemStack, player: PlayerEntity, entity: Entity) =
        attackEntity(stack, player.entityWorld, player, entity, Hand.MAIN_HAND).interruptsFurtherEvaluation()

    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        val oldHandler = oldStack.staffHandlerOrFallback
        val newHandler = newStack.staffHandlerOrFallback

        return if (oldHandler !== newHandler) true
        else oldHandler.allowReequipAnimation(oldStack, newStack, slotChanged)
    }
}
