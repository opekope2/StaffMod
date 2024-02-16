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
import net.minecraft.block.BlockState
import net.minecraft.client.render.block.BlockModels
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.Transformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.util.Identifier
import opekope2.avm_staff.util.transform
import java.util.function.Function

/**
 * Default implementation of [IStaffItemUnbakedModel].
 *
 * @param blockState    The block state to bake
 */
@Environment(EnvType.CLIENT)
class UnbakedBlockStateModel(private val blockState: BlockState) : IStaffItemUnbakedModel {
    private val blockStateId = BlockModels.getModelId(blockState)
    private val dependencies = setOf(blockStateId)

    override fun getModelDependencies() = dependencies

    override fun setParents(modelLoader: Function<Identifier, UnbakedModel>?) {
    }

    override fun bake(
        baker: Baker,
        textureGetter: Function<SpriteIdentifier, Sprite>,
        rotationContainer: ModelBakeSettings,
        modelId: Identifier,
        transformation: Transformation
    ): IStaffItemBakedModel? {
        val baked = baker.bake(blockStateId, rotationContainer) ?: return null

        return StaffItemBakedModel(baked.transform(blockState, transformation, textureGetter))
    }
}
