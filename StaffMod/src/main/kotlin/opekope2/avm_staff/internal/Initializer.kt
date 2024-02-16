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

package opekope2.avm_staff.internal

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.item.ClampedModelPredicateProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import opekope2.avm_staff.IStaffMod
import opekope2.avm_staff.internal.event_handler.addBlockToStaff
import opekope2.avm_staff.internal.event_handler.attack
import opekope2.avm_staff.internal.event_handler.removeBlockFromStaff
import opekope2.avm_staff.internal.networking.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.networking.c2s.play.StaffAttackC2SPacket
import opekope2.avm_staff.util.MOD_ID

fun initializeNetworking() {
    AddItemToStaffC2SPacket.registerHandler(::addBlockToStaff)
    RemoveItemFromStaffC2SPacket.registerHandler(::removeBlockFromStaff)
    StaffAttackC2SPacket.registerHandler(::attack)
}

val USING_ITEM_PREDICATE = Identifier(MOD_ID, "using_item")

@Suppress("UNUSED_PARAMETER")
@Environment(EnvType.CLIENT)
private fun usingItemPredicate(stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int): Float {
    return if (entity != null && entity.isUsingItem) 1f else 0f
}

// ModelPredicateProviderRegistry.register is private, so pass it from Fabric and Forge projects
@Environment(EnvType.CLIENT)
fun registerModelPredicateProviders(register: (Item, Identifier, ClampedModelPredicateProvider) -> Unit) {
    register(
        IStaffMod.get().staffItem,
        USING_ITEM_PREDICATE,
        ::usingItemPredicate
    )
}
