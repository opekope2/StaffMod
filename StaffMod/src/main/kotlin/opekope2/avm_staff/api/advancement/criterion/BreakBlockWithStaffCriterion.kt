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

package opekope2.avm_staff.api.advancement.criterion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LocationPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import opekope2.avm_staff.util.component1
import opekope2.avm_staff.util.component2
import opekope2.avm_staff.util.component3
import java.util.*

/**
 * A criterion, which triggers when a player breaks a block using a staff.
 */
class BreakBlockWithStaffCriterion : AbstractCriterion<BreakBlockWithStaffCriterion.Conditions>() {
    override fun getConditionsCodec() = Conditions.CODEC

    /**
     * Triggers the criterion.
     *
     * @param player    The player triggering the criterion
     * @param world     The world where the player broke the block
     * @param pos       The position of the broken block
     */
    fun trigger(player: ServerPlayerEntity, world: ServerWorld, pos: BlockPos) {
        trigger(player) { conditions -> conditions.test(world, pos) }
    }

    /**
     * Conditions of [BreakBlockWithStaffCriterion].
     *
     * @param player    Predicate matching the player
     * @param location  Predicate matching the location of the broken block
     */
    data class Conditions(val player: Optional<LootContextPredicate>, val location: Optional<LocationPredicate>) :
        AbstractCriterion.Conditions {
        override fun player() = player

        /**
         * Tests the given parameters against the datapack-specified conditions.
         *
         * @param world     The world where the player broke the block
         * @param pos       The position of the broken block
         */
        fun test(world: ServerWorld, pos: BlockPos): Boolean {
            val (x, y, z) = pos.toCenterPos()
            return location.isEmpty || location.get().test(world, x, y, z)
        }

        companion object {
            /**
             * [Codec] for [Conditions].
             */
            @JvmField
            val CODEC: Codec<Conditions> = RecordCodecBuilder.create { instance ->
                instance.group(
                    EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player")
                        .forGetter(AbstractCriterion.Conditions::player),
                    LocationPredicate.CODEC.optionalFieldOf("location").forGetter(Conditions::location)
                ).apply(instance, ::Conditions)
            }
        }
    }
}
