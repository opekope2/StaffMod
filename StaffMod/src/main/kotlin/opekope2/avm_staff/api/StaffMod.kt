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

@file: JvmName("StaffMod")
@file: Suppress("unused")

package opekope2.avm_staff.api

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.client.particle.ParticleManager
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.particle.DefaultParticleType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import opekope2.avm_staff.api.item.CrownItem
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.createCrownItem
import opekope2.avm_staff.internal.createStaffItem
import opekope2.avm_staff.internal.createStaffRendererItem
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.itemInStaff

private val ITEMS = DeferredRegister.create(MOD_ID, RegistryKeys.ITEM)
private val ITEM_GROUPS = DeferredRegister.create(MOD_ID, RegistryKeys.ITEM_GROUP)
private val PARTICLE_TYPES = DeferredRegister.create(MOD_ID, RegistryKeys.PARTICLE_TYPE)

/**
 * Gets `avm_staff:faint_staff_rod` item registered in Minecraft.
 *
 * Due to how Neo/Forge registries work, *always* use this getter instead of storing the result.
 */
val faintStaffRodItem: RegistrySupplier<Item> = ITEMS.register("faint_staff_rod") {
    Item(Item.Settings().requires(FeatureFlags.UPDATE_1_21).`arch$tab`(staffModItemGroup))
}

/**
 * Gets `avm_staff:faint_royal_staff_head` item registered in Minecraft.
 *
 * Due to how Neo/Forge registries work, *always* use this getter instead of storing the result.
 */
val faintRoyalStaffHeadItem: RegistrySupplier<Item> = ITEMS.register("faint_royal_staff_head") {
    Item(
        Item.Settings().maxCount(16).rarity(Rarity.RARE).requires(FeatureFlags.UPDATE_1_21)
            .`arch$tab`(staffModItemGroup)
    )
}

/**
 * Gets `avm_staff:faint_royal_staff` item registered in Minecraft.
 *
 * Due to how Neo/Forge registries work, *always* use this getter instead of storing the result.
 */
val faintRoyalStaffItem: RegistrySupplier<Item> = ITEMS.register("faint_royal_staff") {
    createStaffRendererItem(
        Item.Settings().maxCount(1).rarity(Rarity.RARE).requires(FeatureFlags.UPDATE_1_21)
            .`arch$tab`(staffModItemGroup)
    )
}

/**
 * Gets `avm_staff:royal_staff` item registered in Minecraft.
 *
 * Due to how Neo/Forge registries work, *always* use this getter instead of storing the result.
 */
val royalStaffItem: RegistrySupplier<StaffItem> = ITEMS.register("royal_staff") {
    createStaffItem(
        Item.Settings().maxCount(1).rarity(Rarity.EPIC).requires(FeatureFlags.UPDATE_1_21)
            .`arch$tab`(staffModItemGroup)
    )
}

/**
 * Gets `avm_staff:crown_of_king_orange` item registered in Minecraft.
 *
 * Due to how Neo/Forge registries work, *always* use this getter instead of storing the result.
 */
val crownOfKingOrangeItem: RegistrySupplier<CrownItem> = ITEMS.register("crown_of_king_orange") {
    createCrownItem(
        Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).requires(FeatureFlags.UPDATE_1_21)
            .`arch$tab`(staffModItemGroup)
    )
}

/**
 * Gets the [TagKey] containing all the staffs.
 */
val staffsTag: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, Identifier(MOD_ID, "staffs"))

/**
 * Gets Staff Mod's item group.
 */
val staffModItemGroup: RegistrySupplier<ItemGroup> = ITEM_GROUPS.register("${MOD_ID}_items") {
    CreativeTabRegistry.create(Text.translatable("itemGroup.${MOD_ID}_items")) {
        royalStaffItem.get().defaultStack.apply {
            itemInStaff = Items.COMMAND_BLOCK.defaultStack
        }
    }
}

/**
 * Gets the flamethrower's flame particle type.
 *
 * Due to how Neo/Forge registries work, *always* use this getter instead of storing the result.
 *
 * @see ParticleManager.addParticle
 */
val flamethrowerParticleType: RegistrySupplier<DefaultParticleType> =
    PARTICLE_TYPES.register("flame") { DefaultParticleType(false) }

/**
 * Gets the soul fire flamethrower's flame particle type.
 *
 * Due to how Neo/Forge registries work, *always* use this getter instead of storing the result.
 *
 * @see ParticleManager.addParticle
 */
val soulFlamethrowerParticleType: RegistrySupplier<DefaultParticleType> =
    PARTICLE_TYPES.register("soul_fire_flame") { DefaultParticleType(false) }

/**
 * @suppress
 */
@JvmSynthetic
internal fun registerContent() {
    ITEMS.register()
    ITEM_GROUPS.register()
    PARTICLE_TYPES.register()
}
