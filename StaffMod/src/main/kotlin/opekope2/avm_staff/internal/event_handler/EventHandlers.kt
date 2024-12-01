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

package opekope2.avm_staff.internal.event_handler

import net.minecraft.block.DispenserBlock
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.AbstractPiglinEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.LootTableEntry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraftforge.event.LootTableLoadEvent
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import opekope2.avm_staff.api.block.dispenser.CakeDispenserBehavior
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.content.Criteria
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.content.GameRules
import opekope2.avm_staff.mixin.IPiglinBrainAccessor
import opekope2.avm_staff.util.*
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

object EventHandlers {
    private val MODIFIABLE_LOOT_TABLES = setOf(
        Identifier.ofVanilla("chests/ancient_city"),
        Identifier.ofVanilla("chests/bastion_other"),
        Identifier.ofVanilla("chests/bastion_treasure"),
        Identifier.ofVanilla("chests/trial_chambers/reward_unique"),
    )
    private const val MAX_ANGER_DISTANCE = 16.0

    init {
        FORGE_BUS.addListener(::die)
        FORGE_BUS.addListener(::hurt)
        FORGE_BUS.addListener(::leftClickBlock)
        FORGE_BUS.addListener(::rightClickItem)
        FORGE_BUS.addListener(::setup)
        FORGE_BUS.addListener(::modifyLootTable)
        FORGE_BUS.addListener(::attack)
        FORGE_BUS.addListener(::drop)
    }

    private fun die(event: LivingDeathEvent) {
        val entity = event.entity
        if (entity !is PlayerEntity) return

        if (entity.activeItem.isStaff) {
            entity.stopUsingItem()
        }
    }

    private fun hurt(event: LivingHurtEvent) {
        val entity = event.entity
        val damage = event.source

        if (entity is ServerPlayerEntity && entity.isUsingItem) {
            Criteria.takeDamageWhileUsingItem.trigger(entity, entity.activeItem, damage)
        }
    }

    private fun leftClickBlock(event: LeftClickBlock) {
        val player = event.entity
        val hand = event.hand
        val target = event.pos
        val direction = event.face!!
        val staffStack = player.getStackInHand(hand)
        val staffItem = staffStack.item as? StaffItem ?: return

        val result = staffItem.attackBlock(staffStack, player.entityWorld, player, target, direction, hand)
        if (result != ActionResult.PASS) {
            event.isCanceled = true
            event.cancellationResult = result
            event.useBlock = Event.Result.DENY
            event.useItem = Event.Result.DENY
        }
    }

    private fun rightClickItem(event: RightClickItem) {
        val player = event.entity
        val hand = event.hand
        val world = player.entityWorld
        val cake = player.getStackInHand(hand)
        val spawnPos = EntityTypes.cake.getSpawnPosition(world, player.approximateStaffTipPosition)

        if (!cake.isOf(Items.CAKE)) return
        if (spawnPos == null) return
        if (world.isClient) {
            event.isCanceled = true
            event.cancellationResult = ActionResult.SUCCESS
            return
        }
        if (!world.gameRules.getBoolean(GameRules.THROWABLE_CAKES)) return

        CakeEntity.throwCake(world, spawnPos, player.rotationVector * .5 + player.velocity, player)
        cake.decrementUnlessCreative(1, player)

        event.isCanceled = true
        event.cancellationResult = ActionResult.FAIL
    }

    private fun setup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            DispenserBlock.registerBehavior(Items.CAKE, CakeDispenserBehavior())
        }
    }

    private fun modifyLootTable(event: LootTableLoadEvent) {
        val lootTable = event.name
        if (lootTable !in MODIFIABLE_LOOT_TABLES) return

        event.table.addPool(
            LootPool.builder().with(
                LootTableEntry.builder(
                    RegistryKey.of(
                        RegistryKeys.LOOT_TABLE,
                        Identifier.of(MOD_ID, "add_loot_pool/${lootTable.path}")
                    )
                )
            ).build()
        )
    }

    private fun attack(event: AttackEntityEvent) {
        val player = event.entity
        val world = player.entityWorld
        val target = event.target
        if (world.isClient) return
        if (target !is LivingEntity) return
        if (!player.mainHandStack.isStaff) return
        if (!player.armorItems.any { it.isOf(opekope2.avm_staff.content.Items.crownOfKingOrange) }) return

        val box = Box.of(player.pos, 2 * MAX_ANGER_DISTANCE, 2 * MAX_ANGER_DISTANCE, 2 * MAX_ANGER_DISTANCE)
        world.getEntitiesByClass(AbstractPiglinEntity::class.java, box) {
            it !== target && it.squaredDistanceTo(player) <= MAX_ANGER_DISTANCE * MAX_ANGER_DISTANCE
        }.forEach {
            IPiglinBrainAccessor.callBecomeAngryWith(it, target)
        }
    }

    private fun drop(event: ItemTossEvent) {
        drop(event.player, event.entity)
    }

    fun drop(entity: PlayerEntity, item: ItemEntity) {
        val staffItem = item.stack.item as? StaffItem ?: return

        staffItem.onStoppedUsing(item.stack, entity.entityWorld, entity, entity.itemUseTimeLeft)
    }
}
