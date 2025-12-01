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

package opekope2.avm_staff.mixin.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.DynamicOps;
import net.minecraft.loot.LootDataType;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import opekope2.avm_staff.internal.neoforge.StaffMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Function;

// FIXME Remove on NeoForge 1.21.4+
// This shouldn't need to exist if NeoForge didn't forget to add RegistryWrapper.WrapperLookup to LootTableLoadEvent
@Mixin(LootDataType.class)
public class LootDataTypeMixin {
    @WrapOperation(
            method = "parse",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;map(Ljava/util/function/Function;)Ljava/util/Optional;"
            )
    )
    Optional<?> saveRegistryOps(Optional<?> instance, Function<?, ?> mapper, Operation<Optional<?>> original, Identifier $, DynamicOps<?> ops) {
        if (!LootDataType.LOOT_TABLES.equals(this)) return original.call(instance, mapper);

        assert StaffMod.LOOT_TABLE_LOADER_REGISTRY_OPS.get() == null;

        if (ops instanceof RegistryOps<?> registryOps) StaffMod.LOOT_TABLE_LOADER_REGISTRY_OPS.set(registryOps);
        var result = original.call(instance, mapper);
        StaffMod.LOOT_TABLE_LOADER_REGISTRY_OPS.remove();
        return result;
    }
}
