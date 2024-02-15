/*
 * AvM Staff Mod
 * Copyright (c) 2016-2024 opekope2
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

package opekope2.avm_staff.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import opekope2.avm_staff.internal.forge.item.model.UnbakedForgeStaffItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import static opekope2.avm_staff.util.Constants.MOD_ID;

// Forge geometry loaders are way more difficult, than porting a Fabric mixin
@Mixin(targets = "net/minecraft/client/render/model/ModelLoader$BakerImpl")
public class ModelLoaderBakerImplMixin {
    // Change method to directly call Staff Mod instead of an exposed event
    // Ignore this error, IntelliJ/mcdev is not looking at the correct jar
    @WrapOperation(
            method = "bake(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/ModelBakeSettings;Ljava/util/function/Function;)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/ModelLoader$BakerImpl;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;"
            )
    )
    private UnbakedModel modifyUnbakedModel(@Coerce Baker thiz, Identifier id, Operation<UnbakedModel> original) {
        UnbakedModel model = original.call(thiz, id);
        if (!MOD_ID.equals(id.getNamespace())) return model;

        return switch (id.getPath()) {
            // TODO hardcoded paths
            case "staff", "item/staff_in_use" -> new UnbakedForgeStaffItemModel((JsonUnbakedModel) model); // FIXME
            default -> model;
        };
    }

    // Remove unused mixins: invokeModifyAfterBake
}
