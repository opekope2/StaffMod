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
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsage
import net.minecraft.item.ItemUsageContext
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*
import org.jetbrains.annotations.ApiStatus
import java.util.function.BiConsumer

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
        stack[DataComponentTypes.ATTRIBUTE_MODIFIERS] = stack.staffHandlerOrFallback.attributeModifiers
    }

    override fun getMaxUseTime(stack: ItemStack, user: LivingEntity): Int {
        return stack.staffHandlerOrFallback.getMaxUseTime(stack, user.entityWorld, user)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val staffStack = user.getStackInHand(hand)
        return staffStack.staffHandlerOrFallback.use(staffStack, world, user, hand)
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        stack.staffHandlerOrFallback.usageTick(stack, world, user, remainingUseTicks)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        stack.staffHandlerOrFallback.onStoppedUsing(stack, world, user, remainingUseTicks)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        return stack.staffHandlerOrFallback.finishUsing(stack, world, user)
    }

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) = true

    override fun postDamageEntity(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        stack.damage(1, attacker, Hand.MAIN_HAND)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        return context.stack.staffHandlerOrFallback.useOnBlock(
            context.stack,
            context.world,
            context.player ?: return ActionResult.PASS,
            context.blockPos,
            context.side,
            context.hand
        )
    }

    override fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand) =
        stack.staffHandlerOrFallback.useOnEntity(stack, user.world, user, entity, hand)

    /**
     * @see StaffHandler.attack
     */
    open fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) =
        staffStack.staffHandlerOrFallback.attack(staffStack, world, attacker, hand)

    /**
     * @see StaffHandler.attackBlock
     */
    open fun attackBlock(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: BlockPos, side: Direction, hand: Hand
    ) = staffStack.staffHandlerOrFallback.attackBlock(staffStack, world, attacker, target, side, hand)

    /**
     * @see StaffHandler.attackEntity
     */
    open fun attackEntity(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: Entity, hand: Hand
    ) = staffStack.staffHandlerOrFallback.attackEntity(staffStack, world, attacker, target, hand)

    /**
     * @see StaffHandler.canSwingHand
     */
    open fun canSwingHand(staffStack: ItemStack, world: World, holder: LivingEntity, hand: Hand) =
        staffStack.staffHandlerOrFallback.canSwingHand(staffStack, world, holder, hand)

    /**
     * @see StaffHandler.disablesShield
     */
    open fun disablesShield(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) =
        staffStack.staffHandlerOrFallback.disablesShield(staffStack, world, attacker, hand)

    override fun getName(stack: ItemStack): Text {
        val staffItem = stack.itemStackInStaff ?: return super.getName(stack)
        val staffItemText = Text.translatable(staffItem.item.getTranslationKey(staffItem))
        return Text.translatable(getTranslationKey(stack), staffItemText)
    }

    override fun getTranslationKey(stack: ItemStack): String =
        if (stack.isItemInStaff) "$translationKey.with_item"
        else super.getTranslationKey(stack)

    @ApiStatus.Internal
    fun breakIntoPieces(stack: ItemStack): BiConsumer<ServerWorld, ServerPlayerEntity> {
        val itemInStaff = stack.mutableItemStackInStaff
        val lootTableId = RegistryKey.of(RegistryKeys.LOOT_TABLE, registryId.withPrefixedPath("item_break/"))

        return BiConsumer { world, holder ->
            val lootTable = world.server.reloadableRegistries.getLootTable(lootTableId)
            val lootParameters = LootContextParameterSet.Builder(world).build(LootContextTypes.EMPTY)

            if (itemInStaff != null) giveOrDropLoot(holder, itemInStaff)
            lootTable.generateLoot(lootParameters, world.random.nextLong()) { loot ->
                giveOrDropLoot(holder, loot)
            }
        }
    }

    private fun giveOrDropLoot(player: ServerPlayerEntity, stack: ItemStack) {
        if (!player.inventory.insertStack(stack)) player.dropItem(stack, false)
    }
}
