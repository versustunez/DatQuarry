package dev.vstz.block

import dev.vstz.LootUtils
import dev.vstz.State
import dev.vstz.screen.QuarryScreenHandler
import dev.vstz.world.QuarryChunk
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import team.reborn.energy.api.base.SimpleEnergyStorage


class BasicQuarryEntity(pos: BlockPos?, state: BlockState?) : BlockEntity(BasicQuarry.Instance.Entity, pos, state),
    NamedScreenHandlerFactory {
    private var skippingTicks = 0

    private var chestEntity: ChestBlockEntity? = null
    private var silkBlock: SilkBlock? = null

    private var tickRate = 1

    private var isInit = false
    val quarryChunk = QuarryChunk()

    class QuarryPropertyDelegate(val quarry: BasicQuarryEntity) : PropertyDelegate {
        override fun get(index: Int): Int {
            when (index) {
                0 -> return quarry.energyStorage.amount.toInt()
                1 -> return quarry.quarryChunk.amount
            }
            return 0
        }

        override fun set(index: Int, value: Int) {
        }

        override fun size(): Int {
            return 2
        }
    }

    private val propertyDelegate = QuarryPropertyDelegate(this)


    private fun getIsInvalid(): Boolean {
        return chestEntity == null
    }

    // Store a SimpleEnergyStorage in the block entity class.
    val energyStorage: SimpleEnergyStorage = object : SimpleEnergyStorage(MAX_ENERGY, 100000, 0) {
        override fun onFinalCommit() {
            markDirty()
        }
    }

    companion object {
        val MAX_ENERGY = 10000L
        var pickaxeItem = Registry.ITEM.get(Identifier("minecraft", "netherite_pickaxe"))
    }

    fun tick(world: World) {
        if (world.isClient) return
        if (!isInit) {
            State.logger.info("Initializing Quarry Entity")
            updateNeighborBlocks()
        }
        if (skippingTicks < getTickSkipNeeded()) {
            skippingTicks++
            return
        }
        skippingTicks = 0
        val powerNeeded = getPowerNeeded()
        if (energyStorage.amount < powerNeeded) {
            return
        }
        energyStorage.amount -= powerNeeded
        for (i in 0 until tickRate) {
            val blockState = quarryChunk.nextBlockState
            if (blockState.isAir || !pickaxeItem.isSuitableFor(blockState)) {
                return
            }
            if (silkBlock == null) {
                insertBlockLootIntoChest(blockState)
            } else {
                insertBlockIntoChest(blockState)
            }
        }
        markDirty()
    }

    private fun getPowerNeeded(): Int {
        return (64 * if (silkBlock == null) 1 else 2) * (tickRate * 1.1).toInt()
    }

    private fun getTickSkipNeeded(): Int {
        val ticks = ((-32 + tickRate) * -1)
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
        val loot = LootUtils.getLoot(world!!, blockState, BlockPos(0, 0, 0))
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


    override fun readNbt(nbt: NbtCompound) {
        energyStorage.amount = nbt.getLong("energy")
        super.readNbt(nbt)
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putLong("energy", energyStorage.amount)
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

        var speed = 1
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

    override fun createMenu(syncId: Int, inv: PlayerInventory?, player: PlayerEntity?): ScreenHandler? {
        return QuarryScreenHandler(syncId, inv!!, propertyDelegate)
    }

    override fun getDisplayName(): Text {
        return Text.translatable(cachedState.block.translationKey)
    }
}