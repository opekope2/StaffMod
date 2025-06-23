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
import net.minecraft.network.codec.PacketCodec
import net.minecraft.registry.RegistryKeys
import opekope2.avm_staff.api.component.BlockPickupDataComponent
import opekope2.avm_staff.api.component.StaffFurnaceDataComponent
import opekope2.avm_staff.api.component.StaffItemComponent
import opekope2.avm_staff.api.component.StaffTntDataComponent
import opekope2.avm_staff.internal.MinecraftUnit
import opekope2.avm_staff.internal.minecraftUnit
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryUtil

/**
 * Component types added by AVM Staffs mod.
 */
object DataComponentTypes : RegistryUtil<ComponentType<*>>(MOD_ID, RegistryKeys.DATA_COMPONENT_TYPE) {
    /**
     * Data component registered as `avm_staff:block_pickup_data`.
     */
    @JvmField
    val BLOCK_PICKUP_DATA = register("block_pickup_data") {
        ComponentType.builder<BlockPickupDataComponent>()
            .packetCodec(BlockPickupDataComponent.NON_SYNCING_PACKET_CODEC)
            .build()
    }

    /**
     * @see BLOCK_PICKUP_DATA
     */
    val blockPickupData: ComponentType<BlockPickupDataComponent>
        @JvmName("blockPickupData")
        get() = BLOCK_PICKUP_DATA.get()

    /**
     * Data component registered as `avm_staff:furnace_data`. If this is present, the furnace is lit.
     */
    @JvmField
    val FURNACE_DATA = register("furnace_data") {
        ComponentType.builder<StaffFurnaceDataComponent>()
            .packetCodec(StaffFurnaceDataComponent.NON_SYNCING_PACKET_CODEC)
            .build()
    }

    /**
     * @see FURNACE_DATA
     */
    val furnaceData: ComponentType<StaffFurnaceDataComponent>
        @JvmName("furnaceData")
        get() = FURNACE_DATA.get()

    /**
     * Data component registered as `avm_staff:rocket_mode`. Stores if a campfire staff should propel its user.
     */
    @JvmField
    val ROCKET_MODE = register("rocket_mode") {
        ComponentType.builder<MinecraftUnit>()
            .codec(MinecraftUnit.CODEC)
            .packetCodec(PacketCodec.unit(minecraftUnit))
            .build()
    }

    /**
     * @see ROCKET_MODE
     */
    val rocketMode: ComponentType<MinecraftUnit>
        @JvmName("rocketMode")
        get() = ROCKET_MODE.get()

    /**
     * Data component registered as `avm_staff:staff_item`. Stores the item inserted into the staff.
     */
    @JvmField
    val STAFF_ITEM = register("staff_item") {
        ComponentType.builder<StaffItemComponent>()
            .codec(StaffItemComponent.VALIDATED_CODEC)
            .packetCodec(StaffItemComponent.PACKET_CODEC)
            .build()
    }

    /**
     * @see STAFF_ITEM
     */
    val staffItem: ComponentType<StaffItemComponent>
        @JvmName("staffItem")
        get() = STAFF_ITEM.get()

    /**
     * Data component registered as `avm_staff:tnt_data`.
     */
    @JvmField
    val TNT_DATA = register("tnt_data") {
        ComponentType.builder<StaffTntDataComponent>()
            .packetCodec(StaffTntDataComponent.NON_SYNCING_PACKET_CODEC)
            .build()
    }

    /**
     * @see TNT_DATA
     */
    val tntData: ComponentType<StaffTntDataComponent>
        @JvmName("tntData")
        get() = TNT_DATA.get()
}
