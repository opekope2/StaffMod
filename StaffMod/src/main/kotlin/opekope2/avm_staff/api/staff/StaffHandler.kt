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

import com.mojang.serialization.Lifecycle
import dev.architectury.event.EventResult
import net.minecraft.SharedConstants
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.BlockState
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.block.IClearableBeforeInsertedIntoStaff
import opekope2.avm_staff.api.component.BlockPickupDataComponent
import opekope2.avm_staff.api.staff.StaffHandler.Companion.REGISTRY
import opekope2.avm_staff.api.staff.StaffHandler.Companion.register
import opekope2.avm_staff.content.DataComponentTypes
import opekope2.avm_staff.content.Enchantments
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.util.*
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Provides functionality for a staff, when an item is inserted into it.
 */
abstract class StaffHandler {
    /**
     * Gets the attribute modifiers (damage, attack speed, etc.) of the staff when held.
     */
    open val attributeModifiers: AttributeModifiersComponent
        get() = Fallback.ATTRIBUTE_MODIFIERS

    /**
     * Called by Staff Mod before an item with this staff handler is removed from [staffStack] using
     * [mutableItemStackInStaff].
     *
     * @param staffStack    The item stack of the staff
     */
    open fun beforeRemove(staffStack: ItemStack) {
    }

    /**
     * Called by Staff Mod after an item with this staff handler is inserted into [staffStack] using
     * [mutableItemStackInStaff].
     *
     * @param staffStack    The item stack of the staff
     */
    open fun afterInsert(staffStack: ItemStack) {
    }

    /**
     * Called on both the client and the server my Minecraft to get the number of ticks the staff can be used for using
     * the current item.
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The player, which uses the staff
     * @see Item.getMaxUseTime
     */
    open fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity): Int = 0

    /**
     * Gets the action that happens when a player uses the staff.
     *
     * @param staffStack    The item stack used to perform the action
     * @see Item.getUseAction
     */
    open fun getUseAction(staffStack: ItemStack): UseAction = UseAction.NONE

    /**
     * Called on both the client and the server by Minecraft when the player uses the staff.
     *
     * If the staff can be used for multiple ticks, override [getMaxUseTime] to return a positive number, and call
     * [LivingEntity.setCurrentHand] on [user] with [hand] as the argument.
     *
     * @return
     * On the logical client:
     *
     * - [ActionResult.SUCCESS]:
     *   swings hand, and resets equip progress
     * - [ActionResult.CONSUME], [ActionResult.CONSUME_PARTIAL]:
     *   doesn't swing hand, and resets equip progress
     * - [ActionResult.PASS], [ActionResult.FAIL]:
     *   doesn't swing hand, and doesn't reset equip progress
     *
     * On the logical server (if used by player):
     *
     * - [ActionResult.SUCCESS]:
     *   swings hand
     * - [ActionResult.CONSUME], [ActionResult.CONSUME_PARTIAL], [ActionResult.PASS], [ActionResult.FAIL]:
     *   doesn't swing hand
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The player, which uses the staff
     * @param hand          The hand of the [user], in which the [staff][staffStack] is
     * @see Item.use
     */
    open fun use(staffStack: ItemStack, world: World, user: LivingEntity, hand: Hand): TypedActionResult<ItemStack> =
        TypedActionResult.pass(user.getStackInHand(hand))

    /**
     * Called on both the client and the server by Minecraft every tick an entity uses the staff.
     *
     * @param staffStack        The item stack used to perform the action
     * @param world             The world [user] is in
     * @param user              The entity, which uses the staff
     * @param remainingUseTicks The number of ticks remaining before an entity finishes using the staff counting down
     *   from [getMaxUseTime] to 0
     * @see Item.usageTick
     */
    open fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
    }

    /**
     * Called on both the client and the server by Minecraft, when an entity stops using the staff before being used for
     * [getMaxUseTime]. If that time is reached, [finishUsing] will be called.
     *
     * @param staffStack        The item stack used to perform the action
     * @param world             The world the [user] is in
     * @param user              The entity, which used the staff
     * @param remainingUseTicks The number of ticks left until reaching [getMaxUseTime]
     * @see Item.onStoppedUsing
     */
    open fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
    }

    /**
     * Called on both the client and the server by Minecraft, when an entity finishes using the staff
     * (usage ticks reach [getMaxUseTime]).
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The entity, which used the staff
     * @return The item stack after using the staff
     * @see Item.finishUsing
     */
    open fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity) = staffStack

    /**
     * Called on both the client and the server by Minecraft, when an entity uses the staff on a block.
     * This method may not be called, if the block handles the use event (for example, a chest).
     *
     * @return
     * On the logical client:
     *
     * - [ActionResult.SUCCESS]:
     *   sends a packet to the server, and swings hand
     * - [ActionResult.CONSUME], [ActionResult.CONSUME_PARTIAL], [ActionResult.FAIL]:
     *   sends a packet to the server, and doesn't swing hand
     * - [ActionResult.PASS]:
     *   sends a packet to the server, doesn't swing hand, then interacts with the item using [use]
     *
     * On the logical server (if used by player):
     *
     * - [ActionResult.SUCCESS]:
     *   increments [*player used item* stat][Stats.USED], triggers
     *   [*item used on block* criterion][Criteria.ITEM_USED_ON_BLOCK], and swings hand
     * - [ActionResult.CONSUME]:
     *   increments [*player used item* stat][Stats.USED], triggers
     *   [*item used on block* criterion][Criteria.ITEM_USED_ON_BLOCK], and doesn't swing hand
     * - [ActionResult.CONSUME_PARTIAL]:
     *   doesn't increment [*player used item* stat][Stats.USED], triggers
     *   [*item used on block* criterion][Criteria.ITEM_USED_ON_BLOCK], and doesn't swing hand
     * - [ActionResult.PASS], [ActionResult.FAIL]:
     *   doesn't increment [*player used item* stat][Stats.USED], doesn't trigger
     *   [*item used on block* criterion][Criteria.ITEM_USED_ON_BLOCK], and doesn't swing hand
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The entity, which used the staff
     * @param target        The block, on which [user] used the staff
     * @param side          The side of the [block][target], on which the staff was used
     * @param hand          The hand of the [user], in which the [staff][staffStack] is
     * @see Item.useOnBlock
     */
    open fun useOnBlock(
        staffStack: ItemStack, world: World, user: LivingEntity, target: BlockPos, side: Direction, hand: Hand
    ) = ActionResult.PASS

    /**
     * Called on both the client and the server by Minecraft, when an entity uses the staff on an entity.
     * This method may not be called, if the entity handles the use event (for example, a horse).
     * This method will not be called, if the player is in spectator mode.
     *
     * @return
     * On the logical client:
     *
     * - [ActionResult.SUCCESS]:
     *   sends a packet to the server, emits [*entity interact* game event][GameEvent.ENTITY_INTERACT], and swings hand
     * - [ActionResult.CONSUME], [ActionResult.CONSUME_PARTIAL]:
     *   sends a packet to the server, emits [*entity interact* game event][GameEvent.ENTITY_INTERACT], and doesn't
     *   swing hand
     * - [ActionResult.PASS], [ActionResult.FAIL]:
     *   sends a packet to the server, doesn't emit [*entity interact* game event][GameEvent.ENTITY_INTERACT], doesn't
     *   swing hand, then interacts with the item using [use]
     *
     * On the logical server (if used by player):
     *
     * - [ActionResult.SUCCESS]:
     *   Emits [*entity interact* game event][GameEvent.ENTITY_INTERACT], triggers
     *   [*player interacted with entity* criteria][Criteria.PLAYER_INTERACTED_WITH_ENTITY], and swings hand
     * - [ActionResult.CONSUME], [ActionResult.CONSUME_PARTIAL]:
     *   Emits [*entity interact* game event][GameEvent.ENTITY_INTERACT], triggers
     *   [*player interacted with entity* criteria][Criteria.PLAYER_INTERACTED_WITH_ENTITY], and doesn't swing hand
     * - [ActionResult.PASS], [ActionResult.FAIL]:
     *   Doesn't emit [*entity interact* game event][GameEvent.ENTITY_INTERACT], doesn't trigger
     *   [*player interacted with entity* criteria][Criteria.PLAYER_INTERACTED_WITH_ENTITY], and doesn't swing hand
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The entity, which used the staff
     * @param target        The entity, on which [user] used the staff
     * @param hand          The hand of the [user], in which the [staff][staffStack] is
     * @see Item.useOnEntity
     */
    open fun useOnEntity(
        staffStack: ItemStack, world: World, user: LivingEntity, target: LivingEntity, hand: Hand
    ) = ActionResult.PASS

    /**
     * Called on both the client by Architectury API and the server by Staff Mod, when an entity attacks thin air with a
     * staff.
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [attacker] is in
     * @param attacker      The entity, which attacked with the staff
     * @param hand          The hand of the [attacker], in which the [staff][staffStack] is
     */
    open fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) {
    }

    /**
     * Called on both the client and the server by Architectury API, when an entity attacks a block with a staff.
     *
     * @return
     * - [EventResult.interruptTrue], [EventResult.interruptFalse]:
     *   Cancels vanilla block breaking, and on a Neo/Forge logical client, sends a packet to the server.
     * - [EventResult.interruptDefault], [EventResult.pass]:
     *   Lets Minecraft handle vanilla block breaking.
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [attacker] is in
     * @param attacker      The entity, which attacked with the staff
     * @param target        The block the [attacker] attacked
     * @param side          The side of the [block][target], which was attacked
     * @param hand          The hand of the [attacker], in which the [staff][staffStack] is
     * @see opekope2.avm_staff.content.Criteria.destroyBlockWithStaff
     */
    open fun attackBlock(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: BlockPos, side: Direction, hand: Hand
    ): EventResult = EventResult.pass()

    /**
     * Called on both the client by Fabric/Neo/Forge API and the server by Fabric/Neo/Forge API, when an entity attacks
     * an entity with a staff.
     *
     * @return
     * - [EventResult.interrupt], [EventResult.interruptTrue], [EventResult.interruptFalse], [EventResult.interruptDefault]:
     *   Cancels vanilla entity attack, and on the logical client, sends a packet to the server.
     * - [EventResult.pass]:
     *   Lets Minecraft handle vanilla entity attack.
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [attacker] is in
     * @param attacker      The entity, which attacked with the staff
     * @param target        The entity the [attacker] attacked
     * @param hand          The hand of the [attacker], in which the [staff][staffStack] is
     */
    open fun attackEntity(
        staffStack: ItemStack, world: World, attacker: LivingEntity, target: Entity, hand: Hand
    ): EventResult = EventResult.pass()

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
     * Called on both the client and the server by Minecraft every tick [staffStack] is in a player's inventory.
     *
     * @param staffStack    The item stack of the staff
     * @param world         The world [holder] is in
     * @param holder        The entity holding the staff
     * @param slot          The slot [staffStack] is in
     * @param selected      Whether [staffStack] is in the selected hotbar slot
     */
    open fun tick(staffStack: ItemStack, world: World, holder: Entity, slot: Int, selected: Boolean) {
    }

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
     * @param staffStack    The item stack of the staff
     * @param world         The world the [user] is in
     * @param user          The player, which holds the staff
     * @param hand          The hand of the [user], in which the [staff][staffStack] is
     */
    open fun isInvulnerableToLightning(staffStack: ItemStack, world: World, user: LivingEntity, hand: Hand) = false

    /**
     * Default implementation of [StaffHandler]. Used for staffs with no [registered][register] handler.
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
                state.block.asItem().registryId in REGISTRY &&
                state.block.asItem() in staffStack.enabledItemsInStaffTag

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
            if (world.isClient) {
                val remainingSeconds = remainingUseTicks.toFloat() / SharedConstants.TICKS_PER_SECOND
                val pickupData = staffStack[DataComponentTypes.blockPickupData]
                // FIXME Minecraft is fucking stupid and will reset the counter
                if (DataComponentTypes.blockPickupData in staffStack && pickupData != null) mc.inGameHud.setOverlayMessage(
                    I18n.FEEDBACK_AVM_STAFF_PICKING_UP.getText(
                        pickupData.state.block.name,
                        round(remainingSeconds * 10f) / 10f
                    ),
                    false
                )
            } else if (userChangedTarget(world, user, staffStack[DataComponentTypes.blockPickupData])) {
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
            if (world.isClient || !canPickUp(staffStack, world, pos, state)) return false

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

    companion object {
        /**
         * Registry key of [REGISTRY].
         */
        @JvmField
        val REGISTRY_KEY: RegistryKey<Registry<StaffHandler>> =
            RegistryKey.ofRegistry(Identifier.of(MOD_ID, "staff_handler"))

        /**
         * Registry of staff handlers.
         */
        @JvmField
        val REGISTRY: Registry<StaffHandler> = SimpleRegistry(REGISTRY_KEY, Lifecycle.stable())

        /**
         * Registers an entry to [REGISTRY].
         *
         * @param T     The type of the staff handler
         * @param key   The key to associate a value with
         * @param value The value to register
         */
        fun <T : StaffHandler> register(key: Item, value: T): T = Registry.register(REGISTRY, key.registryId, value)

        /**
         * Sends an [OverlayMessageS2CPacket] to [player].
         *
         * @param player    The player to send an overlay message to
         * @param message   The message to display
         */
        @JvmStatic
        fun overlayMessage(player: ServerPlayerEntity, message: Text) {
            // NeoForge fucked up sendPacket method name, but Mojmap should fix this
            player.networkHandler.send(OverlayMessageS2CPacket(message), null)
        }
    }
}
