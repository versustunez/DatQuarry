package dev.vstz

import dev.vstz.generator.BlockLootTablesProvider
import dev.vstz.generator.BlockRecipeProvider
import dev.vstz.generator.ItemRecipeProvider
import dev.vstz.generator.LanguageProvider
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
    }
}