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
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import opekope2.avm_staff.api.impactTntEntityType

/**
 * A TNT entity, which explodes on collision.
 */
class ImpactTntEntity(entityType: EntityType<ImpactTntEntity>, world: World) : TntEntity(entityType, world), Ownable {
    private var owner: LivingEntity? = null

    /**
     * Creates a new [ImpactTntEntity].
     *
     * @param world     The world to create the TNT in
     * @param x         The X coordinate of the TNT to spawn at
     * @param y         The Y coordinate of the TNT to spawn at
     * @param z         The Z coordinate of the TNT to spawn at
     * @param velocity  The velocity of the spawned TNT
     * @param owner     The entity that caused the TNT to ignite or `null`, if it's not caused by an entity
     *
     */
    constructor(world: World, x: Double, y: Double, z: Double, velocity: Vec3d, owner: LivingEntity?) :
            this(impactTntEntityType.get(), world) {
        setPosition(x, y, z)
        this.velocity = velocity
        fuse = 80
        prevX = x
        prevY = y
        prevZ = z
        this.owner = owner
    }

    override fun move(movementType: MovementType?, movement: Vec3d?) {
        super.move(movementType, movement)
        if (!world.isClient) {
            explodeOnImpact()
        }
    }

    private fun explodeOnImpact() {
        if (horizontalCollision || verticalCollision) {
            explodeLater()
            return
        }

        val colliders = EntityPredicates.EXCEPT_SPECTATOR.and(EntityPredicates.VALID_ENTITY)
        val collisions = world.getOtherEntities(this, boundingBox, colliders)
        for (collider in collisions) {
            if (collider is ImpactTntEntity) {
                // Force explode other TNT, because the current TNT gets discarded before the other TNT gets processed
                collider.explodeLater()
            }
        }

        if (collisions.isNotEmpty()) {
            explodeLater()
        }
    }

    /**
     * Sets the fuse of the TNT to 0, scheduling it to explode when [tick]ed next time.
     */
    fun explodeLater() {
        fuse = 0
    }

    override fun copyFrom(original: Entity?) {
        super.copyFrom(original)
        if (original is ImpactTntEntity) {
            owner = original.owner
        }
    }

    override fun getOwner() = owner
}
