package dev.vstz.item

import dev.vstz.State
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.max


data class GeneratedItem(val name: String, val item: Item)

object BasicItemFactory {
    val itemList: ArrayList<GeneratedItem> = ArrayList()
    val modItemGroup: ItemGroup = FabricItemGroupBuilder
        .create(Identifier(State.modID, "dat-quarry"))
        .icon { ItemStack(quarryItem) }
        .build()
    private val quarryItem = createItem("Quarry Core", 1)

    fun createSettings(maxStackSize: Int = 64): FabricItemSettings {
        return FabricItemSettings().group(modItemGroup).maxCount(maxStackSize)
    }

    fun createItem(showName: String, maxStackSize: Int = 64): Item {
        val name = showName.split(" ").joinToString("-") { it.lowercase() }
        val item = Item(createSettings(maxStackSize))
        Registry.register(Registry.ITEM, Identifier(State.modID, name), item)
        itemList.add(GeneratedItem(showName, item))
        return item
    }

    fun instantiate() {
        createItem("Quarry Control Unit")
        createItem("Quarry Battery")

        createItem("Silk Cloth")
        createItem("Energy Core")
    }
}