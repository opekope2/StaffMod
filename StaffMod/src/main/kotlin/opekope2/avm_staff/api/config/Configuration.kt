// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.api.config

import com.google.gson.JsonParseException
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Identifier
import opekope2.avm_staff.internal.StaffMod.MOD_ID
import opekope2.avm_staff.internal.item.StaffItemHandlers
import opekope2.avm_staff.util.component1
import opekope2.avm_staff.util.component2
import java.io.IOException
import kotlin.jvm.optionals.getOrNull

/**
 * Staff mod logical server and gameplay configuration.
 *
 * @param itemConfigurations    Configuration for each supported item in the staff
 */
data class Configuration(val itemConfigurations: Map<Identifier, IConfiguration<*>>) {
    private object ItemConfigurationMapCodec : Codec<Map<Identifier, IConfiguration<*>>> {
        override fun <T> encode(
            input: Map<Identifier, IConfiguration<*>>,
            ops: DynamicOps<T>,
            prefix: T
        ): DataResult<T> {
            val mapBuilder = ops.mapBuilder()

            for ((key, value) in input) {
                mapBuilder.add(
                    Identifier.CODEC.encode(key, ops, prefix),
                    StaffItemHandlers[key]?.configurationCodec?.encode(value, ops, prefix) ?: continue
                )
            }

            return mapBuilder.build(prefix)
        }

        override fun <T> decode(
            ops: DynamicOps<T>,
            input: T
        ): DataResult<Pair<Map<Identifier, IConfiguration<*>>, T>> {
            val map = ops.getMap(input).result().getOrNull()
                ?: return DataResult.error { "Not a map: $input" }
            val result = mutableMapOf<Identifier, IConfiguration<*>>()

            for ((key, value) in map.entries()) {
                val decodedKey = Identifier.CODEC.decode(ops, key).result().getOrNull()?.first
                    ?: return DataResult.error({ "Can't decode Identifier: $key" }, Pair.of(result, ops.empty()))

                val codec = StaffItemHandlers[decodedKey]?.configurationCodec ?: continue
                val decodedValue = codec.decode(ops, value).result().getOrNull()?.first
                    ?: return DataResult.error(
                        { "Can't decode `$decodedKey` configuration: $value" },
                        Pair.of(result, ops.empty())
                    )

                result[decodedKey] = decodedValue
            }

            return DataResult.success(Pair.of(result, ops.empty()))
        }
    }

    companion object {
        @JvmField
        val CODEC: Codec<Configuration> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemConfigurationMapCodec.fieldOf("items").forGetter(Configuration::itemConfigurations)
            ).apply(instance, ::Configuration)
        }

        private val configurationLoader = ConfigurationLoader(MOD_ID, "config.json", CODEC)

        /**
         * Creates the default configuration.
         */
        @JvmStatic
        fun createDefault(): Configuration = Configuration(mapOf())

        /**
         * Loads the configuration from the configuration folder if exists, or saves the default if it doesn't exist.
         * This doesn't handle IO errors (like inaccessible file), malformed JSON, or encoding and decoding failures.
         */
        @JvmStatic
        @Throws(IOException::class, JsonParseException::class)
        fun loadOrCreateDefault(): Configuration {
            return configurationLoader.loadOrSaveConfiguration(::createDefault)
        }
    }
}
