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

package opekope2.avm_staff.api.staff

import net.minecraft.block.BlockState
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import opekope2.avm_staff.api.block.IClearableBeforeInsertedIntoStaff
import opekope2.avm_staff.api.component.BlockPickupDataComponent
import opekope2.avm_staff.api.registry.RegistryBase
import opekope2.avm_staff.content.DataComponentTypes
import opekope2.avm_staff.content.Enchantments
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.util.*
import kotlin.math.roundToInt

/**
 * Provides functionality for a staff, when an item is inserted into it.
 */
abstract class StaffHandler : IItemHandler {
    /**
     * Gets the attribute modifiers (damage, attack speed, etc.) of the staff when held.
     */
    open val attributeModifiers: AttributeModifiersComponent
        get() = Fallback.ATTRIBUTE_MODIFIERS

    /**
     * Gets the action that happens when a player uses the staff.
     *
     * @param staffStack    The item stack used to perform the action
     * @see Item.getUseAction
     */
    open fun getUseAction(staffStack: ItemStack): UseAction = UseAction.NONE

    /**
     * Called on both the client and the server by Staff Mod on Fabric and Neo/Forge API on Neo/Forge, when an entity
     * holding a staff tries to swing its hand.
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [holder] is in
     * @param holder        The entity, which holds the staff
     * @param hand          The hand of the [holder], in which the [staff][staffStack] is
     * @return `true` to allow hand swing, `false` to cancel it
     */
    open fun canSwingHand(staffStack: ItemStack, world: World, holder: LivingEntity, hand: Hand) = true

    /**
     * Returns if attacking with the staff should disable the target's shield.
     *
     * @param staffStack    The item stack used for attacking
     * @param world         The world [attacker] is in
     * @param attacker      The entity, which attacks
     * @param hand          The hand of [attacker], in which the [staff][staffStack] is
     */
    open fun disablesShield(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) = false

    /**
     * Called on the client side by Fabric API, when the NBT of the held item gets updated.
     *
     * @param oldStaffStack The previous item stack
     * @param newStaffStack The updated item stack
     * @param player        The holder of [oldStaffStack]
     * @param hand          The hand of [player], in which the [old staff][oldStaffStack] is
     * @return `true` to play the update/equip animation, `false` to skip it
     */
    open fun allowComponentsUpdateAnimation(
        oldStaffStack: ItemStack, newStaffStack: ItemStack, player: PlayerEntity, hand: Hand
    ) = true

    /**
     * Called on the client side by Neo/Forge, when the NBT of the held item gets updated.
     *
     * @param oldStaffStack         The previous item stack
     * @param newStaffStack         The updated item stack
     * @param selectedSlotChanged   If the selected hotbar slot was changed
     * @return `true` to play the update/equip animation, `false` to skip it
     */
    open fun allowReequipAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        selectedSlotChanged: Boolean
    ) = oldStaffStack != newStaffStack

    /**
     * Returns if the staff's user is immune to lightning strikes while using the staff.
     * Called on both the client and the server by Staff Mod.
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The player, which holds the staff
     * @param hand          The hand of the [user], in which the [staff][staffStack] is
     */
    open fun isInvulnerableToLightning(staffStack: ItemStack, world: World, user: LivingEntity, hand: Hand) = false

    /**
     * Default implementation of [StaffHandler]. Used for staffs with no [registered][Registry.register] handler.
     */
    object Fallback : StaffHandler() {
        @JvmField
        val ATTRIBUTE_MODIFIERS = StaffAttributeModifiersComponentBuilder.default()
    }

    /**
     * A [StaffHandler] that shows an "item cannot be used in staff" overlay message to the player trying to use it.
     */
    object Disabled : StaffHandler() {
        override fun use(
            staffStack: ItemStack,
            world: World,
            user: LivingEntity,
            hand: Hand
        ): TypedActionResult<ItemStack> {
            val stackInStaff = staffStack.itemStackInStaff
            if (user is ServerPlayerEntity && stackInStaff != null) overlayMessage(
                user,
                I18n.FEEDBACK_AVM_STAFF_HANDLER_NOT_ENABLED.getText(stackInStaff.name, staffStack.name)
            )
            return TypedActionResult.pass(user.getStackInHand(hand))
        }
    }

    /**
     * Handler of a staff with no item inserted into it.
     */
    object Empty : StaffHandler() {
        private inline val LivingEntity.targetPos: BlockPos
            get() = BlockPos.ofFloored(approximateStaffItemPosition)

        override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity): Int {
            val targetPos = user.targetPos
            val state = world.getBlockState(targetPos)
            val quickDraw = staffStack.getEnchantmentLevel(Enchantments.QUICK_DRAW, world.registryManager) + 1

            return if (!canPickUp(staffStack, world, targetPos, state)) 0
            else 10 + (state.getHardness(world, targetPos) / quickDraw).roundToInt()
        }

        override fun use(
            staffStack: ItemStack,
            world: World,
            user: LivingEntity,
            hand: Hand
        ): TypedActionResult<ItemStack> {
            val targetPos = user.targetPos
            val state = world.getBlockState(targetPos)
            if (!canPickUp(staffStack, world, targetPos, state)) return TypedActionResult.fail(staffStack)

            staffStack[DataComponentTypes.blockPickupData] = BlockPickupDataComponent(targetPos, state)

            user.setCurrentHand(hand)
            return TypedActionResult.consume(staffStack)
        }

        private fun canPickUp(staffStack: ItemStack, world: World, pos: BlockPos, state: BlockState) = !state.isAir &&
                state.getHardness(world, pos) != -1f &&
                state.block.asItem().let { it in Registry && it in staffStack.enabledItemsInStaffTag }

        private fun userChangedTarget(
            world: World,
            user: LivingEntity,
            blockPickupData: BlockPickupDataComponent?
        ): Boolean {
            val targetPos = user.targetPos
            val state = world.getBlockState(targetPos)
            return blockPickupData == null || blockPickupData.pos != targetPos || blockPickupData.state != state
        }

        override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
            if (!world.isClient && userChangedTarget(world, user, staffStack[DataComponentTypes.blockPickupData])) {
                user.stopUsingItem()
            }
        }

        override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
            staffStack.remove(DataComponentTypes.blockPickupData)
        }

        override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
            val blockPickupData = staffStack[DataComponentTypes.blockPickupData]
            if (!userChangedTarget(world, user, blockPickupData)) {
                require(blockPickupData != null)
                tryPickUp(world, blockPickupData.pos, blockPickupData.state, staffStack)
                (user as? PlayerEntity)?.resetLastAttackedTicks()
            }

            onStoppedUsing(staffStack, world, user, 0)
            (user as? ServerPlayerEntity)?.incrementStaffItemUseStat(Items.AIR)
            return staffStack
        }

        private fun tryPickUp(world: World, pos: BlockPos, state: BlockState, staffStack: ItemStack): Boolean {
            if (!canPickUp(staffStack, world, pos, state)) return false

            val pickStack = state.block.getPickStack(world, pos, state)
            world.getBlockEntity(pos)?.apply {
                val nbt = createComponentlessNbt(world.registryManager)
                removeFromCopiedStackNbt(nbt)
                BlockItem.setBlockEntityData(pickStack, type, nbt)
                pickStack.applyComponentsFrom(createComponentMap())
                (this as? Clearable)?.clear()
                (this as? IClearableBeforeInsertedIntoStaff)?.staffMod_clearBeforeRemovedFromWorld()
            }

            staffStack.mutableItemStackInStaff = pickStack
            world.removeBlock(pos, false)

            return true
        }

        override fun allowComponentsUpdateAnimation(
            oldStaffStack: ItemStack,
            newStaffStack: ItemStack,
            player: PlayerEntity,
            hand: Hand
        ) = false

        override fun allowReequipAnimation(
            oldStaffStack: ItemStack,
            newStaffStack: ItemStack,
            selectedSlotChanged: Boolean
        ) = selectedSlotChanged
    }

    companion object Registry : RegistryBase<Identifier, StaffHandler>() {
        /**
         * Registers an entry to this registry.
         *
         * @param key The key to associate a value with
         * @param value The value to register
         */
        fun register(key: Item, value: StaffHandler) = register(key.registryId, value)

        /**
         * Checks if the given key is present in the registry
         *
         * @param key The key to check
         */
        operator fun contains(key: Item) = key.registryId in this

        /**
         * Gets the value associated with the given key or throws an exception, if the key is not present in this registry.
         *
         * @param key The key to check
         */
        fun getValue(key: Item) = getValue(key.registryId)

        /**
         * Sends an [OverlayMessageS2CPacket] to [player].
         *
         * @param player    The player to send an overlay message to
         * @param message   The message to display
         */
        @JvmStatic
        fun overlayMessage(player: ServerPlayerEntity, message: Text) {
            player.networkHandler.sendPacket(OverlayMessageS2CPacket(message))
        }
    }
}
