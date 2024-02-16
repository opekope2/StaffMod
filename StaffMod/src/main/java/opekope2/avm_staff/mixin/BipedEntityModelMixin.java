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

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import opekope2.avm_staff.IStaffMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin {
    @Shadow
    public BipedEntityModel.ArmPose leftArmPose;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow
    public BipedEntityModel.ArmPose rightArmPose;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Unique
    private float staffMod$degToRad(float degrees) {
        return degrees * (float) Math.PI / 180f;
    }

    @Inject(method = "positionLeftArm", at = @At("TAIL"))
    private void positionLeftArm(LivingEntity entity, CallbackInfo ci) {
        if (leftArmPose == BipedEntityModel.ArmPose.ITEM && entity.getActiveItem().isIn(IStaffMod.get().getStaffsTag())) {
            leftArm.yaw = staffMod$degToRad(entity.headYaw - entity.bodyYaw);
            leftArm.pitch = staffMod$degToRad(entity.getPitch() - 90f);
        }
    }

    @Inject(method = "positionRightArm", at = @At("TAIL"))
    private void positionRightArm(LivingEntity entity, CallbackInfo ci) {
        if (rightArmPose == BipedEntityModel.ArmPose.ITEM && entity.getActiveItem().isIn(IStaffMod.get().getStaffsTag())) {
            rightArm.yaw = staffMod$degToRad(entity.headYaw - entity.bodyYaw);
            rightArm.pitch = staffMod$degToRad(entity.getPitch() - 90f);
        }
    }
}
