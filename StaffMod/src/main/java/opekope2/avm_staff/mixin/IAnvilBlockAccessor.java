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

package opekope2.avm_staff.mixin;

import net.minecraft.block.AnvilBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnvilBlock.class)
public interface IAnvilBlockAccessor {
    @Accessor("FALLING_BLOCK_ENTITY_DAMAGE_MULTIPLIER")
    static float fallingBlockEntityDamageMultiplier() {
        throw new AssertionError();
    }

    @Accessor("FALLING_BLOCK_ENTITY_MAX_DAMAGE")
    static int fallingBlockEntityMaxDamage() {
        throw new AssertionError();
    }
}
