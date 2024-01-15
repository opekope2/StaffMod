// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal.staff_item_handler

import com.mojang.serialization.Codec
import net.minecraft.entity.LivingEntity
import net.minecraft.item.BoneMealItem
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import net.minecraft.world.event.GameEvent
import opekope2.avm_staff.api.config.IConfiguration
import opekope2.avm_staff.api.initializer.IStaffModInitializationContext
import opekope2.avm_staff.api.item.StaffItemHandler

class BoneBlockItemHandler : StaffItemHandler() {
    override fun useOnBlock(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        target: BlockPos,
        side: Direction,
        hand: Hand
    ): ActionResult {
        if (BoneMealItem.useOnFertilizable(staffStack.copy(), world, target)) {
            // TODO fertilize area when enchanted
            if (!world.isClient) {
                user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH) // FIXME user originally PlayerEntity
                world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, target, 0)
            }

            return ActionResult.success(world.isClient)
        }

        val targetState = world.getBlockState(target)
        if (!targetState.isSideSolidFullSquare(world, target, side)) return ActionResult.PASS

        val neighborOnUsedSide = target.offset(side)
        if (!BoneMealItem.useOnGround(staffStack.copy(), world, neighborOnUsedSide, side)) return ActionResult.PASS

        if (!world.isClient) {
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH) // FIXME user originally PlayerEntity
            world.syncWorldEvent(WorldEvents.BONE_MEAL_USED, neighborOnUsedSide, 0)
        }

        return ActionResult.success(world.isClient)
    }

    // TODO
    private class Configuration : IConfiguration<Unit>

    companion object {
        private val CONFIG_CODEC: Codec<Configuration> = Codec.unit(::Configuration)

        fun registerStaffItemHandler(context: IStaffModInitializationContext) {
            context.registerStaffItemHandler(
                Identifier("bone_block"),
                BoneBlockItemHandler(),
                CONFIG_CODEC
            )
        }
    }
}
