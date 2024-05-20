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

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import dev.architectury.event.EventResult
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import opekope2.avm_staff.api.item.renderer.IStaffItemRenderer
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.attackSpeed
import opekope2.avm_staff.util.push

class BellBlockHandler : StaffHandler() {
    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        world.playSound(user, user.blockPos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2f, 1f)

        return TypedActionResult.success(staffStack)
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        world.playSound(
            target as? PlayerEntity,
            target.blockPos,
            SoundEvents.BLOCK_BELL_USE,
            attacker.soundCategory,
            2f,
            1f
        )

        return EventResult.pass()
    }

    override fun getAttributeModifiers(
        staffStack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return if (slot == EquipmentSlot.MAINHAND) ATTRIBUTE_MODIFIERS
        else super.getAttributeModifiers(staffStack, slot)
    }

    @Environment(EnvType.CLIENT)
    class BellStaffItemRenderer : IStaffItemRenderer {
        private val bellModel = BellBlockEntityRenderer.getTexturedModelData().createModel().apply {
            setPivot(-8f, -12f, -8f)
        }

        override fun renderItemInStaff(
            staffStack: ItemStack,
            mode: ModelTransformationMode,
            matrices: MatrixStack,
            vertexConsumers: VertexConsumerProvider,
            light: Int,
            overlay: Int
        ) {
            matrices.push {
                scale(16f / 9f, 16f / 9f, 16f / 9f)
                translate(0f, 2f / 9f, 0f)

                bellModel.render(
                    matrices,
                    BellBlockEntityRenderer.BELL_BODY_TEXTURE.getVertexConsumer(
                        vertexConsumers,
                        RenderLayer::getEntitySolid
                    ),
                    light,
                    overlay
                )
            }
        }
    }

    companion object {
        private val ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            attackDamage(8.0),
            EntityAttributes.GENERIC_ATTACK_SPEED,
            attackSpeed(1.5)
        )
    }
}
