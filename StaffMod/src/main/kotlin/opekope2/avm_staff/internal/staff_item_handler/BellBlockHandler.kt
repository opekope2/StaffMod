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
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer
import net.minecraft.client.render.model.*
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.render.model.json.Transformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
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
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.model.IStaffItemBakedModel
import opekope2.avm_staff.api.item.model.IStaffItemUnbakedModel
import opekope2.avm_staff.api.item.model.StaffItemBakedModel
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.attackSpeed
import opekope2.avm_staff.util.getBakedQuads
import opekope2.avm_staff.util.transform
import org.joml.Vector3f
import java.util.function.Function
import java.util.function.Supplier

class BellBlockHandler : StaffItemHandler() {
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
    private class BellUnbakedModel : IStaffItemUnbakedModel {
        override fun getModelDependencies() = setOf<Identifier>()

        override fun setParents(modelLoader: Function<Identifier, UnbakedModel>?) {
        }

        override fun bake(
            baker: Baker,
            textureGetter: Function<SpriteIdentifier, Sprite>,
            rotationContainer: ModelBakeSettings,
            modelId: Identifier,
            transformation: Transformation
        ): IStaffItemBakedModel {
            val bellSprite = textureGetter.apply(BellBlockEntityRenderer.BELL_BODY_TEXTURE)
            val bellModel = BellBlockEntityRenderer.getTexturedModelData().createModel().getChild("bell_body")
            val quads = bellModel.getBakedQuads(bellSprite, bellTransformation)
            val baked = BasicBakedModel(
                quads,
                createEmptyFaceQuads(),
                true,
                false,
                false,
                bellSprite,
                ModelTransformation.NONE,
                ModelOverrideList.EMPTY
            )

            return StaffItemBakedModel(baked.transform(null, transformation, textureGetter))
        }

        private companion object {
            private val bellTransformation = Transformation(
                Vector3f(),
                Vector3f(-3.5f / 9f, -4f / 9f, -3.5f / 9f),
                Vector3f(16f / 9f)
            )

            private fun createEmptyFaceQuads(): Map<Direction, List<BakedQuad>> = mapOf(
                Direction.DOWN to listOf(),
                Direction.UP to listOf(),
                Direction.NORTH to listOf(),
                Direction.SOUTH to listOf(),
                Direction.WEST to listOf(),
                Direction.EAST to listOf(),
            )
        }
    }

    companion object {
        private val ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            attackDamage(8.0),
            EntityAttributes.GENERIC_ATTACK_SPEED,
            attackSpeed(1.5)
        )

        val modelSupplierFactory: Supplier<Supplier<out IStaffItemUnbakedModel>> = Supplier {
            Supplier(::BellUnbakedModel)
        }
    }
}
