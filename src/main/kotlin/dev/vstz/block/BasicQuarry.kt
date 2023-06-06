package dev.vstz.block

import dev.vstz.State
import dev.vstz.item.BasicItemFactory
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import net.minecraft.state.property.Properties
import net.minecraft.world.WorldAccess
import team.reborn.energy.api.EnergyStorage


class BasicQuarry(settings: Settings?) : Block(settings), BlockEntityProvider {

    // Deprecated for calling
    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        if (world != null && !world.isClient) {
            val entity = world.getBlockEntity(pos) as BasicQuarryEntity
            player?.sendMessage(
                entity.getLiteral(),
                true
            )
        }
        return ActionResult.CONSUME
    }


    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return defaultState.with(Properties.FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        super.appendProperties(builder)
        builder?.add(Properties.FACING)
    }

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
        return BasicQuarryEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        if (type != Instance.Entity) {
            return null
        }
        return BlockEntityTicker { world1: World?, pos: BlockPos?, state1: BlockState?, be: BlockEntity? ->
            BasicQuarryEntity.tick(
                world1!!,
                pos!!,
                state1!!,
                be as BasicQuarryEntity
            )
        }
    }

    override fun getStateForNeighborUpdate(
        state: BlockState?,
        direction: Direction?,
        neighborState: BlockState?,
        world: WorldAccess?,
        pos: BlockPos?,
        neighborPos: BlockPos?
    ): BlockState {
        (world!!.getBlockEntity(pos) as BasicQuarryEntity).onNeighborUpdated(neighborPos!!)
        return state!!
    }

    object Instance {
        val Quarry = BasicQuarry(BlockFactory.getBasicSettings())
        val QuarryItem = BlockItem(Quarry, BasicItemFactory.createSettings(1))
        val Entity = FabricBlockEntityTypeBuilder.create(BasicQuarryEntity::create, Quarry).build()!!

        fun create() {
            Registry.register(Registry.BLOCK, Identifier(State.modID, "quarry"), Quarry)
            Registry.register(Registry.ITEM, Identifier(State.modID, "quarry"), QuarryItem)
            Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                Identifier(State.modID, "quarry_entity"),
                Entity
            )
            EnergyStorage.SIDED.registerForBlockEntity(
                { myBlockEntity, direction -> myBlockEntity.energyStorage },
                Entity
            )

            BlockFactory.registerBasicBlock(Quarry, QuarryItem, "Quarry")
        }
    }
}