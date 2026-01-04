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

package opekope2.avm_staff.mixin;

import net.minecraft.block.entity.BellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BellBlockEntity.class)
public interface IBellBlockEntityAccessor {
    @Accessor("field_31317")
    static int glowDuration() {
        throw new AssertionError();
    }

    @Accessor("MAX_BELL_HEARING_DISTANCE")
    static int maxBellHearingDistance() {
        throw new AssertionError();
    }

    @Accessor("MAX_RESONATING_TICKS")
    static int maxResonatingTicks() {
        throw new AssertionError();
    }
}
