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

import net.minecraft.component.ComponentType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import opekope2.avm_staff.api.component.*
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.Registrar
import kotlin.properties.PropertyDelegateProvider

/**
 * Component types added by AVM Staffs mod.
 */
object DataComponentTypes : Registrar<ComponentType<*>>(MOD_ID, RegistryKeys.DATA_COMPONENT_TYPE) {
    @JvmStatic
    private inline fun <T> registeringComponentType(crossinline factory: ComponentType.Builder<T>.(RegistryKey<ComponentType<*>>) -> ComponentType.Builder<T>) =
        PropertyDelegateProvider<Registrar<ComponentType<*>>, Lazy<ComponentType<T>>> { _, property ->
            registering(toSnakeCase(property.name)) { factory(ComponentType.builder(), it).build() }
        }

    /**
     * Bell data component type.
     * Stores when to apply the glowing effect.
     */
    @JvmStatic
    val bellData by registeringComponentType { packetCodec(StaffBellDataComponent.PACKET_CODEC) }

    /**
     * Block pickup data component type.
     * Stores info about to block to be picked up from the world.
     */
    @JvmStatic
    val blockPickupData by registeringComponentType { packetCodec(BlockPickupDataComponent.PACKET_CODEC) }

    /**
     * Furnace data component type.
     * If present, the furnace is rendered lit.
     */
    @JvmStatic
    val furnaceData by registeringComponentType { packetCodec(StaffFurnaceDataComponent.PACKET_CODEC) }

    /**
     * Staff item component type.
     * Stores the item inserted into the staff.
     */
    @JvmStatic
    val staffItem by registeringComponentType { codec(StaffItemComponent.VALIDATED_CODEC).packetCodec(StaffItemComponent.PACKET_CODEC) }

    /**
     * TNT data component type.
     * Stores the impact TNT to be detonated remotely.
     */
    @JvmStatic
    val tntData by registeringComponentType { packetCodec(StaffTntDataComponent.PACKET_CODEC) }
}
