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

@file: JvmName("BlockBoxUtil")

package opekope2.avm_staff.util

import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.jvm.optionals.getOrNull

/**
 * Creates a [BlockBox] from encompassing the given vectors.
 *
 * @param posVectors    The vectors to encompass in a [BlockBox]
 * @return A [BlockBox] instance containing the given vectors or `null`, if no vectors were given
 * @see BlockBox.encompassPositions
 */
fun encompassVectors(vararg posVectors: Vec3d) =
    BlockBox.encompassPositions(posVectors.map { BlockPos.ofFloored(it) }).getOrNull()
