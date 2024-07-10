/*
 * AvM Staff Mod
 * Copyright (c) 2023-2024 opekope2
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

package opekope2.avm_staff.api.item

import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*

/**
 * Staff item dispatching functionality to [StaffHandler] without loader specific functionality.
 * Implementing loader-specific interfaces is highly recommended when extending the class to pass loader-specific
 * functionality to [StaffHandler].
 */
abstract class StaffItem(settings: Settings) : Item(settings) {
    override fun onItemEntityDestroyed(entity: ItemEntity) {
        val staffStack = entity.stack
        val staffItem = staffStack.mutableItemStackInStaff ?: return
        ItemUsage.spawnItemContents(entity, listOf(staffItem))
    }

    override fun postProcessComponents(stack: ItemStack) {
        stack[DataComponentTypes.ATTRIBUTE_MODIFIERS] = stack.itemInStaff.staffHandlerOrDefault.attributeModifiers
    }

    override fun getMaxUseTime(stack: ItemStack): Int {
        return stack.itemInStaff.staffHandlerOrDefault.maxUseTime
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val staffStack = user.getStackInHand(hand)
        return staffStack.itemInStaff.staffHandlerOrDefault.use(staffStack, world, user, hand)
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        stack.itemInStaff.staffHandlerOrDefault.usageTick(stack, world, user, remainingUseTicks)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        stack.itemInStaff.staffHandlerOrDefault.onStoppedUsing(stack, world, user, remainingUseTicks)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        return stack.itemInStaff.staffHandlerOrDefault.finishUsing(stack, world, user)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        return context.stack.itemInStaff.staffHandlerOrDefault.useOnBlock(
            context.stack,
            context.world,
            context.player ?: return ActionResult.PASS,
            context.blockPos,
            context.side,
            context.hand
        )
    }

    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        return stack.itemInStaff.staffHandlerOrDefault.useOnEntity(stack, user.world, user, entity, hand)
    }

    /**
     * @see StaffHandler.attack
     */
    open fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) =
        staffStack.itemInStaff.staffHandlerOrDefault.attack(staffStack, world, attacker, hand)

    /**
     * @see StaffHandler.attackBlock
     */
    open fun attackBlock(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: BlockPos, side: Direction, hand: Hand
    ) = staffStack.itemInStaff.staffHandlerOrDefault.attackBlock(staffStack, world, attacker, target, side, hand)

    /**
     * @see StaffHandler.attackEntity
     */
    open fun attackEntity(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: Entity, hand: Hand
    ) = staffStack.itemInStaff.staffHandlerOrDefault.attackEntity(staffStack, world, attacker, target, hand)

    /**
     * @see StaffHandler.canSwingHand
     */
    open fun canSwingHand(staffStack: ItemStack, world: World, holder: LivingEntity, hand: Hand) =
        staffStack.itemInStaff.staffHandlerOrDefault.canSwingHand(staffStack, world, holder, hand)

    /**
     * @see StaffHandler.disablesShield
     */
    open fun disablesShield(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) =
        staffStack.itemInStaff.staffHandlerOrDefault.disablesShield(staffStack, world, attacker, hand)

    override fun getName(stack: ItemStack): Text {
        val staffItem = stack.itemStackInStaff ?: return super.getName(stack)
        val staffItemText = Text.translatable(staffItem.item.getTranslationKey(staffItem))
        return Text.translatable(getTranslationKey(stack), staffItemText)
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return if (stack.isItemInStaff) "$translationKey.with_item"
        else super.getTranslationKey(stack)
    }
}
