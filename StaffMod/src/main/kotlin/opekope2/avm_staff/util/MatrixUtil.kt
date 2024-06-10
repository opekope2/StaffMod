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

@file: JvmName("MatrixUtil")

package opekope2.avm_staff.util

import net.minecraft.client.util.math.MatrixStack

/**
 * [Pushes][MatrixStack.push] to the given matrix stack, invokes [action], then [pops][MatrixStack.pop] from the given
 * matrix stack.
 *
 * @param action    The action to invoke between [MatrixStack.push] and [MatrixStack.pop]
 */
inline fun MatrixStack.push(action: MatrixStack.() -> Unit) {
    push()
    action()
    pop()
}
