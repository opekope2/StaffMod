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

package opekope2.avm_staff.content

import dev.architectury.registry.level.entity.trade.SimpleTrade
import net.minecraft.item.ItemStack
import net.minecraft.village.TradedItem
import java.util.*

/**
 * AvM Staff mod villager trades.
 */
// Do not use RegistrySuppliers because the game will crash on save
object VillagerTrades {
    /**
     * Expert level armorer [Items.faintScepterOfFriendshipHead] trade.
     */
    @JvmStatic
    val armorer4_faintScepterOfFriendshipHead = SimpleTrade(
        TradedItem(net.minecraft.item.Items.EMERALD, 32),
        Optional.of(TradedItem(Items.scepterOfFriendshipIngredient, 5)),
        ItemStack(Items.faintScepterOfFriendshipHead),
        3,
        15,
        .2f
    )

    /**
     * Master level armorer [Items.faintScepterOfFriendship] trade.
     */
    @JvmStatic
    val armorer5_faintScepterOfFriendship = SimpleTrade(
        TradedItem(Items.faintStaffRod, 2),
        Optional.of(TradedItem(Items.faintScepterOfFriendshipHead, 1)),
        ItemStack(Items.faintScepterOfFriendship),
        3,
        30,
        0f
    )

    /**
     * Master level armorer [Items.scepterOfFriendship] trade.
     */
    @JvmStatic
    val cleric5_scepterOfFriendship = SimpleTrade(
        TradedItem(Items.faintScepterOfFriendship, 1),
        Optional.of(TradedItem(net.minecraft.item.Items.LAPIS_LAZULI, 3)),
        ItemStack(Items.scepterOfFriendship),
        3,
        30,
        0f
    )
}
