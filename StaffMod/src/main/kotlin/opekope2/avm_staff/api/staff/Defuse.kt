/*
 * AvM Staff Mod
 * Copyright (c) 2025 opekope2
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

import com.google.gson.JsonParseException
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SinglePreparationResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.profiler.Profiler
import opekope2.avm_staff.api.staff.Defuse.FILE_NAME
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.util.registryId
import org.slf4j.LoggerFactory

/**
 * Loads particles and sounds from a resource pack for entities that can be defused by a staff.
 */
object Defuse : SinglePreparationResourceReloader<Map<Identifier, BlockState>>() {
    /**
     * The file name of the entity defuse data in the root of resource packs.
     */
    const val FILE_NAME = "defuse.json"
    private val LOGGER = LoggerFactory.getLogger(javaClass)
    private val CODEC: Codec<Map<Identifier, BlockState>> = Codec.unboundedMap(Identifier.CODEC, BlockState.CODEC)
    private lateinit var data: Map<Identifier, BlockState>

    /**
     * Gets the block state, whose particles should be shown and sound should be played, when an entity gets defused by
     * a staff.
     *
     * @param entityType    The registry ID of the entity type that got defused
     * @return The block state if specified in [FILE_NAME] in the root of the resource pack, or `null`, if not specified
     */
    @JvmStatic
    fun getDefuseBlockState(entityType: Identifier) = data[entityType]

    /**
     * Gets the block state, whose particles should be shown and sound should be played, when an entity gets defused by
     * a staff.
     *
     * @param entityType    The entity type that got defused
     * @return The block state if specified in [FILE_NAME] in the root of the resource pack, or `null`, if not specified
     */
    @JvmStatic
    fun getDefuseBlockState(entityType: EntityType<*>) = getDefuseBlockState(entityType.registryId)

    override fun prepare(manager: ResourceManager, profiler: Profiler): Map<Identifier, BlockState> {
        val data = mutableMapOf<Identifier, BlockState>().withDefault { Blocks.OAK_LEAVES.defaultState }

        for (namespace in manager.allNamespaces) {
            for (resource in manager.getAllResources(Identifier.of(namespace, FILE_NAME))) {
                try {
                    val json = resource.reader.use(JsonHelper::deserialize)
                    val defuseData = CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(::JsonParseException)
                    data.putAll(defuseData)
                } catch (e: Exception) {
                    LOGGER.atError()
                        .setCause(e)
                        .addArgument(
                            I18n.ERROR_AVM_STAFF_RESOURCE_PACK_FILE
                                .supplyTranslation(FILE_NAME, resource.packId, e.message)
                        )
                        .log("{}")
                }
            }
        }

        return data
    }

    override fun apply(prepared: Map<Identifier, BlockState>, manager: ResourceManager, profiler: Profiler) {
        data = prepared
    }
}
