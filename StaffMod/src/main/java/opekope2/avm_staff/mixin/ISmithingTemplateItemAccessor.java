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

package opekope2.avm_staff.mixin;

import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTemplateItem.class)
public interface ISmithingTemplateItemAccessor {
    @Accessor("ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION_TEXT")
    static Text armorTrimAdditionsSlotDescriptionText() {
        throw new AssertionError();
    }

    @Accessor("ARMOR_TRIM_INGREDIENTS_TEXT")
    static Text armorTrimIngredientsText() {
        throw new AssertionError();
    }

    @Accessor("DESCRIPTION_FORMATTING")
    static Formatting descriptionFormatting() {
        throw new AssertionError();
    }

    @Accessor("EMPTY_SLOT_REDSTONE_DUST_TEXTURE")
    static Identifier emptySlotRedstoneDustTexture() {
        throw new AssertionError();
    }

    @Accessor("TITLE_FORMATTING")
    static Formatting titleFormatting() {
        throw new AssertionError();
    }
}
