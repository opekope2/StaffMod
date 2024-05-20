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
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import opekope2.avm_staff.api.staff.StaffHandler

/**
 * NBT key
 */
private const val ITEM_KEY = "Item"

/**
 * Checks if an item is added the given staff item stack.
 */
val ItemStack.isItemInStaff: Boolean
    @JvmName("isItemInStaff")
    get() = nbt?.contains(ITEM_KEY) ?: false

/**
 * Gets or sets the item added to the given staff item stack.
 */
var ItemStack.itemInStaff: ItemStack?
    get() {
        return if (!isItemInStaff) null
        else ItemStack.fromNbt(nbt?.getCompound(ITEM_KEY) ?: return null)
    }
    set(value) {
        if (value == null) {
            removeSubNbt(ITEM_KEY)
            return
        }

        val staffItemStack = value.split(1)
        val nbt = getOrCreateNbt()
        nbt.put(ITEM_KEY, NbtCompound().also(staffItemStack::writeNbt))
    }

/**
 * Returns if the given staff item stack has a registered handler.
 * This item stack is not the staff item stack, but the one can be inserted into the staff.
 */
val ItemStack.hasHandlerOfItem: Boolean
    @JvmName("hasHandlerOfStaff")
    get() {
        val itemId = Registries.ITEM.getId(item)
        return itemId in StaffHandler
    }

/**
 * Returns the handler of the given item stack, if available.
 * This item stack is not the staff item stack, but the one can be inserted into the staff.
 */
val ItemStack.handlerOfItem: StaffHandler?
    get() {
        val itemId = Registries.ITEM.getId(item)
        return StaffHandler[itemId]
    }

/**
 * Returns the handler of the given item stack, if available, a dummy one if not, and the empty staff handler if the
 * item stack is `null`.
 * This item stack is not the staff item stack, but the one can be inserted into the staff.
 */
val ItemStack?.handlerOfItemOrFallback: StaffHandler
    get() = if (this == null) StaffHandler.EmptyStaffHandler
    else handlerOfItem ?: StaffHandler.FallbackStaffHandler

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
