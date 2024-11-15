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

package opekope2.avm_staff.content

import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.enums.NoteBlockInstrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import opekope2.avm_staff.api.block.CrownBlock
import opekope2.avm_staff.api.block.WallCrownBlock
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.RegistryUtil

/**
 * Blocks added by AVM Staffs mod.
 */
object Blocks : RegistryUtil<Block>(MOD_ID, RegistryKeys.BLOCK) {
    private fun settings() = AbstractBlock.Settings.create()
    private fun settings(block: RegistrySupplier<out Block>) = AbstractBlock.Settings.copy(block.get())

    /**
     * Block registered as `avm_staff:crown_of_king_orange`.
     */
    @JvmField
    val CROWN_OF_KING_ORANGE = register("crown_of_king_orange") {
        CrownBlock(
            settings().instrument(NoteBlockInstrument.BELL).strength(1.0f).pistonBehavior(PistonBehavior.DESTROY)
                .sounds(BlockSoundGroup.COPPER_GRATE).nonOpaque()
        )
    }

    /**
     * @see CROWN_OF_KING_ORANGE
     */
    val crownOfKingOrange: CrownBlock
        @JvmName("crownOfKingOrange")
        get() = CROWN_OF_KING_ORANGE.get()

    /**
     * Block registered as `avm_staff:wall_crown_of_king_orange`.
     */
    @JvmField
    val WALL_CROWN_OF_KING_ORANGE = register("wall_crown_of_king_orange") {
        WallCrownBlock(settings(CROWN_OF_KING_ORANGE))
    }

    /**
     * @see WALL_CROWN_OF_KING_ORANGE
     */
    val wallCrownOfKingOrange: WallCrownBlock
        @JvmName("wallCrownOfKingOrange")
        get() = WALL_CROWN_OF_KING_ORANGE.get()
}
