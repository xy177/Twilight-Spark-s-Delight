package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Invisible structure cell for large placeable foods.
 *
 * The controller owns the visible model and serving state. Parts only provide
 * the remaining footprint and forward interactions to that controller.
 */
public final class TSDStructurePartBlock extends Block {
    private final Supplier<? extends TSDLargeStageFeastBlock> controller;

    public TSDStructurePartBlock(Properties properties,
                                 Supplier<? extends TSDLargeStageFeastBlock> controller) {
        super(properties.noOcclusion());
        this.controller = controller;
        registerDefaultState(stateDefinition.any().setValue(TSDStageFeastBlock.FACING, net.minecraft.core.Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(
            net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TSDStageFeastBlock.FACING);
    }

    @Override
    public net.minecraft.world.level.material.PushReaction getPistonPushReaction(BlockState state) {
        return net.minecraft.world.level.material.PushReaction.BLOCK;
    }

    @Override
    public net.minecraft.world.level.block.RenderShape getRenderShape(BlockState state) {
        return net.minecraft.world.level.block.RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        if (controller != null && controller.get() instanceof TSDTwilightBoarKnuckleBlock) {
            return TSDTwilightBoarKnuckleBlock.cellShape(state.getValue(TSDStageFeastBlock.FACING));
        }
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        TSDLargeStageFeastBlock feast = controller.get();
        BlockPos controllerPos = feast.findController(level, pos);
        if (controllerPos == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return feast.useItemOn(stack, level.getBlockState(controllerPos), level,
                controllerPos, player, hand, hit);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        TSDLargeStageFeastBlock feast = controller.get();
        feast.destroyFromPart(level, pos, player);
        return state;
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state,
            net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        return java.util.List.of();
    }
}
