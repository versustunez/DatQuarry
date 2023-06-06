package dev.vstz.block

import dev.vstz.State
import dev.vstz.generator.CraftingObject
import dev.vstz.item.BasicItemFactory
import net.minecraft.block.Block
import net.minecraft.data.client.*
import net.minecraft.item.BlockItem
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry

class SpeedupBlock(settings: Settings?, val factor: Int, val name: String) : Block(settings), BlockStateModelProvider {
    companion object {
        val CraftingRecipes = HashMap<Int, CraftingObject>()

        init {
            CraftingRecipes[2] = CraftingObject("erercrere", 1)
                .add('e', "datquarry:energy-core")
                .add('r', "minecraft:redstone")
                .add('c', "datquarry:quarry-control-unit")
            var i = 4
            var materialI = 0
            val materials = arrayOf("iron_ingot", "copper_ingot", "gold_ingot", "diamond")
            while (i <= 32) {
                CraftingRecipes[i] = CraftingObject("iris siri", 1)
                    .add('i', "minecraft:${materials[materialI]}")
                    .add('r', "minecraft:redstone")
                    .add('s', "datquarry:speedup_${i / 2}")
                i *= 2
                materialI++
            }
        }

        fun create(factor: Int) {
            if (!CraftingRecipes.contains(factor)) {
                throw Exception("Cannot find CraftingRecipe for speedup factor: $factor")
            }
            val name = "speedup_${factor}"
            val speedBlock = SpeedupBlock(BlockFactory.getBasicSettings(), factor, name)
            val blockItem = CraftableBlockItem.create(
                speedBlock, CraftingRecipes[factor]!!,
                1
            )
            Registry.register(Registry.BLOCK, Identifier(State.modID, name), speedBlock)
            Registry.register(Registry.ITEM, Identifier(State.modID, name), blockItem.item)

            BlockFactory.registerBasicBlock(speedBlock, blockItem, "Speedup x${factor}")
        }
    }

    override fun generateBlockStateModel(blockStateModelGenerator: BlockStateModelGenerator) {
        val factory = TexturedModel.makeFactory({ block: Block? -> TextureMap.all(block) }, Models.CUBE_BOTTOM_TOP)
        val texturedModel = factory.get(this)
        texturedModel.textures {
            it.put(TextureKey.BOTTOM, Identifier("datquarry", "block/speedup-side"))
            it.put(TextureKey.TOP, Identifier("datquarry", "block/quarry-side"))
            it.put(TextureKey.SIDE, Identifier("datquarry", "block/${name}"))
        }
        blockStateModelGenerator.registerSingleton(this, texturedModel.textures, Models.CUBE_BOTTOM_TOP)
    }
}