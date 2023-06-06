package dev.vstz.generator

import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.ItemConvertible
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Consumer

data class CraftingObject(val pattern: String, val outputCount: Int) {
    internal val chunkedPattern = pattern.chunked(3)
    internal val itemList = HashMap<Char, Identifier>()

    fun add(char: Char, name: String): CraftingObject {
        itemList[char] = Identifier(name)
        return this
    }

    fun createRecipe(
        item: ItemConvertible,
        exporter: Consumer<RecipeJsonProvider>?
    ) {
        if (this.chunkedPattern.count() != 3) {
            throw IllegalArgumentException("Invalid pattern for item: ${item.asItem().name}\nPattern: ${this.pattern}, Chunks: ${this.chunkedPattern}")
        }
        val recipe = ShapedRecipeJsonBuilder.create(item, this.outputCount)
            .pattern(this.chunkedPattern[0])
            .pattern(this.chunkedPattern[1])
            .pattern(this.chunkedPattern[2])
        this.itemList.forEach {
            val registerItem = Registry.ITEM.get(it.value)
            recipe.input(it.key, registerItem).criterion(
                FabricRecipeProvider.hasItem(registerItem),
                FabricRecipeProvider.conditionsFromItem(registerItem)
            )
        }
        recipe.offerTo(exporter)
    }
}