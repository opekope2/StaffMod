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

package opekope2.avm_staff.util

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import java.util.function.Supplier
import kotlin.properties.PropertyDelegateProvider

/**
 * Utility class to register content to Minecraft registries.
 *
 * @param TContent  The type of the content to register
 * @param modId     The [namespace][Identifier.namespace] of the content to register.
 * @param registry  The registry to register the content in
 */
abstract class Registrar<TContent>(modId: String, registry: RegistryKey<Registry<TContent>>) :
    RegistryKeyUtil<TContent>(modId, registry) {
    private val deferredRegister = DeferredRegister.create(modId, registry)

    /**
     * Adds a content to be registered in a Minecraft registry using Architectury API.
     *
     * @param path      The [path][Identifier.path] of the identifier of the content to register
     * @param factory   The function creating the object to be registered
     */
    protected fun <T : TContent> register(path: String, factory: Supplier<T>): RegistrySupplier<T> =
        deferredRegister.register(path, factory)

    /**
     * Adds a content to be registered in a Minecraft registry using Architectury API.
     *
     * @param path      The [path][Identifier.path] of the identifier of the content to register
     * @param factory   The function creating the object to be registered
     */
    protected inline fun <T : TContent> register(path: String, crossinline factory: (RegistryKey<TContent>) -> T) =
        register(path) { -> factory(registryKey(path)) }

    /**
     * Adds a content to be registered in a Minecraft registry using Architectury API.
     *
     * @param path      The [path][Identifier.path] of the identifier of the content to register
     * @param factory   The function creating the object to be registered
     */
    protected inline fun <T : TContent> registering(
        path: String,
        crossinline factory: (RegistryKey<TContent>) -> T
    ): Lazy<T> = register(path, factory).asLazy()

    /**
     * Adds a content to be registered in a Minecraft registry using Architectury API.
     * The [path][Identifier.getPath] is derived from the property name (converted to camel_case).
     *
     * @param factory   The function creating the object to be registered
     */
    protected inline fun <T : TContent> registering(crossinline factory: (RegistryKey<TContent>) -> T) =
        PropertyDelegateProvider<Registrar<TContent>, Lazy<T>> { _, property ->
            registering(toSnakeCase(property.name), factory)
        }

    /**
     * @suppress
     */
    internal open fun register() = deferredRegister.register()
}
