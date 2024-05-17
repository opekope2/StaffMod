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

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.block.Blocks.*
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvents
import opekope2.avm_staff.api.flamethrowerParticleType
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.renderer.BlockStateStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.IStaffItemRenderer
import opekope2.avm_staff.api.soulFlamethrowerParticleType

private fun Item.registerHandler(handler: StaffItemHandler) {
    StaffItemHandler.register(Registries.ITEM.getId(this), handler)
}

@Suppress("unused")
fun registerVanillaStaffItemHandlers() {
    Items.ANVIL.registerHandler(AnvilHandler(Items.CHIPPED_ANVIL::getDefaultStack))
    Items.CHIPPED_ANVIL.registerHandler(AnvilHandler(Items.DAMAGED_ANVIL::getDefaultStack))
    Items.DAMAGED_ANVIL.registerHandler(AnvilHandler { null })

    Items.BELL.registerHandler(BellBlockHandler())

    Items.BONE_BLOCK.registerHandler(BoneBlockHandler())

    Items.CAMPFIRE.registerHandler(
        CampfireHandler(flamethrowerParticleType, CampfireHandler.Properties(1 / 20.0, 5 / 20.0, 1, 0.1))
    )
    Items.SOUL_CAMPFIRE.registerHandler(
        CampfireHandler(soulFlamethrowerParticleType, CampfireHandler.Properties(2 / 20.0, 10 / 20.0, 2, 0.12))
    )

    Items.FURNACE.registerHandler(
        FurnaceHandler(RecipeType.SMELTING, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE)
    )
    Items.BLAST_FURNACE.registerHandler(
        FurnaceHandler(RecipeType.BLASTING, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE)
    )
    Items.SMOKER.registerHandler(
        FurnaceHandler(RecipeType.SMOKING, SoundEvents.BLOCK_SMOKER_SMOKE)
    )

    Items.LIGHTNING_ROD.registerHandler(LightningRodHandler())

    Items.MAGMA_BLOCK.registerHandler(MagmaBlockHandler())

    Items.SNOW_BLOCK.registerHandler(SnowBlockHandler())

    Items.TNT.registerHandler(TntHandler())

    Items.WITHER_SKELETON_SKULL.registerHandler(
        WitherSkeletonSkullHandler()
    )

    Items.WHITE_WOOL.registerHandler(WoolHandler(WHITE_WOOL, WHITE_CARPET))
    Items.ORANGE_WOOL.registerHandler(WoolHandler(ORANGE_WOOL, ORANGE_CARPET))
    Items.MAGENTA_WOOL.registerHandler(WoolHandler(MAGENTA_WOOL, MAGENTA_CARPET))
    Items.LIGHT_BLUE_WOOL.registerHandler(WoolHandler(LIGHT_BLUE_WOOL, LIGHT_BLUE_CARPET))
    Items.YELLOW_WOOL.registerHandler(WoolHandler(YELLOW_WOOL, YELLOW_CARPET))
    Items.LIME_WOOL.registerHandler(WoolHandler(LIME_WOOL, LIME_CARPET))
    Items.PINK_WOOL.registerHandler(WoolHandler(PINK_WOOL, PINK_CARPET))
    Items.GRAY_WOOL.registerHandler(WoolHandler(GRAY_WOOL, GRAY_CARPET))
    Items.LIGHT_GRAY_WOOL.registerHandler(WoolHandler(LIGHT_GRAY_WOOL, LIGHT_GRAY_CARPET))
    Items.CYAN_WOOL.registerHandler(WoolHandler(CYAN_WOOL, CYAN_CARPET))
    Items.PURPLE_WOOL.registerHandler(WoolHandler(PURPLE_WOOL, PURPLE_CARPET))
    Items.BLUE_WOOL.registerHandler(WoolHandler(BLUE_WOOL, BLUE_CARPET))
    Items.BROWN_WOOL.registerHandler(WoolHandler(BROWN_WOOL, BROWN_CARPET))
    Items.GREEN_WOOL.registerHandler(WoolHandler(GREEN_WOOL, GREEN_CARPET))
    Items.RED_WOOL.registerHandler(WoolHandler(RED_WOOL, RED_CARPET))
    Items.BLACK_WOOL.registerHandler(WoolHandler(BLACK_WOOL, BLACK_CARPET))
}

@Environment(EnvType.CLIENT)
private fun Item.registerStaffItemRenderer(renderer: IStaffItemRenderer) {
    IStaffItemRenderer.register(Registries.ITEM.getId(this), renderer)
}

@Environment(EnvType.CLIENT)
private fun Item.registerStaffItemRenderer(staffItem: Block) {
    registerStaffItemRenderer(BlockStateStaffItemRenderer(staffItem.defaultState))
}

@Environment(EnvType.CLIENT)
fun registerVanillaStaffItemRenderers() {
    Items.ANVIL.registerStaffItemRenderer(ANVIL)
    Items.CHIPPED_ANVIL.registerStaffItemRenderer(CHIPPED_ANVIL)
    Items.DAMAGED_ANVIL.registerStaffItemRenderer(DAMAGED_ANVIL)

    Items.BELL.registerStaffItemRenderer(BellBlockHandler.BellStaffItemRenderer())

    Items.BONE_BLOCK.registerStaffItemRenderer(BONE_BLOCK)

    Items.CAMPFIRE.registerStaffItemRenderer(CAMPFIRE)
    Items.SOUL_CAMPFIRE.registerStaffItemRenderer(SOUL_CAMPFIRE)

    Items.FURNACE.registerStaffItemRenderer(FurnaceHandler.FurnaceStaffItemRenderer(FURNACE))
    Items.BLAST_FURNACE.registerStaffItemRenderer(FurnaceHandler.FurnaceStaffItemRenderer(BLAST_FURNACE))
    Items.SMOKER.registerStaffItemRenderer(FurnaceHandler.FurnaceStaffItemRenderer(SMOKER))

    Items.LIGHTNING_ROD.registerStaffItemRenderer(LightningRodHandler.LightningRodStaffItemRenderer())

    Items.MAGMA_BLOCK.registerStaffItemRenderer(MAGMA_BLOCK)

    Items.SNOW_BLOCK.registerStaffItemRenderer(SNOW_BLOCK)

    Items.TNT.registerStaffItemRenderer(TNT)

    Items.WITHER_SKELETON_SKULL.registerStaffItemRenderer(WitherSkeletonSkullHandler.WitherSkeletonSkullStaffItemRenderer())

    Items.WHITE_WOOL.registerStaffItemRenderer(WHITE_WOOL)
    Items.ORANGE_WOOL.registerStaffItemRenderer(ORANGE_WOOL)
    Items.MAGENTA_WOOL.registerStaffItemRenderer(MAGENTA_WOOL)
    Items.LIGHT_BLUE_WOOL.registerStaffItemRenderer(LIGHT_BLUE_WOOL)
    Items.YELLOW_WOOL.registerStaffItemRenderer(YELLOW_WOOL)
    Items.LIME_WOOL.registerStaffItemRenderer(LIME_WOOL)
    Items.PINK_WOOL.registerStaffItemRenderer(PINK_WOOL)
    Items.GRAY_WOOL.registerStaffItemRenderer(GRAY_WOOL)
    Items.LIGHT_GRAY_WOOL.registerStaffItemRenderer(LIGHT_GRAY_WOOL)
    Items.CYAN_WOOL.registerStaffItemRenderer(CYAN_WOOL)
    Items.PURPLE_WOOL.registerStaffItemRenderer(PURPLE_WOOL)
    Items.BLUE_WOOL.registerStaffItemRenderer(BLUE_WOOL)
    Items.BROWN_WOOL.registerStaffItemRenderer(BROWN_WOOL)
    Items.GREEN_WOOL.registerStaffItemRenderer(GREEN_WOOL)
    Items.RED_WOOL.registerStaffItemRenderer(RED_WOOL)
    Items.BLACK_WOOL.registerStaffItemRenderer(BLACK_WOOL)

}
