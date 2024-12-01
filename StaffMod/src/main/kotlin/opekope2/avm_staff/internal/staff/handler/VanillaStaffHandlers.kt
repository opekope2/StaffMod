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

import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Items.*
import net.minecraft.recipe.RecipeType
import net.minecraft.sound.SoundEvents
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import opekope2.avm_staff.api.item.renderer.BlockStateStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.StaffItemRenderer
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.ParticleTypes
import opekope2.avm_staff.internal.staff.item_renderer.BellStaffItemRenderer
import opekope2.avm_staff.internal.staff.item_renderer.FurnaceStaffItemRenderer
import opekope2.avm_staff.internal.staff.item_renderer.LightningRodStaffItemRenderer
import opekope2.avm_staff.internal.staff.item_renderer.WitherSkeletonSkullStaffItemRenderer

fun registerVanillaStaffHandlers() {
    StaffHandler.register(ANVIL, AnvilHandler(CHIPPED_ANVIL))
    StaffHandler.register(CHIPPED_ANVIL, AnvilHandler(DAMAGED_ANVIL))
    StaffHandler.register(DAMAGED_ANVIL, AnvilHandler(null))

    StaffHandler.register(BELL, BellHandler())

    StaffHandler.register(BONE_BLOCK, BoneBlockHandler())

    StaffHandler.register(CAKE, CakeHandler())

    StaffHandler.register(
        CAMPFIRE,
        CampfireHandler(
            CampfireHandler.Parameters(5 / 20.0, 1 / 20.0, 4f, 1, 0.1, ParticleTypes.FLAME)
        )
    )
    StaffHandler.register(
        SOUL_CAMPFIRE,
        CampfireHandler(
            CampfireHandler.Parameters(10 / 20.0, 2 / 20.0, 6f, 2, 0.12, ParticleTypes.SOUL_FIRE_FLAME)
        )
    )

    StaffHandler.register(COMMAND_BLOCK, StaffHandler.Fallback) // TODO

    StaffHandler.register(DIAMOND_BLOCK, DiamondBlockHandler())

    StaffHandler.register(
        FURNACE,
        FurnaceHandler(RecipeType.SMELTING, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE)
    )
    StaffHandler.register(
        BLAST_FURNACE,
        FurnaceHandler(RecipeType.BLASTING, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE)
    )
    StaffHandler.register(
        SMOKER,
        FurnaceHandler(RecipeType.SMOKING, SoundEvents.BLOCK_SMOKER_SMOKE)
    )

    StaffHandler.register(GOLD_BLOCK, GoldBlockHandler())

    StaffHandler.register(LIGHTNING_ROD, LightningRodHandler())

    StaffHandler.register(MAGMA_BLOCK, MagmaBlockHandler())

    StaffHandler.register(NETHERITE_BLOCK, NetheriteBlockHandler())

    StaffHandler.register(SNOW_BLOCK, SnowBlockHandler())

    StaffHandler.register(TNT, TntHandler())

    StaffHandler.register(WITHER_SKELETON_SKULL, WitherSkeletonSkullHandler())

    StaffHandler.register(WHITE_WOOL, WoolHandler(WHITE_WOOL as BlockItem, WHITE_CARPET as BlockItem))
    StaffHandler.register(ORANGE_WOOL, WoolHandler(ORANGE_WOOL as BlockItem, ORANGE_CARPET as BlockItem))
    StaffHandler.register(MAGENTA_WOOL, WoolHandler(MAGENTA_WOOL as BlockItem, MAGENTA_CARPET as BlockItem))
    StaffHandler.register(LIGHT_BLUE_WOOL, WoolHandler(LIGHT_BLUE_WOOL as BlockItem, LIGHT_BLUE_CARPET as BlockItem))
    StaffHandler.register(YELLOW_WOOL, WoolHandler(YELLOW_WOOL as BlockItem, YELLOW_CARPET as BlockItem))
    StaffHandler.register(LIME_WOOL, WoolHandler(LIME_WOOL as BlockItem, LIME_CARPET as BlockItem))
    StaffHandler.register(PINK_WOOL, WoolHandler(PINK_WOOL as BlockItem, PINK_CARPET as BlockItem))
    StaffHandler.register(GRAY_WOOL, WoolHandler(GRAY_WOOL as BlockItem, GRAY_CARPET as BlockItem))
    StaffHandler.register(LIGHT_GRAY_WOOL, WoolHandler(LIGHT_GRAY_WOOL as BlockItem, LIGHT_GRAY_CARPET as BlockItem))
    StaffHandler.register(CYAN_WOOL, WoolHandler(CYAN_WOOL as BlockItem, CYAN_CARPET as BlockItem))
    StaffHandler.register(PURPLE_WOOL, WoolHandler(PURPLE_WOOL as BlockItem, PURPLE_CARPET as BlockItem))
    StaffHandler.register(BLUE_WOOL, WoolHandler(BLUE_WOOL as BlockItem, BLUE_CARPET as BlockItem))
    StaffHandler.register(BROWN_WOOL, WoolHandler(BROWN_WOOL as BlockItem, BROWN_CARPET as BlockItem))
    StaffHandler.register(GREEN_WOOL, WoolHandler(GREEN_WOOL as BlockItem, GREEN_CARPET as BlockItem))
    StaffHandler.register(RED_WOOL, WoolHandler(RED_WOOL as BlockItem, RED_CARPET as BlockItem))
    StaffHandler.register(BLACK_WOOL, WoolHandler(BLACK_WOOL as BlockItem, BLACK_CARPET as BlockItem))
}

@OnlyIn(Dist.CLIENT)
fun registerVanillaStaffItemRenderers() {
    StaffItemRenderer.register(ANVIL, BlockStateStaffItemRenderer(Blocks.ANVIL))
    StaffItemRenderer.register(CHIPPED_ANVIL, BlockStateStaffItemRenderer(Blocks.CHIPPED_ANVIL))
    StaffItemRenderer.register(DAMAGED_ANVIL, BlockStateStaffItemRenderer(Blocks.DAMAGED_ANVIL))

    StaffItemRenderer.register(BELL, BellStaffItemRenderer())

    StaffItemRenderer.register(BONE_BLOCK, BlockStateStaffItemRenderer(Blocks.BONE_BLOCK))

    StaffItemRenderer.register(CAKE, BlockStateStaffItemRenderer(Blocks.CAKE))

    StaffItemRenderer.register(CAMPFIRE, BlockStateStaffItemRenderer(Blocks.CAMPFIRE))
    StaffItemRenderer.register(SOUL_CAMPFIRE, BlockStateStaffItemRenderer(Blocks.SOUL_CAMPFIRE))

    StaffItemRenderer.register(COMMAND_BLOCK, BlockStateStaffItemRenderer(Blocks.COMMAND_BLOCK))

    StaffItemRenderer.register(DIAMOND_BLOCK, BlockStateStaffItemRenderer(Blocks.DIAMOND_BLOCK))

    StaffItemRenderer.register(FURNACE, FurnaceStaffItemRenderer(Blocks.FURNACE))
    StaffItemRenderer.register(BLAST_FURNACE, FurnaceStaffItemRenderer(Blocks.BLAST_FURNACE))
    StaffItemRenderer.register(SMOKER, FurnaceStaffItemRenderer(Blocks.SMOKER))

    StaffItemRenderer.register(GOLD_BLOCK, BlockStateStaffItemRenderer(Blocks.GOLD_BLOCK))

    StaffItemRenderer.register(LIGHTNING_ROD, LightningRodStaffItemRenderer())

    StaffItemRenderer.register(MAGMA_BLOCK, BlockStateStaffItemRenderer(Blocks.MAGMA_BLOCK))

    StaffItemRenderer.register(NETHERITE_BLOCK, BlockStateStaffItemRenderer(Blocks.NETHERITE_BLOCK))

    StaffItemRenderer.register(SNOW_BLOCK, BlockStateStaffItemRenderer(Blocks.SNOW_BLOCK))

    StaffItemRenderer.register(TNT, BlockStateStaffItemRenderer(Blocks.TNT))

    StaffItemRenderer.register(WITHER_SKELETON_SKULL, WitherSkeletonSkullStaffItemRenderer())

    StaffItemRenderer.register(WHITE_WOOL, BlockStateStaffItemRenderer(Blocks.WHITE_WOOL))
    StaffItemRenderer.register(ORANGE_WOOL, BlockStateStaffItemRenderer(Blocks.ORANGE_WOOL))
    StaffItemRenderer.register(MAGENTA_WOOL, BlockStateStaffItemRenderer(Blocks.MAGENTA_WOOL))
    StaffItemRenderer.register(LIGHT_BLUE_WOOL, BlockStateStaffItemRenderer(Blocks.LIGHT_BLUE_WOOL))
    StaffItemRenderer.register(YELLOW_WOOL, BlockStateStaffItemRenderer(Blocks.YELLOW_WOOL))
    StaffItemRenderer.register(LIME_WOOL, BlockStateStaffItemRenderer(Blocks.LIME_WOOL))
    StaffItemRenderer.register(PINK_WOOL, BlockStateStaffItemRenderer(Blocks.PINK_WOOL))
    StaffItemRenderer.register(GRAY_WOOL, BlockStateStaffItemRenderer(Blocks.GRAY_WOOL))
    StaffItemRenderer.register(LIGHT_GRAY_WOOL, BlockStateStaffItemRenderer(Blocks.LIGHT_GRAY_WOOL))
    StaffItemRenderer.register(CYAN_WOOL, BlockStateStaffItemRenderer(Blocks.CYAN_WOOL))
    StaffItemRenderer.register(PURPLE_WOOL, BlockStateStaffItemRenderer(Blocks.PURPLE_WOOL))
    StaffItemRenderer.register(BLUE_WOOL, BlockStateStaffItemRenderer(Blocks.BLUE_WOOL))
    StaffItemRenderer.register(BROWN_WOOL, BlockStateStaffItemRenderer(Blocks.BROWN_WOOL))
    StaffItemRenderer.register(GREEN_WOOL, BlockStateStaffItemRenderer(Blocks.GREEN_WOOL))
    StaffItemRenderer.register(RED_WOOL, BlockStateStaffItemRenderer(Blocks.RED_WOOL))
    StaffItemRenderer.register(BLACK_WOOL, BlockStateStaffItemRenderer(Blocks.BLACK_WOOL))
}
