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

package opekope2.avm_staff.api.item

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.attackSpeed

/**
 * Provides functionality for a staff, when an item is inserted into it.
 *
 * @see IDisablesShield
 */
abstract class StaffItemHandler {
    /**
     * The number of ticks the staff can be used for using the current item.
     */
    open val maxUseTime: Int
        get() = 0

    /**
     * Called on both the client and the server by Minecraft when the player uses the staff.
     *
     * If the staff can be used for multiple ticks, override [maxUseTime] to return a positive number, and call
     * [PlayerEntity.setCurrentHand] on [user] with [hand] as the argument.
     *
     * On the logical client, the return values have the following meaning:
     *
     * - SUCCESS: Swing hand, and reset equip progress
     * - CONSUME, CONSUME_PARTIAL: Don't swing hand, and reset equip progress
     * - PASS, FAIL: Don't swing hand, and don't reset equip progress
     *
     * On the logical server, the return values have the following meaning (if used by player):
     *
     * - SUCCESS: Swing hand
     * - CONSUME, CONSUME_PARTIAL, PASS, FAIL: Don't swing hand
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The player, which uses the staff
     * @param hand          The hand of the [user], in which the [staff][staffStack] is
     * @see Item.use
     */
    open fun use(staffStack: ItemStack, world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return TypedActionResult.pass(user.getStackInHand(hand))
    }

    /**
     * Called on both the client and the server by Minecraft every tick an entity uses the staff.
     *
     * @param staffStack        The item stack used to perform the action
     * @param world             The world [user] is in
     * @param user              The entity, which uses the staff
     * @param remainingUseTicks The number of ticks remaining before an entity finishes using the staff counting down
     *   from [maxUseTime] to 0
     * @see Item.usageTick
     */
    open fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
    }

    /**
     * Called on both the client and the server by Minecraft, when an entity stops using the staff before being used for
     * [maxUseTime]. If that time is reached, [finishUsing] will be called.
     *
     * @param staffStack        The item stack used to perform the action
     * @param world             The world the [user] is in
     * @param user              The entity, which used the staff
     * @param remainingUseTicks The number of ticks left until reaching [maxUseTime]
     * @see Item.onStoppedUsing
     */
    open fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
    }

    /**
     * Called on both the client and the server by Minecraft, when an entity finishes using the staff
     * (usage ticks reach [maxUseTime]).
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The entity, which used the staff
     * @return              The item stack after using the staff
     * @see Item.finishUsing
     */
    open fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        return staffStack
    }

    /**
     * Called on both the client and the server by Minecraft, when an entity uses the staff on a block.
     * This method may not be called, if the block handles the use event (for example, a chest).
     *
     * On the logical client, the return values have the following meaning:
     *
     * - SUCCESS: send a packet to the server, and swing hand
     * - CONSUME, CONSUME_PARTIAL, FAIL: send a packet to the server, and don't swing hand
     * - PASS: send a packet to the server, don't swing hand, then interact with the item by itself (see [use])
     *
     * On the logical server, the return values have the following meaning (if used by player):
     *
     * - SUCCESS:
     *   Increment [*player used item* stat][Stats.USED],
     *   trigger [*item used on block* criterion][Criteria.ITEM_USED_ON_BLOCK],
     *   and swing hand
     * - CONSUME:
     *   Increment [*player used item* stat][Stats.USED],
     *   trigger [*item used on block* criterion][Criteria.ITEM_USED_ON_BLOCK],
     *   and don't swing hand
     * - CONSUME_PARTIAL:
     *   Don't increment [*player used item* stat][Stats.USED],
     *   trigger [*item used on block* criterion][Criteria.ITEM_USED_ON_BLOCK],
     *   and don't swing hand
     * - PASS, FAIL:
     *   Don't increment [*player used item* stat][Stats.USED],
     *   don't trigger [*item used on block* criterion][Criteria.ITEM_USED_ON_BLOCK],
     *   and don't swing hand
     *
     * On the logical server, the return values are processed by the caller code (if not used by player).
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
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        target: BlockPos,
        side: Direction,
        hand: Hand
    ): ActionResult {
        return ActionResult.PASS
    }

    /**
     * Called on both the client and the server by Minecraft, when an entity uses the staff on an entity.
     * This method may not be called, if the entity handles the use event (for example, a horse).
     * This method will not be called, if the player is in spectator mode.
     *
     * On the logical client, the return values have the following meaning:
     *
     * - SUCCESS:
     *   send a packet to the server,
     *   emit [*entity interact* game event][GameEvent.ENTITY_INTERACT],
     *   and swing hand
     * - CONSUME, CONSUME_PARTIAL:
     *   send a packet to the server,
     *   emit [*entity interact* game event][GameEvent.ENTITY_INTERACT],
     *   and don't swing hand
     * - PASS, FAIL:
     *   send a packet to the server,
     *   don't emit [*entity interact* game event][GameEvent.ENTITY_INTERACT],
     *   don't swing hand,
     *   then interact with the item by itself (see [use])
     *
     * On the logical server, the return values have the following meaning (if used by player):
     *
     * - SUCCESS:
     *   Emit [*entity interact* game event][GameEvent.ENTITY_INTERACT],
     *   trigger [*player interacted with entity* criteria][Criteria.PLAYER_INTERACTED_WITH_ENTITY],
     *   and swing hand
     * - CONSUME, CONSUME_PARTIAL:
     *   Emit [*entity interact* game event][GameEvent.ENTITY_INTERACT],
     *   trigger [*player interacted with entity* criteria][Criteria.PLAYER_INTERACTED_WITH_ENTITY],
     *   and don't swing hand
     * - PASS, FAIL:
     *   Don't emit [*entity interact* game event][GameEvent.ENTITY_INTERACT],
     *   don't trigger [*player interacted with entity* criteria][Criteria.PLAYER_INTERACTED_WITH_ENTITY],
     *   and don't swing hand
     *
     * On the logical server, the return values are processed by the caller code (if not used by player).
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [user] is in
     * @param user          The entity, which used the staff
     * @param target        The entity, on which [user] used the staff
     * @param hand          The hand of the [user], in which the [staff][staffStack] is
     * @see Item.useOnEntity
     */
    open fun useOnEntity(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        target: LivingEntity,
        hand: Hand
    ): ActionResult {
        return ActionResult.PASS
    }

    /**
     * Called on both the client and the server by Staff Mod, when an entity attacks nothing (left clicks on air) with
     * a staff.
     *
     * On the logical client, the return values have the following meaning:
     *
     * - SUCCESS: send a packet to the server, and swing hand. This doesn't reset attack cooldown
     * - CONSUME, CONSUME_PARTIAL: send a packet to the server, and don't swing hand. This doesn't reset attack cooldown
     * - PASS: Let Minecraft handle vanilla attack
     * - FAIL: don't send a packet to the server, and don't swing hand. This doesn't reset attack cooldown
     *
     * On the logical server, the return value is ignored (if used by player) or processed by the caller code
     * (if the attacker is not a player).
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [attacker] is in
     * @param attacker      The entity, which attacked with the staff
     * @param hand          The hand of the [attacker], in which the [staff][staffStack] is
     */
    open fun attack(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        hand: Hand
    ): ActionResult {
        return ActionResult.PASS
    }

    /**
     * Called on both the client and the server by Fabric API (Fabric) or Staff Mod (Forge),when an entity attacks a
     * block with a staff.
     *
     * On the logical client, the return values have the following meaning:
     *
     * - SUCCESS:
     *   Cancel vanilla block breaking,
     *   send a packet to the server,
     *   spawn block breaking particles,
     *   and swing hand.
     *   This doesn't reset the block breaking cooldown
     * - CONSUME, CONSUME_PARTIAL:
     *   Cancel vanilla block breaking,
     *   send a packet to the server,
     *   don't spawn block breaking particles,
     *   and don't swing hand.
     *   This doesn't reset the block breaking cooldown
     * - PASS: Let Minecraft handle vanilla block breaking
     * - FAIL:
     *   Cancel vanilla block breaking,
     *   don't send a packet to the server,
     *   don't spawn block breaking particles,
     *   and don't swing hand.
     *   This doesn't reset the block breaking cooldown
     *
     * On the logical server, the return values have the following meaning (if used by player):
     *
     * - SUCCESS, CONSUME, CONSUME_PARTIAL, FAIL: Cancel vanilla block breaking, and notify the client
     * - PASS: Let Minecraft handle vanilla block breaking
     *
     * On the logical server, the return values are processed by the caller code (if the attacker is not a player).
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [attacker] is in
     * @param attacker      The entity, which attacked with the staff
     * @param target        The block the [attacker] attacked
     * @param side          The side of the [block][target], which was attacked
     * @param hand          The hand of the [attacker], in which the [staff][staffStack] is
     */
    open fun attackBlock(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: BlockPos,
        side: Direction,
        hand: Hand
    ): ActionResult {
        return ActionResult.PASS
    }

    /**
     * Called on both the client by Staff Mod and the server by Fabric API (Fabric) or Staff Mod (Forge), when an entity
     * attacks an entity with a staff.
     *
     * On the logical client, the return values have the following meaning:
     *
     * - SUCCESS:
     *   Cancel vanilla entity attack,
     *   send a packet to the server,
     *   and swing hand.
     *   This doesn't reset the entity attack cooldown
     * - CONSUME, CONSUME_PARTIAL:
     *   Cancel vanilla entity attack,
     *   send a packet to the server,
     *   and don't swing hand.
     *   This doesn't reset the entity attack cooldown
     * - PASS: Let Minecraft handle vanilla entity attack
     * - FAIL:
     *   Cancel vanilla entity attack,
     *   don't send a packet to the server,
     *   and don't swing hand.
     *   This doesn't reset the entity attack cooldown
     *
     * On the logical server, the return values have the following meaning (if used by player):
     *
     * - SUCCESS, CONSUME, CONSUME_PARTIAL, FAIL: Cancel vanilla entity attack, don't attack the entity
     * - PASS: Let Minecraft handle vanilla entity attack
     *
     * On the logical server, the return values are processed by the caller code (if the attacker is not a player).
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [attacker] is in
     * @param attacker      The entity, which attacked with the staff
     * @param target        The entity the [attacker] attacked
     * @param hand          The hand of the [attacker], in which the [staff][staffStack] is
     */
    open fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): ActionResult {
        return ActionResult.PASS
    }

    /**
     * Called on the client side by Fabric API, when the NBT of the held item gets updated.
     *
     * @param oldStaffStack The previous item stack
     * @param newStaffStack The updated item stack
     * @param player        The holder of [oldStaffStack]
     * @param hand          The hand of [player], in which the [old staff][oldStaffStack] is
     * @return true to play the update/equip animation, false to skip it
     */
    open fun allowNbtUpdateAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        player: PlayerEntity,
        hand: Hand
    ): Boolean {
        return true
    }

    /**
     * Called on the client side by Forge, when the NBT of the held item gets updated.
     *
     * @param oldStaffStack         The previous item stack
     * @param newStaffStack         The updated item stack
     * @param selectedSlotChanged   If the selected hotbar slot was changed
     * @return true to play the update/equip animation, false to skip it
     */
    open fun allowReequipAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        selectedSlotChanged: Boolean
    ): Boolean {
        return oldStaffStack != newStaffStack
    }

    /**
     * Gets the attribute modifiers (damage, attack speed, etc.) of the staff when held.
     *
     * @param staffStack    The staff item stack (not the item in the staff)
     * @param slot          The slot the staff is equipped in
     */
    open fun getAttributeModifiers(
        staffStack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return if (slot == EquipmentSlot.MAINHAND) ATTRIBUTE_MODIFIERS
        else ImmutableMultimap.of()
    }

    object EmptyStaffHandler : StaffItemHandler()

    object FallbackStaffHandler : StaffItemHandler()

    companion object {
        private val ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            attackDamage(4.0),
            EntityAttributes.GENERIC_ATTACK_SPEED,
            attackSpeed(2.0)
        )

        private val staffItemsHandlers = mutableMapOf<Identifier, StaffItemHandler>()

        /**
         * Registers a [StaffItemHandler] for the given [item ID][staffItem]. Call this from your common mod initializer.
         *
         * @param staffItem                     The item ID to register a handler for. This is the item, which can be
         *   inserted into the staff
         * @param handler                       The staff item handler, which processes staff interactions, while the
         *   [registered item][staffItem] is inserted into it
         * @return `true`, if the registration was successful, `false`, if the item was already registered
         */
        @JvmStatic
        fun register(staffItem: Identifier, handler: StaffItemHandler): Boolean {
            if (staffItem in staffItemsHandlers) return false

            staffItemsHandlers[staffItem] = handler
            return true
        }

        /**
         * Checks, if a staff item handler for the [given item][staffItem] is registered.
         *
         * @param staffItem The item ID, which can be inserted into the staff
         */
        @JvmStatic
        operator fun contains(staffItem: Identifier): Boolean = staffItem in staffItemsHandlers

        /**
         * Gets the registered staff item handler for the [given item][staffItem] or `null`, if no staff item handler was
         * registered.
         *
         * @param staffItem The item ID, which can be inserted into the staff
         */
        @JvmStatic
        operator fun get(staffItem: Identifier): StaffItemHandler? = staffItemsHandlers[staffItem]
    }
}
