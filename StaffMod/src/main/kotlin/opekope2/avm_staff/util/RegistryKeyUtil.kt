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

package opekope2.avm_staff.util

import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import kotlin.jvm.optionals.getOrNull

/**
 * Utility class to create [Identifier]s and [RegistryKey]s using a specified [namespace][Identifier.namespace] and
 * registry.
 *
 * @param TContent  The type of the content to register
 * @param modId     The [namespace][Identifier.namespace] of the content to register.
 * @param registry  The registry to register the content in
 */
open class RegistryKeyUtil<TContent>(
    protected val modId: String,
    protected val registry: RegistryKey<Registry<TContent>>
) {
    init {
        require(Identifier.isNamespaceValid(modId)) { "Mod ID is not a valid namespace" }
    }

    /**
     * Creates an [Identifier] from the namespace specified in the constructor and a given path.
     *
     * @param path  The path of the [Identifier] to create
     */
    fun id(path: String): Identifier = Identifier.of(modId, path)

    /**
     * Creates a [RegistryKey] from the registry and namespace specified in the constructor and a given path.
     *
     * @param path  The path of the [Identifier] to create a registry key from
     */
    fun registryKey(path: String): RegistryKey<TContent> = RegistryKey.of(registry, id(path))

    /**
     * Gets the registry entry of the given registry key from the given registry manager or `null`, if it's not found.
     *
     * @param registryManager   The registry manager of a world
     */
    fun RegistryKey<TContent>.getEntry(registryManager: DynamicRegistryManager) =
        registryManager.get(this@RegistryKeyUtil.registry).getEntry(this).getOrNull()
}
