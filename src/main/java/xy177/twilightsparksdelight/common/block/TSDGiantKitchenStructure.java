package xy177.twilightsparksdelight.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.AABB;

public final class TSDGiantKitchenStructure {
    private static final ThreadLocal<Boolean> REMOVING = ThreadLocal.withInitial(() -> false);

    private TSDGiantKitchenStructure() {
    }

    public static List<BlockPos> positions(BlockPos controller, Direction facing) {
        return positions(controller, facing, 2);
    }

    public static List<BlockPos> positions(BlockPos controller, Direction facing, int size) {
        int actualSize = Math.max(1, size);
        List<BlockPos> result = new ArrayList<>(actualSize * actualSize * actualSize);
        for (int y = 0; y < actualSize; y++) {
            for (int z = 0; z < actualSize; z++) {
                for (int x = 0; x < actualSize; x++) {
                    int dx;
                    int dz;
                    switch (facing) {
                        case EAST -> {
                            dx = -z;
                            dz = x;
                        }
                        case SOUTH -> {
                            dx = -x;
                            dz = -z;
                        }
                        case WEST -> {
                            dx = z;
                            dz = -x;
                        }
                        default -> {
                            dx = x;
                            dz = z;
                        }
                    }
                    result.add(controller.offset(dx, y, dz));
                }
            }
        }
        return result;
    }

    public static boolean canPlace(Level level, BlockPos controller, Direction facing,
                                   Supplier<? extends Block> part, int size) {
        for (BlockPos pos : positions(controller, facing, size)) {
            if (pos.equals(controller)) {
                continue;
            }
            if (!level.getBlockState(pos).canBeReplaced()) {
                return false;
            }
        }
        return true;
    }

    public static boolean placeParts(Level level, BlockPos controller, Direction facing,
                                     Supplier<? extends Block> part, int size) {
        List<BlockPos> placed = new ArrayList<>();
        for (BlockPos pos : positions(controller, facing, size)) {
            if (pos.equals(controller)) {
                continue;
            }
            BlockState partState = part.get().defaultBlockState();
            if (partState.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)) {
                partState = partState.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT,
                        level.getBlockState(controller).getOptionalValue(
                                net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT).orElse(false));
            }
            if (!level.setBlock(pos, partState, Block.UPDATE_CLIENTS)) {
                removeParts(level, placed, part.get());
                return false;
            }
            placed.add(pos);
        }
        return true;
    }

    private static void removeParts(Level level, List<BlockPos> positions, Block part) {
        for (BlockPos pos : positions) {
            if (level.getBlockState(pos).is(part)) {
                level.removeBlock(pos, false);
            }
        }
    }

    public static BlockPos findController(Level level, BlockPos partPos, Block controller) {
        int size = controller instanceof TSDGiantKitchenController kitchen
                ? kitchen.getStructureSize() : 2;
        for (int y = 0; y > -size; y--) {
            for (int x = -size + 1; x < size; x++) {
                for (int z = -size + 1; z < size; z++) {
                    BlockPos candidate = partPos.offset(x, y, z);
                    BlockState state = level.getBlockState(candidate);
                    if (state.is(controller)
                            && positions(candidate, controllerFacing(state), size).contains(partPos)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    public static Direction controllerFacing(BlockState state) {
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        }
        return Direction.NORTH;
    }

    public static AABB bounds(BlockPos controller, BlockState state) {
        int size = state.getBlock() instanceof TSDGiantKitchenController kitchen
                ? kitchen.getStructureSize() : 2;
        AABB bounds = new AABB(controller);
        for (BlockPos pos : positions(controller, controllerFacing(state), size)) {
            bounds = bounds.minmax(new AABB(pos));
        }
        return bounds;
    }

    public static void syncLit(Level level, BlockPos controller, BlockState state, Block part) {
        boolean lit = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);
        int size = ((TSDGiantKitchenController) state.getBlock()).getStructureSize();
        for (BlockPos pos : positions(controller, controllerFacing(state), size)) {
            BlockState current = level.getBlockState(pos);
            if (current.is(part)) {
                level.setBlock(pos, current.setValue(
                        net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT, lit), Block.UPDATE_ALL);
            }
        }
    }

    /**
     * Returns the per-block portion of a scaled cooking-pot collision shape.
     * The rendered 2x/4x controller model spans the complete structure, while
     * each invisible part must expose only the portion occupying its own block.
     */
    public static VoxelShape scaledCookingPotShape(Level level, BlockPos partPos,
                                                   Block controller, Block part) {
        BlockPos controllerPos = findController(level, partPos, controller);
        if (controllerPos == null || !(level.getBlockState(controllerPos).getBlock()
                instanceof TSDGiantKitchenController kitchen)) {
            return Shapes.empty();
        }
        BlockState controllerState = level.getBlockState(controllerPos);
        int size = kitchen.getStructureSize();
        List<BlockPos> structure = positions(controllerPos, controllerFacing(controllerState), size);
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos position : structure) {
            minX = Math.min(minX, position.getX());
            maxX = Math.max(maxX, position.getX());
            minZ = Math.min(minZ, position.getZ());
            maxZ = Math.max(maxZ, position.getZ());
        }

        int yOffset = partPos.getY() - controllerPos.getY();
        double bodyHeight = size * 0.625D;
        if (yOffset < 0 || yOffset >= size || yOffset >= bodyHeight) {
            return Shapes.empty();
        }
        double edgeInset = size * 0.125D;
        double minLocalX = partPos.getX() == minX ? edgeInset : 0.0D;
        double maxLocalX = partPos.getX() == maxX ? 1.0D - edgeInset : 1.0D;
        double minLocalZ = partPos.getZ() == minZ ? edgeInset : 0.0D;
        double maxLocalZ = partPos.getZ() == maxZ ? 1.0D : 1.0D;
        if (partPos.getZ() == maxZ) {
            minLocalZ = 0.0D;
            maxLocalZ = 1.0D - edgeInset;
        }
        double maxLocalY = Math.min(1.0D, bodyHeight - yOffset);
        return Shapes.box(minLocalX, 0.0D, minLocalZ, maxLocalX, maxLocalY, maxLocalZ);
    }

    public static void removeFromController(Level level, BlockPos controller, BlockState state,
                                            Block part) {
        if (REMOVING.get()) {
            return;
        }
        int size = state.getBlock() instanceof TSDGiantKitchenController kitchen
                ? kitchen.getStructureSize() : 2;
        boolean previous = REMOVING.get();
        REMOVING.set(true);
        try {
            for (BlockPos pos : positions(controller, controllerFacing(state), size)) {
                if (!pos.equals(controller) && level.getBlockState(pos).is(part)) {
                    level.removeBlock(pos, false);
                }
            }
        } finally {
            REMOVING.set(previous);
        }
    }

    public static void removeControllerFromPart(Level level, BlockPos partPos, Block controller,
                                                Block part) {
        if (REMOVING.get()) {
            return;
        }
        BlockPos controllerPos = findController(level, partPos, controller);
        if (controllerPos == null) {
            return;
        }
        int size = controller instanceof TSDGiantKitchenController kitchen
                ? kitchen.getStructureSize() : 2;
        boolean previous = REMOVING.get();
        REMOVING.set(true);
        try {
            BlockState controllerState = level.getBlockState(controllerPos);
            for (BlockPos pos : positions(controllerPos, controllerFacing(controllerState), size)) {
                if (!pos.equals(partPos) && !pos.equals(controllerPos)
                        && level.getBlockState(pos).is(part)) {
                    level.removeBlock(pos, false);
                }
            }
            level.removeBlock(controllerPos, true);
        } finally {
            REMOVING.set(previous);
        }
    }

    public static boolean isRemoving() {
        return REMOVING.get();
    }
}
