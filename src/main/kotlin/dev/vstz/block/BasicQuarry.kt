package dev.vstz.block

import dev.vstz.State
import dev.vstz.generator.CraftingObject
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.data.client.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import team.reborn.energy.api.EnergyStorage


class BasicQuarry(settings: Settings?) : BlockWithEntity(settings), BlockEntityProvider, BlockStateModelProvider {
    companion object {
        val FACING = Properties.HORIZONTAL_FACING
    }

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
            val screenHandlerFactory = state!!.createScreenHandlerFactory(world, pos)
            if (screenHandlerFactory != null)
                player!!.openHandledScreen(screenHandlerFactory)
        }
        return ActionResult.SUCCESS
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }


    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(FACING)
    }


    override fun onPlaced(
        world: World?,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        super.onPlaced(world, pos, state, placer, itemStack)
        world!!.setBlockState(pos, world.getBlockState(pos).with(FACING, placer!!.horizontalFacing.opposite))
    }

    override fun rotate(state: BlockState?, rotation: BlockRotation?): BlockState {
        State.logger.error("Rotating quarries now")
        return (state!!.with(
            HorizontalFacingBlock.FACING, rotation!!.rotate(
                state.get(HorizontalFacingBlock.FACING)
            )
        ) as BlockState)
    }


    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState? {
        State.logger.error("Mirroring quarries now")
        return state.rotate(mirror.getRotation(state.get(HorizontalFacingBlock.FACING)))
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
            if (be is BasicQuarryEntity) {
                be.tick(world1!!)
            }
        }
    }

    override fun hasComparatorOutput(state: BlockState?): Boolean {
        return true
    }

    override fun getComparatorOutput(state: BlockState?, world: World, pos: BlockPos?): Int {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos))
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
        val QuarryItem = CraftableBlockItem.create(
            Quarry,
            CraftingObject("IRISCBIRI", 1)
                .add('I', "minecraft:iron_ingot")
                .add('R', "minecraft:redstone")
                .add('B', "datquarry:energy-core")
                .add('S', "datquarry:quarry-control-unit")
                .add('C', "datquarry:quarry-core"),
            1,
        )
        val Entity = FabricBlockEntityTypeBuilder.create(::BasicQuarryEntity, Quarry).build()!!

        fun create() {
            Registry.register(Registry.BLOCK, Identifier(State.modID, "quarry"), Quarry)
            Registry.register(Registry.ITEM, Identifier(State.modID, "quarry"), QuarryItem.item)
            Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                Identifier(State.modID, "quarry"),
                Entity
            )
            EnergyStorage.SIDED.registerForBlockEntity(
                { myBlockEntity, _ -> myBlockEntity.energyStorage },
                Entity
            )

            BlockFactory.registerBasicBlock(Quarry, QuarryItem, "Quarry")
        }
    }

    override fun generateBlockStateModel(blockStateModelGenerator: BlockStateModelGenerator) {
        val factory = TexturedModel.makeFactory({ block: Block? -> TextureMap.all(block) }, Models.ORIENTABLE)
        val texturedModel = factory.get(this)
        texturedModel.textures {
            it.put(TextureKey.TOP, Identifier("datquarry", "block/quarry-side"))
            it.put(TextureKey.SIDE, Identifier("datquarry", "block/quarry-side"))
        }
        blockStateModelGenerator.registerNorthDefaultHorizontalRotatable(this, texturedModel.textures)
    }
}