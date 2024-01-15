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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package opekope2.avm_staff.api.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

/**
 * Configuration loader for a [configuration file][configurationFile] using [codec].
 *
 * @param configurationFile The configuration file path
 * @param codec             The codec used to serialize and deserialize the configuration
 * @see ConfigurationLoader.configurationRoot
 */
class ConfigurationLoader<T>(val configurationFile: Path, val codec: Codec<T>) {
    /**
     * Configuration loader for a [configuration file][configurationFile] using [codec].
     *
     * @param subDirectory  The subdirectory name in the [configuration root][configurationRoot]
     * @param fileName      The file name of th configuration
     * @param codec         The codec used to serialize and deserialize the configuration
     * @see configurationRoot
     */
    constructor(subDirectory: String, fileName: String, codec: Codec<T>) : this(
        configurationRoot.resolve(subDirectory).resolve(fileName),
        codec
    )

    @Throws(IOException::class)
    private fun ensureDirectoryExists() {
        val directory = configurationFile.parent

        if (Files.isRegularFile(directory)) {
            throw IOException("Configuration directory is a file")
        }
        if (!directory.exists()) {
            directory.createDirectories()
        }
    }

    /**
     * Reads the configuration from [configurationFile]. This doesn't handle IO errors (like inaccessible file),
     * malformed JSON, or decoding failures.
     *
     * @return The loaded configuration or `null`, if the file doesn't exist
     */
    @Throws(IOException::class, JsonParseException::class)
    fun loadConfiguration(): T? {
        ensureDirectoryExists()

        if (!configurationFile.exists()) return null
        val root = Files.newBufferedReader(configurationFile).use(JsonParser::parseReader)
        val result = codec.parse(JsonOps.INSTANCE, root)

        return result.get().orThrow()
    }

    /**
     * Writes the given configuration to [configurationFile]. This doesn't handle IO errors (like inaccessible file),
     * or encoding failures.
     *
     * @param configuration The configuration to save
     */
    @Throws(IOException::class)
    fun saveConfiguration(configuration: T) {
        val configEncodeResult = codec.encodeStart(JsonOps.INSTANCE, configuration)
        val root = configEncodeResult.get().orThrow()
        Files.newBufferedWriter(configurationFile).use { writer -> GSON.toJson(root, writer) }
    }

    /**
     * Reads the configuration from [configurationFile] if it exists. If it doesn't exist, it returns the supplied
     * default configuration, and writes it to [configurationFile]. This doesn't handle IO errors (like inaccessible file),
     * malformed JSON, or encoding and decoding failures.
     *
     * @param defaultSupplier   The default configuration supplier
     * @return The configuration from the file, or the [default configuration][defaultSupplier], if the file doesn't exist
     */
    @Throws(IOException::class, JsonParseException::class)
    fun loadOrSaveConfiguration(defaultSupplier: () -> T): T {
        val loadedConfiguration = loadConfiguration()
        if (loadedConfiguration != null) return loadedConfiguration

        val newConfiguration = defaultSupplier()
        saveConfiguration(newConfiguration)
        return newConfiguration
    }

    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().create()

        /**
         * Returns the root configuration directory.
         *
         * @see FabricLoader.getConfigDir
         */
        @JvmStatic
        val configurationRoot: Path = FabricLoader.getInstance().configDir
    }
}
