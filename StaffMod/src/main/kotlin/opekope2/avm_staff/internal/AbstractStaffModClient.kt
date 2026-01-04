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

package opekope2.avm_staff.internal

import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Blocks
import net.minecraft.client.item.ClampedModelPredicateProvider
import net.minecraft.client.render.entity.EmptyEntityRenderer
import net.minecraft.client.render.entity.TntEntityRenderer
import net.minecraft.item.Items.*
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import opekope2.avm_staff.api.IStaffModClientPlatform
import opekope2.avm_staff.api.entity.renderer.CakeEntityRenderer
import opekope2.avm_staff.api.item.renderer.BlockStateStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.StaffItemRenderer
import opekope2.avm_staff.api.staff.Defuse
import opekope2.avm_staff.content.EntityTypes
import opekope2.avm_staff.internal.event_handler.ClientEventHandlers
import opekope2.avm_staff.internal.event_handler.KeyBindingHandler
import opekope2.avm_staff.internal.model.MODEL_PREDICATES
import opekope2.avm_staff.internal.staff.item_renderer.BellStaffItemRenderer
import opekope2.avm_staff.internal.staff.item_renderer.FurnaceStaffItemRenderer
import opekope2.avm_staff.internal.staff.item_renderer.LightningRodStaffItemRenderer
import opekope2.avm_staff.internal.staff.item_renderer.WitherSkeletonSkullStaffItemRenderer
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.registryId
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
abstract class AbstractStaffModClient {
    protected fun initialize() {
        initialize(this)

        ClientEventHandlers.initialize()
        KeyBindingHandler.initialize()
        registerEntityRenderers()
        registerStaffItemRenderers()
    }

    protected fun registerEntityRenderers() {
        EntityRendererRegistry.register(EntityTypes::cake, ::CakeEntityRenderer)
        EntityRendererRegistry.register(EntityTypes::campfireFlame, ::EmptyEntityRenderer)
        EntityRendererRegistry.register(EntityTypes::impactTnt, ::TntEntityRenderer)
    }

    // TODO move to RegistryUtil
    protected fun registerStaffItemRenderers() {
        StaffItemRenderer.register(ANVIL, BlockStateStaffItemRenderer(Blocks.ANVIL))
        StaffItemRenderer.register(CHIPPED_ANVIL, BlockStateStaffItemRenderer(Blocks.CHIPPED_ANVIL))
        StaffItemRenderer.register(DAMAGED_ANVIL, BlockStateStaffItemRenderer(Blocks.DAMAGED_ANVIL))

        StaffItemRenderer.register(BELL, BellStaffItemRenderer())

        StaffItemRenderer.register(BONE_BLOCK, BlockStateStaffItemRenderer(Blocks.BONE_BLOCK))

        StaffItemRenderer.register(CAKE, BlockStateStaffItemRenderer(Blocks.CAKE))

        StaffItemRenderer.register(CAMPFIRE, BlockStateStaffItemRenderer(Blocks.CAMPFIRE))
        StaffItemRenderer.register(SOUL_CAMPFIRE, BlockStateStaffItemRenderer(Blocks.SOUL_CAMPFIRE))

        StaffItemRenderer.register(COMMAND_BLOCK, BlockStateStaffItemRenderer(Blocks.COMMAND_BLOCK))

        StaffItemRenderer.register(DIAMOND_BLOCK, BlockStateStaffItemRenderer(Blocks.DIAMOND_BLOCK))

        StaffItemRenderer.register(EMERALD_BLOCK, BlockStateStaffItemRenderer(Blocks.EMERALD_BLOCK))

        StaffItemRenderer.register(FURNACE, FurnaceStaffItemRenderer(Blocks.FURNACE))
        StaffItemRenderer.register(BLAST_FURNACE, FurnaceStaffItemRenderer(Blocks.BLAST_FURNACE))
        StaffItemRenderer.register(SMOKER, FurnaceStaffItemRenderer(Blocks.SMOKER))

        StaffItemRenderer.register(GOLD_BLOCK, BlockStateStaffItemRenderer(Blocks.GOLD_BLOCK))

        StaffItemRenderer.register(LIGHTNING_ROD, LightningRodStaffItemRenderer())

        StaffItemRenderer.register(MAGMA_BLOCK, BlockStateStaffItemRenderer(Blocks.MAGMA_BLOCK))

        StaffItemRenderer.register(NETHERITE_BLOCK, BlockStateStaffItemRenderer(Blocks.NETHERITE_BLOCK))

        StaffItemRenderer.register(SNOW_BLOCK, BlockStateStaffItemRenderer(Blocks.SNOW_BLOCK))

        StaffItemRenderer.register(TNT, BlockStateStaffItemRenderer(Blocks.TNT))

        StaffItemRenderer.register(WITHER_SKELETON_SKULL, WitherSkeletonSkullStaffItemRenderer())

        StaffItemRenderer.register(WHITE_WOOL, BlockStateStaffItemRenderer(Blocks.WHITE_WOOL))
        StaffItemRenderer.register(ORANGE_WOOL, BlockStateStaffItemRenderer(Blocks.ORANGE_WOOL))
        StaffItemRenderer.register(MAGENTA_WOOL, BlockStateStaffItemRenderer(Blocks.MAGENTA_WOOL))
        StaffItemRenderer.register(LIGHT_BLUE_WOOL, BlockStateStaffItemRenderer(Blocks.LIGHT_BLUE_WOOL))
        StaffItemRenderer.register(YELLOW_WOOL, BlockStateStaffItemRenderer(Blocks.YELLOW_WOOL))
        StaffItemRenderer.register(LIME_WOOL, BlockStateStaffItemRenderer(Blocks.LIME_WOOL))
        StaffItemRenderer.register(PINK_WOOL, BlockStateStaffItemRenderer(Blocks.PINK_WOOL))
        StaffItemRenderer.register(GRAY_WOOL, BlockStateStaffItemRenderer(Blocks.GRAY_WOOL))
        StaffItemRenderer.register(LIGHT_GRAY_WOOL, BlockStateStaffItemRenderer(Blocks.LIGHT_GRAY_WOOL))
        StaffItemRenderer.register(CYAN_WOOL, BlockStateStaffItemRenderer(Blocks.CYAN_WOOL))
        StaffItemRenderer.register(PURPLE_WOOL, BlockStateStaffItemRenderer(Blocks.PURPLE_WOOL))
        StaffItemRenderer.register(BLUE_WOOL, BlockStateStaffItemRenderer(Blocks.BLUE_WOOL))
        StaffItemRenderer.register(BROWN_WOOL, BlockStateStaffItemRenderer(Blocks.BROWN_WOOL))
        StaffItemRenderer.register(GREEN_WOOL, BlockStateStaffItemRenderer(Blocks.GREEN_WOOL))
        StaffItemRenderer.register(RED_WOOL, BlockStateStaffItemRenderer(Blocks.RED_WOOL))
        StaffItemRenderer.register(BLACK_WOOL, BlockStateStaffItemRenderer(Blocks.BLACK_WOOL))
    }

    protected inline fun registerModelPredicateProviders(register: (id: Identifier, modelPredicate: ClampedModelPredicateProvider) -> Unit) {
        for ((key, value) in MODEL_PREDICATES) register(key, value)
    }

    protected inline fun registerStaffItemModels(loadModel: (modelId: Identifier) -> Unit) {
        for (item in IStaffModClientPlatform.staffModelItems) {
            val itemId = item.registryId.withPrefixedPath("item/")
            loadModel(itemId.withSuffixedPath("/head"))
            loadModel(itemId.withSuffixedPath("/item_transform"))
            loadModel(itemId.withSuffixedPath("/rod_top"))
            loadModel(itemId.withSuffixedPath("/rod_bottom"))
        }
    }

    protected inline fun registerResourceLoaders(register: (id: Identifier, reloader: ResourceReloader) -> Unit) {
        register(Identifier.of(MOD_ID, "defuse"), Defuse)
    }

    companion object {
        /**
         * Holds the implementation of [AbstractStaffModClient].
         */
        @JvmStatic
        lateinit var implementation: AbstractStaffModClient
            private set

        @JvmStatic
        private fun initialize(instance: AbstractStaffModClient) {
            check(!::implementation.isInitialized) { "Tried to initialize AbstractStaffModClient twice" }
            implementation = instance
        }

        @JvmStatic
        @Suppress("UnusedReceiverParameter")
        protected fun Any.initialize() {
        }
    }
}
