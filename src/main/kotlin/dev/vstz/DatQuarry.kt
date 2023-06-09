package dev.vstz

import dev.vstz.block.BlockFactory
import dev.vstz.item.BasicItemFactory
import dev.vstz.screen.Screens
import dev.vstz.world.QuarryChunkInit
import net.fabricmc.api.ModInitializer

object DatQuarry : ModInitializer {
    override fun onInitialize() {
        State.logger.info("Initializing Dat Quarry")
        BasicItemFactory.instantiate()
        BlockFactory.instantiate()
        QuarryChunkInit.onInitialize()
        Screens.instantiate()
    }
}