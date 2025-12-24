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

package opekope2.avm_staff.internal.event_handler

import dev.architectury.event.CompoundEventResult
import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.InteractionEvent
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.PlayerEvent
import net.minecraft.block.DispenserBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.AbstractPiglinEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.block.dispenser.CakeDispenserBehavior
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.content.Criteria
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.content.GameRules
import opekope2.avm_staff.mixin.IPiglinBrainAccessor
import opekope2.avm_staff.util.*

object EventHandlers :
    EntityEvent.LivingDeath,
    EntityEvent.LivingHurt,
    InteractionEvent.LeftClickBlock,
    InteractionEvent.RightClickItem,
    Runnable,
    PlayerEvent.AttackEntity,
    PlayerEvent.DropItem {
    private const val MAX_ANGER_DISTANCE = 16.0

    init {
        EntityEvent.LIVING_DEATH.register(this)
        EntityEvent.LIVING_HURT.register(this)
        InteractionEvent.LEFT_CLICK_BLOCK.register(this)
        InteractionEvent.RIGHT_CLICK_ITEM.register(this)
        LifecycleEvent.SETUP.register(this)
        PlayerEvent.ATTACK_ENTITY.register(this)
        PlayerEvent.DROP_ITEM.register(this)
    }

    override fun die(entity: LivingEntity, damageSource: DamageSource): EventResult {
        if (entity !is PlayerEntity) return EventResult.pass()

        if (entity.activeItem.isStaff) {
            entity.stopUsingItem()
        }

        return EventResult.pass()
    }

    override fun hurt(entity: LivingEntity, damage: DamageSource, amount: Float): EventResult {
        if (entity is ServerPlayerEntity && entity.isUsingItem) {
            Criteria.takeDamageWhileUsingItem.trigger(entity, entity.activeItem, damage)
        }
        return EventResult.pass()
    }

    override fun click(player: PlayerEntity, hand: Hand, target: BlockPos, direction: Direction): EventResult {
        val staffStack = player.getStackInHand(hand)
        val staffItem = staffStack.item as? StaffItem ?: return EventResult.pass()

        return staffItem.attackBlock(staffStack, player.entityWorld, player, target, direction, hand)
    }

    override fun click(player: PlayerEntity, hand: Hand): CompoundEventResult<ItemStack> {
        val world = player.entityWorld
        val cake = player.getStackInHand(hand)
        val spawnPos = EntityTypes.cake.getSpawnPosition(world, player.approximateStaffTipPosition)

        if (!cake.isOf(Items.CAKE)) return CompoundEventResult.pass()
        if (spawnPos == null) return CompoundEventResult.pass()
        if (world.isClient) return CompoundEventResult.interruptTrue(cake)
        if (!world.gameRules.getBoolean(GameRules.throwableCakes)) return CompoundEventResult.pass()

        CakeEntity.throwCake(world, spawnPos, player.rotationVector * .5 + player.velocity, player)
        cake.decrementUnlessCreative(1, player)

        return CompoundEventResult.interruptFalse(cake)
    }

    // setup
    override fun run() {
        DispenserBlock.registerBehavior(Items.CAKE, CakeDispenserBehavior())
    }

    override fun attack(
        player: PlayerEntity,
        world: World,
        target: Entity,
        hand: Hand,
        hit: EntityHitResult?
    ): EventResult {
        if (world.isClient) return EventResult.pass()
        if (target !is LivingEntity) return EventResult.pass()
        if (!player.getStackInHand(hand).isStaff) return EventResult.pass()
        if (!player.armorItems.any { it.isOf(opekope2.avm_staff.content.Items.crownOfKingOrange) }) return EventResult.pass()

        val box = Box.of(player.pos, 2 * MAX_ANGER_DISTANCE, 2 * MAX_ANGER_DISTANCE, 2 * MAX_ANGER_DISTANCE)
        world.getEntitiesByClass(AbstractPiglinEntity::class.java, box) {
            it !== target && it.squaredDistanceTo(player) <= MAX_ANGER_DISTANCE * MAX_ANGER_DISTANCE
        }.forEach {
            IPiglinBrainAccessor.callBecomeAngryWith(it, target)
        }

        return EventResult.pass()
    }

    override fun drop(entity: PlayerEntity, item: ItemEntity): EventResult {
        val staffItem = item.stack.item as? StaffItem ?: return EventResult.pass()
        staffItem.onStoppedUsing(item.stack, entity.entityWorld, entity, entity.itemUseTimeLeft)
        return EventResult.pass()
    }
}
