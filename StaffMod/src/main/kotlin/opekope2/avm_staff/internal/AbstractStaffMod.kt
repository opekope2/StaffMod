/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

package opekope2.avm_staff.internal

import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Items.*
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.LootTables
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.EnchantRandomlyLootFunction
import net.minecraft.particle.ParticleTypes.FLAME
import net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryOps
import net.minecraft.sound.SoundEvents.*
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.*
import opekope2.avm_staff.internal.event_handler.EventHandlers
import opekope2.avm_staff.internal.loot.ILootPoolBuilder
import opekope2.avm_staff.internal.networking.c2s.play.AttackC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.StaffItemInsertRemoveSwapC2SPacket
import opekope2.avm_staff.internal.networking.s2c.play.MassDestructionS2CPacket
import opekope2.avm_staff.internal.networking.s2c.play.StaffItemInsertRemoveSwapFeedbackS2CPacket
import opekope2.avm_staff.internal.staff.handler.*
import opekope2.avm_staff.util.MOD_ID
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.MustBeInvokedByOverriders
import java.util.function.Consumer

@ApiStatus.Internal
abstract class AbstractStaffMod {
    protected fun initialize() {
        initialize(this)

        registerContent()
        initializeNetworking()
        registerStaffHandlers()
        EventHandlers.initialize()
    }

    @MustBeInvokedByOverriders
    protected open fun registerContent() {
        Blocks.register()
        Criteria.register()
        DamageTypes.initialize()
        DataComponentTypes.register()
        Enchantments.initialize()
        Enchantments.Tags.initialize()
        EntityTypes.register()
        GameRules.initialize()
        ItemGroups.register()
        Items.register()
        Items.Tags.initialize()
        SoundEvents.register()
        StatTypes.register()
    }

    @MustBeInvokedByOverriders
    protected open fun modifyLootTable(
        key: RegistryKey<LootTable>,
        addPool: (name: String) -> LootPool.Builder,
        modifyPool: (ordinal: Int, name: String, modifier: Consumer<ILootPoolBuilder>) -> Unit,
        ops: RegistryOps<*>
    ) {
        fun enchantRandomly(enchantment: RegistryKey<Enchantment>) = EnchantRandomlyLootFunction.create()
            .option(ops.getEntryLookup(RegistryKeys.ENCHANTMENT).orElseThrow().getOrThrow(enchantment))

        when (key) {
            LootTables.ANCIENT_CITY_CHEST -> modifyPool(0, "pool0") { builder ->
                builder.staffMod_addEntry(
                    ItemEntry.builder(BOOK)
                        .weight(3)
                        .apply(enchantRandomly(Enchantments.QUICK_DRAW))
                )
                builder.staffMod_addEntry(
                    ItemEntry.builder(BOOK)
                        .weight(3)
                        .apply(enchantRandomly(Enchantments.RAPID_FIRE))
                )
            }

            LootTables.BASTION_OTHER_CHEST -> modifyPool(0, "pool0") { builder ->
                builder.staffMod_addEntry(
                    ItemEntry.builder(BOOK)
                        .weight(10)
                        .apply(enchantRandomly(Enchantments.SPECTRE))
                )
            }

            LootTables.BASTION_TREASURE_CHEST ->
                addPool("$MOD_ID:crown_of_king_orange").with(ItemEntry.builder(Items.crownOfKingOrange))

            LootTables.TRIAL_CHAMBERS_REWARD_UNIQUE_CHEST -> modifyPool(0, "main") { builder ->
                builder.staffMod_addEntry(ItemEntry.builder(Items.staffInfusionSmithingTemplate))
            }

            LootTables.TRAIL_RUINS_RARE_ARCHAEOLOGY -> modifyPool(0, "main") { builder ->
                builder.staffMod_addEntry(ItemEntry.builder(Items.scepterOfFriendshipIngredient))
            }
        }
    }

    @MustBeInvokedByOverriders
    protected open fun initializeNetworking() {
        AttackC2SPacket.registerReceiver()
        StaffItemInsertRemoveSwapC2SPacket.registerReceiver()

        MassDestructionS2CPacket.registerReceiver()
        StaffItemInsertRemoveSwapFeedbackS2CPacket.registerReceiver()
    }

    @MustBeInvokedByOverriders
    protected open fun registerStaffHandlers() {
        StaffHandler.register(ANVIL, AnvilHandler(CHIPPED_ANVIL))
        StaffHandler.register(CHIPPED_ANVIL, AnvilHandler(DAMAGED_ANVIL))
        StaffHandler.register(DAMAGED_ANVIL, AnvilHandler(null))

        StaffHandler.register(BELL, BellHandler())

        StaffHandler.register(BONE_BLOCK, BoneBlockHandler())

        StaffHandler.register(CAKE, CakeHandler())

        StaffHandler.register(
            CAMPFIRE,
            CampfireHandler(CampfireHandler.Parameters(5 / 20.0, 1 / 20.0, 4f, 1f, 0.1, FLAME))
        )
        StaffHandler.register(
            SOUL_CAMPFIRE,
            CampfireHandler(CampfireHandler.Parameters(10 / 20.0, 2 / 20.0, 8f, 2f, 0.12, SOUL_FIRE_FLAME))
        )

        StaffHandler.register(COMMAND_BLOCK, StaffHandler.Fallback) // TODO

        StaffHandler.register(DIAMOND_BLOCK, DiamondBlockHandler())

        StaffHandler.register(EMERALD_BLOCK, EmeraldBlockHandler())

        StaffHandler.register(FURNACE, FurnaceHandler(RecipeType.SMELTING, BLOCK_FURNACE_FIRE_CRACKLE, 1))
        StaffHandler.register(BLAST_FURNACE, FurnaceHandler(RecipeType.BLASTING, BLOCK_BLASTFURNACE_FIRE_CRACKLE, 2))
        StaffHandler.register(SMOKER, FurnaceHandler(RecipeType.SMOKING, BLOCK_SMOKER_SMOKE, 2))

        StaffHandler.register(GOLD_BLOCK, GoldBlockHandler())

        StaffHandler.register(LIGHTNING_ROD, LightningRodHandler())

        StaffHandler.register(MAGMA_BLOCK, MagmaBlockHandler())

        StaffHandler.register(NETHERITE_BLOCK, NetheriteBlockHandler())

        StaffHandler.register(SNOW_BLOCK, SnowBlockHandler())

        StaffHandler.register(TNT, TntHandler())

        StaffHandler.register(WITHER_SKELETON_SKULL, WitherSkeletonSkullHandler())

        StaffHandler.register(WHITE_WOOL, WoolHandler(WHITE_WOOL, WHITE_CARPET))
        StaffHandler.register(ORANGE_WOOL, WoolHandler(ORANGE_WOOL, ORANGE_CARPET))
        StaffHandler.register(MAGENTA_WOOL, WoolHandler(MAGENTA_WOOL, MAGENTA_CARPET))
        StaffHandler.register(LIGHT_BLUE_WOOL, WoolHandler(LIGHT_BLUE_WOOL, LIGHT_BLUE_CARPET))
        StaffHandler.register(YELLOW_WOOL, WoolHandler(YELLOW_WOOL, YELLOW_CARPET))
        StaffHandler.register(LIME_WOOL, WoolHandler(LIME_WOOL, LIME_CARPET))
        StaffHandler.register(PINK_WOOL, WoolHandler(PINK_WOOL, PINK_CARPET))
        StaffHandler.register(GRAY_WOOL, WoolHandler(GRAY_WOOL, GRAY_CARPET))
        StaffHandler.register(LIGHT_GRAY_WOOL, WoolHandler(LIGHT_GRAY_WOOL, LIGHT_GRAY_CARPET))
        StaffHandler.register(CYAN_WOOL, WoolHandler(CYAN_WOOL, CYAN_CARPET))
        StaffHandler.register(PURPLE_WOOL, WoolHandler(PURPLE_WOOL, PURPLE_CARPET))
        StaffHandler.register(BLUE_WOOL, WoolHandler(BLUE_WOOL, BLUE_CARPET))
        StaffHandler.register(BROWN_WOOL, WoolHandler(BROWN_WOOL, BROWN_CARPET))
        StaffHandler.register(GREEN_WOOL, WoolHandler(GREEN_WOOL, GREEN_CARPET))
        StaffHandler.register(RED_WOOL, WoolHandler(RED_WOOL, RED_CARPET))
        StaffHandler.register(BLACK_WOOL, WoolHandler(BLACK_WOOL, BLACK_CARPET))
    }

    companion object {
        @JvmStatic
        lateinit var implementation: AbstractStaffMod
            private set

        @JvmStatic
        private fun initialize(instance: AbstractStaffMod) {
            check(!::implementation.isInitialized) { "Tried to initialize AbstractStaffMod twice" }
            implementation = instance
        }

        @JvmStatic
        @Suppress("UnusedReceiverParameter")
        protected fun Any.initialize() {
        }
    }
}
