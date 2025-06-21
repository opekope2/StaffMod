/*
 * AvM Staff Mod
 * Copyright (c) 2023-2025 opekope2
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

import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.ItemUsageContext
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
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
abstract class StaffItem(settings: Settings, private val repairIngredientSupplier: RegistrySupplier<Item>?) :
    Item(settings) {
    override fun canRepair(stack: ItemStack, ingredient: ItemStack) =
        repairIngredientSupplier != null && ingredient.isOf(repairIngredientSupplier.get())

    override fun onItemEntityDestroyed(entity: ItemEntity) {
        val staffStack = entity.stack
        val staffItem = staffStack.mutableItemStackInStaff ?: return
        ItemUsage.spawnItemContents(entity, listOf(staffItem))
    }

    override fun postProcessComponents(stack: ItemStack) {
        stack[DataComponentTypes.ATTRIBUTE_MODIFIERS] = stack.itemInStaff.staffHandlerOrFallback.attributeModifiers
    }

    override fun getMaxUseTime(stack: ItemStack, user: LivingEntity): Int {
        return stack.itemInStaff.staffHandlerOrFallback.getMaxUseTime(stack, user.entityWorld, user)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val staffStack = user.getStackInHand(hand)
        return staffStack.itemInStaff.staffHandlerOrFallback.use(staffStack, world, user, hand)
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        stack.itemInStaff.staffHandlerOrFallback.usageTick(stack, world, user, remainingUseTicks)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        stack.itemInStaff.staffHandlerOrFallback.onStoppedUsing(stack, world, user, remainingUseTicks)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        return stack.itemInStaff.staffHandlerOrFallback.finishUsing(stack, world, user)
    }

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        damage(stack, 1, attacker, EquipmentSlot.MAINHAND)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        return context.stack.itemInStaff.staffHandlerOrFallback.useOnBlock(
            context.stack,
            context.world,
            context.player ?: return ActionResult.PASS,
            context.blockPos,
            context.side,
            context.hand
        )
    }

    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        return stack.itemInStaff.staffHandlerOrFallback.useOnEntity(stack, user.world, user, entity, hand)
    }

    /**
     * @see StaffHandler.attack
     */
    open fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) =
        staffStack.itemInStaff.staffHandlerOrFallback.attack(staffStack, world, attacker, hand)

    /**
     * @see StaffHandler.attackBlock
     */
    open fun attackBlock(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: BlockPos, side: Direction, hand: Hand
    ) = staffStack.itemInStaff.staffHandlerOrFallback.attackBlock(staffStack, world, attacker, target, side, hand)

    /**
     * @see StaffHandler.attackEntity
     */
    open fun attackEntity(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: Entity, hand: Hand
    ) = staffStack.itemInStaff.staffHandlerOrFallback.attackEntity(staffStack, world, attacker, target, hand)

    /**
     * @see StaffHandler.canSwingHand
     */
    open fun canSwingHand(staffStack: ItemStack, world: World, holder: LivingEntity, hand: Hand) =
        staffStack.itemInStaff.staffHandlerOrFallback.canSwingHand(staffStack, world, holder, hand)

    /**
     * @see StaffHandler.disablesShield
     */
    open fun disablesShield(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) =
        staffStack.itemInStaff.staffHandlerOrFallback.disablesShield(staffStack, world, attacker, hand)

    override fun getName(stack: ItemStack): Text {
        val staffItem = stack.itemStackInStaff ?: return super.getName(stack)
        val staffItemText = Text.translatable(staffItem.item.getTranslationKey(staffItem))
        return Text.translatable(getTranslationKey(stack), staffItemText)
    }

    override fun getTranslationKey(stack: ItemStack): String =
        if (stack.isItemInStaff) "$translationKey.with_item"
        else super.getTranslationKey(stack)

    /**
     * Damages the staff by a specified amount, and if it breaks, it falls into pieces.
     *
     * @param stack     The staff item to damage
     * @param amount    The amount of damage to deal
     * @param holder    The entity that holds the staff
     * @param slot      The slot the staff is in
     */
    fun damage(stack: ItemStack, amount: Int, holder: LivingEntity, slot: EquipmentSlot) {
        val world = holder.world as? ServerWorld ?: return
        val itemInStaff = stack.mutableItemStackInStaff

        stack.damage(amount, world, holder as? ServerPlayerEntity) {
            holder.sendEquipmentBreakStatus(it, slot)

            val lootTableId =
                RegistryKey.of(RegistryKeys.LOOT_TABLE, Registries.ITEM.getId(it).withPrefixedPath("item_break/"))
            val lootTable = world.server.reloadableRegistries.getLootTable(lootTableId)
            val lootParameters = LootContextParameterSet.Builder(world).build(LootContextTypes.EMPTY)

            if (itemInStaff != null) giveOrDropLoot(world, holder, itemInStaff)
            lootTable.generateLoot(lootParameters, world.random.nextLong()) { loot ->
                giveOrDropLoot(world, holder, loot)
            }
        }
    }

    private fun giveOrDropLoot(world: World, entity: Entity, stack: ItemStack) {
        if (entity is PlayerEntity && !entity.inventory.insertStack(stack)) entity.dropItem(stack, false)
        else ItemScatterer.spawn(world, entity.x, entity.y, entity.z, stack)
    }
}
