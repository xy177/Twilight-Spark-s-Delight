package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class TSDStageFeastBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 28);
    private final IntegerProperty stage;
    protected final int maxStage;
    protected final Supplier<Item> servingItem;
    protected final boolean requiresBowl;
    public TSDStageFeastBlock(BlockBehaviour.Properties properties, int maxStage,
                              Supplier<Item> servingItem, boolean requiresBowl, int footprint) {
        super(properties.noOcclusion().sound(net.minecraft.world.level.block.SoundType.WOOD));
        this.stage = getStateProperty();
        this.maxStage = maxStage;
        this.servingItem = servingItem;
        this.requiresBowl = requiresBowl;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(stage, 0));
    }

    public IntegerProperty stageProperty() {
        return stage;
    }

    protected int currentStage(BlockState state) {
        return state.getValue(stage);
    }

    protected Item servingItem() {
        return servingItem.get();
    }

    protected void setStage(Level level, BlockPos pos, BlockState state, int nextStage) {
        int clamped = Math.max(0, Math.min(maxStage, nextStage));
        if (clamped >= maxStage) {
            level.destroyBlock(pos, false);
        } else {
            level.setBlock(pos, state.setValue(stage, clamped), Block.UPDATE_ALL);
        }
    }

    protected IntegerProperty getStateProperty() {
        return STAGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, getStateProperty());
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(stage, 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
                               net.minecraft.core.BlockPos pos, CollisionContext context) {
        // Large feasts provide one collision cell per structure part.
        return super.getShape(state, level, pos, context);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                           net.minecraft.core.BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        int currentStage = state.getValue(stage);
        if (currentStage >= maxStage) {
            if (!level.isClientSide) {
                level.destroyBlock(pos, true);
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.WOOD_BREAK,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 0.8F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (isKnife(held)) {
            if (!level.isClientSide) {
                giveItem(player, new ItemStack(servingItem.get()));
                if (!player.getAbilities().instabuild) {
                    held.hurtAndBreak(1, player, hand == net.minecraft.world.InteractionHand.MAIN_HAND
                            ? net.minecraft.world.entity.EquipmentSlot.MAINHAND
                            : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                }
                advance(level, pos, state);
                level.playSound(null, pos, ModSounds.BLOCK_FOOD_SLICE.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (requiresBowl && !held.is(net.minecraft.world.item.Items.BOWL)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (servingItem.get() == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            ItemStack result = new ItemStack(servingItem.get());
            if (!held.isEmpty() && !player.getAbilities().instabuild) {
                held.shrink(1);
            }
            giveItem(player, result);
            advance(level, pos, state);
            level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private void advance(Level level, net.minecraft.core.BlockPos pos, BlockState state) {
        int nextStage = state.getValue(stage) + 1;
        if (nextStage >= maxStage) {
            level.destroyBlock(pos, false);
        } else {
            level.setBlock(pos, state.setValue(stage, nextStage), 3);
        }
    }

    private static void giveItem(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static boolean isKnife(ItemStack stack) {
        return xy177.twilightsparksdelight.common.event.TSDLootEvents.isKnife(stack);
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state,
            net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        return currentStage(state) == 0 ? java.util.List.of(new ItemStack(asItem())) : java.util.List.of();
    }

    @Override
    public void onRemove(BlockState state, Level level, net.minecraft.core.BlockPos pos,
                          BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
