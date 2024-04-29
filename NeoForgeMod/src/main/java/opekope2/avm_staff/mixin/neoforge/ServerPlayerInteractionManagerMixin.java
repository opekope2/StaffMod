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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import opekope2.avm_staff.internal.event_handler.StaffAttackHandlerKt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    protected ServerWorld world;

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    // Change method to directly call Staff Mod instead of an exposed event
    @Inject(at = @At("HEAD"), method = "processBlockBreakingAction", cancellable = true)
    public void startBlockBreak(BlockPos pos, PlayerActionC2SPacket.Action playerAction, Direction direction, int worldHeight, int i, CallbackInfo info) {
        if (playerAction != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) return;

        ActionResult result = StaffAttackHandlerKt.attackBlock(player, world, Hand.MAIN_HAND, pos, direction);

        if (result == ActionResult.PASS) return;

        // The client might have broken the block on its side, so make sure to let it know.
        this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));

        if (world.getBlockState(pos).hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity != null) {
                Packet<ClientPlayPacketListener> updatePacket = blockEntity.toUpdatePacket();

                if (updatePacket != null) {
                    this.player.networkHandler.sendPacket(updatePacket);
                }
            }
        }

        info.cancel();
    }

    // Remove unused mixins: interactBlock, interactItem, breakBlock, onBlockBroken
}
