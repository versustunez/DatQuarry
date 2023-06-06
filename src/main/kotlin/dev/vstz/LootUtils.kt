package dev.vstz

import dev.vstz.block.BasicQuarryEntity
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World


object LootUtils {
    fun getLoot(world: World, block: BlockState, pos: BlockPos): ArrayList<ItemStack>? {
        val loot: ArrayList<ItemStack> = ArrayList()
        val lootTable = world.server?.lootManager?.getTable(block.block.lootTableId) ?: return null
        val builder = LootContext.Builder(world as ServerWorld)
            .random(world.random)
            .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
            .parameter(LootContextParameters.BLOCK_STATE, block)
            .parameter(LootContextParameters.TOOL, ItemStack { BasicQuarryEntity.pickaxeItem })

        val lootContext = builder.build(LootContextTypes.BLOCK)
        lootTable.generateLoot(lootContext).stream()
            .limit(27)
            .forEach(loot::add)

        return loot
    }
}