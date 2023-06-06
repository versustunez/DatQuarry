package dev.vstz.world

import com.google.common.collect.ImmutableList
import dev.vstz.mixin.MinecraftServerAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Util
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.source.BiomeAccess
import net.minecraft.world.dimension.DimensionOptions

class RuntimeWorld(server: MinecraftServer, registryKey: RegistryKey<World>, dimensionOptions: DimensionOptions) : ServerWorld(server,
        Util.getMainWorkerExecutor(),
        (server as MinecraftServerAccess).session,
        RuntimeWorldProperties(server.saveProperties),
        registryKey,
        dimensionOptions,
        VoidWorldProgressListener.INSTANCE,
        false,
        BiomeAccess.hashSeed(WorldGenerator.randomInstance.nextLong()),
        ImmutableList.of(),
        false
)