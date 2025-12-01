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

package opekope2.avm_staff.content

import dev.architectury.registry.CreativeTabRegistry
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ToolComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups.INGREDIENTS
import net.minecraft.item.SmithingTemplateItem
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import opekope2.avm_staff.api.IStaffModClientPlatform
import opekope2.avm_staff.api.IStaffModPlatform
import opekope2.avm_staff.api.item.CrownItem
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.Items.CROWN_OF_KING_ORANGE
import opekope2.avm_staff.content.Items.FAINT_SCEPTER_OF_FRIENDSHIP
import opekope2.avm_staff.content.Items.FAINT_STAFF_ROD
import opekope2.avm_staff.content.Items.ROYAL_STAFF
import opekope2.avm_staff.content.Items.ROYAL_STAFF_HEAD
import opekope2.avm_staff.content.Items.ROYAL_STAFF_INGREDIENT
import opekope2.avm_staff.content.Items.ROYAL_STAFF_ROD
import opekope2.avm_staff.content.Items.SCEPTER_OF_FRIENDSHIP
import opekope2.avm_staff.content.Items.STAFF_INFUSION_SMITHING_TEMPLATE
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryUtil
import opekope2.avm_staff.util.TagKeyUtil

/**
 * Items added by AVM Staffs mod.
 */
object Items : RegistryUtil<Item>(MOD_ID, RegistryKeys.ITEM) {
    private fun settings() = Item.Settings()

    /**
     * Item registered as `avm_staff:crown_of_king_orange`.
     */
    @JvmField
    val CROWN_OF_KING_ORANGE = register("crown_of_king_orange") {
        IStaffModPlatform.crownItem(
            Blocks.CROWN_OF_KING_ORANGE.get(),
            Blocks.WALL_CROWN_OF_KING_ORANGE.get(),
            settings().maxCount(1).rarity(Rarity.UNCOMMON).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS)
        )
    }

    /**
     * @see CROWN_OF_KING_ORANGE
     */
    val crownOfKingOrange: CrownItem
        @JvmName("crownOfKingOrange")
        get() = CROWN_OF_KING_ORANGE.get()

    /**
     * Item registered as `avm_staff:faint_staff_rod`.
     */
    @JvmField
    val FAINT_STAFF_ROD = register("faint_staff_rod") {
        Item(settings().`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * @see FAINT_STAFF_ROD
     */
    val faintStaffRod: Item
        @JvmName("faintStaffRod")
        get() = FAINT_STAFF_ROD.get()

    /**
     * Item registered as `avm_staff:royal_staff_rod`.
     */
    @JvmField
    val ROYAL_STAFF_ROD = register("royal_staff_rod") {
        Item(settings().rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * @see ROYAL_STAFF_ROD
     */
    val royalStaffRod: Item
        @JvmName("royalStaffRod")
        get() = ROYAL_STAFF_ROD.get()

    /**
     * Item registered as `avm_staff:royal_staff_head`.
     */
    @JvmField
    val ROYAL_STAFF_HEAD = register("royal_staff_head") {
        Item(settings().maxCount(16).rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * @see ROYAL_STAFF_HEAD
     */
    val royalStaffHead: Item
        @JvmName("royalStaffHead")
        get() = ROYAL_STAFF_HEAD.get()

    /**
     * Item registered as `avm_staff:royal_staff`.
     */
    @JvmField
    val ROYAL_STAFF = register("royal_staff") {
        IStaffModPlatform.staffItem(
            settings().maxCount(1).rarity(Rarity.EPIC).attributeModifiers(StaffHandler.Fallback.ATTRIBUTE_MODIFIERS)
                .maxDamage(5179).component(DataComponentTypes.TOOL, ToolComponent(listOf(), 1f, 1))
                .`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS),
            ROYAL_STAFF_INGREDIENT
        )
    }

    /**
     * @see ROYAL_STAFF
     */
    val royalStaff: StaffItem
        @JvmName("royalStaff")
        get() = ROYAL_STAFF.get()

    /**
     * Item registered as `avm_staff:royal_staff_ingredient`.
     */
    @JvmField
    val ROYAL_STAFF_INGREDIENT = register("royal_staff_ingredient") {
        Item(settings().rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * @see ROYAL_STAFF_INGREDIENT
     */
    val royalStaffIngredient: Item
        @JvmName("royalStaffIngredient")
        get() = ROYAL_STAFF_INGREDIENT.get()

    /**
     * Item registered as `avm_staff:scepter_of_friendship`.
     */
    @JvmField
    val FAINT_SCEPTER_OF_FRIENDSHIP = register("faint_scepter_of_friendship") {
        Item(settings().maxCount(1).rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
            .also { if (IStaffModPlatform.isClient) IStaffModClientPlatform.renderAsStaffModel(it) }
    }

    /**
     * @see FAINT_SCEPTER_OF_FRIENDSHIP
     */
    val faintScepterOfFriendship: Item
        @JvmName("faintScepterOfFriendship")
        get() = FAINT_SCEPTER_OF_FRIENDSHIP.get()

    /**
     * Item registered as `avm_staff:scepter_of_friendship`.
     */
    @JvmField
    val SCEPTER_OF_FRIENDSHIP = register("scepter_of_friendship") {
        IStaffModPlatform.staffItem(
            settings()
                .maxCount(1)
                .rarity(Rarity.EPIC)
                .attributeModifiers(StaffHandler.Fallback.ATTRIBUTE_MODIFIERS)
                .maxDamage(5179)
                .component(DataComponentTypes.TOOL, ToolComponent(listOf(), 1f, 1))
                .`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS),
            null // TODO
        )
    }

    /**
     * @see SCEPTER_OF_FRIENDSHIP
     */
    val scepterOfFriendship: StaffItem
        @JvmName("scepterOfFriendship")
        get() = SCEPTER_OF_FRIENDSHIP.get()

    /**
     * Item registered as `avm_staff:staff_infusion_smithing_template`.
     */
    @JvmField
    val STAFF_INFUSION_SMITHING_TEMPLATE = register("staff_infusion_smithing_template") {
        SmithingTemplateItem(
            Text.translatable("item.$MOD_ID.staff_infusion_smithing_template.applies_to")
                .formatted(ISmithingTemplateItemAccessor.descriptionFormatting()),
            ISmithingTemplateItemAccessor.armorTrimIngredientsText(),
            Text.translatable("item.$MOD_ID.staff_infusion_smithing_template.title")
                .formatted(ISmithingTemplateItemAccessor.titleFormatting()),
            Text.translatable("item.$MOD_ID.staff_infusion_smithing_template.base_slot_description"),
            ISmithingTemplateItemAccessor.armorTrimAdditionsSlotDescriptionText(),
            listOf(Identifier.of(MOD_ID, "item/smithing_table/empty_slot_faint_staff_rod")),
            listOf(ISmithingTemplateItemAccessor.emptySlotRedstoneDustTexture())
        )
    }

    /**
     * @see STAFF_INFUSION_SMITHING_TEMPLATE
     */
    val staffInfusionSmithingTemplate: SmithingTemplateItem
        @JvmName("staffInfusionSmithingTemplate")
        get() = STAFF_INFUSION_SMITHING_TEMPLATE.get()

    override fun register() {
        super.register()
        // Because SmithingTemplateItem doesn't take Item.Settings in its constructor
        CreativeTabRegistry.append(ItemGroups.AVM_STAFF_MOD_ITEMS, STAFF_INFUSION_SMITHING_TEMPLATE)
        // Because arch$tab only allows one tab
        CreativeTabRegistry.append(INGREDIENTS, ROYAL_STAFF_INGREDIENT)
        CreativeTabRegistry.append(INGREDIENTS, STAFF_INFUSION_SMITHING_TEMPLATE)
    }

    /**
     * Item tags added by AVM Staffs mod.
     */
    object Tags : TagKeyUtil<Item>(MOD_ID, RegistryKeys.ITEM) {
        /**
         * Item tag registered as `avm_staff:staffs`.
         */
        @JvmField
        val STAFFS = tagKey("staffs")

        /**
         * Item tag registered as `avm_staff:enabled_in_staff/royal_staff`.
         */
        @JvmField
        val ENABLED_ROYAL_STAFF_ITEMS = tagKey("enabled_in_staff/royal_staff")

        /**
         * Item tag registered as `avm_staff:enabled_in_staff/scepter_of_friendship`.
         */
        @JvmField
        val ENABLED_SCEPTER_OF_FRIENDSHIP_ITEMS = tagKey("enabled_in_staff/scepter_of_friendship")
    }
}
