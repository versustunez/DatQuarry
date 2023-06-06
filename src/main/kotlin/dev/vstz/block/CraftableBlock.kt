package dev.vstz.block

import dev.vstz.generator.CraftingObject
import dev.vstz.item.BasicItemFactory
import net.minecraft.block.Block
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import java.util.function.Consumer

class CraftableBlockItem(
    block: Block,
    settings: Item.Settings,
    val generator: (exporter: Consumer<RecipeJsonProvider>?, item: Item) -> Unit,
) {
    val item = BlockItem(block, settings)
    fun generateRecipe(exporter: Consumer<RecipeJsonProvider>) {
        generator(exporter, item)
    }

    companion object {
        fun create(
            block: Block,
            craftingObject: CraftingObject,
            maxStackSize: Int = 64
        ): CraftableBlockItem {
            return CraftableBlockItem(block, BasicItemFactory.createSettings(maxStackSize)) { exporter, item ->
                craftingObject.createRecipe(item, exporter)
            }
        }
    }
}