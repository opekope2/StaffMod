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

package opekope2.avm_staff.content

import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.enums.NoteBlockInstrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import opekope2.avm_staff.api.block.CrownBlock
import opekope2.avm_staff.api.block.WallCrownBlock
import opekope2.avm_staff.util.MOD_ID
import opekope2.avm_staff.util.Registrar

/**
 * Blocks added by AVM Staffs mod.
 */
object Blocks : Registrar<Block>(MOD_ID, RegistryKeys.BLOCK) {
    @JvmStatic
    private fun settings() = AbstractBlock.Settings.create()

    /**
     * Creates an instance of [AbstractBlock.Settings] for a crown block.
     */
    @JvmStatic
    fun crownSettings(): AbstractBlock.Settings = settings()
        .instrument(NoteBlockInstrument.BELL)
        .strength(1.0f)
        .pistonBehavior(PistonBehavior.DESTROY)
        .sounds(BlockSoundGroup.COPPER_GRATE)
        .nonOpaque()

    /**
     * Crown of King Orange block.
     */
    @JvmStatic
    val crownOfKingOrange by registering { CrownBlock(crownSettings()) }

    /**
     * Crown of King Orange block on the wall.
     */
    @JvmStatic
    val wallCrownOfKingOrange by registering { WallCrownBlock(crownSettings()) }
}
