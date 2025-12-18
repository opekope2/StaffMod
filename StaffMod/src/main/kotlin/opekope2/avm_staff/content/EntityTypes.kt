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

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.entity.CakeEntity
import opekope2.avm_staff.api.entity.CampfireFlameEntity
import opekope2.avm_staff.api.entity.ImpactTntEntity
import opekope2.avm_staff.content.EntityTypes.CAKE
import opekope2.avm_staff.content.EntityTypes.CAMPFIRE_FLAME
import opekope2.avm_staff.content.EntityTypes.IMPACT_TNT
import opekope2.avm_staff.mixin.ICakeBlockAccessor
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.Registrar
import opekope2.avm_staff.util.TagKeyUtil
import kotlin.math.max

/**
 * Entity types added by AVM Staffs mod.
 */
object EntityTypes : Registrar<EntityType<*>>(MOD_ID, RegistryKeys.ENTITY_TYPE) {
    /**
     * Entity registered as `avm_staff:cake`
     */
    @JvmField
    val CAKE = register("cake") {
        val cakeBox = ICakeBlockAccessor.bitesToShape()[0].boundingBox
        val cakeSize = max(cakeBox.lengthX, max(cakeBox.lengthY, cakeBox.lengthZ))

        EntityType.Builder.create(::CakeEntity, SpawnGroup.MISC)
            .dimensions(cakeSize.toFloat(), cakeSize.toFloat())
            .maxTrackingRange(EntityType.FALLING_BLOCK.maxTrackDistance)
            .trackingTickInterval(EntityType.FALLING_BLOCK.trackTickInterval)
            .build(Identifier.of(MOD_ID, "cake").toString())
    }

    /**
     * @see CAKE
     */
    val cake: EntityType<CakeEntity>
        @JvmName("cake")
        get() = CAKE.get()

    /**
     * Technical entity registered as `avm_staff:campfire_flame`
     */
    @JvmField
    val CAMPFIRE_FLAME = register("campfire_flame") {
        EntityType.Builder.create(::CampfireFlameEntity, SpawnGroup.MISC)
            .dimensions(0f, 0f)
            .maxTrackingRange(EntityType.AREA_EFFECT_CLOUD.maxTrackDistance)
            // Don't send existing entities (the ones entering tracking distance) to the client
            // The tracking distance is high enough compared to the max age of the flame
            .trackingTickInterval(Int.MAX_VALUE)
            .disableSaving()
            .disableSummon()
            .makeFireImmune()
            .build(Identifier.of(MOD_ID, "campfire_flame").toString())
    }

    /**
     * @see CAMPFIRE_FLAME
     */
    val campfireFlame: EntityType<CampfireFlameEntity>
        @JvmName("campfireFlame")
        get() = CAMPFIRE_FLAME.get()

    /**
     * Entity registered as `avm_staff:impact_tnt`.
     */
    @JvmField
    val IMPACT_TNT = register("impact_tnt") {
        EntityType.Builder.create(::ImpactTntEntity, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(EntityType.TNT.dimensions.width, EntityType.TNT.dimensions.height)
            .eyeHeight(EntityType.TNT.dimensions.eyeHeight)
            .maxTrackingRange(EntityType.TNT.maxTrackDistance)
            .trackingTickInterval(EntityType.TNT.trackTickInterval)
            .build(Identifier.of(MOD_ID, "impact_tnt").toString())
    }

    /**
     * @see IMPACT_TNT
     */
    val impactTnt: EntityType<ImpactTntEntity>
        @JvmName("impactTnt")
        get() = IMPACT_TNT.get()

    /**
     * Entity type tags added by AVM Staffs mod.
     */
    object Tags : TagKeyUtil<EntityType<*>>(MOD_ID, RegistryKeys.ENTITY_TYPE) {
        /**
         * Entity tag registered as `avm_staff:defusable`.
         */
        @JvmField
        val DEFUSABLE = tagKey("defusable")
    }
}
