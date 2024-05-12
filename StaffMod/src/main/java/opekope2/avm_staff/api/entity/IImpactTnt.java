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

package opekope2.avm_staff.api.entity;

import net.minecraft.entity.TntEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

/**
 * A TNT, which can be configured to explode when collides (with blocks or other entities).
 */
public interface IImpactTnt {
    /**
     * Returns if the current TNT explodes, when it collides with a block or entity.
     */
    boolean staffMod$explodesOnImpact();

    /**
     * Configures the current TNT to explode or not, when it collides with a block or entity.
     *
     * @param explode Whether to explode on collision
     */
    void staffMod$explodeOnImpact(boolean explode);

    /**
     * NBT Key for TNT's explodes on impact property.
     */
    String EXPLODES_ON_IMPACT_NBT_KEY = "ExplodesOnImpact";

    /**
     * Data tracker for TNT's explodes on impact property.
     */
    TrackedData<Boolean> EXPLODES_ON_IMPACT = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
}
