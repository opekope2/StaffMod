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

package opekope2.avm_staff.content

import dev.architectury.registry.CreativeTabRegistry
import net.minecraft.item.Item
import net.minecraft.item.SmithingTemplateItem
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Rarity
import opekope2.avm_staff.api.IStaffModPlatform
import opekope2.avm_staff.api.item.CrownItem
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.api.staff.StaffInfusionSmithingRecipeTextures
import opekope2.avm_staff.mixin.ISmithingTemplateItemAccessor
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryUtil

/**
 * Items added by AVM Staffs mod.
 */
object Items : RegistryUtil<Item>(MOD_ID, RegistryKeys.ITEM) {
    private fun settings() = Item.Settings()

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
     * Item registered as `avm_staff:faint_royal_staff_head`.
     */
    @JvmField
    val FAINT_ROYAL_STAFF_HEAD = register("faint_royal_staff_head") {
        Item(settings().maxCount(16).rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * @see FAINT_ROYAL_STAFF_HEAD
     */
    val faintRoyalStaffHead: Item
        @JvmName("faintRoyalStaffHead")
        get() = FAINT_ROYAL_STAFF_HEAD.get()

    /**
     * Item registered as `avm_staff:faint_royal_staff`.
     */
    @JvmField
    val FAINT_ROYAL_STAFF = register("faint_royal_staff") {
        IStaffModPlatform.itemWithStaffRenderer(
            settings().maxCount(1).rarity(Rarity.RARE).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS)
        )
    }

    /**
     * @see FAINT_ROYAL_STAFF
     */
    val faintRoyalStaff: Item
        @JvmName("faintRoyalStaff")
        get() = FAINT_ROYAL_STAFF.get()

    /**
     * Item registered as `avm_staff:royal_staff`.
     */
    @JvmField
    val ROYAL_STAFF = register("royal_staff") {
        IStaffModPlatform.staffItem(
            settings().maxCount(1).rarity(Rarity.EPIC).attributeModifiers(StaffHandler.Fallback.ATTRIBUTE_MODIFIERS)
                .maxDamage(5179).`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS),
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
        Item(settings().`arch$tab`(ItemGroups.AVM_STAFF_MOD_ITEMS))
    }

    /**
     * @see ROYAL_STAFF_INGREDIENT
     */
    val royalStaffIngredient: Item
        @JvmName("royalStaffIngredient")
        get() = ROYAL_STAFF_INGREDIENT.get()

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
            StaffInfusionSmithingRecipeTextures.baseSlotTextures,
            StaffInfusionSmithingRecipeTextures.additionsSlotTextures
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
    }
}
