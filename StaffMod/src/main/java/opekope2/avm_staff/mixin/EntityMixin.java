/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import opekope2.avm_staff.api.item.StaffItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract World getWorld();

    @Inject(method = "onStruckByLightning", at = @At("HEAD"), cancellable = true)
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        var self = (Entity) (Object) this;
        if (!(self instanceof LivingEntity livingEntity)) return;

        var activeItem = livingEntity.getActiveItem();
        if (!(activeItem.getItem() instanceof StaffItem staffItem)) return;
        if (!staffItem.isInvulnerableToLightning(activeItem, getWorld(), livingEntity, livingEntity.getActiveHand()))
            return;

        ci.cancel();
    }
}
