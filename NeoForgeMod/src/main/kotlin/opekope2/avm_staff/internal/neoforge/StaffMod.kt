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

package opekope2.avm_staff.internal.neoforge

import net.minecraft.loot.LootPool
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryOps
import net.minecraft.village.VillagerProfession
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.LootTableLoadEvent
import net.neoforged.neoforge.event.village.VillagerTradesEvent
import net.neoforged.neoforge.registries.DataPackRegistryEvent
import opekope2.avm_staff.api.staff.StaffCommand
import opekope2.avm_staff.content.VillagerTrades
import opekope2.avm_staff.internal.AbstractStaffMod
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.internal.loot.ILootPoolBuilder
import opekope2.avm_staff.util.MOD_ID
import org.slf4j.LoggerFactory
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.function.Consumer

@Mod(MOD_ID)
object StaffMod : AbstractStaffMod() {
    private val LOGGER = LoggerFactory.getLogger(javaClass)

    // FIXME Remove on NeoForge 1.21.4+
    // This shouldn't need to exist if NeoForge didn't forget to add RegistryWrapper.WrapperLookup to LootTableLoadEvent
    @JvmField
    val LOOT_TABLE_LOADER_REGISTRY_OPS = ThreadLocal<RegistryOps<*>?>()

    init {
        super.initialize()

        MOD_BUS.register(this)
        FORGE_BUS.addListener(::registerVillagerTrades)
        FORGE_BUS.addListener(::modifyLootTables)
    }

    @SubscribeEvent
    fun registerDynamicRegistries(event: DataPackRegistryEvent.NewRegistry) {
        event.dataPackRegistry(StaffCommand.REGISTRY_KEY, StaffCommand.CODEC, StaffCommand.CODEC)
    }

    fun registerVillagerTrades(event: VillagerTradesEvent) {
        when (event.type) {
            VillagerProfession.ARMORER -> {
                event.trades.get(4) += VillagerTrades.armorer4_faintScepterOfFriendshipHead
                event.trades.get(5) += VillagerTrades.armorer5_faintScepterOfFriendship
            }

            VillagerProfession.CLERIC -> {
                event.trades.get(5) += VillagerTrades.cleric5_scepterOfFriendship
            }
        }
    }

    fun modifyLootTables(event: LootTableLoadEvent) {
        fun modifyPool(name: String, delegate: Consumer<ILootPoolBuilder>) {
            val builder = event.table.getPool(name) as ILootPoolBuilder?
            if (builder != null) delegate.accept(builder)
            else LOGGER.atWarn()
                .addArgument(
                    I18n.ERROR_AVM_STAFF_LOOT_TABLE_MODIFIER_NEOFORGE_POOL_NOT_FOUND.supplyTranslation(name, event.name)
                )
                .addArgument(I18n.ERROR_AVM_STAFF_LOOT_TABLE_MODIFIER_SURVIVAL_OBTAINABILITY.getTranslation())
                .log("{}. {}")
        }

        val lootTableOps = LOOT_TABLE_LOADER_REGISTRY_OPS.get()
        if (lootTableOps == null) {
            LOGGER.atError()
                .addArgument(
                    I18n.ERROR_AVM_STAFF_LOOT_TABLE_MODIFIER_NEOFORGE_REGISTRY_OPS_ERROR.supplyTranslation(event.name)
                )
                .addArgument(I18n.ERROR_AVM_STAFF_LOOT_TABLE_MODIFIER_SURVIVAL_OBTAINABILITY.getTranslation())
                .log("{}. {}")
            return
        }

        val addedPools = mutableListOf<LootPool.Builder>()
        super.modifyLootTable(
            RegistryKey.of(RegistryKeys.LOOT_TABLE, event.name),
            { LootPool.builder().name(it).also(addedPools::add) },
            { _, name, modifier -> modifyPool(name, modifier) },
            lootTableOps
        )
        addedPools.forEach { event.table.addPool(it.build()) }
    }
}
