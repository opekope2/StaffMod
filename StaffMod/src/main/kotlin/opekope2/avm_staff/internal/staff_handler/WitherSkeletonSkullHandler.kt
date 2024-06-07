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

package opekope2.avm_staff.internal.staff_handler

import dev.architectury.event.EventResult
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.AbstractSkullBlock
import net.minecraft.block.Blocks
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer
import net.minecraft.client.render.entity.model.SkullEntityModel
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.WitherSkullEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.Difficulty
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.api.item.renderer.IStaffItemRenderer
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.*

class WitherSkeletonSkullHandler : StaffHandler() {
    override val maxUseTime = 20

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if ((remainingUseTicks and 1) == 0) {
            shootSkull(world, user, Math.random() < 0.1f) // TODO ratio
        }
    }

    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) {
        if (attacker is PlayerEntity && attacker.itemCooldownManager.isCoolingDown(staffStack.item)) return

        shootSkull(world, attacker, false)
        (attacker as? PlayerEntity)?.resetLastAttackedTicks()
    }

    private fun shootSkull(world: World, user: LivingEntity, charged: Boolean) {
        if (world.isClient) return
        if (!user.canUseStaff) return
        if (user is PlayerEntity && user.isAttackCoolingDown) return

        world.syncWorldEvent(WorldEvents.WITHER_SHOOTS, user.blockPos, 0)

        val (x, y, z) = user.rotationVector
        world.spawnEntity(WitherSkullEntity(world, user, x, y, z).apply {
            isCharged = charged
            setPosition(user.approximateStaffTipPosition)
        })
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        if (world.isClient) return EventResult.pass()
        if (target is LivingEntity && !target.isInvulnerableTo(world.damageSources.wither())) {
            val amplifier = if (world.difficulty == Difficulty.HARD) 1 else 0
            target.addStatusEffect(StatusEffectInstance(StatusEffects.WITHER, 10 * 20, amplifier))
        }

        return EventResult.pass()
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        (user as? PlayerEntity)?.itemCooldownManager?.set(staffStack.item, 4 * (maxUseTime - remainingUseTicks))
    }

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        onStoppedUsing(staffStack, world, user, 0)
        return staffStack
    }

    @Environment(EnvType.CLIENT)
    class WitherSkeletonSkullStaffItemRenderer : IStaffItemRenderer {
        private val skullModel = SkullEntityModel.getSkullTexturedModelData().createModel()

        override fun renderItemInStaff(
            staffStack: ItemStack,
            mode: ModelTransformationMode,
            matrices: MatrixStack,
            vertexConsumers: VertexConsumerProvider,
            light: Int,
            overlay: Int
        ) {
            matrices.push {
                scale(-1f, -1f, 1f)
                translate(0f, 8f / 16f, 0f)
                scale(2f, 2f, 2f)
                skullModel.render(
                    matrices,
                    vertexConsumers.getBuffer(
                        SkullBlockEntityRenderer.getRenderLayer(
                            (Blocks.WITHER_SKELETON_SKULL as AbstractSkullBlock).skullType, null
                        )
                    ),
                    light,
                    overlay
                )
            }
        }
    }
}
