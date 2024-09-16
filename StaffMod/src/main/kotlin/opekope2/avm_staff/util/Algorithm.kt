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

@file: JvmName("Algorithm")

package opekope2.avm_staff.util

import java.util.function.IntSupplier

/**
 * Sorts [input] using [counting sort](https://en.wikipedia.org/wiki/Counting_sort) into a new array and returns it.
 * The input array will not be changed.
 *
 * @param input The array to sort by [IntSupplier.getAsInt]
 * @param k     The maximum value returned by [IntSupplier.getAsInt]
 */
inline fun <reified T : IntSupplier> countingSort(input: Array<T>, k: Int): Array<T> {
    val count = IntArray(k + 1)
    val output = arrayOfNulls<T?>(input.size)

    for (i in input) {
        count[i.asInt]++
    }

    for (i in 1..k) {
        count[i] += count[i - 1]
    }

    for (i in input.size - 1 downTo 0) {
        val j = input[i].asInt
        count[j]--
        output[count[j]] = input[i]
    }

    @Suppress("UNCHECKED_CAST")
    return output as Array<T>
}
