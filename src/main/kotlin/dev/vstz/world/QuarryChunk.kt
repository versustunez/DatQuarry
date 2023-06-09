package dev.vstz.world

import dev.vstz.State
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.OreFeatureConfig
import kotlin.random.Random


// Note: this is not really a Chunk, its more of a "Big Fat" Array with blocks that gets regenerated each time its empty
//       this means that we don't have to worry about all the Chunk loading/unloading and Ticking the world. because there is no world
//       At best this will be faster than the other case because we can multi-thread that.

class QuarryChunk {

    data class PossibleBlock(val block: BlockState, val weight: Float)
    data class BiomePlacement(val biome: Identifier) {
        private val blocks = ArrayList<PossibleBlock>()
        private val lookupArray = ArrayList<Int>()
        var allSize = 0
        val randomBlock: BlockState
            get() = blocks[lookupArray.random()].block

        fun fill(possibleBlockMap: HashMap<BlockState, Int>) {
            blocks.ensureCapacity(possibleBlockMap.size)
            lookupArray.ensureCapacity(allSize)
            for ((item, weight) in possibleBlockMap) {
                blocks.add(PossibleBlock(item, weight.toFloat() / allSize.toFloat()))
                repeat(weight) { lookupArray.add(blocks.size - 1) }
            }
        }
    }

    companion object {
        const val MAX_CAPACITY = 2048 * 2 * 2
        private val possibleBlocks = HashMap<Identifier, BiomePlacement>()
        fun onWorldLoad(server: MinecraftServer) {
            val data = server.registryManager.get(Registry.BIOME_KEY)
            data.entrySet.forEach { biomeEntry ->
                val keyIdentifier = biomeEntry.key.value
                possibleBlocks[keyIdentifier] = BiomePlacement(keyIdentifier)
                val possibleBlockMap = HashMap<BlockState, Int>()
                val biome = biomeEntry.value
                val settings = biome.generationSettings
                settings.features.forEach { featureList ->
                    featureList.forEach { featureEntry ->
                        val feature = featureEntry.value()
                        if (settings.isFeatureAllowed(feature)) {
                            val f = feature.feature.value().config()
                            if (f is OreFeatureConfig) {
                                possibleBlocks[keyIdentifier]!!.allSize += f.size
                                f.targets.forEach {
                                    if (!possibleBlockMap.containsKey(it.state)) {
                                        possibleBlockMap[it.state] = 0
                                    }
                                    possibleBlockMap[it.state] = possibleBlockMap[it.state]!! + f.size
                                }
                            }
                        }
                    }
                }
                if (possibleBlockMap.isNotEmpty()) {
                    possibleBlockMap[Blocks.STONE.defaultState] = 50
                    possibleBlockMap[Blocks.DEEPSLATE.defaultState] = 15
                    possibleBlocks[keyIdentifier]!!.allSize += 65
                    possibleBlocks[keyIdentifier]!!.fill(possibleBlockMap)
                } else {
                    possibleBlocks.remove(keyIdentifier)
                }
            }
            State.logger.warn("QuarryChunk::onWorldLoad >> Possible biomes loaded: ${possibleBlocks.keys.size})")
        }

        fun onWorldUnload() {
            State.logger.warn("QuarryChunk::onWorldUnload called")
            possibleBlocks.clear()
        }
    }

    val biomeName: String get() = currentBiome!!.toTranslationKey() // maybe later used for the ui
    val amount: Int get() = MAX_CAPACITY - currentIndex
    private val blockStateArray = ArrayList<BlockState>(MAX_CAPACITY)
    private var currentIndex = 0

    // @TODO: Let the user say where he wants to farm maybe
    private var currentBiome: Identifier? = null
    private val random = Random(System.currentTimeMillis())

    init {
        // resize array ;)
        for (i in 0 until MAX_CAPACITY) {
            blockStateArray.add(Blocks.AIR.defaultState)
        }
    }

    val nextBlockState: BlockState
        get() = prepareNextBlock()


    private fun prepareNextBlock(): BlockState {
        currentIndex++
        if (currentIndex >= blockStateArray.size || currentBiome == null) {
            currentIndex = 0
            generateList()
        }
        return blockStateArray[currentIndex]
    }

    private fun generateList() {
        if (random.nextInt(0, 100) < 25 || currentBiome == null) {
            currentBiome = possibleBlocks.keys.random()
        }
        for (i in 0 until MAX_CAPACITY) {
            blockStateArray[i] = possibleBlocks[currentBiome]!!.randomBlock
        }
    }
}