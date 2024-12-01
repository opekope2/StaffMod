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

@file: JvmName("MinecraftClientUtil")
@file: OnlyIn(Dist.CLIENT)

package opekope2.avm_staff.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.GameOptions
import net.minecraft.client.particle.ParticleManager
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.entity.model.EntityModelLoader
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.render.model.BakedModelManager
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

/**
 * @see MinecraftClient.bakedModelManager
 */
inline val bakedModelManager: BakedModelManager
    get() = MinecraftClient.getInstance().bakedModelManager

/**
 * @see MinecraftClient.blockEntityRenderDispatcher
 */
inline val blockEntityRenderDispatcher: BlockEntityRenderDispatcher
    get() = MinecraftClient.getInstance().blockEntityRenderDispatcher

/**
 * @see MinecraftClient.options
 */
inline val clientOptions: GameOptions
    get() = MinecraftClient.getInstance().options

/**
 * @see MinecraftClient.entityModelLoader
 */
inline val entityModelLoader: EntityModelLoader
    get() = MinecraftClient.getInstance().entityModelLoader

/**
 * @see MinecraftClient.itemRenderer
 */
inline val itemRenderer: ItemRenderer
    get() = MinecraftClient.getInstance().itemRenderer

/**
 * @see MinecraftClient.particleManager
 */
inline val particleManager: ParticleManager
    get() = MinecraftClient.getInstance().particleManager
