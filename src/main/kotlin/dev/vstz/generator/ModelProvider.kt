package dev.vstz.generator

import dev.vstz.block.BlockFactory
import dev.vstz.block.BlockStateModelProvider
import dev.vstz.item.BasicItemFactory
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models

class ModelProvider(dataGenerator: FabricDataGenerator?) : FabricModelProvider(dataGenerator) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator?) {
        BlockFactory.registery.forEach {
            if (it.block is BlockStateModelProvider) {
                it.block.generateBlockStateModel(blockStateModelGenerator!!)
            }
        }
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator?) {
        BasicItemFactory.itemList.forEach {
            itemModelGenerator!!.register(it.item.item, Models.GENERATED)
        }
    }
}