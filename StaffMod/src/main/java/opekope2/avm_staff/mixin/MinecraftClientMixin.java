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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import opekope2.avm_staff.internal.event_handler.StaffAttackHandlerKt;
import opekope2.avm_staff.internal.networking.c2s.play.StaffAttackC2SPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

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

        ActionResult result = StaffAttackHandlerKt.attack(stackInHand, world, player, Hand.MAIN_HAND);
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
