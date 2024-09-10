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

package opekope2.avm_staff.internal.staff.handler

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items.*
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvents
import opekope2.avm_staff.api.flamethrowerParticleType
import opekope2.avm_staff.api.item.renderer.BlockStateStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.IStaffItemRenderer
import opekope2.avm_staff.api.soulFlamethrowerParticleType
import opekope2.avm_staff.api.staff.StaffHandler

private fun Item.registerHandler(handler: StaffHandler) {
    StaffHandler.register(Registries.ITEM.getId(this), handler)
}

fun registerVanillaStaffHandlers() {
    ANVIL.registerHandler(AnvilHandler(CHIPPED_ANVIL))
    CHIPPED_ANVIL.registerHandler(AnvilHandler(DAMAGED_ANVIL))
    DAMAGED_ANVIL.registerHandler(AnvilHandler(null))

    BELL.registerHandler(BellBlockHandler())

    BONE_BLOCK.registerHandler(BoneBlockHandler())

    CAKE.registerHandler(CakeHandler())

    CAMPFIRE.registerHandler(
        CampfireHandler(
            flamethrowerParticleType,
            CampfireHandler.Properties(1 / 20.0, 5 / 20.0, 4f, 1, 0.1)
        )
    )
    SOUL_CAMPFIRE.registerHandler(
        CampfireHandler(
            soulFlamethrowerParticleType,
            CampfireHandler.Properties(2 / 20.0, 10 / 20.0, 6f, 2, 0.12)
        )
    )

    // TODO command block

    FURNACE.registerHandler(
        FurnaceHandler(RecipeType.SMELTING, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE)
    )
    BLAST_FURNACE.registerHandler(
        FurnaceHandler(RecipeType.BLASTING, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE)
    )
    SMOKER.registerHandler(
        FurnaceHandler(RecipeType.SMOKING, SoundEvents.BLOCK_SMOKER_SMOKE)
    )

    GOLD_BLOCK.registerHandler(GoldBlockHandler())

    LIGHTNING_ROD.registerHandler(LightningRodHandler())

    MAGMA_BLOCK.registerHandler(MagmaBlockHandler())

    NETHERITE_BLOCK.registerHandler(NetheriteBlockHandler())

    SNOW_BLOCK.registerHandler(SnowBlockHandler())

    TNT.registerHandler(TntHandler())

    WITHER_SKELETON_SKULL.registerHandler(
        WitherSkeletonSkullHandler()
    )

    WHITE_WOOL.registerHandler(WoolHandler(WHITE_WOOL as BlockItem, WHITE_CARPET as BlockItem))
    ORANGE_WOOL.registerHandler(WoolHandler(ORANGE_WOOL as BlockItem, ORANGE_CARPET as BlockItem))
    MAGENTA_WOOL.registerHandler(WoolHandler(MAGENTA_WOOL as BlockItem, MAGENTA_CARPET as BlockItem))
    LIGHT_BLUE_WOOL.registerHandler(WoolHandler(LIGHT_BLUE_WOOL as BlockItem, LIGHT_BLUE_CARPET as BlockItem))
    YELLOW_WOOL.registerHandler(WoolHandler(YELLOW_WOOL as BlockItem, YELLOW_CARPET as BlockItem))
    LIME_WOOL.registerHandler(WoolHandler(LIME_WOOL as BlockItem, LIME_CARPET as BlockItem))
    PINK_WOOL.registerHandler(WoolHandler(PINK_WOOL as BlockItem, PINK_CARPET as BlockItem))
    GRAY_WOOL.registerHandler(WoolHandler(GRAY_WOOL as BlockItem, GRAY_CARPET as BlockItem))
    LIGHT_GRAY_WOOL.registerHandler(WoolHandler(LIGHT_GRAY_WOOL as BlockItem, LIGHT_GRAY_CARPET as BlockItem))
    CYAN_WOOL.registerHandler(WoolHandler(CYAN_WOOL as BlockItem, CYAN_CARPET as BlockItem))
    PURPLE_WOOL.registerHandler(WoolHandler(PURPLE_WOOL as BlockItem, PURPLE_CARPET as BlockItem))
    BLUE_WOOL.registerHandler(WoolHandler(BLUE_WOOL as BlockItem, BLUE_CARPET as BlockItem))
    BROWN_WOOL.registerHandler(WoolHandler(BROWN_WOOL as BlockItem, BROWN_CARPET as BlockItem))
    GREEN_WOOL.registerHandler(WoolHandler(GREEN_WOOL as BlockItem, GREEN_CARPET as BlockItem))
    RED_WOOL.registerHandler(WoolHandler(RED_WOOL as BlockItem, RED_CARPET as BlockItem))
    BLACK_WOOL.registerHandler(WoolHandler(BLACK_WOOL as BlockItem, BLACK_CARPET as BlockItem))
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
    ANVIL.registerStaffItemRenderer(Blocks.ANVIL)
    CHIPPED_ANVIL.registerStaffItemRenderer(Blocks.CHIPPED_ANVIL)
    DAMAGED_ANVIL.registerStaffItemRenderer(Blocks.DAMAGED_ANVIL)

    BELL.registerStaffItemRenderer(BellBlockHandler.BellStaffItemRenderer())

    BONE_BLOCK.registerStaffItemRenderer(Blocks.BONE_BLOCK)

    CAKE.registerStaffItemRenderer(Blocks.CAKE)

    CAMPFIRE.registerStaffItemRenderer(Blocks.CAMPFIRE)
    SOUL_CAMPFIRE.registerStaffItemRenderer(Blocks.SOUL_CAMPFIRE)

    COMMAND_BLOCK.registerStaffItemRenderer(Blocks.COMMAND_BLOCK)

    FURNACE.registerStaffItemRenderer(FurnaceHandler.FurnaceStaffItemRenderer(Blocks.FURNACE))
    BLAST_FURNACE.registerStaffItemRenderer(FurnaceHandler.FurnaceStaffItemRenderer(Blocks.BLAST_FURNACE))
    SMOKER.registerStaffItemRenderer(FurnaceHandler.FurnaceStaffItemRenderer(Blocks.SMOKER))

    GOLD_BLOCK.registerStaffItemRenderer(Blocks.GOLD_BLOCK)

    LIGHTNING_ROD.registerStaffItemRenderer(LightningRodHandler.LightningRodStaffItemRenderer())

    MAGMA_BLOCK.registerStaffItemRenderer(Blocks.MAGMA_BLOCK)

    NETHERITE_BLOCK.registerStaffItemRenderer(Blocks.NETHERITE_BLOCK)

    SNOW_BLOCK.registerStaffItemRenderer(Blocks.SNOW_BLOCK)

    TNT.registerStaffItemRenderer(Blocks.TNT)

    WITHER_SKELETON_SKULL.registerStaffItemRenderer(WitherSkeletonSkullHandler.WitherSkeletonSkullStaffItemRenderer())

    WHITE_WOOL.registerStaffItemRenderer(Blocks.WHITE_WOOL)
    ORANGE_WOOL.registerStaffItemRenderer(Blocks.ORANGE_WOOL)
    MAGENTA_WOOL.registerStaffItemRenderer(Blocks.MAGENTA_WOOL)
    LIGHT_BLUE_WOOL.registerStaffItemRenderer(Blocks.LIGHT_BLUE_WOOL)
    YELLOW_WOOL.registerStaffItemRenderer(Blocks.YELLOW_WOOL)
    LIME_WOOL.registerStaffItemRenderer(Blocks.LIME_WOOL)
    PINK_WOOL.registerStaffItemRenderer(Blocks.PINK_WOOL)
    GRAY_WOOL.registerStaffItemRenderer(Blocks.GRAY_WOOL)
    LIGHT_GRAY_WOOL.registerStaffItemRenderer(Blocks.LIGHT_GRAY_WOOL)
    CYAN_WOOL.registerStaffItemRenderer(Blocks.CYAN_WOOL)
    PURPLE_WOOL.registerStaffItemRenderer(Blocks.PURPLE_WOOL)
    BLUE_WOOL.registerStaffItemRenderer(Blocks.BLUE_WOOL)
    BROWN_WOOL.registerStaffItemRenderer(Blocks.BROWN_WOOL)
    GREEN_WOOL.registerStaffItemRenderer(Blocks.GREEN_WOOL)
    RED_WOOL.registerStaffItemRenderer(Blocks.RED_WOOL)
    BLACK_WOOL.registerStaffItemRenderer(Blocks.BLACK_WOOL)
}
