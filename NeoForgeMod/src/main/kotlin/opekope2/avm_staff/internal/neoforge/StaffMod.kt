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

package opekope2.avm_staff.internal.neoforge

import net.minecraft.item.Item
import net.minecraft.particle.DefaultParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.ItemTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.util.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredRegister
import opekope2.avm_staff.IStaffMod
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.initializeNetworking
import opekope2.avm_staff.internal.neoforge.item.NeoForgeStaffItem
import opekope2.avm_staff.internal.staff_item_handler.registerVanillaStaffItemHandlers
import opekope2.avm_staff.util.MOD_ID
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn

@Mod(MOD_ID)
object StaffMod : IStaffMod {
    private val ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID)

    private val FAINT_STAFF_ROD_ITEM = ITEMS.register("faint_staff_rod") { ->
        Item(Item.Settings().requires(FeatureFlags.UPDATE_1_21))
    }
    private val FAINT_ROYAL_STAFF_HEAD_ITEM = ITEMS.register("faint_royal_staff_head") { ->
        Item(Item.Settings().requires(FeatureFlags.UPDATE_1_21))
    }
    private val FAINT_ROYAL_STAFF_ITEM = ITEMS.register("faint_royal_staff") { ->
        Item(Item.Settings().maxCount(1).requires(FeatureFlags.UPDATE_1_21))
    }
    private val ROYAL_STAFF_ITEM = ITEMS.register("royal_staff") { ->
        NeoForgeStaffItem(Item.Settings().maxCount(1).requires(FeatureFlags.UPDATE_1_21))
    }

    private val PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID)

    private val FLAMETHROWER_PARTICLE_TYPE = PARTICLE_TYPES.register("flame") { ->
        DefaultParticleType(false)
    }

    private val SOUL_FLAMETHROWER_PARTICLE_TYPE = PARTICLE_TYPES.register("soul_fire_flame") { ->
        DefaultParticleType(false)
    }

    init {
        initialize()
        initializeNetworking()
        registerVanillaStaffItemHandlers()
        runWhenOn(Dist.CLIENT, StaffModClient::initializeClient)
    }

    override val faintStaffRodItem: Item
        get() = FAINT_STAFF_ROD_ITEM.get()

    override val faintRoyalStaffHeadItem: Item
        get() = FAINT_ROYAL_STAFF_HEAD_ITEM.get()

    override val faintRoyalStaffItem: Item
        get() = FAINT_ROYAL_STAFF_ITEM.get()

    override val royalStaffItem: StaffItem
        get() = ROYAL_STAFF_ITEM.get()

    override val staffsTag: TagKey<Item> = ItemTags.create(Identifier(MOD_ID, "staffs"))

    override val flamethrowerParticleType: DefaultParticleType
        get() = FLAMETHROWER_PARTICLE_TYPE.get()

    override val soulFlamethrowerParticleType: DefaultParticleType
        get() = SOUL_FLAMETHROWER_PARTICLE_TYPE.get()

    private fun initialize() {
        ITEMS.register(MOD_BUS)
        PARTICLE_TYPES.register(MOD_BUS)
    }
}
