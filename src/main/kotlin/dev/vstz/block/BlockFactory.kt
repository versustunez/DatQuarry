package dev.vstz.block

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.item.BlockItem

data class RegisteredBlock(val block: Block, val item: BlockItem, val showName: String = "")

object BlockFactory {
    val registery = ArrayList<RegisteredBlock>()
    fun registerBasicBlock(block: Block, item: BlockItem, showName: String = "") {
        registery.add(RegisteredBlock(block, item, showName))
    }

    fun instantiate() {
        BasicQuarry.Instance.create()

        SpeedupBlock.create(2)
        SpeedupBlock.create(4)
        SpeedupBlock.create(8)
        SpeedupBlock.create(16)
        SpeedupBlock.create(32)
        SilkBlock.create()
    }

    fun getBasicSettings(): FabricBlockSettings {
        return FabricBlockSettings.of(Material.METAL)
            .strength(4.0f)
            .resistance(1.0f)
    }
}