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

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import opekope2.avm_staff.api.StaffMod;
import opekope2.avm_staff.api.item.IActiveItemTempDataHolder;
import opekope2.avm_staff.api.item.IDisablesShield;
import opekope2.avm_staff.api.item.StaffItemHandler;
import opekope2.avm_staff.util.StaffUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements IActiveItemTempDataHolder {
    @Unique
    @Nullable
    private Object staffMod$activeItemTempData;

    @Shadow
    public abstract ItemStack getMainHandStack();

    @Inject(method = "disablesShield", at = @At("HEAD"), cancellable = true)
    public void disableShield(CallbackInfoReturnable<Boolean> cir) {
        ItemStack mainHandStack = getMainHandStack();
        if (!mainHandStack.isIn(StaffMod.getStaffsTag())) return;

        ItemStack itemInStaff = StaffUtil.getItemInStaff(mainHandStack);
        if (itemInStaff == null) return;

        StaffItemHandler handlerOfItem = StaffUtil.getHandlerOfItem(itemInStaff);
        if (handlerOfItem instanceof IDisablesShield) {
            cir.setReturnValue(true);
        }
    }

    // Only invoke on server
    @Inject(method = "clearActiveItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setLivingFlag(IZ)V"))
    public void clearActiveItem(CallbackInfo ci) {
        staffMod$setActiveItemTempData(null);
    }

    @Nullable
    @Override
    public Object staffMod$getActiveItemTempData() {
        return staffMod$activeItemTempData;
    }

    @Override
    public void staffMod$setActiveItemTempData(@Nullable Object value) {
        staffMod$activeItemTempData = value;
    }
}
