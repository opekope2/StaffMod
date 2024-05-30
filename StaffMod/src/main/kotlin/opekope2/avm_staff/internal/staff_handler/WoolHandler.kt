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

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.mixin.IMinecraftClientMixin
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.attackSpeed
import opekope2.avm_staff.util.mutableItemStackInStaff

class WoolHandler(private val woolBlockItem: BlockItem, private val carpetBlockItem: BlockItem) : StaffHandler() {
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
        if (world.isClient && user is ClientPlayerEntity) {
            // Allow fast block placement
            (MinecraftClient.getInstance() as IMinecraftClientMixin).setItemUseCooldown(0)
        }

        val originalState = world.getBlockState(target)
        if (originalState.isIn(BlockTags.WOOL) || originalState.isIn(BlockTags.WOOL_CARPETS)) return ActionResult.FAIL

        val itemToPlace = if (side == Direction.UP) carpetBlockItem else woolBlockItem
        val woolPlaceContext = WoolPlacementContext(
            world,
            user as? PlayerEntity,
            hand,
            staffStack.mutableItemStackInStaff!!,
            BlockHitResult(target.toCenterPos(), side, target, false)
        )
        return itemToPlace.place(woolPlaceContext)
    }

    private class WoolPlacementContext(
        world: World,
        playerEntity: PlayerEntity?,
        hand: Hand,
        itemStack: ItemStack,
        blockHitResult: BlockHitResult
    ) : ItemPlacementContext(world, playerEntity, hand, itemStack, blockHitResult)

    private companion object {
        private val ATTRIBUTE_MODIFIERS = AttributeModifiersComponent.builder()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(2.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(2.0), AttributeModifierSlot.MAINHAND)
            .build()
    }
}
