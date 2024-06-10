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

package opekope2.avm_staff.api.staff

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.staffInfusionSmithingTemplateItem

/**
 * Object holding textures to be displayed in a smithing table, when using a
 * [staff infusion smithing template][staffInfusionSmithingTemplateItem].
 */
@Environment(EnvType.CLIENT)
object StaffInfusionSmithingRecipeTextures {
    /**
     * @suppress
     */
    @JvmSynthetic
    internal val baseSlotTextures = mutableListOf<Identifier>()

    /**
     * @suppress
     */
    @JvmSynthetic
    internal val additionsSlotTextures = mutableListOf<Identifier>()

    /**
     * Registers a pair of staff texture and an ingredient texture to be displayed in a smithing table, when using a
     * [staff infusion smithing template][staffInfusionSmithingTemplateItem]. This method should be called for every
     * `minecraft:smithing_transform` recipe in the mod's data pack, which infuses an ingredient into a faint staff.
     *
     * @param baseSlotTexture       The background texture of the 2nd slot of the smithing table.
     * @param additionsSlotTexture  The background texture of the 3rd slot of the smithing table.
     */
    fun register(baseSlotTexture: Identifier, additionsSlotTexture: Identifier) {
        baseSlotTextures += baseSlotTexture
        additionsSlotTextures += additionsSlotTexture
    }
}
