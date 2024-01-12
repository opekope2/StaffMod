// Copyright (c) 2023-2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal

import com.mojang.serialization.Codec
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import opekope2.avm_staff.api.config.ConfigurationHolder
import opekope2.avm_staff.api.config.IConfiguration
import opekope2.avm_staff.api.initializer.IStaffModInitializationContext
import opekope2.avm_staff.api.initializer.IStaffModInitializer
import opekope2.avm_staff.api.item.StaffItem
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.internal.item.StaffItemHandlers
import opekope2.avm_staff.internal.packet.c2s.play.AddItemToStaffC2SPacket
import opekope2.avm_staff.internal.packet.c2s.play.RemoveItemFromStaffC2SPacket
import opekope2.avm_staff.internal.packet.c2s.play.StaffAttackC2SPacket
import opekope2.avm_staff.internal.packet.s2c.configure.ConfigureStaffModS2CPacket
import opekope2.avm_staff.internal.server.StaffPacketHandler
import opekope2.avm_staff.util.handlerOfItem
import opekope2.avm_staff.util.itemInStaff

@Suppress("unused")
object StaffMod : ModInitializer {
    const val MOD_ID = "avm_staff"

    @JvmField
    val STAFF_ITEM: StaffItem = Registry.register(
        Registries.ITEM,
        Identifier(MOD_ID, "staff"),
        StaffItem(FabricItemSettings().maxCount(1))
    )

    override fun onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register { entries ->
            entries.addAfter(Items.NETHERITE_HOE, STAFF_ITEM)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register { entries ->
            entries.addAfter(Items.TRIDENT, STAFF_ITEM)
        }

        AddItemToStaffC2SPacket.registerGlobalReceiver(StaffPacketHandler::addBlockToStaff)
        RemoveItemFromStaffC2SPacket.registerGlobalReceiver(StaffPacketHandler::removeBlockFromStaff)
        StaffAttackC2SPacket.registerGlobalReceiver(StaffPacketHandler::attack)

        AttackBlockCallback.EVENT.register(::handleBlockAttackEvent)
        AttackEntityCallback.EVENT.register(::handleEntityAttackEvent)

        ServerConfigurationConnectionEvents.CONFIGURE.register { handler, server ->
            ConfigureStaffModS2CPacket(ConfigurationHolder.localConfiguration).send(handler)
        }

        FabricLoader.getInstance().invokeEntrypoints("avm-staff", IStaffModInitializer::class.java) { entryPoint ->
            entryPoint.onInitializeStaffMod(StaffModInitializationContext)
        }
    }

    private fun handleBlockAttackEvent(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        target: BlockPos,
        direction: Direction
    ): ActionResult {
        val staffStack = player.getStackInHand(hand)
        if (!staffStack.isOf(STAFF_ITEM)) return ActionResult.PASS

        return staffStack.itemInStaff?.handlerOfItem?.attackBlock(staffStack, world, player, target, direction, hand)
            ?: ActionResult.PASS
    }

    private fun handleEntityAttackEvent(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        target: Entity,
        hit: EntityHitResult?
    ): ActionResult {
        if (world.isClient) return ActionResult.PASS // Handled with Staff Mod mixin

        val staffStack = player.getStackInHand(hand)
        if (!staffStack.isOf(STAFF_ITEM)) return ActionResult.PASS

        return staffStack.itemInStaff?.handlerOfItem?.attackEntity(staffStack, world, player, target, hand)
            ?: ActionResult.PASS
    }

    private object StaffModInitializationContext : IStaffModInitializationContext {
        override fun <TConfig : IConfiguration<TProfile>, TProfile : Any> registerStaffItemHandler(
            itemInStaff: Identifier,
            handler: StaffItemHandler,
            configurationCodec: Codec<TConfig>,
            profileCodec: Codec<TProfile>
        ): Boolean {
            return StaffItemHandlers.register(itemInStaff, handler, configurationCodec)
        }
    }
}
