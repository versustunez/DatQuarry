package dev.vstz

import dev.vstz.generator.*
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object DatQuarryDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        State.logger.info("Registering Quarry Data Generator")
        State.isGeneration = true
        fabricDataGenerator.addProvider(::BlockLootTablesProvider)
        fabricDataGenerator.addProvider(::LanguageProvider)
        fabricDataGenerator.addProvider(::BlockRecipeProvider)
        fabricDataGenerator.addProvider(::ItemRecipeProvider)
        fabricDataGenerator.addProvider(::ModelProvider)
    }
}