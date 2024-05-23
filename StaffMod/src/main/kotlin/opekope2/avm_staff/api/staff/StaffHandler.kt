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

package opekope2.avm_staff.api.staff

import dev.architectury.event.EventResult
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
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
 */
abstract class StaffHandler {
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
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        target: LivingEntity,
        hand: Hand
    ): ActionResult {
        return ActionResult.PASS
    }

    /**
     * Called on both the client by Architectury API and the server by Staff Mod, when an entity attacks thin air with a
     * staff.
     *
     * @param staffStack    The item stack used to perform the action
     * @param world         The world the [attacker] is in
     * @param attacker      The entity, which attacked with the staff
     * @param hand          The hand of the [attacker], in which the [staff][staffStack] is
     */
    open fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) {}

    /**
     * Called on both the client and the server by Architectury API, when an entity attacks a block with a staff.
     *
     * @return
     * - [EventResult.interruptTrue], [EventResult.interruptTrue]:
     *   Cancels vanilla block breaking, and on the logical client, sends a packet to the server.
     * - [EventResult.interruptDefault], [EventResult.pass]:
     *   Lets Minecraft handle vanilla block breaking.
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
    ): EventResult {
        return EventResult.pass()
    }

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
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        return EventResult.pass()
    }

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
    open fun canSwingHand(staffStack: ItemStack, world: World, holder: LivingEntity, hand: Hand): Boolean {
        return true
    }

    /**
     * Returns if attacking with the staff should disable the target's shield.
     */
    open fun disablesShield(): Boolean {
        return false
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
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        player: PlayerEntity,
        hand: Hand
    ): Boolean {
        return true
    }

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
    ): Boolean {
        return oldStaffStack != newStaffStack
    }

    /**
     * Gets the attribute modifiers (damage, attack speed, etc.) of the staff when held.
     *
     * @param staffStack    The staff item stack (not the item in the staff)
     */
    open fun getAttributeModifiers(staffStack: ItemStack): AttributeModifiersComponent {
        return ATTRIBUTE_MODIFIERS
    }

    /**
     * Handler of a staff with no item inserted into it.
     */
    object Default : StaffHandler()

    companion object {
        private val ATTRIBUTE_MODIFIERS = AttributeModifiersComponent.builder()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(4.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(2.0), AttributeModifierSlot.MAINHAND)
            .build()

        private val staffItemsHandlers = mutableMapOf<Identifier, StaffHandler>()

        /**
         * Registers a [StaffHandler] for the given [item ID][staffItem]. Call this from your common mod initializer.
         *
         * @param staffItem                     The item ID to register a handler for. This is the item, which can be
         *   inserted into the staff
         * @param handler                       The staff item handler, which processes staff interactions, while the
         *   [registered item][staffItem] is inserted into it
         * @return `true`, if the registration was successful, `false`, if the item was already registered
         */
        @JvmStatic
        fun register(staffItem: Identifier, handler: StaffHandler): Boolean {
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
        operator fun get(staffItem: Identifier): StaffHandler? = staffItemsHandlers[staffItem]
    }
}
