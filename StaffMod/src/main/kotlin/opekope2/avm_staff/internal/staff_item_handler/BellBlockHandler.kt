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

package opekope2.avm_staff.internal.staff_item_handler

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer
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
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.renderer.IStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.StaffBlockStateRenderer
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.attackSpeed
import org.joml.Vector3f
import java.util.function.Supplier

class BellBlockHandler : StaffItemHandler() {
    override val staffItemRenderer: IStaffItemRenderer = BellRenderer()

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
    ): ActionResult {
        world.playSound(
            target as? PlayerEntity,
            target.blockPos,
            SoundEvents.BLOCK_BELL_USE,
            attacker.soundCategory,
            2f,
            1f
        )

        return ActionResult.PASS
    }

    override fun getAttributeModifiers(
        staffStack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return if (slot == EquipmentSlot.MAINHAND) ATTRIBUTE_MODIFIERS
        else super.getAttributeModifiers(staffStack, slot)
    }

    @Environment(EnvType.CLIENT)
    private class BellRenderer : IStaffItemRenderer, VertexConsumer {
        private val transform: StaffBlockStateRenderer.Transformation
            get() = StaffBlockStateRenderer.Transformation(
                7f / 9f,
                Vector3f((9f - 7f) / 9f / 2f, (22f - 3f) / 16f, (9f - 7f) / 9f / 2f)
            )

        private val bellModel = BellBlockEntityRenderer.getTexturedModelData().createModel().getChild("bell_body")
        private val bellSprite by lazy { BellBlockEntityRenderer.BELL_BODY_TEXTURE.sprite }

        private var emitter: QuadEmitter? = null
        private var vertex = 0

        override fun emitItemQuads(staffStack: ItemStack, randomSupplier: Supplier<Random>, context: RenderContext) {
            context.pushTransform(transform)

            emitter = context.emitter
            bellModel.render(MatrixStack(), this, 0, 0) // Light and overlay are ignored
            emitter = null

            context.popTransform()
        }

        override fun vertex(x: Double, y: Double, z: Double): VertexConsumer {
            emitter!!.pos(vertex, x.toFloat(), y.toFloat(), z.toFloat())
            return this
        }

        override fun color(red: Int, green: Int, blue: Int, alpha: Int): VertexConsumer {
            emitter!!.color(vertex, (alpha shl 24) or (red shl 16) or (green shl 8) or blue)
            return this
        }

        override fun texture(u: Float, v: Float): VertexConsumer {
            emitter!!.uv(vertex, u, v)
            return this
        }

        override fun overlay(u: Int, v: Int): VertexConsumer {
            return this
        }

        override fun light(u: Int, v: Int): VertexConsumer {
            return this
        }

        override fun normal(x: Float, y: Float, z: Float): VertexConsumer {
            emitter!!.normal(vertex, x, y, z)
            return this
        }

        override fun next() {
            if (++vertex == 4) {
                emitter!!.spriteBake(bellSprite, MutableQuadView.BAKE_NORMALIZED).emit()
                vertex = 0
            }
        }

        override fun fixedColor(red: Int, green: Int, blue: Int, alpha: Int) {
        }

        override fun unfixColor() {
        }
    }

    private companion object {
        private val ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            attackDamage(8.0),
            EntityAttributes.GENERIC_ATTACK_SPEED,
            attackSpeed(1.5)
        )
    }
}
