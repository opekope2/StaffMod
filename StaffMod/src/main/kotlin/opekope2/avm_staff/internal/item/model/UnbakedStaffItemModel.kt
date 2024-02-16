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

package opekope2.avm_staff.internal.item.model

import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.model.IStaffItemBakedModel
import opekope2.avm_staff.util.TRANSFORM_INTO_STAFF
import java.util.function.Function

abstract class UnbakedStaffItemModel(private val original: JsonUnbakedModel) : UnbakedModel by original {
    abstract fun bake(
        baker: Baker,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier,
        itemModels: Map<Identifier, IStaffItemBakedModel>
    ): BakedModel?

    override fun bake(
        baker: Baker,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier
    ): BakedModel? {
        val itemModels = mutableMapOf<Identifier, IStaffItemBakedModel>()

        for ((id, modelSupplier) in StaffItemHandler.iterateStaffItemModelProviders()) {
            val unbaked = modelSupplier.get()
            // TODO cache baked model, because it will be baked each time UnbakedStaffItemModel is baked. Maybe use resource loader
            val baked = unbaked.bake(baker, textureGetter, rotationContainer, modelId, TRANSFORM_INTO_STAFF)
            itemModels[id] = baked ?: continue
        }

        return bake(baker, textureGetter, rotationContainer, modelId, itemModels)
    }
}
