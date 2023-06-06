package dev.vstz.generator

import dev.vstz.block.BlockFactory
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider

class BlockLootTablesProvider(dataGenerator: FabricDataGenerator?) : FabricBlockLootTableProvider(dataGenerator) {
    override fun generateBlockLootTables() {
        BlockFactory.registery.forEach {
            addDrop(it.block, drops(it.item.item))
        }
    }
}