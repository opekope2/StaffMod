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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;
import opekope2.avm_staff.api.entity.IImpactTnt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity implements IImpactTnt {
    private TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected abstract void explode();

    @Shadow
    @Nullable
    public abstract LivingEntity getOwner();

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo ci) {
        dataTracker.startTracking(EXPLODES_ON_IMPACT, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(EXPLODES_ON_IMPACT_NBT_KEY, explodesOnImpact());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(EXPLODES_ON_IMPACT_NBT_KEY, NbtElement.BYTE_TYPE)) {
            explodeOnImpact(nbt.getBoolean(EXPLODES_ON_IMPACT_NBT_KEY));
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/TntEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void explodeOnImpact(CallbackInfo ci) {
        if (!explodesOnImpact()) return;

        boolean explode = horizontalCollision || verticalCollision;
        if (!explode) {
            Entity owner = getOwner();
            List<Entity> collisions = getWorld().getOtherEntities(this, getBoundingBox(), entity -> entity != owner);
            explode = !collisions.isEmpty();

            for (Entity collider : collisions) {
                if (collider instanceof TntEntity tnt && ((IImpactTnt) tnt).explodesOnImpact()) {
                    // Force explode other TNT, because the current TNT gets discarded before the other TNT gets processed
                    tnt.setFuse(0);
                }
            }
        }

        if (!explode) return;

        if (!getWorld().isClient) {
            // Server sends EntitiesDestroyS2CPacket to client, because TntEntity.getOwner() isn't available on the client.
            discard();
            explode();
        }
    }

    @Override
    public boolean explodesOnImpact() {
        return dataTracker.get(EXPLODES_ON_IMPACT);
    }

    @Override
    public void explodeOnImpact(boolean explode) {
        dataTracker.set(EXPLODES_ON_IMPACT, explode);
    }
}
