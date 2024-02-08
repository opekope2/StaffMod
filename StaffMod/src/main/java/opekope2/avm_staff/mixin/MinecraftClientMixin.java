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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import opekope2.avm_staff.internal.event_handler.StaffAttackHandler;
import opekope2.avm_staff.internal.networking.c2s.play.StaffAttackC2SPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public abstract ClientPlayNetworkHandler getNetworkHandler();

    // Because Fabric API can't not swing hand (it always swings hand)
    @Inject(
            method = "doAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void handleEntityAttack(CallbackInfoReturnable<Boolean> cir) {
        assert player != null;
        assert world != null;
        assert crosshairTarget != null;

        Entity target = ((EntityHitResult) crosshairTarget).getEntity();
        ActionResult result = StaffAttackHandler.attackEntity(player, world, Hand.MAIN_HAND, target);
        switch (result) {
            case SUCCESS -> {
                Objects.requireNonNull(getNetworkHandler())
                        .sendPacket(PlayerInteractEntityC2SPacket.attack(target, player.isSneaking()));
                player.swingHand(Hand.MAIN_HAND);
                cir.setReturnValue(false);
            }
            case CONSUME, CONSUME_PARTIAL -> {
                Objects.requireNonNull(getNetworkHandler())
                        .sendPacket(PlayerInteractEntityC2SPacket.attack(target, player.isSneaking()));
                cir.setReturnValue(false);
            }
            case FAIL -> cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "doAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasLimitedAttackSpeed()Z",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void handleAttack(CallbackInfoReturnable<Boolean> cir) {
        assert player != null;
        assert world != null;

        ItemStack stackInHand = player.getStackInHand(Hand.MAIN_HAND);

        ActionResult result = StaffAttackHandler.attack(stackInHand, world, player, Hand.MAIN_HAND);
        switch (result) {
            case SUCCESS -> {
                new StaffAttackC2SPacket().send();
                player.swingHand(Hand.MAIN_HAND);
                cir.setReturnValue(false);
            }
            case CONSUME, CONSUME_PARTIAL -> {
                new StaffAttackC2SPacket().send();
                cir.setReturnValue(false);
            }
            case FAIL -> cir.setReturnValue(false);
        }
    }
}
