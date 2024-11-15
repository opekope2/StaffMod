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

package opekope2.avm_staff.content

import net.minecraft.world.GameRules

/**
 * Game rules added by AVM Staffs mod.
 */
object GameRules {
    /**
     * Throwable cakes game rule. When set to true, cakes can be thrown by right clicking, and dispensers will shoot cakes
     * instead of dropping them as item.
     */
    @JvmField
    val THROWABLE_CAKES: GameRules.Key<GameRules.BooleanRule> =
        GameRules.register("throwableCakes", GameRules.Category.MISC, GameRules.BooleanRule.create(false))
}
