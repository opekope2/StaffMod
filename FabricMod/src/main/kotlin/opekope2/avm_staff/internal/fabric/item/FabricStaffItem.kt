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

package opekope2.avm_staff.internal.fabric.item

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.item.v1.FabricItem
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.item.renderer.StaffRenderer
import opekope2.avm_staff.util.itemInStaff
import opekope2.avm_staff.util.staffHandlerOrDefault

class FabricStaffItem(settings: Item.Settings) : StaffItem(settings), FabricItem {
    init {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            BuiltinItemRendererRegistry.INSTANCE.register(this, StaffRenderer::renderStaff)
        }
    }

    override fun allowComponentsUpdateAnimation(
        player: PlayerEntity,
        hand: Hand,
        oldStack: ItemStack,
        newStack: ItemStack
    ): Boolean {
        val oldHandler = oldStack.itemInStaff.staffHandlerOrDefault
        val newHandler = newStack.itemInStaff.staffHandlerOrDefault

        return if (oldHandler !== newHandler) true
        else oldHandler.allowComponentsUpdateAnimation(oldStack, newStack, player, hand)
    }
}
