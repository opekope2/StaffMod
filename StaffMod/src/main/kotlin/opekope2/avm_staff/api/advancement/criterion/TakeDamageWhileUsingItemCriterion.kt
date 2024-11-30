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
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion
import net.minecraft.advancement.criterion.UsingItemCriterion
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.DamageSourcePredicate
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

/**
 * A fusion criterion between [UsingItemCriterion] and [EntityHurtPlayerCriterion].
 */
class TakeDamageWhileUsingItemCriterion : AbstractCriterion<TakeDamageWhileUsingItemCriterion.Conditions>() {
    override fun getConditionsCodec() = Conditions.CODEC

    /**
     * Triggers the criterion.
     *
     * @param player        The player triggering the criterion
     * @param stack         The item stack the player is using
     * @param damageSource  The damage the player took
     */
    fun trigger(player: ServerPlayerEntity, stack: ItemStack, damageSource: DamageSource) {
        trigger(player) { conditions -> conditions.test(player, stack, damageSource) }
    }

    /**
     * Conditions of [TakeDamageWhileUsingItemCriterion].
     *
     * @param player        Predicate matching the player
     * @param item          Predicate matching the item the player is using
     * @param damageType    Predicate matching the damage the player took
     */
    data class Conditions(
        val player: Optional<LootContextPredicate>,
        val item: Optional<ItemPredicate>,
        val damageType: Optional<DamageSourcePredicate>
    ) : AbstractCriterion.Conditions {
        override fun player() = player

        /**
         * Tests the given parameters against the datapack-specified conditions.
         *
         * @param player        The player triggering the criterion
         * @param stack         The item stack the player is using
         * @param damageSource  The damage the player took
         */
        fun test(player: ServerPlayerEntity, stack: ItemStack, damageSource: DamageSource): Boolean {
            if (item.isPresent && !item.get().test(stack)) return false
            if (damageType.isPresent && !damageType.get().test(player, damageSource)) return false

            return true
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
                    ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item),
                    DamageSourcePredicate.CODEC.optionalFieldOf("damage_type").forGetter(Conditions::damageType)
                ).apply(instance, ::Conditions)
            }
        }
    }
}
