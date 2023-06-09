package dev.vstz.world

import dev.vstz.State
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

class QuarryChunkInit {
    companion object {
        // This makes sure that quarry chunks are loaded before the server starts
        // And unload on server shutdown
        fun onInitialize() {
            State.logger.info("Initializing QuarryChunk Listener")
            ServerLifecycleEvents.SERVER_STARTED.register {
                QuarryChunk.onWorldLoad(it)
            }
            ServerLifecycleEvents.START_DATA_PACK_RELOAD.register { server, _ ->
                QuarryChunk.onWorldLoad(server)
            }
            ServerLifecycleEvents.SERVER_STOPPED.register {
                QuarryChunk.onWorldUnload()
            }
        }

    }
}