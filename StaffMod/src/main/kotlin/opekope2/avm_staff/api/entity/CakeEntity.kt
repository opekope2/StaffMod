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

package opekope2.avm_staff.api.entity

import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.*
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.server.network.EntityTrackerEntry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import opekope2.avm_staff.content.DamageTypes
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.content.SoundEvents
import opekope2.avm_staff.util.*

/**
 * A flying cake entity, which splashes on collision damaging target(s).
 */
class CakeEntity(entityType: EntityType<CakeEntity>, world: World) : Entity(entityType, world), Ownable {
    private var thrower: LivingEntity? = null
    private var timeFalling = 0
    private var redirectedByImpactTnt = false

    /**
     * Creates a new [CakeEntity].
     *
     * @param world     The world to create the cake in
     * @param position  The position of the cake to spawn at
     * @param velocity  The velocity of the spawned cake
     * @param thrower   The entity that threw the cake
     */
    constructor(world: World, position: Vec3d, velocity: Vec3d, thrower: LivingEntity?) :
            this(EntityTypes.cake, world) {
        val (x, y, z) = position
        val (vx, vy, vz) = velocity
        init(x, y, z, vx, vy, vz, thrower)
    }

    private fun init(x: Double, y: Double, z: Double, vx: Double, vy: Double, vz: Double, thrower: LivingEntity?) {
        intersectionChecked = true
        setPosition(x, y, z)
        setVelocity(vx, vy, vz)
        prevX = x
        prevY = y
        prevZ = z
        lookForward()
        setPrevData()
        startPos = blockPos
        this.thrower = thrower
    }

    /**
     * The position, where the cake was spawned.
     */
    var startPos: BlockPos = BlockPos.ORIGIN
        private set

    override fun getMoveEffect(): MoveEffect {
        return MoveEffect.NONE
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
    }

    override fun canHit() = !isRemoved

    override fun getGravity() = 0.04

    override fun tick() {
        setPrevData()
        ++timeFalling
        applyGravity()
        move(MovementType.SELF, velocity)
        lookForward()
        if (!world.isClient) {
            if (timeFalling > 100 && blockPos.y !in world.topY downTo (world.bottomY + 1) || timeFalling > 600) {
                discard()
            } else {
                splashOnImpact()
            }
        }
        velocity *= 0.98
    }

    private fun setPrevData() {
        prevYaw = yaw
        prevPitch = pitch
        prevHorizontalSpeed = horizontalSpeed
    }

    private fun splashOnImpact() {
        if (horizontalCollision || verticalCollision) {
            damageCollidingEntities()
            world.syncWorldEvent(ENTITY_DEFUSED_WORLD_EVENT, blockPos, id)
            discard()
            return
        }

        val colliders = EntityPredicates.EXCEPT_SPECTATOR
            .and(EntityPredicates.VALID_ENTITY)
            .and { it !is CakeEntity }
        val collisions = world.getOtherEntities(this, boundingBox, colliders)

        if (collisions.isNotEmpty()) {
            damageCollidingEntities()
            world.syncWorldEvent(ENTITY_DEFUSED_WORLD_EVENT, blockPos, id)
            discard()
        }
    }

    private fun damageCollidingEntities() {
        val damageables = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.and(EntityPredicates.VALID_LIVING_ENTITY)
        val thrower = thrower
        val damageSource =
            if (thrower == null) world.damageSource(DamageTypes.PRANKED)
            else world.damageSource(DamageTypes.PRANKED_BY_PLAYER, this, thrower)

        world.getOtherEntities(this, boundingBox, damageables).forEach {
            it.damage(damageSource, 1f)
        }
    }

    override fun handleFallDamage(fallDistance: Float, damageMultiplier: Float, damageSource: DamageSource) = false

    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (world.isClient) return super.damage(source, amount)

        val causer = source.source
        if (causer is ImpactTntEntity && causer.owner != null) {
            thrower = causer.owner
            redirectedByImpactTnt = true
        }

        if (!isRemoved && !isInvulnerableTo(source) && source !in DamageTypeTags.IS_EXPLOSION) {
            discard()

            val attacker = source.attacker
            if (attacker is ServerPlayerEntity) {
                Criteria.PLAYER_KILLED_ENTITY.trigger(attacker, this, source)
            }
        }

        return super.damage(source, amount)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        nbt.putInt(TIME_KEY, timeFalling)
        nbt.putBoolean(REDIRECTED_BY_IMPACT_TNT_KEY, redirectedByImpactTnt)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        timeFalling = nbt.getInt(TIME_KEY)
        redirectedByImpactTnt = nbt.getBoolean(REDIRECTED_BY_IMPACT_TNT_KEY)
    }

    override fun doesRenderOnFire() = false

    override fun entityDataRequiresOperator() = true

    override fun createSpawnPacket(entityTrackerEntry: EntityTrackerEntry) =
        EntitySpawnS2CPacket(this, entityTrackerEntry, thrower?.id ?: 0)

    override fun onSpawnPacket(packet: EntitySpawnS2CPacket) {
        super.onSpawnPacket(packet)
        init(
            packet.x, packet.y, packet.z,
            packet.velocityX, packet.velocityY, packet.velocityZ,
            world.getEntityById(packet.entityData) as? LivingEntity
        )
    }

    override fun getOwner() = thrower

    companion object {
        private const val TIME_KEY = "Time"
        private const val REDIRECTED_BY_IMPACT_TNT_KEY = "EngineeredAttack"

        /**
         * Creates a new [CakeEntity] and throws it.
         *
         * @param world     The world to spawn the cake in
         * @param position  The position to spawn the cake at
         * @param velocity  The speed of the cake
         * @param thrower   The owner of the cake, who takes credit for the cake's damage. If `null`, no sound is played
         */
        @JvmStatic
        fun throwCake(world: World, position: Vec3d, velocity: Vec3d, thrower: LivingEntity?) {
            world.spawnEntity(CakeEntity(world, position, velocity, thrower))
            world.playSound(
                null,
                position.x, position.y, position.z,
                SoundEvents.cakeThrow, thrower?.soundCategory ?: return,
                0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
            )
        }
    }
}
