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
import net.minecraft.item.Items
import opekope2.avm_staff.api.initializer.IStaffModInitializationContext

@Suppress("unused")
fun register(context: IStaffModInitializationContext) {
    AnvilHandler.registerStaffItemHandler(Items.ANVIL as BlockItem, Items.CHIPPED_ANVIL, context)
    AnvilHandler.registerStaffItemHandler(Items.CHIPPED_ANVIL as BlockItem, Items.DAMAGED_ANVIL, context)
    AnvilHandler.registerStaffItemHandler(Items.DAMAGED_ANVIL as BlockItem, null, context)

    BoneBlockHandler.registerStaffItemHandler(Items.BONE_BLOCK, context)


    MagmaBlockHandler.registerStaffItemHandler(Items.MAGMA_BLOCK, context)

    SnowBlockHandler.registerStaffItemHandler(Items.SNOW_BLOCK, context)

    WoolHandler.registerStaffItemHandler(Items.WHITE_WOOL as BlockItem, Items.WHITE_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.ORANGE_WOOL as BlockItem, Items.ORANGE_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.MAGENTA_WOOL as BlockItem, Items.MAGENTA_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(
        Items.LIGHT_BLUE_WOOL as BlockItem, Items.LIGHT_BLUE_CARPET as BlockItem, context
    )
    WoolHandler.registerStaffItemHandler(Items.YELLOW_WOOL as BlockItem, Items.YELLOW_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.LIME_WOOL as BlockItem, Items.LIME_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.PINK_WOOL as BlockItem, Items.PINK_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.GRAY_WOOL as BlockItem, Items.GRAY_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(
        Items.LIGHT_GRAY_WOOL as BlockItem, Items.LIGHT_GRAY_CARPET as BlockItem, context
    )
    WoolHandler.registerStaffItemHandler(Items.CYAN_WOOL as BlockItem, Items.CYAN_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.PURPLE_WOOL as BlockItem, Items.PURPLE_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.BLUE_WOOL as BlockItem, Items.BLUE_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.BROWN_WOOL as BlockItem, Items.BROWN_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.GREEN_WOOL as BlockItem, Items.GREEN_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.RED_WOOL as BlockItem, Items.RED_CARPET as BlockItem, context)
    WoolHandler.registerStaffItemHandler(Items.BLACK_WOOL as BlockItem, Items.BLACK_CARPET as BlockItem, context)
}
