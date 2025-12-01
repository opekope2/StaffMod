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

package opekope2.avm_staff.internal.fabric

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.fabricmc.fabric.api.loot.v3.LootTableSource
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryOps
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.dynamic.NullOps
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.AbstractStaffMod
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.internal.loot.ILootPoolBuilder
import org.slf4j.LoggerFactory
import java.util.function.Consumer

object StaffMod : AbstractStaffMod(), ModInitializer, AttackEntityCallback, LootTableEvents.Modify {
    private val LOGGER = LoggerFactory.getLogger(javaClass)

    override fun onInitialize() {
        super.initialize()

        AttackEntityCallback.EVENT.register(this)
        LootTableEvents.MODIFY.register(this)
    }

    override fun interact(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        target: Entity,
        hit: EntityHitResult?
    ): ActionResult {
        val staffStack = player.getStackInHand(hand)
        val staffItem = staffStack.item as? StaffItem ?: return ActionResult.PASS
        val result = staffItem.attackEntity(staffStack, world, player, target, hand)

        return if (result.interruptsFurtherEvaluation()) ActionResult.SUCCESS
        else ActionResult.PASS
    }

    override fun modifyLootTable(
        key: RegistryKey<LootTable>,
        tableBuilder: LootTable.Builder,
        source: LootTableSource,
        registries: RegistryWrapper.WrapperLookup
    ) {
        val addedPools = mutableListOf<LootPool.Builder>()
        val poolBuilders = mutableListOf<VanillaPoolBuilder>()
        super.modifyLootTable(
            key,
            { LootPool.builder().also(addedPools::add) },
            { ordinal, _, modifier ->
                tableBuilder.modifyPools(VanillaPoolBuilder(key, source, ordinal, modifier).also(poolBuilders::add))
            },
            RegistryOps.of(NullOps.INSTANCE, registries)
        )
        addedPools.forEach(tableBuilder::pool)
        poolBuilders.forEach(VanillaPoolBuilder::check)
    }

    private class VanillaPoolBuilder(
        private val key: RegistryKey<LootTable>,
        private val source: LootTableSource,
        private val ordinal: Int,
        private val delegate: Consumer<ILootPoolBuilder>
    ) : Consumer<LootPool.Builder> {
        init {
            if (!source.isBuiltin) LOGGER.atWarn()
                .addArgument(I18n.ERROR_AVM_STAFF_LOOT_TABLE_MODIFIER_FABRIC_NON_VANILLA.supplyTranslation(key.value))
                .addArgument(I18n.ERROR_AVM_STAFF_LOOT_TABLE_MODIFIER_SURVIVAL_OBTAINABILITY.getTranslation())
                .log("{}. {}")
        }

        private var index = 0

        override fun accept(builder: LootPool.Builder) {
            if (index++ == ordinal && source.isBuiltin) delegate.accept(builder::with)
        }

        fun check() {
            if (index <= ordinal) LOGGER.atWarn()
                .addArgument(
                    I18n.ERROR_AVM_STAFF_LOOT_TABLE_MODIFIER_FABRIC_LESS_POOLS_THAN_EXPECTED
                        .supplyTranslation(ordinal + 1, key.value, index)
                )
                .addArgument(I18n.ERROR_AVM_STAFF_LOOT_TABLE_MODIFIER_SURVIVAL_OBTAINABILITY.getTranslation())
                .log("{}. {}")
        }
    }
}
