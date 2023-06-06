package dev.vstz.world

import net.minecraft.server.WorldGenerationProgressListener
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.chunk.ChunkStatus

class VoidWorldProgressListener : WorldGenerationProgressListener {
    companion object {
        val INSTANCE: VoidWorldProgressListener = VoidWorldProgressListener()
    }

    override fun start(spawnPos: ChunkPos?) {
    }

    override fun start() {
    }

    override fun setChunkStatus(pos: ChunkPos?, status: ChunkStatus?) {
    }

    override fun stop() {
    }

}