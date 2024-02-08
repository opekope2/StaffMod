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

package opekope2.avm_staff.internal.forge

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS
import net.minecraft.item.ItemGroups
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import opekope2.avm_staff.internal.event_handler.ADD_REMOVE_KEYBINDING
import opekope2.avm_staff.internal.forge.item.model.StaffItemModel
import opekope2.avm_staff.internal.event_handler.handleKeyBindings
import opekope2.avm_staff.internal.platform.forge.getStaffMod
import opekope2.avm_staff.util.MOD_ID
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@OnlyIn(Dist.CLIENT)
object StaffModClient {
    fun initializeClient() {
        MOD_BUS.register(this)
        EVENT_BUS.register(javaClass)
    }

    @SubscribeEvent
    fun addItemsToItemGroups(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey === ItemGroups.TOOLS) {
            event.entries.putAfter(
                ItemStack(Items.NETHERITE_HOE),
                ItemStack(getStaffMod().staffItem),
                PARENT_AND_SEARCH_TABS
            )
        } else if (event.tabKey === ItemGroups.COMBAT) {
            event.entries.putAfter(
                ItemStack(Items.TRIDENT),
                ItemStack(getStaffMod().staffItem),
                PARENT_AND_SEARCH_TABS
            )
        }
    }

    @SubscribeEvent
    fun registerKeyBindings(event: RegisterKeyMappingsEvent) {
        event.register(ADD_REMOVE_KEYBINDING)
    }

    @JvmStatic
    @SubscribeEvent
    fun handleStaffKeybinding(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        handleKeyBindings(MinecraftClient.getInstance())
    }

    @SubscribeEvent
    fun modifyModelAfterBake(event: ModelEvent.ModifyBakingResult) {
        val id = ModelIdentifier(MOD_ID, "staff", "inventory")

        event.models[id] = StaffItemModel(event.models[id]!!)
    }
}
