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

import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootPoolEntry;
import opekope2.avm_staff.internal.loot.ILootPoolBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Mixin(value = LootPool.class, priority = 2000)
public abstract class LootPoolMixin implements ILootPoolBuilder {
    @Shadow
    @Final
    private List<LootPoolEntry> entries;

    @ModifyVariable(method = "<init>", at = @At(value = "HEAD"), index = 1, argsOnly = true)
    private static List<LootPoolEntry> makeEntriesMutable(List<LootPoolEntry> value) {
        return value instanceof ArrayList<?> || value instanceof LinkedList<?> ? value : new ArrayList<>(value);
    }

    @Override
    public void staffMod_addEntry(LootPoolEntry entry) {
        entries.add(entry);
    }
}
