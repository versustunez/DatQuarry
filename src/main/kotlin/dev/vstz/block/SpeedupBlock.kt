package dev.vstz.block

import dev.vstz.State
import dev.vstz.item.BasicItemFactory
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class SpeedupBlock(settings: Settings?, val factor: Int, val name: String) : Block(settings) {
    companion object {
        fun create(factor: Int) {
            val name = "speedup_${factor}"
            val speedBlock = SpeedupBlock(BlockFactory.getBasicSettings(), factor, name)
            val blockItem = BlockItem(speedBlock, BasicItemFactory.createSettings(16))
            Registry.register(Registry.BLOCK, Identifier(State.modID, name), speedBlock)
            Registry.register(Registry.ITEM, Identifier(State.modID, name), blockItem)

            BlockFactory.registerBasicBlock(speedBlock, blockItem, "Speedup x${factor}")
        }
    }
}