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

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.renderer.IStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.InsideStaffBlockStateRenderer
import opekope2.avm_staff.api.item.renderer.StaffBlockStateRenderer
import org.joml.Vector3f

class LightningRodHandler : StaffItemHandler() {
    override val staffItemRenderer: IStaffItemRenderer = LightningRodRenderer()

    override fun useOnBlock(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        target: BlockPos,
        side: Direction,
        hand: Hand
    ): ActionResult {
        if (!EntityType.LIGHTNING_BOLT.isEnabled(world.enabledFeatures)) return ActionResult.FAIL

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

        return ActionResult.SUCCESS
    }

    private class LightningRodRenderer : StaffBlockStateRenderer() {
        override val scale: Float
            get() = InsideStaffBlockStateRenderer.SCALE
        override val offset: Vector3f = InsideStaffBlockStateRenderer.OFFSET.add(0f, 10f / 16f, 0f, Vector3f())

        override fun getBlockState(staffStack: ItemStack): BlockState = Blocks.LIGHTNING_ROD.defaultState
    }
}
