package dev.vstz.generator

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.RecipeJsonProvider
import java.util.function.Consumer

class BlockRecipeProvider(dataGenerator: FabricDataGenerator?) : FabricRecipeProvider(dataGenerator) {
    override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>?) {

    }
}