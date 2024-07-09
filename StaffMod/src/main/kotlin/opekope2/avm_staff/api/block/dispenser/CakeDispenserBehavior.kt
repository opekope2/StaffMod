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

package opekope2.avm_staff.api.block.dispenser

import net.minecraft.block.DispenserBlock
import net.minecraft.block.dispenser.ItemDispenserBehavior
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPointer
import net.minecraft.util.math.Vec3d
import net.minecraft.world.WorldEvents
import opekope2.avm_staff.api.cakeEntityType
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.throwableCakesGameRule

/**
 * Dispenser behavior, which throws [cakes][CakeEntity], if [throwableCakes][throwableCakesGameRule] game rule is
 * enabled.
 */
class CakeDispenserBehavior : ItemDispenserBehavior() {
    override fun dispenseSilently(pointer: BlockPointer, stack: ItemStack): ItemStack {
        if (!pointer.world.gameRules.getBoolean(throwableCakesGameRule)) return super.dispenseSilently(pointer, stack)

        var spawnPos = DispenserBlock.getOutputLocation(pointer, 1.0, Vec3d.ZERO)
        spawnPos = Vec3d(spawnPos.x, spawnPos.y, spawnPos.z).add(0.0, NEGATIVE_HALF_CAKE_HEIGHT, 0.0)
        val rng = pointer.world.random
        val side = pointer.state()[DispenserBlock.FACING]
        val mode = rng.nextDouble() * 0.1 + 1
        val speed = Vec3d(
            rng.nextTriangular(side.offsetX * mode, 0.1),
            rng.nextTriangular(side.offsetY * mode, 0.1),
            rng.nextTriangular(side.offsetZ * mode, 0.1)
        )

        stack.decrement(1)
        CakeEntity.throwCake(pointer.world, spawnPos, speed, null)

        return stack
    }

    override fun playSound(pointer: BlockPointer) {
        if (!pointer.world.gameRules.getBoolean(throwableCakesGameRule)) return super.playSound(pointer)

        pointer.world().syncWorldEvent(WorldEvents.DISPENSER_LAUNCHES_PROJECTILE, pointer.pos(), 0)
    }

    private companion object {
        private val NEGATIVE_HALF_CAKE_HEIGHT = cakeEntityType.get().dimensions.height / -2.0
    }
}
