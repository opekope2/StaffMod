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
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import opekope2.avm_staff.api.IStaffModClientPlatform
import opekope2.avm_staff.api.IStaffModPlatform
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.Items.royalStaff
import opekope2.avm_staff.content.Items.scepterOfFriendship
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.Registrar
import opekope2.avm_staff.util.RegistryKeyUtil

/**
 * Items added by AVM Staffs mod.
 */
@Suppress("unused")
object Items : Registrar<Item>(MOD_ID, RegistryKeys.ITEM) {
    @JvmStatic
    private fun settings() = Item.Settings()

    /**
     * Chromatic crystal item.
     */
    @JvmStatic
    val chromaticCrystal by registering {
        Item(settings().rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * Crown of King Orange item.
     */
    @JvmStatic
    val crownOfKingOrange by registering {
        IStaffModPlatform.crownItem(
            Blocks.crownOfKingOrange,
            Blocks.wallCrownOfKingOrange,
            settings().maxCount(1).rarity(Rarity.UNCOMMON).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS)
        )
    }

    /**
     * Faint staff rod item.
     */
    @JvmStatic
    val faintStaffRod by registering { Item(settings().`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS)) }

    /**
     * Royal staff rod item.
     */
    @JvmStatic
    val royalStaffRod by registering { Item(settings().rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS)) }

    /**
     * Royal staff head item.
     */
    @JvmStatic
    val royalStaffHead by registering {
        Item(settings().maxCount(16).rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * Royal staff item.
     */
    @JvmStatic
    val royalStaff by registering {
        IStaffModPlatform.staffItem(
            settings().maxCount(1).rarity(Rarity.EPIC).attributeModifiers(StaffHandler.Fallback.ATTRIBUTE_MODIFIERS)
                .maxDamage(5179).component(DataComponentTypes.TOOL, ToolComponent(listOf(), 1f, 1))
                .`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS),
            ::royalStaffIngredient
        )
    }

    /**
     * Royal staff scrap item.
     */
    @JvmStatic
    val royalStaffIngredient by registering {
        Item(settings().rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * Faint scepter of friendship item.
     */
    @JvmStatic
    val faintScepterOfFriendship by registering {
        Item(settings().maxCount(1).rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
            .also { if (IStaffModPlatform.isClient) IStaffModClientPlatform.renderAsStaffModel(it) }
    }

    /**
     * Faint scepter of friendship head item.
     */
    @JvmStatic
    val faintScepterOfFriendshipHead by registering {
        Item(settings().maxCount(16).rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * Scepter of friendship item.
     */
    @JvmStatic
    val scepterOfFriendship by registering {
        IStaffModPlatform.staffItem(
            settings()
                .maxCount(1)
                .rarity(Rarity.EPIC)
                .attributeModifiers(StaffHandler.Fallback.ATTRIBUTE_MODIFIERS)
                .maxDamage(5179)
                .component(DataComponentTypes.TOOL, ToolComponent(listOf(), 1f, 1))
                .`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS),
            ::chromaticCrystal
        )
    }

    /**
     * Staff infusion smithing template item.
     */
    @JvmStatic
    val staffInfusionSmithingTemplate by registering {
        SmithingTemplateItem(
            I18n.ITEM_AVM_STAFF_STAFF_INFUSION_SMITHING_TEMPLATE_APPLIES_TO.getText()
                .formatted(ISmithingTemplateItemAccessor.descriptionFormatting()),
            ISmithingTemplateItemAccessor.armorTrimIngredientsText(),
            I18n.ITEM_AVM_STAFF_STAFF_INFUSION_SMITHING_TEMPLATE_TITLE.getText()
                .formatted(ISmithingTemplateItemAccessor.titleFormatting()),
            I18n.ITEM_AVM_STAFF_STAFF_INFUSION_SMITHING_TEMPLATE_BASE_SLOT_DESCRIPTION.getText(),
            I18n.ITEM_AVM_STAFF_STAFF_INFUSION_SMITHING_TEMPLATE_ADDITIONS_SLOT_DESCRIPTION.getText(),
            listOf(Identifier.of(MOD_ID, "item/smithing_table/empty_slot_faint_staff_rod")),
            listOf(Identifier.of(MOD_ID, "item/empty_slot_royal_staff_ingredient"))
        )
    }

    override fun register() {
        super.register()
        // Because SmithingTemplateItem doesn't take Item.Settings in its constructor
        CreativeTabRegistry.append(ItemGroups.AVM_STAFF_MOD_ITEMS, ::staffInfusionSmithingTemplate)
        // Because arch$tab only allows one tab
        CreativeTabRegistry.append(INGREDIENTS, ::royalStaffIngredient)
        CreativeTabRegistry.append(INGREDIENTS, ::chromaticCrystal)
        CreativeTabRegistry.append(INGREDIENTS, ::staffInfusionSmithingTemplate)
    }

    /**
     * Item tags added by AVM Staffs mod.
     */
    object Tags : RegistryKeyUtil<Item>(MOD_ID, RegistryKeys.ITEM) {
        /**
         * Staff items. Used by data packs.
         */
        @JvmStatic
        val staffs by tagKey

        /**
         * Items that can be used in [royalStaff].
         */
        @JvmStatic
        val enabledInRoyalStaff = tagKey("enabled_in/royal_staff")

        /**
         * Items that can be used in [scepterOfFriendship].
         */
        @JvmStatic
        val enabledInScepterOfFriendship = tagKey("enabled_in/scepter_of_friendship")
    }
}
