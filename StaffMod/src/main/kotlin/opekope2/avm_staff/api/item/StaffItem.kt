// Copyright (c) 2023-2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

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
    override fun getAttributeModifiers(
        stack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return stack.itemInStaff?.handlerOfItem?.getAttributeModifiers(stack, slot)
            ?: super.getAttributeModifiers(stack, slot)
    }

    override fun onItemEntityDestroyed(entity: ItemEntity) {
        val staffStack = entity.stack
        val staffItem = staffStack.itemInStaff ?: return
        ItemUsage.spawnItemContents(entity, Stream.of(staffItem))
    }

    override fun getMaxUseTime(stack: ItemStack): Int {
        return stack.itemInStaff?.handlerOfItem?.maxUseTime ?: super.getMaxUseTime(stack)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val staffStack = user.getStackInHand(hand)
        return staffStack.itemInStaff?.handlerOfItem?.use(staffStack, world, user, hand) ?: super.use(world, user, hand)
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        stack.itemInStaff?.handlerOfItem?.usageTick(stack, world, user, remainingUseTicks)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        stack.itemInStaff?.handlerOfItem?.onStoppedUsing(stack, world, user, remainingUseTicks)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        return stack.itemInStaff?.handlerOfItem?.finishUsing(stack, world, user)
            ?: super.finishUsing(stack, world, user)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        return context.stack.itemInStaff?.handlerOfItem?.useOnBlock(
            context.stack,
            context.world,
            context.player ?: return ActionResult.PASS,
            context.blockPos,
            context.side,
            context.hand
        ) ?: super.useOnBlock(context)
    }

    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        return stack.itemInStaff?.handlerOfItem?.useOnEntity(stack, user.world, user, entity, hand)
            ?: super.useOnEntity(stack, user, entity, hand)
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
