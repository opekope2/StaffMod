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

package opekope2.avm_staff.api.registry

/**
 * AvM Staff mod base registration utility.
 * This is not to be confused with Minecraft registries.
 */
abstract class RegistryBase<TKey, TValue> : Iterable<Map.Entry<TKey, TValue>> {
    private val entries = mutableMapOf<TKey, TValue>()

    /**
     * Gets all the registered keys in this registry.
     */
    val keys: Set<TKey>
        get() = entries.keys

    /**
     * Validates an entry to be registered. Throws an exception, if the entry is invalid.
     *
     * Implementors must call the super method to check if the key is not already registered.
     */
    open fun validateEntry(key: TKey, value: TValue) {
        require(key !in entries) { "Key `$key` is already registered" }
    }

    /**
     * Registers an entry to this registry.
     *
     * @param key The key to associate a value with
     * @param value The value to register
     */
    open fun register(key: TKey, value: TValue) {
        validateEntry(key, value)
        entries[key] = value
    }

    /**
     * Checks if the given key is present in the registry
     *
     * @param key The key to check
     */
    operator fun contains(key: TKey) = key in entries

    /**
     * Gets the value associated with the given key or throws an exception, if the key is not present in this registry.
     *
     * @param key The key to check
     */
    fun getValue(key: TKey) = entries.getValue(key)

    override fun iterator(): Iterator<Map.Entry<TKey, TValue>> = entries.iterator()
}
