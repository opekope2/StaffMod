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
import opekope2.avm_staff.internal.event_handler.StaffAttackHandler;
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

        ActionResult result = StaffAttackHandler.attackBlock(player, world, Hand.MAIN_HAND, pos, direction);

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
