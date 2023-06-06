package dev.vstz.block

import dev.vstz.State
import dev.vstz.generator.CraftingObject
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class SilkBlock(settings: Settings?) : Block(settings) {
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
}