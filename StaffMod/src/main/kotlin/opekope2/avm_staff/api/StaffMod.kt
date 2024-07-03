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

import com.mojang.serialization.Codec
import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.AbstractBlock
import net.minecraft.block.enums.Instrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.client.particle.ParticleManager
import net.minecraft.component.DataComponentType
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.item.SmithingTemplateItem
import net.minecraft.network.codec.PacketCodec
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import opekope2.avm_staff.api.block.CrownBlock
import opekope2.avm_staff.api.block.WallCrownBlock
import opekope2.avm_staff.api.entity.ImpactTntEntity
import opekope2.avm_staff.api.item.CrownItem
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.staff.*
import opekope2.avm_staff.internal.MinecraftUnit
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.mutableItemStackInStaff

private val BLOCKS = DeferredRegister.create(MOD_ID, RegistryKeys.BLOCK)
private val ITEMS = DeferredRegister.create(MOD_ID, RegistryKeys.ITEM)
private val ITEM_GROUPS = DeferredRegister.create(MOD_ID, RegistryKeys.ITEM_GROUP)
private val ENTITY_TYPES = DeferredRegister.create(MOD_ID, RegistryKeys.ENTITY_TYPE)
private val PARTICLE_TYPES = DeferredRegister.create(MOD_ID, RegistryKeys.PARTICLE_TYPE)
private val DATA_COMPONENT_TYPES = DeferredRegister.create(MOD_ID, RegistryKeys.DATA_COMPONENT_TYPE)

/**
 * Block registered as `avm_staff:crown_of_king_orange`.
 */
val crownOfKingOrangeBlock: RegistrySupplier<CrownBlock> = BLOCKS.register("crown_of_king_orange") {
    CrownBlock(
        AbstractBlock.Settings.create().instrument(Instrument.BELL).strength(1.0f)
            .pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER_GRATE).nonOpaque()
    )
}

/**
 * Block registered as `avm_staff:wall_crown_of_king_orange`.
 */
val wallCrownOfKingOrangeBlock: RegistrySupplier<WallCrownBlock> = BLOCKS.register("wall_crown_of_king_orange") {
    WallCrownBlock(AbstractBlock.Settings.copy(crownOfKingOrangeBlock.get()))
}

/**
 * Item registered as `avm_staff:faint_staff_rod`.
 */
val faintStaffRodItem: RegistrySupplier<Item> = ITEMS.register("faint_staff_rod") {
    Item(Item.Settings().`arch$tab`(staffModItemGroup))
}

/**
 * Item registered as `avm_staff:faint_royal_staff_head`.
 */
val faintRoyalStaffHeadItem: RegistrySupplier<Item> = ITEMS.register("faint_royal_staff_head") {
    Item(
        Item.Settings().maxCount(16).rarity(Rarity.RARE).`arch$tab`(staffModItemGroup)
    )
}

/**
 * Item registered as `avm_staff:faint_royal_staff`.
 */
val faintRoyalStaffItem: RegistrySupplier<Item> = ITEMS.register("faint_royal_staff") {
    IStaffModPlatform.itemWithStaffRenderer(
        Item.Settings().maxCount(1).rarity(Rarity.RARE).`arch$tab`(staffModItemGroup)
    )
}

/**
 * Item registered as `avm_staff:royal_staff`.
 */
val royalStaffItem: RegistrySupplier<StaffItem> = ITEMS.register("royal_staff") {
    IStaffModPlatform.staffItem(
        Item.Settings().maxCount(1).rarity(Rarity.EPIC).attributeModifiers(StaffHandler.Default.ATTRIBUTE_MODIFIERS)
            .`arch$tab`(staffModItemGroup)
    )
}

/**
 * Item registered as `avm_staff:royal_staff_ingredient`.
 */
val royalStaffIngredientItem: RegistrySupplier<Item> = ITEMS.register("royal_staff_ingredient") {
    Item(Item.Settings().`arch$tab`(staffModItemGroup))
}

/**
 * Item registered as `avm_staff:crown_of_king_orange`.
 */
val crownOfKingOrangeItem: RegistrySupplier<CrownItem> = ITEMS.register("crown_of_king_orange") {
    IStaffModPlatform.crownItem(
        crownOfKingOrangeBlock.get(),
        wallCrownOfKingOrangeBlock.get(),
        Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).`arch$tab`(staffModItemGroup)
    )
}

/**
 * Item registered as `avm_staff:staff_infusion_smithing_template`.
 */
val staffInfusionSmithingTemplateItem: RegistrySupplier<Item> = ITEMS.register("staff_infusion_smithing_template") {
    SmithingTemplateItem(
        Text.translatable("item.$MOD_ID.staff_infusion_smithing_template.applies_to")
            .formatted(ISmithingTemplateItemAccessor.descriptionFormatting()),
        ISmithingTemplateItemAccessor.armorTrimIngredientsText(),
        Text.translatable("item.$MOD_ID.staff_infusion_smithing_template.title")
            .formatted(ISmithingTemplateItemAccessor.titleFormatting()),
        Text.translatable("item.$MOD_ID.staff_infusion_smithing_template.base_slot_description"),
        ISmithingTemplateItemAccessor.armorTrimAdditionsSlotDescriptionText(),
        StaffInfusionSmithingRecipeTextures.baseSlotTextures,
        StaffInfusionSmithingRecipeTextures.additionsSlotTextures
    )
}

/**
 * Tag registered as `avm_staff:staffs`.
 */
val staffsTag: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, Identifier(MOD_ID, "staffs"))

/**
 * Item group containing items added by Staff Mod.
 */
val staffModItemGroup: RegistrySupplier<ItemGroup> = ITEM_GROUPS.register("${MOD_ID}_items") {
    CreativeTabRegistry.create(Text.translatable("itemGroup.${MOD_ID}_items")) {
        royalStaffItem.get().defaultStack.apply {
            mutableItemStackInStaff = Items.COMMAND_BLOCK.defaultStack
        }
    }
}

/**
 * Entity registered as `avm_staff:impact_tnt`.
 */
val impactTntEntityType: RegistrySupplier<EntityType<ImpactTntEntity>> = ENTITY_TYPES.register("impact_tnt") {
    EntityType.Builder.create(::ImpactTntEntity, SpawnGroup.MISC)
        .makeFireImmune()
        .dimensions(EntityType.TNT.dimensions.width, EntityType.TNT.dimensions.height)
        .eyeHeight(EntityType.TNT.dimensions.eyeHeight)
        .maxTrackingRange(EntityType.TNT.maxTrackDistance)
        .trackingTickInterval(EntityType.TNT.trackTickInterval)
        .build(Identifier(MOD_ID, "impact_tnt").toString())
}

/**
 * Particle registered as `avm_staff:flame`.
 *
 * @see ParticleManager.addParticle
 */
val flamethrowerParticleType: RegistrySupplier<SimpleParticleType> =
    PARTICLE_TYPES.register("flame") { IStaffModPlatform.simpleParticleType(false) }

/**
 * Particle registered as `avm_staff:soul_fire_flame`.
 *
 * @see ParticleManager.addParticle
 */
val soulFlamethrowerParticleType: RegistrySupplier<SimpleParticleType> =
    PARTICLE_TYPES.register("soul_fire_flame") { IStaffModPlatform.simpleParticleType(false) }

/**
 * Data component registered as `avm_staff:staff_item`. Stores the item inserted into the staff.
 */
val staffItemComponentType: RegistrySupplier<DataComponentType<StaffItemComponent>> =
    DATA_COMPONENT_TYPES.register("staff_item") {
        DataComponentType.builder<StaffItemComponent>()
            .codec(StaffItemComponent.CODEC)
            .packetCodec(StaffItemComponent.PACKET_CODEC)
            .build()
    }

/**
 * Data component registered as `avm_staff:rocket_mode`. Stores if a campfire staff should propel its user.
 */
val rocketModeComponentType: RegistrySupplier<DataComponentType<MinecraftUnit>> =
    DATA_COMPONENT_TYPES.register("rocket_mode") {
        DataComponentType.builder<MinecraftUnit>()
            .codec(Codec.unit(MinecraftUnit.INSTANCE))
            .packetCodec(PacketCodec.unit(MinecraftUnit.INSTANCE))
            .build()
    }

/**
 * Data component registered as `avm_staff:furnace_data`. If this is present, the furnace is lit.
 */
val staffFurnaceDataComponentType: RegistrySupplier<DataComponentType<StaffFurnaceDataComponent>> =
    DATA_COMPONENT_TYPES.register("furnace_data") {
        DataComponentType.builder<StaffFurnaceDataComponent>()
            .packetCodec(StaffFurnaceDataComponent.PACKET_CODEC)
            .build()
    }

/**
 * Data component registered as `avm_staff:staff_renderer_override`. Specifies how a staff is rendered. Intended for
 * Isometric Renders mod compatibility.
 */
val staffRendererOverrideComponentType: RegistrySupplier<DataComponentType<StaffRendererOverrideComponent>> =
    DATA_COMPONENT_TYPES.register("staff_renderer_override") {
        DataComponentType.builder<StaffRendererOverrideComponent>()
            .codec(StaffRendererOverrideComponent.CODEC)
            .packetCodec(StaffRendererOverrideComponent.PACKET_CODEC)
            .build()
    }

/**
 * @suppress
 */
@JvmSynthetic
internal fun registerContent() {
    BLOCKS.register()
    ITEMS.register()
    ITEM_GROUPS.register()
    ENTITY_TYPES.register()
    PARTICLE_TYPES.register()
    DATA_COMPONENT_TYPES.register()

    // Because SmithingTemplateItem doesn't take Item.Settings in its constructor
    CreativeTabRegistry.append(staffModItemGroup, staffInfusionSmithingTemplateItem)
}
