package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.SoundType;
import vectorwing.farmersdelight.common.registry.ModSounds;

/**
 * Shared five-stage crop colony used by the two legacy colony ingredients.
 */
public class TSDColonyBlock extends Block implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    private static final VoxelShape[] SHAPES = {
            box(5, 0, 5, 11, 6, 11),
            box(4, 0, 4, 12, 8, 12),
            box(3, 0, 3, 13, 10, 13),
            box(2, 0, 2, 14, 12, 14),
            box(1, 0, 1, 15, 14, 15)
    };

    private final Supplier<Item> harvestItem;
    public TSDColonyBlock(BlockBehaviour.Properties properties, Supplier<Item> harvestItem,
                          Supplier<Item> ignoredColonyItem) {
        super(properties.sound(SoundType.GRASS).noCollission().randomTicks());
        this.harvestItem = harvestItem;
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(AGE)];
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return new ResourceLocation("farmersdelight", "rich_soil").equals(id);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
    }

    @Override
    public BlockState updateShape(BlockState state, net.minecraft.core.Direction direction, BlockState neighbor,
                                   LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return canSurvive(state, level, pos)
                ? super.updateShape(state, direction, neighbor, level, pos, neighborPos)
                : net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(AGE) < 4 && canSurvive(state, level, pos) && random.nextInt(4) == 0) {
            level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), 2);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                           net.minecraft.world.entity.player.Player player,
                                           net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS) && state.getValue(AGE) > 0) {
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(harvestItem.get()));
                level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) - 1), 2);
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(hand));
                }
            }
            level.playSound(player, pos, net.minecraft.sounds.SoundEvents.SHEEP_SHEAR,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean clientSide) {
        return state.getValue(AGE) < 4;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(AGE, Math.min(4, state.getValue(AGE) + 1 + random.nextInt(2))), 2);
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        var tool = params.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL);
        if (state.getValue(AGE) == 4 && tool != null && tool.is(Items.SHEARS)) {
            return java.util.List.of(new ItemStack(asItem()));
        }
        return java.util.List.of(new ItemStack(harvestItem.get(), state.getValue(AGE) + 1));
    }
}
