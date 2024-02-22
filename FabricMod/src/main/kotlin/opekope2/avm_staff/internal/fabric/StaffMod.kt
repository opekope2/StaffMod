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
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.particle.DefaultParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World
import opekope2.avm_staff.IStaffMod
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.internal.event_handler.attackBlock
import opekope2.avm_staff.internal.event_handler.attackEntity
import opekope2.avm_staff.internal.fabric.item.FabricStaffItem
import opekope2.avm_staff.util.MOD_ID

@Suppress("unused")
object StaffMod : ModInitializer, IStaffMod {
    override val staffItem: StaffItem = Registry.register(
        Registries.ITEM,
        Identifier(MOD_ID, "staff"),
        FabricStaffItem(FabricItemSettings().maxCount(1))
    )

    override val isPhysicalClient: Boolean
        get() = FabricLoader.getInstance().environmentType == EnvType.CLIENT

    override val staffsTag: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, Identifier(MOD_ID, "staffs"))

    override val flamethrowerParticleType: DefaultParticleType = Registry.register(
        Registries.PARTICLE_TYPE,
        Identifier(MOD_ID, "flame"),
        FabricParticleTypes.simple()
    )

    override val soulFlamethrowerParticleType: DefaultParticleType = Registry.register(
        Registries.PARTICLE_TYPE,
        Identifier(MOD_ID, "soul_fire_flame"),
        FabricParticleTypes.simple()
    )

    override fun onInitialize() {
        AttackBlockCallback.EVENT.register(::attackBlock)
        AttackEntityCallback.EVENT.register(::handleEntityAttackEvent)
    }

    private fun handleEntityAttackEvent(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        target: Entity,
        @Suppress("UNUSED_PARAMETER") hit: EntityHitResult?
    ): ActionResult {
        if (world.isClient) return ActionResult.PASS // Handled with mixin

        return attackEntity(player, world, hand, target)
    }
}
