/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opekope2.avm_staff.mixin.forge;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import opekope2.avm_staff.internal.event_handler.StaffAttackHandlerKt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private GameMode gameMode;

    @Shadow
    protected abstract void sendSequencedPacket(ClientWorld clientWorld, SequencedPacketCreator supplier);

    // Change method to directly call Staff Mod instead of an exposed event
    @Inject(
            method = "attackBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z", ordinal = 0),
            cancellable = true
    )
    private void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        assert client.player != null;
        assert client.world != null;

        ActionResult result = StaffAttackHandlerKt.attackBlock(client.player, client.world, Hand.MAIN_HAND, pos, direction);

        if (result != ActionResult.PASS) {
            // Returning true will spawn particles and trigger the animation of the hand -> only for SUCCESS.
            info.setReturnValue(result == ActionResult.SUCCESS);

            // We also need to let the server process the action if it's accepted.
            if (result.isAccepted()) {
                sendSequencedPacket(client.world, id -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, id));
            }
        }
    }

    @Inject(
            method = "updateBlockBreakingProgress",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z", ordinal = 0),
            cancellable = true
    )
    private void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        if (gameMode.isCreative()) {
            attackBlock(pos, direction, info);
        }
    }

    // Inline fabric_fireAttackBlockCallback
    // Remove unused mixins: fabric$onBlockBroken, interactBlock, interactItem, attackEntity
}
