package xy177.twilightsparksdelight.common.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stage feast controller with a rectangular footprint.
 */
public class TSDLargeStageFeastBlock extends TSDStageFeastBlock implements net.minecraft.world.level.block.EntityBlock {
    private final int width;
    private final int depth;
    private final Supplier<TSDStructurePartBlock> part;

    public TSDLargeStageFeastBlock(Properties properties, String stateName, int maxStage,
                                   Supplier<Item> servingItem, boolean requiresBowl,
                                   int width, int depth,
                                   Supplier<TSDStructurePartBlock> part) {
        super(properties, maxStage, servingItem, requiresBowl, 1);
        this.width = width;
        this.depth = depth;
        this.part = part;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof xy177.twilightsparksdelight.common.tile.TSDLargeFeastBlockEntity feast) {
            feast.readItem(stack);
        }
        placeParts(level, pos, state);
    }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new xy177.twilightsparksdelight.common.tile.TSDLargeFeastBlockEntity(pos, state);
    }

    @Override
    public net.minecraft.world.level.block.RenderShape getRenderShape(BlockState state) {
        return net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public net.minecraft.world.phys.AABB structureBounds(BlockPos pos, BlockState state) {
        var bounds = new net.minecraft.world.phys.AABB(pos);
        for (BlockPos cell : footprint(pos, state)) {
            bounds = bounds.minmax(new net.minecraft.world.phys.AABB(cell));
        }
        return bounds;
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state,
            net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        if (currentStage(state) != 0) return finalDrops();
        ItemStack result = new ItemStack(asItem());
        var entity = params.getOptionalParameter(
                net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        if (entity instanceof xy177.twilightsparksdelight.common.tile.TSDLargeFeastBlockEntity feast) {
            feast.writeItem(result, true);
        }
        return java.util.List.of(result);
    }

    protected java.util.List<ItemStack> finalDrops() {
        return java.util.List.of();
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        for (BlockPos partPos : footprint(context.getClickedPos(), state)) {
            if (!partPos.equals(context.getClickedPos())
                    && !context.getLevel().getBlockState(partPos).canBeReplaced()) {
                return null;
            }
        }
        return state;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state,
                                        net.minecraft.world.entity.player.Player player) {
        removeStructure(level, pos, false);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                          BlockState newState, boolean movedByPiston) {
        if (newState.getBlock() != this) {
            removePartsForState(level, pos, state);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private void removePartsForState(Level level, BlockPos controllerPos, BlockState controllerState) {
        for (BlockPos partPos : footprint(controllerPos, controllerState)) {
            if (!partPos.equals(controllerPos) && level.getBlockState(partPos).is(part.get())) {
                level.removeBlock(partPos, false);
            }
        }
    }

    public BlockPos findController(Level level, BlockPos origin) {
        int radius = Math.max(width, depth);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos candidate = origin.offset(dx, 0, dz);
                if (level.getBlockState(candidate).is(this)
                        && contains(level, candidate, origin)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    public void removeStructure(Level level, BlockPos controllerPos, boolean dropController) {
        BlockState controllerState = level.getBlockState(controllerPos);
        if (!controllerState.is(this)) {
            return;
        }
        if (dropController) {
            popResource(level, controllerPos, new ItemStack(asItem()));
        }
        for (BlockPos partPos : footprint(controllerPos, controllerState)) {
            if (!partPos.equals(controllerPos)
                    && level.getBlockState(partPos).is(part.get())) {
                level.removeBlock(partPos, false);
            }
        }
    }

    public void destroyFromPart(Level level, BlockPos partPos, net.minecraft.world.entity.player.Player player) {
        BlockPos controllerPos = findController(level, partPos);
        if (controllerPos == null) {
            level.removeBlock(partPos, false);
            return;
        }
        BlockState controllerState = level.getBlockState(controllerPos);
        for (BlockPos other : footprint(controllerPos, controllerState)) {
            if (!other.equals(controllerPos) && !other.equals(partPos)
                    && level.getBlockState(other).is(part.get())) {
                level.removeBlock(other, false);
            }
        }
        level.destroyBlock(controllerPos, !player.getAbilities().instabuild);
    }

    private void placeParts(Level level, BlockPos controllerPos, BlockState state) {
        for (BlockPos partPos : footprint(controllerPos, state)) {
            if (!partPos.equals(controllerPos)
                    && level.getBlockState(partPos).canBeReplaced()) {
                level.setBlock(partPos, part.get().defaultBlockState().setValue(FACING, state.getValue(FACING)), UPDATE_ALL);
            }
        }
    }

    private boolean contains(Level level, BlockPos controllerPos, BlockPos target) {
        for (BlockPos pos : footprint(controllerPos, level.getBlockState(controllerPos))) {
            if (pos.equals(target)) {
                return true;
            }
        }
        return false;
    }

    protected java.util.List<BlockPos> footprint(BlockPos controllerPos, BlockState state) {
        Direction facing = state.getValue(FACING);
        Direction away = facing.getOpposite();
        Direction side = facing.getClockWise();
        java.util.ArrayList<BlockPos> positions = new java.util.ArrayList<>(width * depth);
        int sideStart = width % 2 == 0 ? 0 : -(width / 2);
        int depthStart = 0;
        for (int d = 0; d < depth; d++) {
            for (int w = 0; w < width; w++) {
                positions.add(controllerPos.relative(side, sideStart + w).relative(away, depthStart + d));
            }
        }
        return positions;
    }
}
