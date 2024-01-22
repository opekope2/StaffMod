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

import com.google.common.collect.Multimap
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import opekope2.avm_staff.util.handlerOfItem
import opekope2.avm_staff.util.isItemInStaff
import opekope2.avm_staff.util.itemInStaff
import java.util.stream.Stream

/**
 * Staff item.
 */
class StaffItem(settings: Settings) : Item(settings) {
    private val ItemStack?.handlerOfItemOrDefault: StaffItemHandler
        get() = if (this == null) StaffItemHandler.EmptyStaffHandler
        else handlerOfItem ?: StaffItemHandler.FallbackStaffHandler

    override fun allowNbtUpdateAnimation(
        player: PlayerEntity,
        hand: Hand,
        oldStack: ItemStack,
        newStack: ItemStack
    ): Boolean {
        val oldHandler = oldStack.itemInStaff.handlerOfItemOrDefault
        val newHandler = newStack.itemInStaff.handlerOfItemOrDefault

        return if (oldHandler !== newHandler) true
        else oldHandler.allowNbtUpdateAnimation(oldStack, newStack, player, hand)
    }

    override fun getAttributeModifiers(
        stack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return stack.itemInStaff.handlerOfItemOrDefault.getAttributeModifiers(stack, slot)
    }

    override fun onItemEntityDestroyed(entity: ItemEntity) {
        val staffStack = entity.stack
        val staffItem = staffStack.itemInStaff ?: return
        ItemUsage.spawnItemContents(entity, Stream.of(staffItem))
    }

    override fun getMaxUseTime(stack: ItemStack): Int {
        return stack.itemInStaff.handlerOfItemOrDefault.maxUseTime
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val staffStack = user.getStackInHand(hand)
        return staffStack.itemInStaff.handlerOfItemOrDefault.use(staffStack, world, user, hand)
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        stack.itemInStaff.handlerOfItemOrDefault.usageTick(stack, world, user, remainingUseTicks)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        stack.itemInStaff.handlerOfItemOrDefault.onStoppedUsing(stack, world, user, remainingUseTicks)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        return stack.itemInStaff.handlerOfItemOrDefault.finishUsing(stack, world, user)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        return context.stack.itemInStaff.handlerOfItemOrDefault.useOnBlock(
            context.stack,
            context.world,
            context.player ?: return ActionResult.PASS,
            context.blockPos,
            context.side,
            context.hand
        )
    }

    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        return stack.itemInStaff.handlerOfItemOrDefault.useOnEntity(stack, user.world, user, entity, hand)
    }

    override fun getName(stack: ItemStack): Text {
        val staffItem = stack.itemInStaff ?: return super.getName(stack)
        val staffItemText = Text.translatable(staffItem.item.getTranslationKey(staffItem))
        return Text.translatable(getTranslationKey(stack), staffItemText)
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return if (stack.isItemInStaff) "$translationKey.with_item"
        else super.getTranslationKey(stack)
    }
}
