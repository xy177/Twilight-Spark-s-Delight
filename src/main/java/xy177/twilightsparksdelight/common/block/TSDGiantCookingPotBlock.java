package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import xy177.twilightsparksdelight.common.tile.TSDGiantCookingPotBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;

public final class TSDGiantCookingPotBlock extends CookingPotBlock implements TSDGiantKitchenController {
    private final Supplier<? extends Block> part;
    private final int structureSize;

    public TSDGiantCookingPotBlock(Properties properties, Supplier<? extends Block> part, int structureSize) {
        super(properties.noOcclusion());
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
    public InteractionResult useOnPart(ItemStack held, BlockState state, Level level, BlockPos pos,
                                            Player player, InteractionHand hand, BlockHitResult hit) {
        return use(state, level, pos, player, hand, hit);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && structureSize == 4) {
            var stove = xy177.twilightsparksdelight.registry.TSDBlocks.GIANTS_STOVE.get();
            BlockPos support = TSDGiantKitchenStructure.findController(
                    context.getLevel(), context.getClickedPos().below(), stove);
            if (support != null) {
                state = state.setValue(FACING, TSDGiantKitchenStructure.controllerFacing(
                        context.getLevel().getBlockState(support)));
            }
        }
        if (state == null || !TSDGiantKitchenStructure.canPlace(context.getLevel(), context.getClickedPos(),
                TSDGiantKitchenStructure.controllerFacing(state), part, structureSize)) {
            return null;
        }
        return state;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TSDGiantCookingPotBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        if (level instanceof Level actualLevel) {
            return TSDGiantKitchenStructure.scaledCookingPotShape(actualLevel, pos, this, part.get());
        }
        return super.getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        if (level instanceof Level actualLevel) {
            return TSDGiantKitchenStructure.scaledCookingPotShape(actualLevel, pos, this, part.get());
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                    BlockEntityType<T> type) {
        return createTickerHelper(type, TSDBlockEntities.GIANT_COOKING_POT.get(),
                level.isClientSide
                        ? TSDGiantCookingPotBlockEntity::animationTick
                        : TSDGiantCookingPotBlockEntity::cookingTick);
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
}
