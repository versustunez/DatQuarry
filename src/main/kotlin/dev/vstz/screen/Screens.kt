package dev.vstz.screen


import dev.vstz.State
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object Screens {
    var quarryScreenHandler = ScreenHandlerType(::QuarryScreenHandler)

    fun instantiate() {
        Registry.register(Registry.SCREEN_HANDLER, Identifier(State.modID, "quarry"), quarryScreenHandler)
    }
}