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

package opekope2.avm_staff.api.item;

import org.jetbrains.annotations.Nullable;

/**
 * Holder of a temporary data while an entity is using an item. To be used by the item class being used on server side.
 */
public interface IActiveItemTempDataHolder {
    /**
     * Gets the temporary data associated with the entity.
     */
    @Nullable
    Object staffMod$getActiveItemTempData();

    /**
     * Associates a temporary data with an entity, overwriting previous data.
     *
     * @param value The data to associate
     */
    void staffMod$setActiveItemTempData(@Nullable Object value);
}
