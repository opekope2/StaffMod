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

package opekope2.avm_staff.internal.staff.handler

import net.minecraft.block.Blocks
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.util.attackDamage
import opekope2.avm_staff.util.attackSpeed
import opekope2.avm_staff.util.cameraUp
import opekope2.avm_staff.util.destruction.BlockDestructionPredicate
import opekope2.avm_staff.util.destruction.GoldBlockStaffShapePredicate
import opekope2.avm_staff.util.destruction.IShapedBlockDestructionPredicate
import opekope2.avm_staff.util.destruction.MaxHardnessPredicate
import opekope2.avm_staff.util.dropcollector.VanillaBlockDropCollector

internal class GoldBlockHandler : AbstractMassDestructiveStaffHandler() {
    override val attributeModifiers = StaffAttributeModifiersComponentBuilder()
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(14.0), AttributeModifierSlot.MAINHAND)
        .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(1.0), AttributeModifierSlot.MAINHAND)
        .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
        .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
        .build()

    override fun createBlockDestructionShapePredicate(
        world: World,
        attacker: LivingEntity,
        target: BlockPos
    ): IShapedBlockDestructionPredicate {
        val forwardVector = attacker.facing.vector
        val upVector = attacker.cameraUp.vector
        return GoldBlockStaffShapePredicate(target, forwardVector, upVector)
    }

    override fun createDestructionPredicate(shapePredicate: IShapedBlockDestructionPredicate): BlockDestructionPredicate =
        MAX_OBSIDIAN_HARDNESS.and(shapePredicate)

    override fun createBlockDropCollector(shapePredicate: IShapedBlockDestructionPredicate) =
        VanillaBlockDropCollector()

    private companion object {
        private val MAX_OBSIDIAN_HARDNESS = MaxHardnessPredicate(Blocks.OBSIDIAN)
    }
}
