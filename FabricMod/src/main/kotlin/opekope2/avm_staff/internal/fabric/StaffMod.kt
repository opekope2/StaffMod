/*
 * AvM Staff Mod
 * Copyright (c) 2023-2024 opekope2
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

package opekope2.avm_staff.internal.fabric

import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.particle.SimpleParticleType
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.avm_staff.api.IStaffModPlatform
import opekope2.avm_staff.api.item.CrownItem
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.item.renderer.StaffRenderer
import opekope2.avm_staff.internal.fabric.item.FabricStaffItem

@Suppress("unused")
object StaffMod : ModInitializer, IStaffModPlatform {
    override fun onInitialize() {
        AttackEntityCallback.EVENT.register(::dispatchStaffEntityAttack)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun dispatchStaffEntityAttack(
        player: PlayerEntity, world: World, hand: Hand, target: Entity, hit: EntityHitResult?
    ): ActionResult {
        val staffStack = player.getStackInHand(hand)
        val staffItem = staffStack.item as? StaffItem ?: return ActionResult.PASS
        val result = staffItem.attackEntity(staffStack, world, player, target, hand)

        return if (result.interruptsFurtherEvaluation()) ActionResult.SUCCESS
        else ActionResult.PASS
    }

    override fun staffItem(settings: Item.Settings) = FabricStaffItem(settings)

    override fun itemWithStaffRenderer(settings: Item.Settings) = Item(settings).also { item ->
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            BuiltinItemRendererRegistry.INSTANCE.register(item, StaffRenderer::renderStaff)
        }
    }

    override fun crownItem(groundBlock: Block, wallBlock: Block, settings: Item.Settings) =
        CrownItem(groundBlock, wallBlock, settings)

    override fun simpleParticleType(alwaysShow: Boolean): SimpleParticleType = FabricParticleTypes.simple(alwaysShow)
}
