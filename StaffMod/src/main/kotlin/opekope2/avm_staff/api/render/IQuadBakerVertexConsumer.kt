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

package opekope2.avm_staff.api.render

import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.texture.Sprite

/**
 * A vertex consumer, which creates [BakedQuad]s.
 */
interface IQuadBakerVertexConsumer : VertexConsumer {
    /**
     * Gets or sets the sprite used to create [BakedQuad]s.
     */
    var sprite: Sprite
}
