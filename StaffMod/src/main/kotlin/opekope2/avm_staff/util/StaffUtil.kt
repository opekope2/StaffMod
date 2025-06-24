/*
 * AvM Staff Mod
 * Copyright (c) 2023-2025 opekope2
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

@file: JvmName("StaffUtil")

package opekope2.avm_staff.util

import net.minecraft.component.ComponentChanges
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import opekope2.avm_staff.api.component.StaffItemComponent
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.DataComponentTypes

/**
 * Checks if an item is added the given staff item stack.
 */
val ItemStack.isItemInStaff: Boolean
    @JvmName("isItemInStaff")
    get() = DataComponentTypes.staffItem in this

/**
 * Gets the item inserted into the given staff item stack.
 */
val ItemStack.itemInStaff: Item?
    get() = getOrDefault(DataComponentTypes.staffItem, null)?.item?.item

/**
 * Gets the item stack inserted into the given staff item stack.
 * The value returned MUST NOT be modified in any way, use [mutableItemStackInStaff] instead.
 *
 * @see mutableItemStackInStaff
 */
val ItemStack.itemStackInStaff: ItemStack?
    get() = getOrDefault(DataComponentTypes.staffItem, null)?.item

/**
 * Gets or sets a copy of the item stack inserted into the given staff item stack. The value returned or passed in can
 * be freely modified.
 *
 * @see itemStackInStaff
 */
var ItemStack.mutableItemStackInStaff: ItemStack?
    get() = itemStackInStaff?.copy()
    set(value) {
        val changes = ComponentChanges.builder()

        if (value == null || value.isEmpty) {
            changes.remove(DataComponentTypes.staffItem)
        } else {
            changes.add(DataComponentTypes.staffItem, StaffItemComponent(value.copy()))
        }

        applyChanges(changes.build())
    }

private val staff2enabledItemsTag = mutableMapOf<Item, TagKey<Item>>()

/**
 * Returns the [item tag][TagKey] representing the enabled items in the given staff [ItemStack].
 */
val ItemStack.enabledItemsInStaffTag: TagKey<Item>
    get() = staff2enabledItemsTag.getOrPut(item) {
        TagKey.of(RegistryKeys.ITEM, item.registryId.withPrefixedPath("enabled_in_staff/"))
    }

/**
 * Returns the registered staff handler of the item in the given staff [ItemStack] if available, [StaffHandler.Fallback]
 * otherwise.
 */
val ItemStack.staffHandlerOrFallback: StaffHandler
    get() = when (val itemInStaff = this.itemInStaff) {
        null -> StaffHandler.Empty
        !in StaffHandler.Registry -> StaffHandler.Fallback
        in enabledItemsInStaffTag -> StaffHandler.Registry.getValue(itemInStaff)
        else -> StaffHandler.Fallback
    }

private const val STAFF_MODEL_LENGTH = 40.0 / 16.0
private const val STAFF_MODEL_ITEM_POSITION_CENTER = 33.5 / 16.0
private const val STAFF_MODEL_SCALE = 0.85

/**
 * Gets the approximate position of the staff's tip, when held by an entity.
 */
val Entity.approximateStaffTipPosition: Vec3d
    get() = eyePos + rotationVector * (STAFF_MODEL_LENGTH * STAFF_MODEL_SCALE)

/**
 * Gets the approximate position of the item in the staff, when held my an entity.
 */
val Entity.approximateStaffItemPosition: Vec3d
    get() = eyePos + rotationVector * (STAFF_MODEL_ITEM_POSITION_CENTER * STAFF_MODEL_SCALE)

/**
 * Checks if the user has sufficient space in front to use the staff.
 */
val Entity.canUseStaff: Boolean
    get() = world.raycast(
        RaycastContext(
            eyePos,
            eyePos + rotationVector * STAFF_MODEL_LENGTH,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            this
        )
    ).type == HitResult.Type.MISS
