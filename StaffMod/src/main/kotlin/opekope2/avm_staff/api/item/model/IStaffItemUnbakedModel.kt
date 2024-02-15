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

package opekope2.avm_staff.api.item.model

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.Transformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.item.StaffItemHandler
import java.util.function.Function

/**
 * An [UnbakedModel] of an item, which can be placed into a staff.
 *
 * @see StaffItemHandler.register
 */
@Environment(EnvType.CLIENT)
interface IStaffItemUnbakedModel : UnbakedModel {
    override fun bake(
        baker: Baker,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModel? = bake(baker, textureGetter, rotationContainer, modelId, Transformation.IDENTITY)

    /**
     * Bakes the item model.
     *
     * @param baker             The baker used to load and bake additional models
     * @param textureGetter     Function to get a [Sprite] from a [SpriteIdentifier]
     * @param rotationContainer The model rotation container
     * @param modelId           The identifier of the staff model being baked. This is not the ID of the item in the staff
     * @param transformation    The transformation, which transforms the item into the staff
     */
    fun bake(
        baker: Baker,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier,
        transformation: Transformation
    ): IStaffItemBakedModel?
}
