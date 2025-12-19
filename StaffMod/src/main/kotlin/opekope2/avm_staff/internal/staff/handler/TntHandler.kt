/*
 * AvM Staff Mod
 * Copyright (c) 2024-2025 opekope2
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

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.component.StaffTntDataComponent
import opekope2.avm_staff.api.entity.ImpactTntEntity
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.DataComponentTypes
import opekope2.avm_staff.content.Enchantments
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.util.*

internal class TntHandler : StaffHandler() {
    override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity) = 4 * 20

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        if (!staffStack.isEnchantedWith(Enchantments.distantDetonation, world.registryManager)) {
            if (user is ServerPlayerEntity) overlayMessage(
                user,
                I18n.FEEDBACK_AVM_STAFF_REQUIRES_ENCHANTMENT.getText(I18n.ENCHANTMENT_AVM_STAFF_DISTANT_DETONATION.getText())
            )
            return TypedActionResult.pass(staffStack)
        }
        val tnt = tryShootTnt(world, user) ?: return TypedActionResult.pass(staffStack)

        staffStack[DataComponentTypes.tntData] = StaffTntDataComponent(tnt)

        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (!world.isClient && staffStack[DataComponentTypes.tntData]?.tnt?.isRemoved == true) {
            user.stopUsingItem() // TODO reset last attacked ticks on client
        }
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        val tntData = staffStack.remove(DataComponentTypes.tntData)
        if (!world.isClient && tntData?.tnt?.isAlive == true) {
            tntData.tnt.explodeLater()
        }

        (user as? ServerPlayerEntity)?.incrementItemUseStat(staffStack.item)
        (user as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
        (user as? PlayerEntity)?.resetLastAttackedTicks()
    }

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        onStoppedUsing(staffStack, world, user, 0)
        return staffStack
    }

    override fun attack(staffStack: ItemStack, world: World, attacker: LivingEntity, hand: Hand) {
        if (tryShootTnt(world, attacker) != null) {
            staffStack.damage(1, attacker, hand)
            (attacker as? ServerPlayerEntity)?.incrementItemUseStat(staffStack.item)
            (attacker as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
        }
        (attacker as? PlayerEntity)?.resetLastAttackedTicks()
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

    private fun tryShootTnt(world: World, shooter: LivingEntity): ImpactTntEntity? {
        if (world.isClient) return null
        if (!shooter.canUseStaff()) return null
        if (shooter is PlayerEntity && shooter.isAttackCoolingDown) return null

        val spawnPos =
            EntityTypes.impactTnt.getSpawnPosition(world, shooter.approximateStaffTipPosition) ?: return null
        val (x, y, z) = spawnPos

        val tnt = ImpactTntEntity(world, x, y, z, shooter.rotationVector + shooter.velocity, shooter)
        // TODO Power Charge enchantment: increase explosion power
        world.spawnEntity(tnt)
        world.playSound(
            null,
            x, y, z,
            SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS,
            1f, world.random.nextFloat() * 0.4f + 0.8f
        )
        world.playSound(null, x, y, z, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1f, 1f)
        world.emitGameEvent(shooter, GameEvent.PRIME_FUSE, spawnPos)

        return tnt
    }
}
