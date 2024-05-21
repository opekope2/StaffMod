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

package opekope2.avm_staff.mixin.fabric;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import opekope2.avm_staff.api.StaffMod;
import opekope2.avm_staff.api.staff.StaffHandler;
import opekope2.avm_staff.util.StaffUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract ItemStack getMainHandStack();

    @Inject(method = "disablesShield", at = @At("HEAD"), cancellable = true)
    public void disableShield(CallbackInfoReturnable<Boolean> cir) {
        ItemStack mainHandStack = getMainHandStack();
        if (!mainHandStack.isIn(StaffMod.getStaffsTag())) return;

        Item itemInStaff = StaffUtil.getItemInStaff(mainHandStack);
        if (itemInStaff == null) return;

        StaffHandler handlerOfItem = StaffUtil.getStaffHandlerOrFallback(itemInStaff);
        if (handlerOfItem.disablesShield()) {
            cir.setReturnValue(true);
        }
    }
}
