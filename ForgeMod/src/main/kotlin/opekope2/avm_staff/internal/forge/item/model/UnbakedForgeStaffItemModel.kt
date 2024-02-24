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

package opekope2.avm_staff.internal.forge.item.model

import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.ModelLoader
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import opekope2.avm_staff.api.item.model.IStaffItemBakedModel
import opekope2.avm_staff.internal.item.model.UnbakedStaffItemModel
import java.util.function.Function

@OnlyIn(Dist.CLIENT)
class UnbakedForgeStaffItemModel(private val original: JsonUnbakedModel) : UnbakedStaffItemModel(original) {
    override fun bake(
        baker: Baker,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier,
        itemModels: Map<Identifier, IStaffItemBakedModel>
    ): BakedModel? {
        return BakedForgeStaffItemModel(
            original.bake(baker, original, textureGetter, rotationContainer, modelId, true) ?: return null,
            itemModels,
            baker.bake(ModelLoader.MISSING_ID, rotationContainer, textureGetter)!!
        )
    }
}
