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

package opekope2.avm_staff.internal.staff_item_handler

import net.minecraft.block.Block
import net.minecraft.block.Blocks.*
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvents
import opekope2.avm_staff.IStaffMod
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.model.IStaffItemUnbakedModel
import opekope2.avm_staff.api.item.model.UnbakedBlockStateModelSupplier
import java.util.function.Supplier

private fun Item.registerHandler(
    handler: StaffItemHandler,
    itemModelSupplierFactory: Supplier<Supplier<out IStaffItemUnbakedModel>>
) {
    StaffItemHandler.register(Registries.ITEM.getId(this), handler, itemModelSupplierFactory)
}

private fun Item.registerHandler(handler: StaffItemHandler, staffItem: Block) {
    registerHandler(handler) { UnbakedBlockStateModelSupplier(staffItem.defaultState) }
}

@Suppress("unused")
fun registerVanillaStaffItemHandlers() {
    Items.ANVIL.registerHandler(AnvilHandler(Items.CHIPPED_ANVIL::getDefaultStack), ANVIL)
    Items.CHIPPED_ANVIL.registerHandler(AnvilHandler(Items.DAMAGED_ANVIL::getDefaultStack), CHIPPED_ANVIL)
    Items.DAMAGED_ANVIL.registerHandler(AnvilHandler { null }, DAMAGED_ANVIL)

    Items.BELL.registerHandler(BellBlockHandler(), BellBlockHandler.modelSupplierFactory)

    Items.BONE_BLOCK.registerHandler(BoneBlockHandler(), BONE_BLOCK)

    Items.CAMPFIRE.registerHandler(
        CampfireHandler(
            IStaffMod::flamethrowerParticleType,
            CampfireHandler.Properties(1 / 20.0, 5 / 20.0, 1, 0.1)
        ),
        CAMPFIRE
    )
    Items.SOUL_CAMPFIRE.registerHandler(
        CampfireHandler(
            IStaffMod::soulFlamethrowerParticleType,
            CampfireHandler.Properties(2 / 20.0, 10 / 20.0, 2, 0.12)
        ),
        SOUL_CAMPFIRE
    )

    Items.FURNACE.registerHandler(
        FurnaceHandler(RecipeType.SMELTING, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE),
        FurnaceHandler.getModelSupplierFactory(FURNACE)
    )
    Items.BLAST_FURNACE.registerHandler(
        FurnaceHandler(RecipeType.BLASTING, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE),
        FurnaceHandler.getModelSupplierFactory(BLAST_FURNACE)
    )
    Items.SMOKER.registerHandler(
        FurnaceHandler(RecipeType.SMOKING, SoundEvents.BLOCK_SMOKER_SMOKE),
        FurnaceHandler.getModelSupplierFactory(SMOKER)
    )

    Items.LIGHTNING_ROD.registerHandler(LightningRodHandler(), LightningRodHandler.modelSupplier)

    Items.MAGMA_BLOCK.registerHandler(MagmaBlockHandler(), MAGMA_BLOCK)

    Items.SNOW_BLOCK.registerHandler(SnowBlockHandler(), SNOW_BLOCK)

    Items.TNT.registerHandler(TntHandler(), TNT)

    Items.WITHER_SKELETON_SKULL.registerHandler(
        WitherSkeletonSkullHandler(),
        WitherSkeletonSkullHandler.modelSupplierFactory
    )

    Items.WHITE_WOOL.registerHandler(WoolHandler(WHITE_WOOL, WHITE_CARPET), WHITE_WOOL)
    Items.ORANGE_WOOL.registerHandler(WoolHandler(ORANGE_WOOL, ORANGE_CARPET), ORANGE_WOOL)
    Items.MAGENTA_WOOL.registerHandler(WoolHandler(MAGENTA_WOOL, MAGENTA_CARPET), MAGENTA_WOOL)
    Items.LIGHT_BLUE_WOOL.registerHandler(WoolHandler(LIGHT_BLUE_WOOL, LIGHT_BLUE_CARPET), LIGHT_BLUE_WOOL)
    Items.YELLOW_WOOL.registerHandler(WoolHandler(YELLOW_WOOL, YELLOW_CARPET), YELLOW_WOOL)
    Items.LIME_WOOL.registerHandler(WoolHandler(LIME_WOOL, LIME_CARPET), LIME_WOOL)
    Items.PINK_WOOL.registerHandler(WoolHandler(PINK_WOOL, PINK_CARPET), PINK_WOOL)
    Items.GRAY_WOOL.registerHandler(WoolHandler(GRAY_WOOL, GRAY_CARPET), GRAY_WOOL)
    Items.LIGHT_GRAY_WOOL.registerHandler(WoolHandler(LIGHT_GRAY_WOOL, LIGHT_GRAY_CARPET), LIGHT_GRAY_WOOL)
    Items.CYAN_WOOL.registerHandler(WoolHandler(CYAN_WOOL, CYAN_CARPET), CYAN_WOOL)
    Items.PURPLE_WOOL.registerHandler(WoolHandler(PURPLE_WOOL, PURPLE_CARPET), PURPLE_WOOL)
    Items.BLUE_WOOL.registerHandler(WoolHandler(BLUE_WOOL, BLUE_CARPET), BLUE_WOOL)
    Items.BROWN_WOOL.registerHandler(WoolHandler(BROWN_WOOL, BROWN_CARPET), BROWN_WOOL)
    Items.GREEN_WOOL.registerHandler(WoolHandler(GREEN_WOOL, GREEN_CARPET), GREEN_WOOL)
    Items.RED_WOOL.registerHandler(WoolHandler(RED_WOOL, RED_CARPET), RED_WOOL)
    Items.BLACK_WOOL.registerHandler(WoolHandler(BLACK_WOOL, BLACK_CARPET), BLACK_WOOL)
}
