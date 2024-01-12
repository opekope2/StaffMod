// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

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
