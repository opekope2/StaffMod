package opekope2.avm_staff.model

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.MultipartBakedModel
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import opekope2.avm_staff.util.hasBlock
import opekope2.avm_staff.util.readBlock
import org.joml.Vector3f
import java.util.function.Supplier

@Environment(EnvType.CLIENT)
class StaffItemModel(model: BakedModel) : ForwardingBakedModel() {
    init {
        super.wrapped = model
    }

    override fun emitItemQuads(stack: ItemStack, randomSupplier: Supplier<Random>, context: RenderContext) {
        super.emitItemQuads(stack, randomSupplier, context)

        if (!stack.hasBlock) return

        val blockStack = stack.readBlock()

        val item = blockStack.item as? BlockItem
        if (item == null) {
            // TODO render item model
            context.pushTransform(::transformBlockIntoStaff)
            BAKED_MODEL_MANAGER.missingModel.emitItemQuads(stack, randomSupplier, context)
            context.popTransform()
            return
        }
        val state = item.block.defaultState

        context.pushTransform(::transformBlockIntoStaff)
        if (state.renderType == BlockRenderType.MODEL) {
            val model: FabricBakedModel = BLOCK_RENDERING_MANAGER.getModel(state)
            if (model is MultipartBakedModel) {
                model.emitMultipartItemQuads(state, randomSupplier, context)
            } else {
                model.emitItemQuads(stack, randomSupplier, context)
            }
        } else {
            // TODO block entities
            BAKED_MODEL_MANAGER.missingModel.emitItemQuads(stack, randomSupplier, context)
        }
        context.popTransform()
    }

    private fun MultipartBakedModel.emitMultipartItemQuads(
        state: BlockState,
        randomSupplier: Supplier<Random>,
        context: RenderContext
    ) {
        val emitter = context.emitter

        for (cullFace in FACES) {
            val quads = getQuads(state, cullFace, randomSupplier.get())

            for (quad in quads) {
                emitter.fromVanilla(quad, MATERIAL_STANDARD, cullFace).emit()
            }
        }
    }

    private fun transformBlockIntoStaff(quad: MutableQuadView): Boolean {
        val vec = Vector3f()
        for (i in 0..3) {
            val pos = quad.copyPos(i, vec)
            pos.mul(7f / 16f)
            pos.add(9f / 16f / 2f, 22f / 16f, (16f - 7f) / 16f / 2f)
            quad.pos(i, pos)
        }
        return true
    }

    override fun isVanillaAdapter() = false

    companion object {
        private val MATERIAL_STANDARD = RendererAccess.INSTANCE.renderer!!.materialFinder().find()
        private val FACES = arrayOf(*Direction.values(), null)
        private val BLOCK_RENDERING_MANAGER = MinecraftClient.getInstance().blockRenderManager
        private val BAKED_MODEL_MANAGER = MinecraftClient.getInstance().bakedModelManager
    }
}
