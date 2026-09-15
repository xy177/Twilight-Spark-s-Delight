package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class TSDPickledBrackenJarBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 8);
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 16, 13);

    public TSDPickledBrackenJarBlock(Properties properties) {
        super(properties.noOcclusion().sound(net.minecraft.world.level.block.SoundType.GLASS));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LEVEL, 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return SHAPE;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        int levelValue = state.getValue(LEVEL);
        if (levelValue >= 8) {
            if (!level.isClientSide) {
                level.removeBlock(pos, false);
                popResource(level, pos, TFItems.MASON_JAR.get().getDefaultInstance());
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GLASS_BREAK,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            ItemStack result = TSDItems.PICKLED_BRACKEN.get().getDefaultInstance();
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
            level.setBlock(pos, state.setValue(LEVEL, levelValue + 1), Block.UPDATE_ALL);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_FILL,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
