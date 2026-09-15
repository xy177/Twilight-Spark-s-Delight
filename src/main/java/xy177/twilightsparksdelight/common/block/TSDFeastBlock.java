package xy177.twilightsparksdelight.common.block;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import vectorwing.farmersdelight.common.registry.ModSounds;

/**
 * NeoForge implementation of a Farmer's Delight-style serving block.
 *
 * Farmer's Delight's modern FeastBlock has a fixed four-serving property.
 * Twilight Spark's Delight contains meals with six to eight servings, so
 * these blocks need their own state property and interaction implementation.
 */
public class TSDFeastBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 16);

    protected final Supplier<Item> servingItem;
    protected final int maxServings;
    protected final boolean hasLeftovers;
    private final VoxelShape[] shapes;

    public TSDFeastBlock(Properties properties, Supplier<Item> servingItem,
                         int maxServings, boolean hasLeftovers) {
        super(properties.noOcclusion().sound(net.minecraft.world.level.block.SoundType.WOOD));
        this.servingItem = servingItem;
        this.maxServings = Math.max(1, Math.min(16, maxServings));
        this.hasLeftovers = hasLeftovers;
        this.shapes = makeShapes(this.maxServings);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SERVINGS, this.maxServings));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SERVINGS);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(SERVINGS, maxServings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        return shapes[state.getValue(SERVINGS)];
    }

    @Override
    public InteractionResult use(BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        int servings = state.getValue(SERVINGS);
        if (servings == 0) {
            return useLeftovers(held, state, level, pos, player, hand, hit);
        }

        ItemStack meal = new ItemStack(servingItem.get());
        ItemStack requiredContainer = meal.getCraftingRemainingItem();
        if (requiredContainer.isEmpty() && requiresBowl(meal)) {
            requiredContainer = Items.BOWL.getDefaultInstance();
        }
        if (!requiredContainer.isEmpty()
                && !ItemStack.isSameItemSameTags(held, requiredContainer)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable(
                                "farmersdelight.block.feast.use_container",
                                requiredContainer.getHoverName()), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide) {
            if (!player.getAbilities().instabuild && !held.isEmpty()
                    && !requiredContainer.isEmpty()) {
                held.shrink(1);
            }
            give(player, meal);

            int next = servings - 1;
            if (next == 0 && !hasLeftovers) {
                level.destroyBlock(pos, false);
            } else {
                level.setBlock(pos, state.setValue(SERVINGS, next), Block.UPDATE_ALL);
            }
            level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected InteractionResult useLeftovers(ItemStack held, BlockState state, Level level,
                                                  BlockPos pos, Player player,
                                                  net.minecraft.world.InteractionHand hand,
                                                  BlockHitResult hit) {
        if (!level.isClientSide) {
            level.destroyBlock(pos, true);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.WOOD_BREAK,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 0.8F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(SERVINGS);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state,
                                    net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        return state.getValue(SERVINGS) == maxServings ? List.of(new ItemStack(asItem())) : List.of();
    }

    private static void give(Player player, ItemStack stack) {
        if (!stack.isEmpty() && !player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static boolean requiresBowl(ItemStack meal) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(meal.getItem());
        return id != null && (id.getPath().startsWith("bowl_of_")
                || id.getPath().contains("_soup")
                || id.getPath().contains("sauce"));
    }

    private static VoxelShape[] makeShapes(int max) {
        // SERVINGS is shared by all migrated feasts and covers 0..16. The
        // block-state cache is built for every declared value during startup.
        VoxelShape[] result = new VoxelShape[17];
        result[0] = Block.box(2, 0, 2, 14, 2, 14);
        for (int index = 1; index < result.length; index++) {
            double height = 2.0D + 12.0D * index / max;
            result[index] = Block.box(2, 0, 2, 14, height, 14);
        }
        return result;
    }
}
