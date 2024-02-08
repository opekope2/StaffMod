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

import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items.*
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvents
import opekope2.avm_staff.api.item.StaffItemHandler

private fun Item.registerHandler(handler: StaffItemHandler) {
    StaffItemHandler.register(Registries.ITEM.getId(this), handler)
}

@Suppress("unused")
fun registerVanillaStaffItemHandlers() {
    ANVIL.registerHandler(AnvilHandler(ANVIL as BlockItem, CHIPPED_ANVIL::getDefaultStack))
    CHIPPED_ANVIL.registerHandler(AnvilHandler(CHIPPED_ANVIL as BlockItem, DAMAGED_ANVIL::getDefaultStack))
    DAMAGED_ANVIL.registerHandler(AnvilHandler(DAMAGED_ANVIL as BlockItem) { null })

    BELL.registerHandler(BellBlockHandler())

    BONE_BLOCK.registerHandler(BoneBlockHandler())

    FURNACE.registerHandler(
        FurnaceHandler(FURNACE as BlockItem, RecipeType.SMELTING, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE)
    )
    BLAST_FURNACE.registerHandler(
        FurnaceHandler(BLAST_FURNACE as BlockItem, RecipeType.BLASTING, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE)
    )
    SMOKER.registerHandler(
        FurnaceHandler(SMOKER as BlockItem, RecipeType.SMOKING, SoundEvents.BLOCK_SMOKER_SMOKE)
    )

    LIGHTNING_ROD.registerHandler(LightningRodHandler())

    MAGMA_BLOCK.registerHandler(MagmaBlockHandler())

    SNOW_BLOCK.registerHandler(SnowBlockHandler())

    TNT.registerHandler(TntHandler())

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
