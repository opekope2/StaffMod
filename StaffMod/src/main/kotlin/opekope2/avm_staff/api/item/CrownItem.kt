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

package opekope2.avm_staff.api.item

import net.minecraft.block.Block
import net.minecraft.block.DispenserBlock
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.Equipment
import net.minecraft.item.ItemStack
import net.minecraft.item.VerticallyAttachableBlockItem
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Direction
import net.minecraft.world.World

/**
 * A crown, which makes piglins neutral when worn, just like gold armor.
 */
open class CrownItem(standingBlock: Block, wallBlock: Block, settings: Settings) :
    VerticallyAttachableBlockItem(standingBlock, wallBlock, settings, Direction.DOWN), Equipment {
    init {
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        return equipAndSwap(this, world, user, hand)
    }

    override fun getEquipSound(): RegistryEntry<SoundEvent> = SoundEvents.ITEM_ARMOR_EQUIP_GOLD

    override fun getSlotType(): EquipmentSlot = EquipmentSlot.HEAD
}
