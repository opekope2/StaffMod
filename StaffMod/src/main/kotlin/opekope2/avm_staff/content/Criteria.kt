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

import net.minecraft.advancement.criterion.Criterion
import net.minecraft.registry.RegistryKeys
import opekope2.avm_staff.api.advancement.criterion.BreakBlockWithStaffCriterion
import opekope2.avm_staff.api.advancement.criterion.TakeDamageWhileUsingItemCriterion
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryUtil

/**
 * Criteria added by AVM Staffs mod.
 */
object Criteria : RegistryUtil<Criterion<*>>(MOD_ID, RegistryKeys.CRITERION) {
    /**
     * Criterion registered as `avm_staff:get_hurt_while_using_item`.
     */
    @JvmField
    val TAKE_DAMAGE_WHILE_USING_ITEM = register("get_hurt_while_using_item") {
        TakeDamageWhileUsingItemCriterion()
    }

    /**
     * @see TAKE_DAMAGE_WHILE_USING_ITEM
     */
    val takeDamageWhileUsingItem: TakeDamageWhileUsingItemCriterion
        @JvmName("takeDamageWhileUsingItem")
        get() = TAKE_DAMAGE_WHILE_USING_ITEM.get()

    /**
     * Criterion registered as `avm_staff:destroy_block_with_staff`. Triggers before a block is destroyed by a staff.
     */
    @JvmField
    val DESTROY_BLOCK_WITH_STAFF = register("destroy_block_with_staff") {
        BreakBlockWithStaffCriterion()
    }

    /**
     * @see DESTROY_BLOCK_WITH_STAFF
     */
    val destroyBlockWithStaff: BreakBlockWithStaffCriterion
        @JvmName("destroyBlockWithStaff")
        get() = DESTROY_BLOCK_WITH_STAFF.get()
}
