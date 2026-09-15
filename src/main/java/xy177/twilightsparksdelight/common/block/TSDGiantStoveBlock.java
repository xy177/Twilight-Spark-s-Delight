package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import vectorwing.farmersdelight.common.block.StoveBlock;
import xy177.twilightsparksdelight.common.tile.TSDGiantStoveBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;

public final class TSDGiantStoveBlock extends StoveBlock implements TSDGiantKitchenController {
    private final Supplier<? extends Block> part;
    private final int structureSize;

    public TSDGiantStoveBlock(Properties properties, Supplier<? extends Block> part, int structureSize) {
        super(properties.noOcclusion().lightLevel(state -> state.getValue(LIT) ? 13 : 0));
        this.part = part;
        this.structureSize = Math.max(1, structureSize);
    }

    @Override
    public int getStructureSize() {
        return structureSize;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ItemInteractionResult useOnPart(ItemStack held, BlockState state, Level level, BlockPos pos,
                                            Player player, InteractionHand hand, BlockHitResult hit) {
        return useItemOn(held, state, level, pos, player, hand, hit);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null || !TSDGiantKitchenStructure.canPlace(level(context), context.getClickedPos(),
                TSDGiantKitchenStructure.controllerFacing(state), part, structureSize)) {
            return null;
        }
        return state;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TSDGiantStoveBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult tryToPlaceFoodItem(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof TSDGiantStoveBlockEntity stove) || stove.shouldDropItems()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        var recipe = stove.getCookingRecipe(held);
        if (recipe.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!level.isClientSide && stove.placeFood(player, player.isCreative() ? held.copy() : held, recipe.get())) {
            level.playSound(null, hit.getBlockPos(), net.minecraft.sounds.SoundEvents.LANTERN_PLACE,
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void ignite(net.minecraft.world.entity.Entity entity, net.minecraft.world.level.LevelAccessor level,
                       BlockPos pos, BlockState state) {
        super.ignite(entity, level, pos, state);
        if (level instanceof Level actual && !actual.isClientSide) {
            TSDGiantKitchenStructure.syncLit(actual, pos, actual.getBlockState(pos), part.get());
        }
    }

    @Override
    public void extinguish(net.minecraft.world.entity.Entity entity, net.minecraft.world.level.LevelAccessor level,
                           BlockPos pos, BlockState state) {
        super.extinguish(entity, level, pos, state);
        if (level instanceof Level actual && !actual.isClientSide) {
            TSDGiantKitchenStructure.syncLit(actual, pos, actual.getBlockState(pos), part.get());
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                    BlockEntityType<T> type) {
        if (level.isClientSide) {
            return state.getValue(LIT)
                    ? createTickerHelper(type, TSDBlockEntities.GIANT_STOVE.get(),
                            TSDGiantStoveBlockEntity::particleTick)
                    : null;
        }
        return createStoveTicker(level, type, TSDBlockEntities.GIANT_STOVE.get());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide) {
            TSDGiantKitchenStructure.placeParts(level, pos,
                    TSDGiantKitchenStructure.controllerFacing(state), part, structureSize);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            TSDGiantKitchenStructure.removeFromController(level, pos, state, part.get());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static Level level(BlockPlaceContext context) {
        return context.getLevel();
    }
}
