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

package opekope2.avm_staff.internal.staff.handler

import it.unimi.dsi.fastutil.ints.IntSet
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.SharedConstants.TICKS_PER_SECOND
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.AbstractCookingRecipe
import net.minecraft.recipe.RecipeEntry
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.input.SingleStackRecipeInput
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import opekope2.avm_staff.api.component.StaffFurnaceDataComponent
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.content.DataComponentTypes
import opekope2.avm_staff.internal.I18n
import opekope2.avm_staff.mixin.IAbstractFurnaceBlockEntityAccessor
import opekope2.avm_staff.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.round

internal class FurnaceHandler<TRecipe : AbstractCookingRecipe>(
    private val recipeType: RecipeType<TRecipe>,
    private val smeltSound: SoundEvent,
    private val speed: Int,
) : StaffHandler() {
    override fun getMaxUseTime(staffStack: ItemStack, world: World, user: LivingEntity) = 3600 * TICKS_PER_SECOND

    override val attributeModifiers = StaffAttributeModifiersComponentBuilder()
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(8.0), AttributeModifierSlot.MAINHAND)
        .addDefault(EntityAttributes.GENERIC_ATTACK_SPEED)
        .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
        .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
        .build()

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: LivingEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        StaffFurnaceDataComponent().saveTo(staffStack)

        user.setCurrentHand(hand)
        return TypedActionResult.pass(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (!user.canUseStaff()) return

        var furnaceData = staffStack[DataComponentTypes.furnaceData]!!
        var itemToSmelt = world.getEntityById(furnaceData.smeltedItemId) as? ItemEntity

        if (world.isClient) {
            if (itemToSmelt == null) return

            val stackToSmelt = itemToSmelt.stack
            val remainingSeconds = (stackToSmelt.count - furnaceData.smeltTicks).toFloat() / speed / TICKS_PER_SECOND
            mc.inGameHud.setOverlayMessage(
                I18n.FEEDBACK_AVM_STAFF_SMELTING.getText(stackToSmelt.name, round(remainingSeconds * 10f) / 10f),
                false
            )
            playSmeltingEffects(world, itemToSmelt)

            return
        }

        val findNewItemToSmelt = itemToSmelt == null ||
                !isInSmeltingVolume(itemToSmelt, user.approximateStaffItemPosition) &&
                !isInSmeltingVolume(itemToSmelt, user.approximateStaffTipPosition)
        if (findNewItemToSmelt) {
            furnaceData = furnaceData.copy(smeltedItemId = -1, smeltTicks = 0).saveTo(staffStack)
            itemToSmelt = findItemToSmelt(world, user.approximateStaffItemPosition, furnaceData.unsmeltableItemIds)
                ?: findItemToSmelt(world, user.approximateStaffTipPosition, furnaceData.unsmeltableItemIds) ?: return
        }

        furnaceData = furnaceData.copy(smeltedItemId = itemToSmelt.id, smeltTicks = furnaceData.smeltTicks + speed)
            .saveTo(staffStack)

        val stackToSmelt = itemToSmelt.stack
        if (furnaceData.smeltTicks < stackToSmelt.count) return

        val recipe = getRecipe(stackToSmelt, world)!!.value
        val resultItem = recipe.getResult(world.registryManager).copyWithCount(stackToSmelt.count)

        val (vx, vy, vz) = itemToSmelt.velocity
        world.spawnEntity(ItemEntity(world, itemToSmelt.x, itemToSmelt.y, itemToSmelt.z, resultItem, vx, vy, vz))
        IAbstractFurnaceBlockEntityAccessor.callDropExperience(
            world as ServerWorld, itemToSmelt.pos, stackToSmelt.count, recipe.experience
        )
        itemToSmelt.discard()

        furnaceData.copy(smeltedItemId = -1, smeltTicks = 0).saveTo(staffStack)
        staffStack.damage(stackToSmelt.count, user)
        (user as? ServerPlayerEntity)?.incrementStaffItemUseStat(staffStack.itemInStaff!!)
    }

    private fun isInSmeltingVolume(item: ItemEntity, smeltingPosition: Vec3d) =
        SMELTING_VOLUME.offset(smeltingPosition).intersects(item.boundingBox)

    private fun getRecipe(stack: ItemStack, world: World): RecipeEntry<TRecipe>? {
        val recipeInput = SingleStackRecipeInput(stack)
        return world.recipeManager.getFirstMatch(recipeType, recipeInput, world).getOrNull()
    }

    private fun findItemToSmelt(world: World, smeltingPosition: Vec3d, excluded: IntSet): ItemEntity? {
        val items = world.getEntitiesByClass(
            ItemEntity::class.java,
            SMELTING_VOLUME.offset(smeltingPosition)
        ) { it.id !in excluded }
        val itr = items.listIterator()
        for (item in itr) {
            if (getRecipe(item.stack, world) != null) continue
            excluded.add(item.id) // Do not use += because it boxes primitives
            itr.remove()
        }

        return items.minByOrNull { (smeltingPosition - it.pos).lengthSquared() }
    }

    @Environment(EnvType.CLIENT)
    private fun playSmeltingEffects(world: World, itemToSmelt: ItemEntity) {
        if (Math.random() >= 0.1) return

        val (x, y, z) = itemToSmelt.pos
        world.playSound(x, y, z, smeltSound, SoundCategory.BLOCKS, 1f, 1f, false)

        val rx = Math.random() * 0.25 - 0.25 / 2
        val ry = Math.random() * 0.5
        val rz = Math.random() * 0.25 - 0.25 / 2

        mc.particleManager.addParticle(ParticleTypes.FLAME, x + rx, y + ry, z + rz, 0.0, 0.0, 0.0)
        mc.particleManager.addParticle(ParticleTypes.SMOKE, x + rx, y + ry, z + rz, 0.0, 0.0, 0.0)
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        staffStack.remove(DataComponentTypes.furnaceData)
        (user as? ServerPlayerEntity)?.incrementItemUseStat(staffStack.item)
    }

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        onStoppedUsing(staffStack, world, user, 0)
        return staffStack
    }

    override fun allowComponentsUpdateAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        player: PlayerEntity,
        hand: Hand
    ) = false

    override fun allowReequipAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        selectedSlotChanged: Boolean
    ) = selectedSlotChanged

    private companion object {
        private val ITEM_DIMENSIONS = EntityType.ITEM.dimensions
        private val SMELTING_VOLUME = Box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5).contract(
            ITEM_DIMENSIONS.width / 2.0, ITEM_DIMENSIONS.height / 2.0, ITEM_DIMENSIONS.width / 2.0
        )

        private fun StaffFurnaceDataComponent.saveTo(stack: ItemStack): StaffFurnaceDataComponent {
            stack[DataComponentTypes.furnaceData] = this
            return this
        }
    }
}
