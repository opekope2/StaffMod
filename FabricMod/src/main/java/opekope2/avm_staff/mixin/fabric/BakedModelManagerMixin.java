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

import net.minecraft.client.render.model.BakedModelManager;
import opekope2.avm_staff.api.item.StaffItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Forge parity lite
@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {
    @Inject(method = "upload", at = @At("TAIL"))
    @SuppressWarnings("deprecation")
    void upload(CallbackInfo ci) {
        StaffItemHandler.reloadModels(); // Shhh - this is internal use
    }
}
