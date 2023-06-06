package dev.vstz.block

import net.minecraft.data.client.BlockStateModelGenerator

interface BlockStateModelProvider {
    fun generateBlockStateModel(blockStateModelGenerator: BlockStateModelGenerator) {
    }
}
