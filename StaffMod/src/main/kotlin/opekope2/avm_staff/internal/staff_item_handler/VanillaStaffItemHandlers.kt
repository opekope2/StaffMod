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

import net.minecraft.item.Items
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.initializer.IStaffModInitializationContext

@Suppress("unused")
fun register(context: IStaffModInitializationContext) {
    AnvilHandler.registerStaffItemHandler(Identifier("anvil"), Items.CHIPPED_ANVIL::getDefaultStack, context)
    AnvilHandler.registerStaffItemHandler(Identifier("chipped_anvil"), Items.DAMAGED_ANVIL::getDefaultStack, context)
    AnvilHandler.registerStaffItemHandler(Identifier("damaged_anvil"), { null }, context)

    BoneBlockHandler.registerStaffItemHandler(context)

    MagmaBlockHandler.registerStaffItemHandler(context)

    SnowBlockHandler.registerStaffItemHandler(context)

    WoolHandler.registerStaffItemHandler(Identifier("white_wool"), Identifier("white_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("orange_wool"), Identifier("orange_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("magenta_wool"), Identifier("magenta_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("light_blue_wool"), Identifier("light_blue_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("yellow_wool"), Identifier("yellow_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("lime_wool"), Identifier("lime_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("pink_wool"), Identifier("pink_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("gray_wool"), Identifier("gray_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("light_gray_wool"), Identifier("light_gray_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("cyan_wool"), Identifier("cyan_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("purple_wool"), Identifier("purple_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("blue_wool"), Identifier("blue_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("brown_wool"), Identifier("brown_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("green_wool"), Identifier("green_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("red_wool"), Identifier("red_carpet"), context)
    WoolHandler.registerStaffItemHandler(Identifier("black_wool"), Identifier("black_carpet"), context)
}
