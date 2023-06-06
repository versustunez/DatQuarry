package dev.vstz

import dev.vstz.block.BlockFactory
import dev.vstz.item.BasicItemFactory
import net.fabricmc.api.ModInitializer

object DatQuarry : ModInitializer {
    override fun onInitialize() {
        State.logger.info("Initializing Dat Quarry")
        BasicItemFactory.instantiate()
        BlockFactory.instantiate()
    }

    fun onCleanup() {
        // This is metadata... we don't need any of that for the real game.
        if (!State.isGeneration) {
            BlockFactory.registery.clear()
            BasicItemFactory.itemList.clear()
        }
    }
}