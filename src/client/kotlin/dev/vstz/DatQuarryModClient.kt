package dev.vstz

import dev.vstz.screen.QuarryScreen
import dev.vstz.screen.Screens
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screen.ingame.HandledScreens

object DatQuarryModClient : ClientModInitializer {
    override fun onInitializeClient() {
        HandledScreens.register(Screens.quarryScreenHandler, ::QuarryScreen)
    }
}