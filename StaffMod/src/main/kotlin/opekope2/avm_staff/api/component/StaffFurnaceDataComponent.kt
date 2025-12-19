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

package opekope2.avm_staff.api.component

import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

/**
 * Data components to store the state of a furnace staff.
 *
 * @param smeltedItemId         The [network ID][net.minecraft.entity.ItemEntity.getId] of the item entity being smelted
 * @param unsmeltableItemIds    The [network ID][net.minecraft.entity.ItemEntity.getId] of the item entities which
 * cannot be smelted
 * @param smeltTicks            The ticks elapsed since the furnace started to smelt [smeltedItemId]
 */
data class StaffFurnaceDataComponent(val smeltedItemId: Int, val unsmeltableItemIds: IntSet, val smeltTicks: Int) {
    constructor(smeltedItemId: Int, smeltTicks: Int) : this(smeltedItemId, IntOpenHashSet(), smeltTicks)
    constructor() : this(-1, 0)

    companion object {
        /**
         * [PacketCodec] for [StaffFurnaceDataComponent].
         */
        @JvmField
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, StaffFurnaceDataComponent> = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            StaffFurnaceDataComponent::smeltedItemId,
            PacketCodecs.VAR_INT,
            StaffFurnaceDataComponent::smeltTicks,
            ::StaffFurnaceDataComponent
        )
    }
}
