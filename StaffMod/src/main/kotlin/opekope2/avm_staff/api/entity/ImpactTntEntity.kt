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

package opekope2.avm_staff.api.entity

import net.minecraft.entity.*
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import opekope2.avm_staff.api.impactTntEntityType

class ImpactTntEntity(entityType: EntityType<ImpactTntEntity>, world: World) : TntEntity(entityType, world), Ownable {
    private var owner: LivingEntity? = null

    constructor(world: World, x: Double, y: Double, z: Double, velocity: Vec3d, igniter: LivingEntity?) :
            this(impactTntEntityType.get(), world) {
        setPosition(x, y, z)
        this.velocity = velocity
        fuse = 80
        prevX = x
        prevY = y
        prevZ = z
        owner = igniter
    }

    override fun move(movementType: MovementType?, movement: Vec3d?) {
        super.move(movementType, movement)
        explodeOnImpact()
    }

    private fun explodeOnImpact() {
        var explode = horizontalCollision || verticalCollision
        if (!explode) {
            val collisions = world.getOtherEntities(this, boundingBox) { true }
            explode = collisions.isNotEmpty()

            for (collider in collisions) {
                if (collider is ImpactTntEntity) {
                    // Force explode other TNT, because the current TNT gets discarded before the other TNT gets processed
                    collider.fuse = 0
                }
            }
        }

        if (explode && !world.isClient) {
            fuse = 0
        }
    }

    override fun copyFrom(original: Entity?) {
        super.copyFrom(original)
        if (original is ImpactTntEntity) {
            owner = original.owner
        }
    }

    override fun getOwner() = owner
}
