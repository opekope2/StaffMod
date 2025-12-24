/*
 * AvM Staff Mod
 * Copyright (c) 2024-2025 opekope2
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
import kotlin.properties.PropertyDelegateProvider

private typealias TGameRules = opekope2.avm_staff.content.GameRules

/**
 * Game rules added by AVM Staffs mod.
 */
object GameRules {
    @JvmStatic
    private fun <T : GameRules.Rule<T>> registering(category: GameRules.Category, type: GameRules.Type<T>) =
        PropertyDelegateProvider<TGameRules, Lazy<GameRules.Key<T>>> { _, property ->
            lazyOf(GameRules.register(property.name, category, type))
        }

    /**
     * Throwable cakes game rule.
     * When enabled, cakes can be thrown by right-clicking, and dispensers will shoot cakes instead of dropping them as item.
     */
    @JvmStatic
    val throwableCakes by registering(GameRules.Category.MISC, GameRules.BooleanRule.create(false))

    /**
     * Bell Staff ESP rule.
     * When enabled, bell staff will apply the glowing effect to all living entities in a smaller range.
     */
    @JvmStatic
    val bellStaffEsp by registering(GameRules.Category.MISC, GameRules.BooleanRule.create(false))
}
