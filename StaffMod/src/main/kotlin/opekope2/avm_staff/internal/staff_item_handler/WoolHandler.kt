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
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.mixin.IMinecraftClientMixin
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.attackSpeed

class WoolHandler(woolBlock: Block, carpetBlock: Block) : StaffItemHandler() {
    private val woolState = woolBlock.defaultState
    private val carpetState = carpetBlock.defaultState

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

        val woolPlaceContext = WoolPlacementContext(
            world,
            user as? PlayerEntity,
            hand,
            staffStack,
            BlockHitResult(target.toCenterPos(), side, target, false)
        )
        if (!woolPlaceContext.canPlace()) return ActionResult.FAIL

        val placedState = if (side == Direction.UP) carpetState else woolState
        if (!world.isClient) {
            world.setBlockState(woolPlaceContext.blockPos, placedState)
        }

        val woolSoundGroup = placedState.soundGroup
        world.playSound(
            user,
            woolPlaceContext.blockPos,
            woolSoundGroup.placeSound,
            SoundCategory.BLOCKS,
            (woolSoundGroup.volume + 1.0f) / 2.0f,
            woolSoundGroup.pitch * 0.8f
        )
        world.emitGameEvent(GameEvent.BLOCK_PLACE, woolPlaceContext.blockPos, GameEvent.Emitter.of(user, placedState))

        return ActionResult.SUCCESS
    }

    override fun getAttributeModifiers(
        staffStack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return if (slot == EquipmentSlot.MAINHAND) ATTRIBUTE_MODIFIERS
        else super.getAttributeModifiers(staffStack, slot)
    }

    private class WoolPlacementContext(
        world: World,
        playerEntity: PlayerEntity?,
        hand: Hand,
        itemStack: ItemStack,
        blockHitResult: BlockHitResult
    ) : ItemPlacementContext(world, playerEntity, hand, itemStack, blockHitResult)

    private companion object {
        private val ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            attackDamage(2.0),
            EntityAttributes.GENERIC_ATTACK_SPEED,
            attackSpeed(2.0)
        )
    }
}
