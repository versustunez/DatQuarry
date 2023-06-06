package dev.vstz.block

import dev.vstz.State
import dev.vstz.generator.CraftingObject
import net.minecraft.block.Block
import net.minecraft.data.client.*
import net.minecraft.item.BlockItem
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry

class SilkBlock(settings: Settings?) : Block(settings), BlockStateModelProvider {
    companion object {
        fun create() {
            val silkBlock = SilkBlock(BlockFactory.getBasicSettings())
            val blockItem = CraftableBlockItem.create(
                silkBlock,
                CraftingObject("IRIRSRIRI", 1)
                    .add('S', "datquarry:quarry-control-unit")
                    .add('R', "minecraft:redstone")
                    .add('I', "datquarry:silk-cloth"),
                1
            )
            Registry.register(Registry.BLOCK, Identifier(State.modID, "silk"), silkBlock)
            Registry.register(Registry.ITEM, Identifier(State.modID, "silk"), blockItem.item)
            BlockFactory.registerBasicBlock(silkBlock, blockItem, "Silk Touch")
        }
    }

    override fun generateBlockStateModel(blockStateModelGenerator: BlockStateModelGenerator) {
        val factory = TexturedModel.makeFactory({ block: Block? -> TextureMap.all(block) }, Models.CUBE_BOTTOM_TOP)
        val texturedModel = factory.get(this)
        texturedModel.textures {
            it.put(TextureKey.BOTTOM, Identifier("datquarry", "block/quarry-side"))
            it.put(TextureKey.TOP, Identifier("datquarry", "block/quarry-side"))
            it.put(TextureKey.SIDE, Identifier("datquarry", "block/silk"))
        }
        blockStateModelGenerator.registerSingleton(this, texturedModel.textures, Models.CUBE_BOTTOM_TOP)
    }
}