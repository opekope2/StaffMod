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

package opekope2.avm_staff.mixin.neoforge;

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
