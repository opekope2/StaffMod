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

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.client.render.block.BlockModels
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.Transformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.model.IStaffItemBakedModel
import opekope2.avm_staff.api.item.model.IStaffItemUnbakedModel
import opekope2.avm_staff.api.item.model.StaffItemBakedModel
import opekope2.avm_staff.util.isItemCoolingDown
import opekope2.avm_staff.util.transform
import org.joml.Vector3f
import java.util.function.Function

class LightningRodHandler : StaffItemHandler() {
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
    class LightningRodUnbakedModel(private val state: BlockState) : IStaffItemUnbakedModel {
        private val stateId = BlockModels.getModelId(state)
        private val dependencies = setOf(stateId)

        override fun getModelDependencies() = dependencies

        override fun setParents(modelLoader: Function<Identifier, UnbakedModel>?) {
        }

        override fun bake(
            baker: Baker,
            textureGetter: Function<SpriteIdentifier, Sprite>,
            rotationContainer: ModelBakeSettings,
            modelId: Identifier,
            transformation: Transformation
        ): IStaffItemBakedModel? {
            val baked = baker.bake(stateId, rotationContainer) ?: return null

            return StaffItemBakedModel(
                baked
                    .transform(state, lightningRodTransformation, textureGetter)
                    .transform(state, transformation, textureGetter)
            )
        }

        private companion object {
            private val lightningRodTransformation = Transformation(
                Vector3f(),
                Vector3f(-1f / 14f, 10f / 7f, -1f / 14f),
                Vector3f(8f / 7f)
            )
        }
    }
}
