package dev.vstz.block

import dev.vstz.State
import dev.vstz.item.BasicItemFactory
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class SilkBlock(settings: Settings?) : Block(settings) {
    companion object {
        fun create() {
            val speedBlock = SilkBlock(BlockFactory.getBasicSettings())
            val blockItem = BlockItem(speedBlock, BasicItemFactory.createSettings(1))
            Registry.register(Registry.BLOCK, Identifier(State.modID, "silk"), speedBlock)
            Registry.register(Registry.ITEM, Identifier(State.modID, "silk"), blockItem)
            BlockFactory.registerBasicBlock(speedBlock, blockItem, "Silk Touch")
        }
    }
}