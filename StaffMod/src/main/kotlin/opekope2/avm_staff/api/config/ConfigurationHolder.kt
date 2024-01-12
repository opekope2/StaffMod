// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.api.config

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.slf4j.LoggerFactory

/**
 * Holds the current configurations.
 */
object ConfigurationHolder {
    /**
     * The error occurred during the ast [configuration load][tryLoadLocalConfiguration]. Will be `null` if it was
     * successful.
     */
    @JvmStatic
    var localConfigurationLoadException: Exception? = null
        private set

    /**
     * The currently loaded local configuration.
     */
    @JvmStatic
    var localConfiguration: Configuration = Configuration.createDefault()

    /**
     * [remoteConfiguration] if the client is currently connected to a server, [localConfiguration] otherwise.
     */
    @JvmStatic
    val currentConfiguration: Configuration
        get() = remoteConfiguration /* field */ ?: localConfiguration

    /**
     * The configuration of remote server the client is currently connected to, or `null`, if the client is not connected
     * to a server. It is the configuration of the integrated server, if running.
     */
    @JvmStatic
    @get: Environment(EnvType.CLIENT)
    @set: Environment(EnvType.CLIENT)
    var remoteConfiguration: Configuration? = null // Field not removed on client, because it's not annotated

    /**
     * Tries to load the local configuration from disk into [localConfiguration].
     * If the load fails, [localConfiguration] won't be changed, and the error can be found in [localConfigurationLoadException].
     *
     * @return Whether the configuration was loaded successfully
     * @see Configuration.loadOrCreateDefault
     */
    @JvmStatic
    fun tryLoadLocalConfiguration(): Boolean {
        try {
            localConfiguration = Configuration.loadOrCreateDefault()
            localConfigurationLoadException = null
            return true
        } catch (e: Exception) {
            localConfigurationLoadException = e
            LOGGER.error("Error loading configuration", e)
            return false
        }
    }

    private val LOGGER = LoggerFactory.getLogger("StaffMod/ConfigurationHolder")
}
