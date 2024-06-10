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

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Blocks
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.item.renderer.BlockStateStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.IStaffItemRenderer
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.addDefault
import opekope2.avm_staff.util.interactionRange
import opekope2.avm_staff.util.isItemCoolingDown
import opekope2.avm_staff.util.push

class LightningRodHandler : StaffHandler() {
    override val attributeModifiers: AttributeModifiersComponent
        get() = ATTRIBUTE_MODIFIERS

    override fun useOnBlock(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        target: BlockPos,
        side: Direction,
        hand: Hand
    ): ActionResult {
        if (!EntityType.LIGHTNING_BOLT.isEnabled(world.enabledFeatures)) return ActionResult.FAIL

        if (user is PlayerEntity && user.isItemCoolingDown(staffStack.item)) return ActionResult.FAIL

        val thunder = world.isThundering
        val lightningPos = target.offset(side)
        val skylit = world.isSkyVisible(lightningPos)

        if (world.isClient) {
            return if (thunder && skylit) ActionResult.SUCCESS
            else ActionResult.CONSUME
        }

        if (!thunder) return ActionResult.FAIL
        if (!skylit) return ActionResult.FAIL

        val lightning = EntityType.LIGHTNING_BOLT.create(world) ?: return ActionResult.FAIL
        lightning.refreshPositionAfterTeleport(lightningPos.toCenterPos())
        world.spawnEntity(lightning)

        (user as? PlayerEntity)?.itemCooldownManager?.set(staffStack.item, 4 * 20)

        return ActionResult.SUCCESS
    }

    @Environment(EnvType.CLIENT)
    class LightningRodStaffItemRenderer : IStaffItemRenderer {
        private val lightningRodRenderer = BlockStateStaffItemRenderer(Blocks.LIGHTNING_ROD.defaultState)

        override fun renderItemInStaff(
            staffStack: ItemStack,
            mode: ModelTransformationMode,
            matrices: MatrixStack,
            vertexConsumers: VertexConsumerProvider,
            light: Int,
            overlay: Int
        ) {
            matrices.push {
                if (mode != ModelTransformationMode.GUI && mode != ModelTransformationMode.FIXED) {
                    translate(0f, 22f / 16f, 0f)
                }
                lightningRodRenderer.renderItemInStaff(staffStack, mode, matrices, vertexConsumers, light, overlay)
            }
        }
    }

    private companion object {
        val ATTRIBUTE_MODIFIERS: AttributeModifiersComponent = AttributeModifiersComponent.builder()
            .addDefault(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            .addDefault(EntityAttributes.GENERIC_ATTACK_SPEED)
            .add(
                EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, interactionRange(2.0), AttributeModifierSlot.MAINHAND
            )
            .add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, interactionRange(2.0), AttributeModifierSlot.MAINHAND)
            .build()
    }
}
