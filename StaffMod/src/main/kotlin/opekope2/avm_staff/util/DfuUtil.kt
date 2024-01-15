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

@file: JvmName("DfuUtil")
@file: Suppress("NOTHING_TO_INLINE")

package opekope2.avm_staff.util

import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import java.util.*

inline operator fun <F, S> Pair<F, S>.component1(): F = first

inline operator fun <F, S> Pair<F, S>.component2(): S = second

inline operator fun <L, R> Either<L, R>.component1(): Optional<L> = left()

inline operator fun <L, R> Either<L, R>.component2(): Optional<R> = right()
