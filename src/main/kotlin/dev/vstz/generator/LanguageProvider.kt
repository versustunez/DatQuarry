package dev.vstz.generator

import dev.vstz.block.BlockFactory
import dev.vstz.item.BasicItemFactory
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider

class LanguageProvider(dataGenerator: FabricDataGenerator?) : FabricLanguageProvider(dataGenerator, "en_us") {
    override fun generateTranslations(translationBuilder: TranslationBuilder) {
        BlockFactory.registery.forEach {
            translationBuilder.add(it.block, it.showName)
        }
        BasicItemFactory.itemList.forEach {
            translationBuilder.add(it.item, it.name)
        }
        translationBuilder.add(BasicItemFactory.modItemGroup, "DatQuarry")
    }
}