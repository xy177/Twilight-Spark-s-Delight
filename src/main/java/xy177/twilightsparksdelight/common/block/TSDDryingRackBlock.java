package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xy177.twilightsparksdelight.common.tile.TSDDryingRackBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;

public final class TSDDryingRackBlock extends Block implements EntityBlock {
    public TSDDryingRackBlock(Properties properties) {
        super(properties.noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,
                context.getClickedFace().getAxis().isHorizontal() ? context.getClickedFace()
                        : context.getHorizontalDirection().getOpposite());
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH -> box(0, 12, 12, 16, 16, 16);
            case SOUTH -> box(0, 12, 0, 16, 16, 4);
            case WEST -> box(12, 12, 0, 16, 16, 16);
            default -> box(0, 12, 0, 4, 16, 16);
        };
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TSDDryingRackBlockEntity(pos, state);
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide && type == TSDBlockEntities.DRYING_RACK.get()
                ? (world, pos, block, entity) -> TSDDryingRackBlockEntity.tick(world, pos, block, (TSDDryingRackBlockEntity) entity)
                : null;
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof TSDDryingRackBlockEntity rack)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (rack.content().isEmpty()) {
            ItemStack held = player.getItemInHand(hand);
            ItemStack remainder = rack.insert(held);
            if (remainder.getCount() == held.getCount()) return InteractionResult.PASS;
            if (!player.getAbilities().instabuild) player.setItemInHand(hand, remainder);
        } else {
            ItemStack stored = rack.extract();
            if (player.getItemInHand(hand).isEmpty()) player.setItemInHand(hand, stored);
            else if (!player.addItem(stored)) player.drop(stored, false);
        }
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_PICKUP,
                net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
        return InteractionResult.CONSUME;
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof TSDDryingRackBlockEntity rack) {
            popResource(level, pos, rack.content());
        }
        super.onRemove(state, level, pos, next, moving);
    }
}
