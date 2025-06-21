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

import dev.architectury.extensions.network.EntitySpawnExtension
import dev.architectury.networking.NetworkManager
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.*
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.client.option.GraphicsMode
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.network.EntityTrackerEntry
import net.minecraft.state.property.Properties.LIT
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.content.ParticleTypes
import opekope2.avm_staff.util.*
import java.util.*

/**
 * Technical entity representing a part of a flame of a campfire staff.
 */
class CampfireFlameEntity : Entity, EntitySpawnExtension {
    private var currentRelativeRight: Vec3d = Vec3d.ZERO
    private var currentRelativeUp: Vec3d = Vec3d.ZERO

    private lateinit var parameters: Parameters

    private lateinit var shooter: LivingEntity

    private var rays: BitSet
    private inline val rayResolution: Int
        get() = (parameters as? ServerParameters)?.rayResolution ?: flameParticleRayResolution

    @Deprecated("This constructor is not supported on the server")
    internal constructor(type: EntityType<*>, world: World) : super(type, world) {
        require(world.isClient) { "This constructor is not supported on the server" }
        // Leave edges unset for accurate visuals
        rays = BitSet(flameParticleRayResolution * flameParticleRayResolution).apply {
            for (i in 1 until flameParticleRayResolution - 1) {
                for (j in 1 until flameParticleRayResolution - 1) {
                    this.set(i * flameParticleRayResolution + j)
                }
            }
        }
    }

    /**
     * Creates a new [CampfireFlameEntity].
     *
     * @param world         The world to create the flame entity in
     * @param parameters    Parameters for the flame
     * @param shooter       The entity shooting the flame
     */
    constructor(world: World, parameters: ServerParameters, shooter: LivingEntity) :
            super(EntityTypes.campfireFlame, world) {
        this.setPosition(parameters.origin)
        this.velocity = shooter.velocity + parameters.relativeTarget * (1.0 / parameters.stepResolution)

        this.parameters = parameters

        this.shooter = shooter

        val rayCount = rayResolution * rayResolution
        this.rays = BitSet(rayCount).apply { set(0, rayCount) }
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound?) {
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound?) {
    }

    override fun tick() {
        super.tick()

        val nextPos = pos + velocity
        val nextRelativeRight = parameters.flameConeWidth * (age.toDouble() / parameters.stepResolution)
        val nextRelativeUp = parameters.flameConeHeight * (age.toDouble() / parameters.stepResolution)

        if (world.isClient) {
            @Suppress("UNCHECKED_CAST")
            val particleType = Registries.PARTICLE_TYPE[parameters.particleType as RegistryKey<ParticleType<*>>]
            val particleEffect = particleType as? ParticleEffect ?: ParticleTypes.flame

            tickRays(nextPos, nextRelativeRight, nextRelativeUp) { start, end ->
                val result = tickRayClient(start, end)
                if (!result.stopsRay) spawnParticle(particleEffect, start, end)
                result
            }
        } else {
            val parameters = parameters as ServerParameters
            val entitiesToBurn = mutableSetOf<Entity>()
            val blocksToLight = mutableSetOf<BlockPos>()
            val blocksToSetOnFire = mutableSetOf<BlockPos>()

            tickRays(nextPos, nextRelativeRight, nextRelativeUp) { start, end ->
                tickRayServer(start, end, parameters, entitiesToBurn, blocksToLight, blocksToSetOnFire)
            }

            burnEntities(entitiesToBurn, parameters.flameFireTicks)
            lightBlocks(blocksToLight)
            setBlocksOnFire(blocksToSetOnFire)
        }

        currentRelativeRight = nextRelativeRight
        currentRelativeUp = nextRelativeUp
        setPosition(nextPos)
        lookForward()

        if (!world.isClient && age >= parameters.stepResolution) discard()
    }

    override fun onSwimmingStart() {
    }

    @Environment(EnvType.CLIENT)
    private fun spawnParticle(particleEffect: ParticleEffect, start: Vec3d, end: Vec3d) {
        val particleOffset = (end - start) * random.nextDouble() +
                currentRelativeRight * ((random.nextDouble() * 2 - 1) / rayResolution) +
                currentRelativeUp * ((random.nextDouble() * 2 - 1) / rayResolution)
        val (x, y, z) = start + particleOffset
        particleManager.addParticle(particleEffect, x, y, z, 0.0, 0.0, 0.0)!!.apply {
            scale(random.nextFloat() - random.nextFloat() + 1f)
            maxAge = (0.25 * FLAME_MAX_AGE / (Math.random() * 0.8 + 0.2) - 0.05 * FLAME_MAX_AGE).toInt()
        }
    }

    private inline fun tickRays(
        nextPos: Vec3d,
        nextRelativeRight: Vec3d,
        nextRelativeUp: Vec3d,
        tickRay: (start: Vec3d, end: Vec3d) -> HitResult.Type
    ) {
        for (offsetX in 0 until rayResolution) {
            for (offsetY in 0 until rayResolution) {
                val index = offsetX * rayResolution + offsetY
                if (!rays[index]) continue

                val scaleX = offsetX / (rayResolution - 1.0) - 0.5
                val scaleY = offsetY / (rayResolution - 1.0) - 0.5
                val start = pos + currentRelativeRight * scaleX + currentRelativeUp * scaleY
                val end = nextPos + nextRelativeRight * scaleX + nextRelativeUp * scaleY

                rays[index] = !tickRay(start, end).stopsRay
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private fun tickRayClient(start: Vec3d, end: Vec3d): HitResult.Type {
        val blockHit = raycastBlock(start, end)
        val entityHit = raycastEntity(start, end, false)

        return when {
            entityHit != null && entityHit.pos.squaredDistanceTo(start) < blockHit.pos.squaredDistanceTo(start) -> HitResult.Type.ENTITY
            blockHit.type == HitResult.Type.BLOCK -> HitResult.Type.BLOCK
            else -> HitResult.Type.MISS
        }
    }

    private fun tickRayServer(
        start: Vec3d,
        end: Vec3d,
        parameters: ServerParameters,
        entitiesToBurn: MutableSet<Entity>,
        blocksToLight: MutableSet<BlockPos>,
        blocksToSetOnFire: MutableSet<BlockPos>
    ): HitResult.Type {
        val blockHit = raycastBlock(start, end)
        val entityHit = raycastEntity(start, end, parameters.damageShooter)

        return when {
            entityHit != null && entityHit.pos.squaredDistanceTo(start) < blockHit.pos.squaredDistanceTo(start) -> {
                entitiesToBurn += entityHit.entity
                HitResult.Type.ENTITY
            }

            blockHit.type == HitResult.Type.BLOCK -> {
                collectBlockToLight(
                    blockHit,
                    blocksToLight,
                    blocksToSetOnFire,
                    parameters.flammableBlockFireChance,
                    parameters.nonFlammableBlockFireChance
                )
                HitResult.Type.BLOCK
            }

            else -> HitResult.Type.MISS
        }
    }

    private fun raycastBlock(start: Vec3d, end: Vec3d) = world.raycast(
        RaycastContext(
            start,
            end,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.ANY,
            ShapeContext.absent()
        )
    )

    private fun raycastEntity(start: Vec3d, end: Vec3d, includeShooter: Boolean) = ProjectileUtil.raycast(
        this,
        start,
        end,
        Box(start, end),
        { it != shooter || includeShooter },
        velocity.lengthSquared()
    )

    private fun collectBlockToLight(
        blockHit: BlockHitResult,
        blocksToLight: MutableSet<BlockPos>,
        blocksToSetOnFire: MutableSet<BlockPos>,
        flammableBlockFireChance: Double,
        nonFlammableBlockFireChance: Double
    ) {
        val firePos = blockHit.blockPos.offset(blockHit.side)
        val blockToLight = world.getBlockState(blockHit.blockPos)
        if (CampfireBlock.canBeLit(blockToLight) ||
            CandleBlock.canBeLit(blockToLight) ||
            CandleCakeBlock.canBeLit(blockToLight)
        ) {
            blocksToLight += blockHit.blockPos
            return
        }

        if (!world.canSetBlock(firePos)) return
        if (!AbstractFireBlock.canPlaceAt(world, firePos, horizontalFacing)) return

        var fireCauseChance =
            if (world.getBlockState(blockHit.blockPos).isBurnable) flammableBlockFireChance
            else nonFlammableBlockFireChance
        fireCauseChance /= rayResolution * rayResolution

        if (Math.random() < fireCauseChance) {
            blocksToSetOnFire += firePos
        }
    }

    private fun burnEntities(entities: Set<Entity>, flameFireTicks: Int) {
        for (target in entities) {
            if (target.isOnFire) {
                // Technically inFire, but use onFire, because it has a more fitting death message
                target.damage(target.damageSources.onFire(), flameFireTicks.toFloat())
            }
            target.fireTicks = target.fireTicks.coerceAtLeast(0) + flameFireTicks + 1
            (target as? LivingEntity)?.attacker = shooter
        }
    }

    private fun lightBlocks(blocksToLight: MutableSet<BlockPos>) {
        for (pos in blocksToLight) {
            world.setBlockState(pos, world.getBlockState(pos).with(LIT, true), Block.NOTIFY_ALL_AND_REDRAW)
            world.emitGameEvent(this, GameEvent.BLOCK_CHANGE, pos)
        }
    }

    private fun setBlocksOnFire(blocksToSetOnFire: MutableSet<BlockPos>) {
        for (pos in blocksToSetOnFire) {
            world.setBlockState(pos, AbstractFireBlock.getState(world, pos), Block.NOTIFY_ALL_AND_REDRAW)
            world.emitGameEvent(this, GameEvent.BLOCK_PLACE, pos)
        }
    }

    override fun canUsePortals(allowVehicles: Boolean) = false

    override fun getPistonBehavior() = PistonBehavior.IGNORE

    override fun createSpawnPacket(entityTrackerEntry: EntityTrackerEntry): Packet<ClientPlayPacketListener> =
        NetworkManager.createAddEntityPacket(this, entityTrackerEntry)

    override fun saveAdditionalSpawnData(buf: PacketByteBuf) {
        parameters.write(buf)
        buf.writeVarInt(shooter.id)
    }

    override fun loadAdditionalSpawnData(buf: PacketByteBuf) {
        parameters = Parameters(buf)
        shooter = world.getEntityById(buf.readVarInt()) as LivingEntity
    }

    /**
     * Parameters of a [CampfireFlameEntity].
     *
     * @param origin                        Starting position of the entity
     * @param relativeTarget                The position towards the fire goes relative to [origin]
     * @param flameConeWidth                The width of the fire cone, points right relative to the shooter's POV
     * @param flameConeHeight               The height of the fire cone, points up relative to the shooter's POV
     * @param stepResolution                How many ticks to divide the distance between [origin] and [relativeTarget]
     * @param particleType                  The registry key of the flame particle type in [Registries.PARTICLE_TYPE]
     */
    open class Parameters(
        val origin: Vec3d,
        val relativeTarget: Vec3d,
        val flameConeWidth: Vec3d,
        val flameConeHeight: Vec3d,
        val stepResolution: Int,
        val particleType: RegistryKey<out ParticleType<*>>,
    ) {
        constructor(buf: PacketByteBuf) : this(
            buf.readVec3d(),
            buf.readVec3d(),
            buf.readVec3d(),
            buf.readVec3d(),
            buf.readVarInt(),
            buf.readRegistryKey(RegistryKeys.PARTICLE_TYPE)
        )

        fun write(buf: PacketByteBuf) {
            buf.writeVec3d(origin)
            buf.writeVec3d(relativeTarget)
            buf.writeVec3d(flameConeWidth)
            buf.writeVec3d(flameConeHeight)
            buf.writeVarInt(stepResolution)
            buf.writeRegistryKey(particleType)
        }
    }

    /**
     * Server-side parameters of a [CampfireFlameEntity].
     *
     * @param origin                        Starting position of the entity
     * @param relativeTarget                The position towards the fire goes relative to [origin]
     * @param flameConeWidth                The width of the fire cone, points right relative to the shooter's POV
     * @param flameConeHeight               The height of the fire cone, points up relative to the shooter's POV
     * @param stepResolution                How many ticks to divide the distance between [origin] and [relativeTarget]
     * @param rayResolution                 The resolution to divide the fire cone both horizontally and vertically
     * @param particleType                  The registry key of the flame particle type in [Registries.PARTICLE_TYPE]
     * @param flammableBlockFireChance      The chance a [flammable][BlockState.isBurnable] block is set on fire
     * @param nonFlammableBlockFireChance   The chance a [non-flammable][BlockState.isBurnable] block is set on fire
     * @param flameFireTicks                The number of ticks an entity is additionally set on fire for
     * @param damageShooter                 Whether the flame should damage the shooter
     */
    class ServerParameters(
        origin: Vec3d,
        relativeTarget: Vec3d,
        flameConeWidth: Vec3d,
        flameConeHeight: Vec3d,
        stepResolution: Int,
        particleType: RegistryKey<out ParticleType<*>>,
        val rayResolution: Int,
        val flammableBlockFireChance: Double,
        val nonFlammableBlockFireChance: Double,
        val flameFireTicks: Int,
        val damageShooter: Boolean
    ) : Parameters(origin, relativeTarget, flameConeWidth, flameConeHeight, stepResolution, particleType)

    private companion object {
        private const val FLAME_MAX_AGE = 16

        private val HitResult.Type.stopsRay: Boolean
            get() = this == HitResult.Type.BLOCK

        private val flameParticleRayResolution: Int
            @Environment(EnvType.CLIENT)
            get() = when (clientOptions.graphicsMode.value) {
                GraphicsMode.FABULOUS -> 6
                GraphicsMode.FANCY -> 5
                else -> 4
            }
    }
}
