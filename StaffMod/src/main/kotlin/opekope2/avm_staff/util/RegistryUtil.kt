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

import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.MOD_BUS

/**
 * Utility class to register content to Minecraft registries.
 *
 * @param TContent  The type of the content to register
 */
abstract class RegistryUtil<TContent> : RegistryKeyUtil<TContent> {
    /**
     * Creates a new [RegistryUtil] instance.
     *
     * @param TContent  The type of the content to register
     * @param modId     The [namespace][Identifier.namespace] of the content to register.
     * @param registry  The registry to register the content in
     */
    protected constructor(modId: String, registry: RegistryKey<Registry<TContent>>) : super(modId, registry) {
        this.deferredRegister = DeferredRegister.create(registry, modId)
    }

    /**
     * Creates a new [RegistryUtil] instance.
     *
     * @param TContent  The type of the content to register
     * @param modId     The [namespace][Identifier.namespace] of the content to register.
     * @param registry  The registry to register the content in
     */
    protected constructor(modId: String, registry: IForgeRegistry<TContent>) : super(modId, registry.registryKey) {
        this.deferredRegister = DeferredRegister.create(registry, modId)
    }

    private val deferredRegister: DeferredRegister<TContent>

    /**
     * Adds a content to be registered in a Minecraft registry using Architectury API.
     *
     * @param path      The [path][Identifier.path] of the identifier of the content to register
     * @param factory   The function creating
     */
    protected fun <T : TContent> register(path: String, factory: (RegistryKey<TContent>) -> T): RegistryObject<T> =
        deferredRegister.register(path) { factory(registryKey(path)) }

    /**
     * @suppress
     */
    @JvmSynthetic
    internal open fun register() = deferredRegister.register(MOD_BUS)
}
