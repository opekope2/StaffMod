package opekope2.avm_staff.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.util.InputUtil
import opekope2.avm_staff.StaffMod.MOD_ID
import opekope2.avm_staff.model.StaffItemModel
import opekope2.avm_staff.packet.AddBlockToStaffC2SPacket
import opekope2.avm_staff.packet.RemoveBlockFromStaffC2SPacket
import opekope2.avm_staff.util.staffHasItem
import org.lwjgl.glfw.GLFW

@Suppress("unused")
@Environment(EnvType.CLIENT)
object StaffModClient : ClientModInitializer {
    @JvmStatic
    val ADD_REMOVE_KEYBIND = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.avm_staff.add_remove_staff_block",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.avm_staff"
        )
    )

    override fun onInitializeClient() {
        ModelLoadingPlugin.register(::modelLoadingPlugin)
        ClientTickEvents.END_CLIENT_TICK.register(::handleStaffKeybind)
    }

    private fun modelLoadingPlugin(pluginContext: ModelLoadingPlugin.Context) {
        pluginContext.modifyModelAfterBake().register(::modifyModelAfterBake)
    }

    private fun modifyModelAfterBake(model: BakedModel?, context: ModelModifier.AfterBake.Context): BakedModel? {
        val id = context.id()

        return if (model == null || id.namespace != MOD_ID || id.path != "staff") model
        else StaffItemModel(model)
    }

    private fun handleStaffKeybind(client: MinecraftClient) {
        if (ADD_REMOVE_KEYBIND.isPressed) {
            ADD_REMOVE_KEYBIND.isPressed = false

            val player = client.player ?: return

            if (player.mainHandStack.staffHasItem || player.offHandStack.staffHasItem) {
                RemoveBlockFromStaffC2SPacket().send()
            } else {
                AddBlockToStaffC2SPacket().send()
            }
        }
    }
}
