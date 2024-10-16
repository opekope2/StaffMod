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

package opekope2.avm_staff.internal.staff.handler

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.AbstractFurnaceBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.AbstractCookingRecipe
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.input.SingleStackRecipeInput
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import opekope2.avm_staff.api.component.StaffFurnaceDataComponent
import opekope2.avm_staff.api.item.renderer.BlockStateStaffItemRenderer
import opekope2.avm_staff.api.item.renderer.IStaffItemRenderer
import opekope2.avm_staff.api.staff.StaffAttributeModifiersComponentBuilder
import opekope2.avm_staff.api.staff.StaffHandler
import opekope2.avm_staff.api.staffFurnaceDataComponentType
import opekope2.avm_staff.mixin.IAbstractFurnaceBlockEntityAccessor
import opekope2.avm_staff.util.*
import kotlin.jvm.optionals.getOrNull

internal class FurnaceHandler<TRecipe : AbstractCookingRecipe>(
    private val recipeType: RecipeType<TRecipe>,
    private val smeltSound: SoundEvent
) : StaffHandler() {
    override val maxUseTime = 72000

    override val attributeModifiers: AttributeModifiersComponent
        get() = ATTRIBUTE_MODIFIERS

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        staffStack[staffFurnaceDataComponentType.get()] = StaffFurnaceDataComponent(0)

        user.setCurrentHand(hand)
        return TypedActionResult.consume(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (!user.canUseStaff) return

        val itemToSmelt = findItemToSmelt(world, user.approximateStaffItemPosition)
            ?: findItemToSmelt(world, user.approximateStaffTipPosition)

        if (world.isClient) {
            playSmeltingEffects(world, itemToSmelt ?: return)
            return
        }

        val furnaceData = staffStack[staffFurnaceDataComponentType.get()]!!
        furnaceData.serverBurnTicks++

        val stackToSmelt = itemToSmelt?.stack ?: return
        if (furnaceData.serverBurnTicks < stackToSmelt.count) return

        val recipeInput = SingleStackRecipeInput(itemToSmelt.stack)
        val recipe = world.recipeManager.getFirstMatch(recipeType, recipeInput, world).getOrNull()?.value ?: return
        val resultItem = recipe.getResult(world.registryManager).copyWithCount(stackToSmelt.count)

        val (vx, vy, vz) = itemToSmelt.velocity
        world.spawnEntity(ItemEntity(world, itemToSmelt.x, itemToSmelt.y, itemToSmelt.z, resultItem, vx, vy, vz))
        IAbstractFurnaceBlockEntityAccessor.callDropExperience(
            world as ServerWorld, itemToSmelt.pos, stackToSmelt.count, recipe.experience
        )
        itemToSmelt.discard()

        furnaceData.serverBurnTicks -= stackToSmelt.count
    }

    private fun findItemToSmelt(world: World, smeltingPosition: Vec3d): ItemEntity? {
        val items = world.getEntitiesByClass(ItemEntity::class.java, SMELTING_VOLUME.offset(smeltingPosition)) { true }
        val closest = items.minByOrNull { (smeltingPosition - it.pos).lengthSquared() }
        return closest
    }

    @Environment(EnvType.CLIENT)
    private fun playSmeltingEffects(world: World, itemToSmelt: ItemEntity) {
        if (Math.random() >= 0.1) return

        val (x, y, z) = itemToSmelt.pos
        world.playSound(x, y, z, smeltSound, SoundCategory.BLOCKS, 1f, 1f, false)

        val rx = Math.random() * 0.25 - 0.25 / 2
        val ry = Math.random() * 0.5
        val rz = Math.random() * 0.25 - 0.25 / 2

        val particleManager = MinecraftClient.getInstance().particleManager
        particleManager.addParticle(ParticleTypes.FLAME, x + rx, y + ry, z + rz, 0.0, 0.0, 0.0)
        particleManager.addParticle(ParticleTypes.SMOKE, x + rx, y + ry, z + rz, 0.0, 0.0, 0.0)
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        staffStack.remove(staffFurnaceDataComponentType.get())
    }

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        onStoppedUsing(staffStack, world, user, 0)
        return staffStack
    }

    @Environment(EnvType.CLIENT)
    class FurnaceStaffItemRenderer(unlitState: BlockState, litState: BlockState) : IStaffItemRenderer {
        constructor(furnaceBlock: Block) : this(
            furnaceBlock.defaultState,
            furnaceBlock.defaultState.with(AbstractFurnaceBlock.LIT, true)
        )

        private val unlitRenderer = BlockStateStaffItemRenderer(unlitState)
        private val litRenderer = BlockStateStaffItemRenderer(litState)

        override fun renderItemInStaff(
            staffStack: ItemStack,
            mode: ModelTransformationMode,
            matrices: MatrixStack,
            vertexConsumers: VertexConsumerProvider,
            light: Int,
            overlay: Int
        ) {
            val renderer =
                if (staffFurnaceDataComponentType.get() in staffStack) litRenderer
                else unlitRenderer

            renderer.renderItemInStaff(staffStack, mode, matrices, vertexConsumers, light, overlay)
        }
    }

    private companion object {
        private val ITEM_DIMENSIONS = EntityType.ITEM.dimensions
        private val SMELTING_VOLUME = Box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5).contract(
            ITEM_DIMENSIONS.width / 2.0, ITEM_DIMENSIONS.height / 2.0, ITEM_DIMENSIONS.width / 2.0
        )
        private val ATTRIBUTE_MODIFIERS = StaffAttributeModifiersComponentBuilder()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage(10.0), AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.GENERIC_ATTACK_SPEED, attackSpeed(1.25), AttributeModifierSlot.MAINHAND)
            .addDefault(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)
            .addDefault(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE)
            .build()
    }
}
