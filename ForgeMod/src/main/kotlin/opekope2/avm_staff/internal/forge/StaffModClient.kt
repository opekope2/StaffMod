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
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.item.ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS
import net.minecraft.item.ItemGroups
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import opekope2.avm_staff.IStaffMod
import opekope2.avm_staff.api.particle.FlamethrowerParticle
import opekope2.avm_staff.internal.event_handler.ADD_REMOVE_KEYBINDING
import opekope2.avm_staff.internal.event_handler.handleKeyBindings
import opekope2.avm_staff.internal.registerModelPredicateProviders
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@OnlyIn(Dist.CLIENT)
object StaffModClient {
    fun initializeClient() {
        MOD_BUS.register(this)
        FORGE_BUS.register(javaClass)
    }

    @SubscribeEvent
    fun initializeClient(event: FMLClientSetupEvent) {
        event.enqueueWork {
            registerModelPredicateProviders(ModelPredicateProviderRegistry::register)
        }
    }

    @SubscribeEvent
    fun addItemsToItemGroups(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey === ItemGroups.TOOLS) {
            event.entries.putAfter(
                ItemStack(Items.NETHERITE_HOE),
                ItemStack(StaffMod.staffItem),
                PARENT_AND_SEARCH_TABS
            )
        } else if (event.tabKey === ItemGroups.COMBAT) {
            event.entries.putAfter(
                ItemStack(Items.TRIDENT),
                ItemStack(StaffMod.staffItem),
                PARENT_AND_SEARCH_TABS
            )
        }
    }

    @SubscribeEvent
    fun registerParticleProviders(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(IStaffMod.get().flamethrowerParticleType, FlamethrowerParticle::Factory)
        event.registerSpriteSet(IStaffMod.get().soulFlamethrowerParticleType, FlamethrowerParticle::Factory)
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
}
