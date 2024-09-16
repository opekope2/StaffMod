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

import net.minecraft.block.BlockState;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import opekope2.avm_staff.api.block.IBlockAfterDestroyHandler;
import opekope2.avm_staff.util.dropcollector.IBlockDropCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin implements IBlockAfterDestroyHandler {
    @Shadow
    @Final
    public static IntProperty EGGS;

    @Override
    public void staffMod_afterBlockDestroyed(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull IBlockDropCollector dropCollector, @NotNull LivingEntity destroyer, @NotNull ItemStack tool) {
        int eggs = state.get(EGGS);
        for (int i = 1; i < eggs; i++) {
            dropCollector.collect(world, pos, state, blockEntity, destroyer, tool);
        }
    }
}
