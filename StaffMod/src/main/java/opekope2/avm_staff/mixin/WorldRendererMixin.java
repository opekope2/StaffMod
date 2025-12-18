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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import opekope2.avm_staff.api.staff.Defuse;
import opekope2.avm_staff.util.Constants;
import opekope2.avm_staff.util.ParticleUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private @Nullable ClientWorld world;

    @Inject(method = "processWorldEvent", at = @At("TAIL"))
    private void processWorldEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) {
        if (eventId == Constants.ENTITY_DEFUSED_WORLD_EVENT) staffMod_onEntityDefused(data);
    }

    @Unique
    private void staffMod_onEntityDefused(int id) {
        assert world != null;
        var entity = world.getEntityById(id);
        if (entity == null) return;

        var defuseState = Defuse.getDefuseBlockState(entity.getType());
        if (defuseState == null) return;

        var soundGroup = defuseState.getSoundGroup();
        ParticleUtil.addBlockBreakParticles(client.particleManager, world, entity.getBoundingBox(), defuseState);
        world.playSound(
                entity.getX(), entity.getY(), entity.getZ(),
                soundGroup.getBreakSound(), entity.getSoundCategory(),
                (soundGroup.getVolume() + 1.0f) / 2.0f, soundGroup.getPitch() * 0.8f,
                false
        );
    }
}
