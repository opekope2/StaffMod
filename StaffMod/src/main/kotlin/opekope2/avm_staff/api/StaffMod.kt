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

import net.minecraft.block.AbstractBlock
import net.minecraft.block.enums.NoteBlockInstrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.client.particle.ParticleManager
import net.minecraft.component.ComponentType
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.damage.DamageType
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraft.item.SmithingTemplateItem
import net.minecraft.network.codec.PacketCodec
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.world.GameRules
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import opekope2.avm_staff.api.block.CrownBlock
import opekope2.avm_staff.api.block.WallCrownBlock
import opekope2.avm_staff.api.component.StaffFurnaceDataComponent
import opekope2.avm_staff.api.component.StaffItemComponent
import opekope2.avm_staff.api.component.StaffRendererOverrideComponent
import opekope2.avm_staff.api.component.StaffRendererPartComponent
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.entity.ImpactTntEntity
import opekope2.avm_staff.api.item.CrownItem
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.api.staff.StaffInfusionSmithingRecipeTextures
import opekope2.avm_staff.internal.MinecraftUnit
import opekope2.avm_staff.mixin.ICakeBlockAccessor
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.mutableItemStackInStaff
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import kotlin.math.max

private val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)
private val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)
private val ITEM_GROUPS = DeferredRegister.create(RegistryKeys.ITEM_GROUP, MOD_ID)
private val ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID)
private val PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID)
private val DATA_COMPONENT_TYPES = DeferredRegister.create(RegistryKeys.DATA_COMPONENT_TYPE, MOD_ID)
private val SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID)

/**
 * Block registered as `avm_staff:crown_of_king_orange`.
 */
val crownOfKingOrangeBlock: RegistryObject<CrownBlock> = BLOCKS.register("crown_of_king_orange") {
    CrownBlock(
        AbstractBlock.Settings.create().instrument(NoteBlockInstrument.BELL).strength(1.0f)
            .pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER_GRATE).nonOpaque()
    )
}

/**
 * Block registered as `avm_staff:wall_crown_of_king_orange`.
 */
val wallCrownOfKingOrangeBlock: RegistryObject<WallCrownBlock> = BLOCKS.register("wall_crown_of_king_orange") {
    WallCrownBlock(AbstractBlock.Settings.copy(crownOfKingOrangeBlock.get()))
}

/**
 * Item registered as `avm_staff:faint_staff_rod`.
 */
val faintStaffRodItem: RegistryObject<Item> = ITEMS.register("faint_staff_rod") {
    Item(Item.Settings())
}

/**
 * Item registered as `avm_staff:faint_royal_staff_head`.
 */
val faintRoyalStaffHeadItem: RegistryObject<Item> = ITEMS.register("faint_royal_staff_head") {
    Item(Item.Settings().maxCount(16).rarity(Rarity.RARE))
}

/**
 * Item registered as `avm_staff:faint_royal_staff`.
 */
val faintRoyalStaffItem: RegistryObject<Item> = ITEMS.register("faint_royal_staff") {
    IStaffModPlatform.itemWithStaffRenderer(Item.Settings().maxCount(1).rarity(Rarity.RARE))
}

/**
 * Item registered as `avm_staff:royal_staff`.
 */
val royalStaffItem: RegistryObject<StaffItem> = ITEMS.register("royal_staff") {
    IStaffModPlatform.staffItem(
        Item.Settings().maxCount(1).rarity(Rarity.EPIC).attributeModifiers(StaffHandler.Default.ATTRIBUTE_MODIFIERS)
    )
}

/**
 * Item registered as `avm_staff:royal_staff_ingredient`.
 */
val royalStaffIngredientItem: RegistryObject<Item> = ITEMS.register("royal_staff_ingredient") {
    Item(Item.Settings())
}

/**
 * Item registered as `avm_staff:crown_of_king_orange`.
 */
val crownOfKingOrangeItem: RegistryObject<CrownItem> = ITEMS.register("crown_of_king_orange") {
    IStaffModPlatform.crownItem(
        crownOfKingOrangeBlock.get(),
        wallCrownOfKingOrangeBlock.get(),
        Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
    )
}

/**
 * Item registered as `avm_staff:staff_infusion_smithing_template`.
 */
val staffInfusionSmithingTemplateItem: RegistryObject<Item> = ITEMS.register("staff_infusion_smithing_template") {
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
 * Item group containing items added by Staff Mod.
 */
val staffModItemGroup: RegistryObject<ItemGroup> = ITEM_GROUPS.register("${MOD_ID}_items") {
    ItemGroup.builder()
        .displayName(Text.translatable("itemGroup.${MOD_ID}_items"))
        .icon {
            royalStaffItem.get().defaultStack.apply {
                mutableItemStackInStaff = Items.COMMAND_BLOCK.defaultStack
            }
        }
        .entries { _, entries ->
            entries.add(faintStaffRodItem.get())
            entries.add(faintRoyalStaffHeadItem.get())
            entries.add(faintRoyalStaffItem.get())
            entries.add(royalStaffItem.get())
            entries.add(royalStaffIngredientItem.get())
            entries.add(crownOfKingOrangeItem.get())
            entries.add(staffInfusionSmithingTemplateItem.get())
        }
        .build()
}

/**
 * Entity registered as `avm_staff:impact_tnt`.
 */
val impactTntEntityType: RegistryObject<EntityType<ImpactTntEntity>> = ENTITY_TYPES.register("impact_tnt") {
    EntityType.Builder.create(::ImpactTntEntity, SpawnGroup.MISC)
        .makeFireImmune()
        .dimensions(EntityType.TNT.dimensions.width, EntityType.TNT.dimensions.height)
        .eyeHeight(EntityType.TNT.dimensions.eyeHeight)
        .maxTrackingRange(EntityType.TNT.maxTrackDistance)
        .trackingTickInterval(EntityType.TNT.trackTickInterval)
        .build(Identifier.of(MOD_ID, "impact_tnt").toString())
}

/**
 * Entity registered as `avm_staff:cake`
 */
val cakeEntityType: RegistryObject<EntityType<CakeEntity>> = ENTITY_TYPES.register("cake") {
    val cakeBox = ICakeBlockAccessor.bitesToShape()[0].boundingBox
    val cakeSize = max(cakeBox.lengthX, max(cakeBox.lengthY, cakeBox.lengthZ))

    EntityType.Builder.create(::CakeEntity, SpawnGroup.MISC)
        .dimensions(cakeSize.toFloat(), cakeSize.toFloat())
        .maxTrackingRange(EntityType.FALLING_BLOCK.maxTrackDistance)
        .trackingTickInterval(EntityType.FALLING_BLOCK.trackTickInterval)
        .build(Identifier.of(MOD_ID, "cake").toString())
}

/**
 * Particle registered as `avm_staff:flame`.
 *
 * @see ParticleManager.addParticle
 */
val flamethrowerParticleType: RegistryObject<SimpleParticleType> =
    PARTICLE_TYPES.register("flame") { IStaffModPlatform.simpleParticleType(false) }

/**
 * Particle registered as `avm_staff:soul_fire_flame`.
 *
 * @see ParticleManager.addParticle
 */
val soulFlamethrowerParticleType: RegistryObject<SimpleParticleType> =
    PARTICLE_TYPES.register("soul_fire_flame") { IStaffModPlatform.simpleParticleType(false) }

/**
 * Data component registered as `avm_staff:staff_item`. Stores the item inserted into the staff.
 */
val staffItemComponentType: RegistryObject<ComponentType<StaffItemComponent>> =
    DATA_COMPONENT_TYPES.register("staff_item") {
        ComponentType.builder<StaffItemComponent>()
            .codec(StaffItemComponent.CODEC)
            .packetCodec(StaffItemComponent.PACKET_CODEC)
            .build()
    }

/**
 * Data component registered as `avm_staff:rocket_mode`. Stores if a campfire staff should propel its user.
 */
val rocketModeComponentType: RegistryObject<ComponentType<MinecraftUnit>> =
    DATA_COMPONENT_TYPES.register("rocket_mode") {
        ComponentType.builder<MinecraftUnit>()
            .packetCodec(PacketCodec.unit(MinecraftUnit.INSTANCE))
            .build()
    }

/**
 * Data component registered as `avm_staff:furnace_data`. If this is present, the furnace is lit.
 */
val staffFurnaceDataComponentType: RegistryObject<ComponentType<StaffFurnaceDataComponent>> =
    DATA_COMPONENT_TYPES.register("furnace_data") {
        ComponentType.builder<StaffFurnaceDataComponent>()
            .packetCodec(StaffFurnaceDataComponent.PACKET_CODEC)
            .build()
    }

/**
 * Data component registered as `avm_staff:staff_renderer_override`. Specifies how a staff is rendered. Intended for
 * Isometric Renders mod compatibility.
 */
val staffRendererOverrideComponentType: RegistryObject<ComponentType<StaffRendererOverrideComponent>> =
    DATA_COMPONENT_TYPES.register("staff_renderer_override") {
        ComponentType.builder<StaffRendererOverrideComponent>()
            .codec(StaffRendererOverrideComponent.CODEC)
            .packetCodec(StaffRendererOverrideComponent.PACKET_CODEC)
            .build()
    }

/**
 * Data component registered as `avm_staff:staff_renderer_part`. Only used for rendering.
 */
val staffRendererPartComponentType: RegistryObject<ComponentType<StaffRendererPartComponent>> =
    DATA_COMPONENT_TYPES.register("staff_renderer_part") {
        ComponentType.builder<StaffRendererPartComponent>()
            .packetCodec(StaffRendererPartComponent.PACKET_CODEC)
            .build()
    }

/**
 * Sound event registered as `avm_staff:entity.cake.splash`.
 */
val cakeSplashSoundEvent: RegistryObject<SoundEvent> = SOUND_EVENTS.register("entity.cake.splash") {
    SoundEvent.of(Identifier.of(MOD_ID, "entity.cake.splash"))
}

/**
 * Sound event registered as `avm_staff:entity.cake.throw`.
 */
val cakeThrowSoundEvent: RegistryObject<SoundEvent> = SOUND_EVENTS.register("entity.cake.throw") {
    SoundEvent.of(Identifier.of(MOD_ID, "entity.cake.throw"))
}

/**
 * `avm_staff:pranked` damage type.
 */
val cakeDamageType: RegistryKey<DamageType> =
    RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, "pranked"))

/**
 * `avm_staff:pranked_by_player` damage type.
 */
val playerCakeDamageType: RegistryKey<DamageType> =
    RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, "pranked_by_player"))

/**
 * Throwable cakes game rule. When set to true, cakes can be thrown by right clicking, and dispensers will shoot cakes
 * instead of dropping them as item.
 */
val throwableCakesGameRule: GameRules.Key<GameRules.BooleanRule> =
    GameRules.register("throwableCakes", GameRules.Category.MISC, GameRules.BooleanRule.create(false))

/**
 * @suppress
 */
@JvmSynthetic
internal fun registerContent() {
    BLOCKS.register(MOD_BUS)
    ITEMS.register(MOD_BUS)
    ITEM_GROUPS.register(MOD_BUS)
    ENTITY_TYPES.register(MOD_BUS)
    PARTICLE_TYPES.register(MOD_BUS)
    DATA_COMPONENT_TYPES.register(MOD_BUS)
    SOUND_EVENTS.register(MOD_BUS)
}
