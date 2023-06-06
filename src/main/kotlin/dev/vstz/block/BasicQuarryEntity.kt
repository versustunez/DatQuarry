package dev.vstz.block

import dev.vstz.LootUtils
import dev.vstz.world.WorldGenerator
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import team.reborn.energy.api.base.SimpleEnergyStorage
import net.minecraft.util.registry.Registry

class BasicQuarryEntity(pos: BlockPos?, state: BlockState?) : BlockEntity(BasicQuarry.Instance.Entity, pos, state) {
    private var farmingPosition = BlockPos(0, 64, 0)
    private var skippingTicks = 0

    private var chestEntity: ChestBlockEntity? = null
    private var speedBlock: SpeedupBlock? = null
    private var silkBlock: SilkBlock? = null

    private var tickRate = 1

    private var isInit = false


    private fun getIsInvalid(): Boolean {
        return chestEntity == null
    }

    // Store a SimpleEnergyStorage in the block entity class.
    val energyStorage: SimpleEnergyStorage = object : SimpleEnergyStorage(10000, 10000, 0) {
        override fun onFinalCommit() {
            markDirty()
        }
    }

    companion object {
        var internalWorld: World? = null
        var pickaxeItem = Registry.ITEM.get(Identifier("minecraft", "netherite_pickaxe"))
        fun create(pos: BlockPos?, state: BlockState?): BasicQuarryEntity {
            return BasicQuarryEntity(pos, state)
        }

        fun tick(world: World, pos: BlockPos, state: BlockState, be: BasicQuarryEntity) {
            be._tick(world, state, pos)
        }
    }

    private fun _tick(world: World, ignoreState: BlockState, ignore: BlockPos) {
        if (world.isClient) return
        if (skippingTicks < getTickSkipNeeded()) {
            skippingTicks++
            return
        }
        skippingTicks = 0

        if (!isInit) updateNeighborBlocks()
        val powerNeeded = getPowerNeeded()
        for (i in 0 until tickRate) {
            if (energyStorage.amount < powerNeeded) {
                break
            }
            energyStorage.amount -= powerNeeded
            val blockState = createHarvestBlock() ?: continue
            if (blockState.isAir || !pickaxeItem.isSuitableFor(blockState)) {
                advanceFarmingPosition()
                continue
            }
            if (silkBlock == null) {
                if (!insertBlockLootIntoChest(blockState)) continue
            } else {
                if (!insertBlockIntoChest(blockState)) continue
            }
            advanceFarmingPosition()
        }
        markDirty()
    }

    private fun getPowerNeeded(): Int {
        return 64 * if (silkBlock == null) 1 else 2
    }

    private fun getTickSkipNeeded(): Int {
        val ticks = ((-32 + tickRate) * -1) / 2
        return 0.coerceAtLeast(ticks)
    }

    private fun insertBlockIntoChest(blockState: BlockState): Boolean {
        val spot = findEmptySpot(blockState.block)
        if (spot == -1) return false
        val chestStack = chestEntity!!.getStack(spot)
        if (chestStack.isEmpty) {
            chestEntity!!.setStack(spot, ItemStack { blockState.block.asItem() })
        } else {
            chestStack.increment(1)
        }
        return true
    }

    private fun insertBlockLootIntoChest(blockState: BlockState): Boolean {
        if (chestEntity == null) return false
        val loot = LootUtils.getLoot(world!!, blockState, farmingPosition)
        var added = loot.isNullOrEmpty()
        loot?.forEach {
            val spot = findSpotOfItemStack(it)
            if (spot != -1) {
                val chestStack = chestEntity!!.getStack(spot)
                if (chestStack.isEmpty) {
                    chestEntity!!.setStack(spot, it)
                } else {
                    chestStack.increment(it.count)
                }
                added = true
            }
        }
        return added
    }

    private fun findSpotOfItemStack(stack: ItemStack): Int {
        var firstEmptySpot = -1
        for (i in 0 until chestEntity!!.size()) {
            val stack2 = chestEntity!!.getStack(i)
            if (stack2.isOf(stack.item) && stack2.count < stack2.maxCount) return i
            if (stack2.isEmpty) firstEmptySpot = i
        }
        return firstEmptySpot
    }


    private fun findEmptySpot(farmed: Block): Int {
        if (chestEntity == null) return -1
        for (i in 0 until chestEntity!!.size()) {
            val stack = chestEntity!!.getStack(i)
            if (stack.isEmpty) return i
            if (stack.isOf(farmed.asItem()) && stack.count < stack.maxCount) return i
        }
        return -1
    }

    private fun createHarvestBlock(): BlockState? {
        if (internalWorld == null) {
            internalWorld = WorldGenerator.createNewWorld(world!!)
            skippingTicks -= 100
        }
        if (internalWorld == null) {
            throw IllegalStateException("Internal world is null")
        }
        return internalWorld!!.getBlockState(farmingPosition)
    }

    private fun advanceFarmingPosition() {
        farmingPosition = BlockPos(farmingPosition.x, farmingPosition.y - 1, farmingPosition.z)
        if (farmingPosition.y == -64) {
            farmingPosition = BlockPos(farmingPosition.x + 1, 64, farmingPosition.z)
        }
        if (farmingPosition.x > Int.MAX_VALUE - 10) {
            farmingPosition = BlockPos(0, 64, farmingPosition.z + 1)
        }
        if (farmingPosition.z > Int.MAX_VALUE - 10) {
            farmingPosition = BlockPos(0, 64, 0)
        }
    }


    override fun readNbt(nbt: NbtCompound) {
        energyStorage.amount = nbt.getLong("energy")
        farmingPosition = BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"))
        super.readNbt(nbt)
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putLong("energy", energyStorage.amount)
        nbt.putInt("x", farmingPosition.x)
        nbt.putInt("y", farmingPosition.y)
        nbt.putInt("z", farmingPosition.z)
        super.writeNbt(nbt)
    }

    fun onNeighborUpdated(neighborPos: BlockPos) {
        updateNeighborBlocks()
    }

    private fun updateNeighborBlocks() {
        val possibleChest = world!!.getBlockEntity(pos.up())
        chestEntity = if (possibleChest == null || possibleChest !is ChestBlockEntity) {
            null
        } else {
            possibleChest
        }

        val directNeighbor = arrayOf(
            BlockPos(pos.x + 1, pos.y, pos.z),
            BlockPos(pos.x - 1, pos.y, pos.z),
            BlockPos(pos.x, pos.y, pos.z + 1),
            BlockPos(pos.x, pos.y, pos.z - 1),
        )

        var speed = 0
        var silkFound: SilkBlock? = null

        directNeighbor.forEach {
            val possibleSpeedBlock = world!!.getBlockState(it).block
            if (possibleSpeedBlock is SpeedupBlock) {
                speed += 1 * possibleSpeedBlock.factor
            }
            val possibleSilkBlock = world!!.getBlockState(it).block
            if (possibleSilkBlock is SilkBlock) {
                silkFound = possibleSilkBlock
            }
        }
        tickRate = speed
        silkBlock = silkFound
        isInit = true
    }

    fun getLiteral(): Text {
        if (getIsInvalid()) {
            return Text.literal("[Invalid Quarry... Missing Chest at Top]").setStyle(Style.EMPTY.withColor(0xff3232))
        }
        val powerNeeded = getPowerNeeded()
        return Text.literal(
            "Energy: ${energyStorage.amount}E - ${powerNeeded}/T | " +
                    "Pos(X:${farmingPosition.x}, Y:${farmingPosition.y}, Z:${farmingPosition.z}) | " +
                    "Ticks: $tickRate | ${getTickSkipNeeded()}"
        ).setStyle(Style.EMPTY.withColor(0xff0089))
    }
}