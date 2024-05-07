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

package opekope2.avm_staff.internal.staff_item_handler

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.AbstractFurnaceBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.block.BlockModels
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.Baker
import net.minecraft.client.render.model.ModelBakeSettings
import net.minecraft.client.render.model.UnbakedModel
import net.minecraft.client.render.model.json.Transformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SingleStackInventory
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.AbstractCookingRecipe
import net.minecraft.recipe.RecipeType
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.world.World
import opekope2.avm_staff.api.item.StaffItemHandler
import opekope2.avm_staff.api.item.model.IStaffItemBakedModel
import opekope2.avm_staff.api.item.model.IStaffItemUnbakedModel
import opekope2.avm_staff.mixin.IAbstractFurnaceBlockEntityMixin
import opekope2.avm_staff.util.*
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

class FurnaceHandler<TRecipe : AbstractCookingRecipe>(
    private val recipeType: RecipeType<TRecipe>,
    private val smeltSound: SoundEvent
) : StaffItemHandler() {
    override val maxUseTime = 72000

    override fun use(
        staffStack: ItemStack,
        world: World,
        user: PlayerEntity,
        hand: Hand
    ): TypedActionResult<ItemStack> {
        staffStack.getOrCreateNbt().apply {
            putBoolean(LIT_KEY, true)
            putInt(BURN_TIME_KEY, 0)
        }

        user.setCurrentHand(hand)
        return TypedActionResult.pass(staffStack)
    }

    override fun usageTick(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (!user.canUseStaff) return

        val itemToSmelt = findItemToSmelt(user, world)

        if (world.isClient) {
            playSmeltingEffects(world, itemToSmelt ?: return)
            return
        }

        val nbt = staffStack.getOrCreateNbt()
        var burnTime = nbt.getInt(BURN_TIME_KEY)
        nbt.putInt(BURN_TIME_KEY, burnTime + 1)

        val stackToSmelt = itemToSmelt?.stack ?: return
        if (burnTime < stackToSmelt.count) return

        val inventory = ItemEntityInventory(itemToSmelt)
        val recipe = world.recipeManager.getFirstMatch(recipeType, inventory, world).getOrNull()?.value ?: return
        val resultItem = recipe.getResult(world.registryManager).copyWithCount(stackToSmelt.count)

        val (vx, vy, vz) = itemToSmelt.velocity
        world.spawnEntity(ItemEntity(world, itemToSmelt.x, itemToSmelt.y, itemToSmelt.z, resultItem, vx, vy, vz))
        IAbstractFurnaceBlockEntityMixin.invokeDropExperience(
            world as ServerWorld,
            itemToSmelt.pos,
            stackToSmelt.count,
            recipe.experience
        )

        burnTime -= stackToSmelt.count
        nbt.putInt(BURN_TIME_KEY, burnTime + 1)

        itemToSmelt.discard()
    }

    private fun findItemToSmelt(
        user: LivingEntity,
        world: World
    ): ItemEntity? {
        val smeltingPosition = user.approximateStaffItemPosition
        val items = world.getEntitiesByClass(ItemEntity::class.java, SMELTING_VOLUME.offset(smeltingPosition)) { true }
        val closest = items.minByOrNull { (smeltingPosition - it.pos).lengthSquared() }
        return closest
    }

    @Environment(EnvType.CLIENT)
    private fun playSmeltingEffects(world: World, itemToSmelt: ItemEntity) {
        if (Math.random() >= 0.1) return

        val (x, y, z) = itemToSmelt.pos
        world.playSound(x, y, z, smeltSound, SoundCategory.BLOCKS, 1.0f, 1.0f, false)

        val rx = Math.random() * 0.25 - 0.25 / 2
        val ry = Math.random() * 0.5
        val rz = Math.random() * 0.25 - 0.25 / 2

        val particleManager = MinecraftClient.getInstance().particleManager
        particleManager.addParticle(ParticleTypes.FLAME, x + rx, y + ry, z + rz, 0.0, 0.0, 0.0)
        particleManager.addParticle(ParticleTypes.SMOKE, x + rx, y + ry, z + rz, 0.0, 0.0, 0.0)
    }

    override fun onStoppedUsing(staffStack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        staffStack.getOrCreateNbt().apply {
            remove(LIT_KEY)
            remove(BURN_TIME_KEY)
        }
    }

    override fun finishUsing(staffStack: ItemStack, world: World, user: LivingEntity): ItemStack {
        onStoppedUsing(staffStack, world, user, 0)
        return staffStack
    }

    override fun allowNbtUpdateAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        player: PlayerEntity,
        hand: Hand
    ): Boolean {
        return false
    }

    override fun allowReequipAnimation(
        oldStaffStack: ItemStack,
        newStaffStack: ItemStack,
        selectedSlotChanged: Boolean
    ): Boolean {
        return selectedSlotChanged
    }

    override fun getAttributeModifiers(
        staffStack: ItemStack,
        slot: EquipmentSlot
    ): Multimap<EntityAttribute, EntityAttributeModifier> {
        return if (slot == EquipmentSlot.MAINHAND) ATTRIBUTE_MODIFIERS
        else super.getAttributeModifiers(staffStack, slot)
    }

    @Environment(EnvType.CLIENT)
    class FurnaceUnbakedModel(private val unlitState: BlockState, private val litState: BlockState) :
        IStaffItemUnbakedModel {
        constructor(furnaceBlock: Block) : this(
            furnaceBlock.defaultState,
            furnaceBlock.defaultState.with(AbstractFurnaceBlock.LIT, true)
        )

        private val litStateId = BlockModels.getModelId(litState)
        private val unlitStateId = BlockModels.getModelId(unlitState)
        private val dependencies = setOf(litStateId, unlitStateId)

        override fun getModelDependencies() = dependencies

        override fun setParents(modelLoader: Function<Identifier, UnbakedModel>?) {
        }

        override fun bake(
            baker: Baker,
            textureGetter: Function<SpriteIdentifier, Sprite>,
            rotationContainer: ModelBakeSettings,
            modelId: Identifier,
            transformation: Transformation
        ): IStaffItemBakedModel? {
            val unlitModel = baker.bake(unlitStateId, rotationContainer) ?: return null
            val litModel = baker.bake(litStateId, rotationContainer) ?: return null

            return FurnaceBakedModel(
                unlitModel.transform(unlitState, transformation, textureGetter),
                litModel.transform(litState, transformation, textureGetter)
            )
        }
    }

    @Environment(EnvType.CLIENT)
    private class FurnaceBakedModel(private val unlitModel: BakedModel, private val litModel: BakedModel) :
        BakedModel by unlitModel, IStaffItemBakedModel {
        override fun getModel(staffStack: ItemStack): BakedModel {
            return if (staffStack.nbt?.getBoolean(LIT_KEY) == true) litModel
            else unlitModel
        }
    }

    private class ItemEntityInventory(private val itemEntity: ItemEntity) : SingleStackInventory {
        override fun getStack(): ItemStack = itemEntity.stack

        override fun setStack(stack: ItemStack) {}

        override fun markDirty() {}

        override fun decreaseStack(count: Int): ItemStack = ItemStack.EMPTY

        override fun asBlockEntity(): Nothing = throw UnsupportedOperationException()
    }

    companion object {
        private const val LIT_KEY = "Lit"
        private const val BURN_TIME_KEY = "BurnTime"
        private val SMELTING_VOLUME = Box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5).contract(0.25 / 2)
        private val ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            attackDamage(5.0),
            EntityAttributes.GENERIC_ATTACK_SPEED,
            attackSpeed(2.0)
        )
    }
}
