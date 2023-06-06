package dev.vstz.item

import dev.vstz.State
import dev.vstz.generator.CraftingObject
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

data class GeneratedCraftableItem(val name: String, val item: CraftableItem)

object BasicItemFactory {
    val itemList: ArrayList<GeneratedCraftableItem> = ArrayList()
    val modItemGroup: ItemGroup = FabricItemGroupBuilder
        .create(Identifier(State.modID, "dat-quarry"))
        .icon { ItemStack(quarryItem.item) }
        .build()

    private val quarryItem = registerCraftableItem(
        "Quarry Core", CraftableItem.createSimpleShapedRecipe(
            CraftingObject("cicDRDcBc", 1)
                .add('c', "minecraft:copper_ingot")
                .add('i', "minecraft:iron_ingot")
                .add('D', "minecraft:diamond")
                .add('R', "minecraft:redstone_block")
                .add('B', "minecraft:diamond_pickaxe"), 1
        )
    )

    fun createSettings(maxStackSize: Int = 64): FabricItemSettings {
        return FabricItemSettings().group(modItemGroup).maxCount(maxStackSize)
    }

    fun registerCraftableItem(showName: String, item: CraftableItem): CraftableItem {
        val name = showName.split(" ").joinToString("-") { it.lowercase() }
        item.showName = name
        Registry.register(Registry.ITEM, Identifier(State.modID, name), item.item)
        itemList.add(GeneratedCraftableItem(showName, item))

        return item
    }

    fun instantiate() {
        registerCraftableItem(
            "Quarry Control Unit",
            CraftableItem.createSimpleShapedRecipe(
                CraftingObject("#i#GPG#i#", 1)
                    .add('#', "minecraft:redstone")
                    .add('i', "minecraft:iron_ingot")
                    .add('G', "minecraft:glass")
                    .add('P', "minecraft:piston"),
            )
        )

        registerCraftableItem(
            "Quarry Battery",
            CraftableItem.createSimpleShapedRecipe(
                CraftingObject(" # cRccic", 1)
                    .add('#', "minecraft:heavy_weighted_pressure_plate")
                    .add('c', "minecraft:copper_ingot")
                    .add('R', "minecraft:redstone")
                    .add('i', "minecraft:iron_ingot")
            )
        )

        registerCraftableItem(
            "Silk Cloth",
            CraftableItem.createSimpleShapedRecipe(
                CraftingObject("####g####", 1)
                    .add('#', "minecraft:string")
                    .add('g', "minecraft:gold_ingot"),
                64
            )
        )

        registerCraftableItem(
            "Energy Core",
            CraftableItem.createSimpleShapedRecipe(
                CraftingObject("#i#iPi#i#", 1)
                    .add('#', "minecraft:redstone")
                    .add('i', "minecraft:iron_ingot")
                    .add('P', "datquarry:quarry-battery"),
                1
            )
        )
    }
}