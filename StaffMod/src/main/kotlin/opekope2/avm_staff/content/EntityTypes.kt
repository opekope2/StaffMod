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

package opekope2.avm_staff.content

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.entity.CampfireFlameEntity
import opekope2.avm_staff.api.entity.ImpactTntEntity
import opekope2.avm_staff.mixin.ICakeBlockAccessor
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.Registrar
import opekope2.avm_staff.util.RegistryKeyUtil
import kotlin.math.max
import kotlin.properties.PropertyDelegateProvider

/**
 * Entity types added by AVM Staffs mod.
 */
object EntityTypes : Registrar<EntityType<*>>(MOD_ID, RegistryKeys.ENTITY_TYPE) {
    @JvmStatic
    private inline fun <T : Entity> registeringEntityType(
        constructor: EntityType.EntityFactory<T>,
        spawnGroup: SpawnGroup,
        crossinline factory: EntityType.Builder<T>.(RegistryKey<EntityType<*>>) -> EntityType.Builder<T>
    ) =
        PropertyDelegateProvider<Registrar<EntityType<*>>, Lazy<EntityType<T>>> { _, property ->
            registering(toSnakeCase(property.name)) { key ->
                factory(EntityType.Builder.create(constructor, spawnGroup), key).build(key.value.toString())
            }
        }

    /**
     * Cake entity type.
     */
    @JvmStatic
    val cake by registeringEntityType(::CakeEntity, SpawnGroup.MISC) {
        val cakeBox = ICakeBlockAccessor.bitesToShape()[0].boundingBox
        val cakeSize = max(cakeBox.lengthX, max(cakeBox.lengthY, cakeBox.lengthZ)).toFloat()

        this
            .dimensions(cakeSize, cakeSize)
            .maxTrackingRange(EntityType.FALLING_BLOCK.maxTrackDistance)
            .trackingTickInterval(EntityType.FALLING_BLOCK.trackTickInterval)
    }

    /**
     * Technical campfire flame entity type.
     */
    @JvmStatic
    val campfireFlame by registeringEntityType(::CampfireFlameEntity, SpawnGroup.MISC) {
        this
            .dimensions(0f, 0f)
            .disableSaving()
            .disableSummon()
            .makeFireImmune()
            .maxTrackingRange(EntityType.AREA_EFFECT_CLOUD.maxTrackDistance)
            // Don't send existing entities (the ones entering tracking distance) to the client
            // The tracking distance is high enough compared to the max age of the flame
            .trackingTickInterval(Int.MAX_VALUE)
    }

    /**
     * Impact TNT entity type.
     */
    @JvmStatic
    val impactTnt by registeringEntityType(::ImpactTntEntity, SpawnGroup.MISC) {
        this
            .dimensions(EntityType.TNT.dimensions.width, EntityType.TNT.dimensions.height)
            .eyeHeight(EntityType.TNT.dimensions.eyeHeight)
            .makeFireImmune()
            .maxTrackingRange(EntityType.TNT.maxTrackDistance)
            .trackingTickInterval(EntityType.TNT.trackTickInterval)
    }

    /**
     * Entity type tags added by AVM Staffs mod.
     */
    object Tags : RegistryKeyUtil<EntityType<*>>(MOD_ID, RegistryKeys.ENTITY_TYPE) {
        /**
         * Entities that can be defused by an emerald block staff.
         */
        @JvmStatic
        val defusable by tagKey
    }
}
