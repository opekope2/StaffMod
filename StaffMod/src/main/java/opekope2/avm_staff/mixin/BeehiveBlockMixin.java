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

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import opekope2.avm_staff.api.block.IBlockAfterDestroyHandler;
import opekope2.avm_staff.util.dropcollector.IBlockDropCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BlockWithEntity implements IBlockAfterDestroyHandler {
    protected BeehiveBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    protected abstract void angerNearbyBees(World world, BlockPos pos);

    @Override
    public void staffMod_afterBlockDestroyed(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull IBlockDropCollector dropCollector, @NotNull LivingEntity destroyer, @NotNull ItemStack tool) {
        if (!(blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity)) return;
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
            staffMod_angerBees(destroyer, state, (IBeehiveBlockEntityAccessor) beehiveBlockEntity);
            world.updateComparators(pos, this);
            angerNearbyBees(world, pos);
        }

        if (destroyer instanceof ServerPlayerEntity player) {
            Criteria.BEE_NEST_DESTROYED.trigger(player, state, tool, beehiveBlockEntity.getBeeCount());
        }
    }

    @Unique
    private void staffMod_angerBees(@NotNull LivingEntity destroyer, @NotNull BlockState state, @NotNull IBeehiveBlockEntityAccessor beehiveBlockEntity) {
        List<Entity> bees = beehiveBlockEntity.callTryReleaseBee(state, BeehiveBlockEntity.BeeState.EMERGENCY);
        for (Entity entity : bees) {
            if (!(entity instanceof BeeEntity beeEntity)) continue;
            if (destroyer.getPos().squaredDistanceTo(entity.getPos()) > 16.0) continue;
            if (!beehiveBlockEntity.callIsSmoked()) beeEntity.setTarget(destroyer);
            else beeEntity.setCannotEnterHiveTicks(400);
        }
    }
}
