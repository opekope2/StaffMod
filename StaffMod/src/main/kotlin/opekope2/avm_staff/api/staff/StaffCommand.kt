/*
 * AvM Staff Mod
 * Copyright (c) 2025-2026 opekope2
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

package opekope2.avm_staff.api.staff

import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.entry.RegistryFixedCodec
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.staff.StaffCommand.Companion.ENTRY_CODEC
import opekope2.avm_staff.api.staff.StaffCommand.Type.Companion.REGISTRY
import opekope2.avm_staff.content.DataComponentTypes
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.util.MOD_ID

/**
 * Base class for staff commands.
 */
abstract class StaffCommand<TArgs : StaffCommand.IArgs> : IItemHandler {
    /**
     * The type of this command registered in [Type.REGISTRY].
     */
    abstract val type: Type<*>

    /**
     * Map codec for the arguments of this command.
     */
    abstract val argsCodec: MapCodec<TArgs>

    /**
     * Gets the arguments stored in a staff.
     *
     * @param staffStack    The [ItemStack] representing the staff
     */
    protected fun getArgs(staffStack: ItemStack) =
        requireNotNull(staffStack[DataComponentTypes.staffCommand<TArgs>()]) {
            I18n.ERROR_AVM_STAFF_MISSING_COMPONENT.getText(DataComponentTypes.staffCommand, staffStack)
        }.args

    /**
     * Base interface for staff command arguments serialized by [argsCodec].
     */
    interface IArgs

    /**
     * The type of staff command.
     *
     * @param codec The map codec used to load a staff command from a data pack
     */
    data class Type<T : StaffCommand<*>>(val codec: MapCodec<T>) {
        companion object {
            /**
             * The registry key of [REGISTRY].
             */
            @JvmField
            val REGISTRY_KEY: RegistryKey<Registry<Type<*>>> =
                RegistryKey.ofRegistry(Identifier.of(MOD_ID, "staff_command_type"))

            /**
             * Registry of staff commands.
             */
            @JvmField
            val REGISTRY: Registry<Type<*>> = SimpleRegistry(REGISTRY_KEY, Lifecycle.stable())

            /**
             * Codec for [Type].
             */
            @JvmField
            val CODEC: Codec<Type<*>> = REGISTRY.codec
        }
    }

    companion object {
        /**
         * The registry key of the staff commands dynamic registry.
         */
        @JvmField
        val REGISTRY_KEY: RegistryKey<Registry<StaffCommand<*>>> =
            RegistryKey.ofRegistry(Identifier.of(MOD_ID, "staff_command"))

        /**
         * Codec for [StaffCommand].
         */
        @JvmField
        val CODEC: Codec<StaffCommand<*>> = Type.CODEC.dispatch("command", StaffCommand<*>::type, Type<*>::codec)

        /**
         * Registry entry codec for [StaffCommand].
         */
        @JvmField
        val ENTRY_CODEC: Codec<RegistryEntry<StaffCommand<*>>> = RegistryFixedCodec.of(REGISTRY_KEY)

        /**
         * Typed [ENTRY_CODEC].
         */
        @JvmStatic
        fun <T : IArgs> entryCodec() = ENTRY_CODEC as Codec<RegistryEntry<StaffCommand<T>>>
    }
}
