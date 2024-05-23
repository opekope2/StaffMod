/*
 * AvM Staff Mod
 * Copyright (c) 2023-2024 opekope2
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

import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.api.staff.StaffItemComponent
import opekope2.avm_staff.api.staffItemComponentType

/**
 * Checks if an item is added the given staff item stack.
 */
val ItemStack.isItemInStaff: Boolean
    @JvmName("isItemInStaff")
    get() = staffItemComponentType.get() in this

/**
 * Gets the item inserted into the given staff item stack.
 */
val ItemStack.itemInStaff: Item?
    get() = getOrDefault(staffItemComponentType.get(), null)?.item?.item

/**
 * Gets or sets the item stack inserted into the given staff item stack.
 * The value returned MUST NOT be modified in any way, use [mutableItemStackInStaff] instead.
 * The value passed in MUST NOT be stored elsewhere, pass in a copy of it instead.
 *
 * @see mutableItemStackInStaff
 */
var ItemStack.itemStackInStaff: ItemStack?
    get() = getOrDefault(staffItemComponentType.get(), null)?.item
    set(value) {
        if (value == null || value.isEmpty) {
            remove(staffItemComponentType.get())
            return
        }

        this[staffItemComponentType.get()] = StaffItemComponent(value.copy())
    }

/**
 * Gets a copy of the item stack inserted into the given staff item stack. The value returned can be freely modified.
 *
 * @see itemStackInStaff
 */
val ItemStack.mutableItemStackInStaff: ItemStack?
    get() = itemStackInStaff?.copy()

/**
 * Returns if the given item has a registered handler when inserted into a staff.
 */
val Item.hasStaffHandler: Boolean
    @JvmName("hasStaffHandler")
    get() {
        val itemId = Registries.ITEM.getId(this)
        return itemId in StaffHandler
    }

/**
 * Returns the registered staff handler of the given item if available.
 */
val Item.staffHandler: StaffHandler?
    get() {
        val itemId = Registries.ITEM.getId(this)
        return StaffHandler[itemId]
    }

/**
 * Returns the registered staff handler of the given item if available, [StaffHandler.Default] otherwise.
 */
val Item?.staffHandlerOrDefault: StaffHandler
    get() = this?.staffHandler ?: StaffHandler.Default

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
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            this
        )
    ).type == HitResult.Type.MISS
