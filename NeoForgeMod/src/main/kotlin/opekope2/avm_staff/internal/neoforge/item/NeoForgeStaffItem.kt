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

package opekope2.avm_staff.internal.neoforge.item

import com.google.common.collect.Multimap
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.item.BuiltinModelItemRenderer
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import net.neoforged.neoforge.common.extensions.IItemExtension
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.item.renderer.StaffRenderer
import opekope2.avm_staff.util.itemInStaff
import java.util.function.Consumer

class NeoForgeStaffItem(settings: Item.Settings) : StaffItem(settings), IItemExtension {
    override fun getAttributeModifiers(
        slot: EquipmentSlot,
        stack: ItemStack
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return stack.itemInStaff.handlerOfItemOrFallback.getAttributeModifiers(stack, slot)
    }

    @Suppress("RemoveExplicitSuperQualifier") // Required because StaffItem apparently also has canDisableShield
    override fun canDisableShield(
        stack: ItemStack,
        shield: ItemStack,
        entity: LivingEntity,
        attacker: LivingEntity
    ): Boolean {
        return stack.itemInStaff.staffHandlerOrFallback.disablesShield() ||
                super<IItemExtension>.canDisableShield(stack, shield, entity, attacker)
    }

    override fun isRepairable(arg: ItemStack): Boolean {
        return false
    }

    override fun onLeftClickEntity(stack: ItemStack, player: PlayerEntity, entity: Entity): Boolean {
        return stack.itemInStaff.staffHandlerOrFallback.attackEntity(
            stack, player.entityWorld, player, entity, Hand.MAIN_HAND
        ).interruptsFurtherEvaluation()
    }

    override fun shouldCauseReequipAnimation(
        oldStack: ItemStack,
        newStack: ItemStack,
        slotChanged: Boolean
    ): Boolean {
        val oldHandler = oldStack.itemInStaff.staffHandlerOrFallback
        val newHandler = newStack.itemInStaff.staffHandlerOrFallback

        return if (oldHandler !== newHandler) true
        else oldHandler.allowReequipAnimation(oldStack, newStack, slotChanged)
    }

    // Calm down IDEA, this is beyond your understanding
    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) {
        consumer.accept(object : IClientItemExtensions {
            override fun getCustomRenderer() = Renderer
        })
    }

    object Renderer : BuiltinModelItemRenderer(
        MinecraftClient.getInstance().blockEntityRenderDispatcher,
        MinecraftClient.getInstance().entityModelLoader
    ) {
        override fun render(
            stack: ItemStack,
            mode: ModelTransformationMode,
            matrices: MatrixStack,
            vertexConsumers: VertexConsumerProvider,
            light: Int,
            overlay: Int
        ) {
            StaffRenderer.renderStaff(stack, mode, matrices, vertexConsumers, light, overlay)
        }
    }
}
