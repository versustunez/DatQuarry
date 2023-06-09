package dev.vstz.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler

class QuarryScreenHandler(
    syncId: Int,
    val playerInventory: PlayerInventory, // is needed for the screen stuff. even if we don't use it
    val delegate: PropertyDelegate = ArrayPropertyDelegate(2)
) :
    ScreenHandler(Screens.quarryScreenHandler, syncId) {

    init {
        this.addProperties(delegate)
    }

    override fun transferSlot(player: PlayerEntity?, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return true
    }
}