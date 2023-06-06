package dev.vstz.item

import dev.vstz.generator.CraftingObject
import net.minecraft.data.server.recipe.RecipeJsonProvider

import net.minecraft.item.Item
import java.util.function.Consumer

class CraftableItem(
    settings: Item.Settings,
    var showName: String = "unnamed-item",
    val generator: (exporter: Consumer<RecipeJsonProvider>?, item: Item) -> Unit,
) {
    val item: Item = Item(settings)
    fun generateRecipe(exporter: Consumer<RecipeJsonProvider>) {
        generator(exporter, item)
    }

    companion object {
        fun createSimpleShapedRecipe(
            craftingObject: CraftingObject,
            maxStackSize: Int = 64
        ): CraftableItem {
            return CraftableItem(BasicItemFactory.createSettings(maxStackSize)) { exporter, item ->
                craftingObject.createRecipe(item, exporter)
            }
        }
    }
}