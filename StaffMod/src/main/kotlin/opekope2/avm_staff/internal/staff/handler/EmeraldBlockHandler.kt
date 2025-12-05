/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

package opekope2.avm_staff.internal.staff.handler

import dev.architectury.event.EventResult
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.CreeperEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.util.math.Box
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.util.*

internal class EmeraldBlockHandler : StaffHandler() {
    override val attributeModifiers: AttributeModifiersComponent
        get() = super.attributeModifiers

    override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity) = 72000

    override fun getUseAction(staffStack: ItemStack) = UseAction.BLOCK

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (world.isClient) return

        val entities = world.getEntitiesByClass(
            Entity::class.java,
            DESPAWN_VOLUME.offset(user.approximateStaffItemPosition)
        ) { it.type in EntityTypes.Tags.DEFUSABLE }
        entities.forEach(::defuse)
        staffStack.damage(entities.size, user)
    }

    override fun attackEntity(
        staffStack: ItemStack,
        world: World,
        attacker: LivingEntity,
        target: Entity,
        hand: Hand
    ): EventResult {
        if (world.isClient) return EventResult.pass()
        if (target.type !in EntityTypes.Tags.DEFUSABLE) return EventResult.pass()

        defuse(target)
        staffStack.damage(entity = attacker, hand = hand)
        return EventResult.pass()
    }

    override fun allowComponentsUpdateAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        player: PlayerEntity,
        hand: Hand
    ) = false

    override fun allowReequipAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        selectedSlotChanged: Boolean
    ) = selectedSlotChanged

    override fun isInvulnerableToLightning(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ) = true

    private fun defuse(entity: Entity) {
        if (entity is CreeperEntity) {
            entity.fuseSpeed = -1
            entity.target = null
        } else {
            entity.world.syncWorldEvent(ENTITY_DEFUSED_WORLD_EVENT, entity.blockPos, entity.id)
            entity.discard()
        }
    }

    private companion object {
        private const val DESPAWN_VOLUME_SIZE = 0.25 * STAFF_MODEL_SCALE
        private val DESPAWN_VOLUME = Box(
            -DESPAWN_VOLUME_SIZE,
            -DESPAWN_VOLUME_SIZE,
            -DESPAWN_VOLUME_SIZE,
            DESPAWN_VOLUME_SIZE,
            DESPAWN_VOLUME_SIZE,
            DESPAWN_VOLUME_SIZE
        )
    }
}
