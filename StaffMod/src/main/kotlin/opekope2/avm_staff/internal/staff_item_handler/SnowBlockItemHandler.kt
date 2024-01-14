// Copyright (c) 2024 opekope2
// Staff Mod is licensed under the MIT license: https://github.com/opekope2/StaffMod/blob/main/LICENSE

package opekope2.avm_staff.internal.staff_item_handler

import com.mojang.serialization.Codec
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.thrown.SnowballEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import opekope2.avm_staff.api.config.IConfiguration
import opekope2.avm_staff.api.initializer.IStaffModInitializationContext
import opekope2.avm_staff.api.item.StaffItemHandler

class SnowBlockItemHandler : StaffItemHandler() {
    override val maxUseTime = 72000

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        user.setCurrentHand(hand)
        return TypedActionResult.pass(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (world.isClient) return

        world.playSound(
            null,
            user.x,
            user.y,
            user.z,
            SoundEvents.ENTITY_SNOWBALL_THROW,
            SoundCategory.NEUTRAL,
            0.5f,
            0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        )

        world.spawnEntity(SnowballEntity(world, user).apply {
            // TODO speed
            setVelocity(user, user.pitch, user.yaw, 0f, 4f, 1f)
        })
    }

    // TODO
    private class Configuration : IConfiguration<Unit>

    companion object {
        private val CONFIG_CODEC: Codec<Configuration> = Codec.unit(::Configuration)

        fun registerStaffItemHandler(context: IStaffModInitializationContext) {
            context.registerStaffItemHandler(
                Identifier("snow_block"),
                SnowBlockItemHandler(),
                CONFIG_CODEC
            )
        }
    }
}
