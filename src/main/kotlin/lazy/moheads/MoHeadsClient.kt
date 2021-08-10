package lazy.moheads

import lazy.moheads.head.HeadUtils
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents

@Environment(EnvType.CLIENT)
object MoHeadsClient : ClientModInitializer {

    override fun onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(HeadUtils::load)
    }
}