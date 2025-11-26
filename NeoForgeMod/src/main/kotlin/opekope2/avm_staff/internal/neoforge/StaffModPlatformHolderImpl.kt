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

package opekope2.avm_staff.internal.neoforge

import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.Block
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.item.Item
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import opekope2.avm_staff.api.IStaffModClientPlatform
import opekope2.avm_staff.api.IStaffModPlatform
import opekope2.avm_staff.internal.neoforge.item.NeoForgeCrownItem
import opekope2.avm_staff.internal.neoforge.item.NeoForgeStaffItem
import opekope2.avm_staff.util.bakedModelManager
import thedarkcolour.kotlinforforge.neoforge.forge.DIST

val staffModPlatform = object : IStaffModPlatform {
    override val isClient: Boolean
        get() = DIST == Dist.CLIENT

    override fun staffItem(settings: Item.Settings, repairIngredient: RegistrySupplier<Item>?) =
        NeoForgeStaffItem(settings, repairIngredient)

    override fun crownItem(groundBlock: Block, wallBlock: Block, settings: Item.Settings) =
        NeoForgeCrownItem(groundBlock, wallBlock, settings)

    override fun simpleParticleType(alwaysShow: Boolean) = SimpleParticleType(alwaysShow)
}

@get:OnlyIn(Dist.CLIENT)
val staffModClientPlatform: IStaffModClientPlatform
    get() = StaffModClientPlatform

// Do not create this in <clinit> because it will crash the server
@OnlyIn(Dist.CLIENT)
private object StaffModClientPlatform : IStaffModClientPlatform {
    override val staffModelItems = mutableListOf<Item>()

    override fun renderAsStaffModel(item: Item) {
        staffModelItems += item
    }

    override fun getStandaloneModel(modelId: Identifier): BakedModel =
        bakedModelManager.getModel(ModelIdentifier.standalone(modelId))
}
