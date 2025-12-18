/*
 * AvM Staff Mod
 * Copyright (c) 2024-2025 opekope2
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

@file:JvmName("StaffModPlatformHolderImpl")
@file:Suppress("unused")

package opekope2.avm_staff.internal.fabric

import dev.architectury.registry.registries.RegistrySupplier
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.client.render.model.BakedModel
import net.minecraft.item.Item
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.IStaffModClientPlatform
import opekope2.avm_staff.api.IStaffModPlatform
import opekope2.avm_staff.api.item.CrownItem
import opekope2.avm_staff.api.item.renderer.StaffRenderer
import opekope2.avm_staff.internal.fabric.item.FabricStaffItem
import opekope2.avm_staff.util.mc

val staffModPlatform = object : IStaffModPlatform {
    override val isClient: Boolean
        get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT

    override fun staffItem(settings: Item.Settings, repairIngredient: RegistrySupplier<Item>?) =
        FabricStaffItem(settings, repairIngredient)

    override fun crownItem(groundBlock: Block, wallBlock: Block, settings: Item.Settings) =
        CrownItem(groundBlock, wallBlock, settings)

    override fun simpleParticleType(alwaysShow: Boolean): SimpleParticleType = FabricParticleTypes.simple(alwaysShow)
}

val staffModClientPlatform: IStaffModClientPlatform
    @Environment(EnvType.CLIENT)
    get() = StaffModClientPlatform

// Do not create this in <clinit> because it will crash the server
@Environment(EnvType.CLIENT)
private object StaffModClientPlatform : IStaffModClientPlatform {
    override val staffModelItems = mutableListOf<Item>()

    override fun renderAsStaffModel(item: Item) {
        staffModelItems += item
        BuiltinItemRendererRegistry.INSTANCE.register(item, StaffRenderer::renderStaff)
    }

    override fun getStandaloneModel(modelId: Identifier): BakedModel =
        mc.bakedModelManager.getModel(modelId) ?: mc.bakedModelManager.missingModel
}
