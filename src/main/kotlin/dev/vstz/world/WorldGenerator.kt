package dev.vstz.world

import com.mojang.datafixers.kinds.IdF.Mu
import com.mojang.serialization.Lifecycle
import dev.vstz.mixin.MinecraftServerAccess
import kotlinx.coroutines.sync.Mutex
import net.fabricmc.fabric.api.event.EventFactory
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionOptions
import net.minecraft.util.registry.SimpleRegistry
import org.apache.logging.log4j.core.jmx.Server
import java.util.*
import kotlin.collections.ArrayList


object WorldGenerator {
    var index = 0
    var randomInstance: Random = Random()

    private var worldsAdded = HashMap<RegistryKey<World>, ServerWorld>()
    private var mutex = Mutex()
    private val Overworld = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, Identifier("overworld"))

    init {
        val tickListener = ServerTickEvents.StartTick { server ->
            run {
                mutex.tryLock()
                worldsAdded.forEach { (registryKey, serverWorld) ->
                    (server as MinecraftServerAccess).worlds[registryKey] = serverWorld
                }
                worldsAdded.clear()
                mutex.unlock()
            }
        }
        ServerTickEvents::START_SERVER_TICK.get().register(tickListener)
    }

    fun createNewWorld(world: World): World? {
        if (world.server == null) return null
        index++
        val ident = Identifier("dat-quarry-$index")
        val worldKey: RegistryKey<World> = RegistryKey.of(Registry.WORLD_KEY, ident)

        var newWorld = world.server!!.getWorld(worldKey)
        if (newWorld == null) {
            // Create a new world
            newWorld = createPersistentWorld(world.server!!, worldKey) as ServerWorld
        }
        return newWorld
    }

    fun deleteWorld(index: Int) {
        // @TODO: Delete World.
    }

    private fun createPersistentWorld(server: MinecraftServer, worldKey: RegistryKey<World>): World {
        val dimension = server.registryManager.get(Registry.DIMENSION_TYPE_KEY).getEntry(Overworld)
        if (dimension.isEmpty) {
            throw IllegalArgumentException("Overworld dimension not found")
        }
        val options = DimensionOptions(dimension.get(), server.overworld.chunkManager.chunkGenerator)
        val dimensionsRegistry = server.saveProperties.generatorOptions.dimensions as SimpleRegistry<DimensionOptions>
        dimensionsRegistry.add(RegistryKey.of(Registry.DIMENSION_KEY, worldKey.value), options, Lifecycle.stable())
        val myWorld = RuntimeWorld(server, worldKey, options)

        mutex.tryLock()
        worldsAdded[worldKey] = myWorld
        mutex.unlock()

        //myWorld.tick { true }
        return myWorld
    }
}