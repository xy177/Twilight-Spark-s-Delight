package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockStove;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GiantKitchenStructure
{
    private static final ThreadLocal<Boolean> REMOVING = ThreadLocal.withInitial(() -> false);

    private GiantKitchenStructure()
    {
    }

    public static EnumFacing getFacing(IBlockState state)
    {
        if (state.getBlock() instanceof BlockStove) {
            return state.getValue(BlockStove.FACING);
        }
        if (state.getBlock() instanceof BlockCookingPot) {
            return state.getValue(BlockCookingPot.FACING);
        }
        return EnumFacing.NORTH;
    }

    public static List<BlockPos> getStructurePositions(BlockPos controllerPos, EnumFacing facing)
    {
        return getStructurePositions(controllerPos, facing, 2);
    }

    public static List<BlockPos> getStructurePositions(BlockPos controllerPos, EnumFacing facing, int size)
    {
        int clampedSize = Math.max(1, size);
        List<BlockPos> positions = new ArrayList<>(clampedSize * clampedSize * clampedSize);
        for (int y = 0; y < clampedSize; y++) {
            for (int z = 0; z < clampedSize; z++) {
                for (int x = 0; x < clampedSize; x++) {
                    positions.add(controllerPos.add(rotateX(x, z, facing), y, rotateZ(x, z, facing)));
                }
            }
        }
        return positions;
    }

    public static void repairLegacyLayout(World world, BlockPos controllerPos, IBlockState controllerState)
    {
        if (world.isRemote || !(controllerState.getBlock() instanceof IGiantKitchenController)) {
            return;
        }
        Block controllerBlock = controllerState.getBlock();
        IGiantKitchenController controller = (IGiantKitchenController) controllerBlock;
        Block partBlock = controller.getStructurePartBlock();
        if (partBlock == null) {
            return;
        }

        EnumFacing facing = getFacing(controllerState);
        Set<BlockPos> currentPositions = new HashSet<>(getStructurePositions(controllerPos, facing, controller.getStructureSize()));
        if (controller.getStructureSize() == 2) {
            for (BlockPos legacyPos : getLegacyStructurePositions(controllerPos, facing)) {
                if (!currentPositions.contains(legacyPos)
                    && world.getBlockState(legacyPos).getBlock() == partBlock
                    && findController(world, legacyPos, controllerBlock) == null) {
                    world.setBlockToAir(legacyPos);
                }
            }
        }

        for (BlockPos currentPos : currentPositions) {
            if (currentPos.equals(controllerPos) || world.getBlockState(currentPos).getBlock() == partBlock) {
                continue;
            }
            IBlockState state = world.getBlockState(currentPos);
            if (state.getBlock().isReplaceable(world, currentPos)) {
                world.setBlockState(currentPos, partBlock.getDefaultState(), 3);
            }
        }
    }

    private static List<BlockPos> getLegacyStructurePositions(BlockPos controllerPos, EnumFacing facing)
    {
        List<BlockPos> positions = new ArrayList<>(8);
        for (int y = 0; y < 2; y++) {
            for (int z = 0; z < 2; z++) {
                for (int x = 0; x < 2; x++) {
                    int rotatedX;
                    int rotatedZ;
                    switch (facing) {
                        case EAST:
                            rotatedX = z;
                            rotatedZ = -x;
                            break;
                        case SOUTH:
                            rotatedX = -x;
                            rotatedZ = -z;
                            break;
                        case WEST:
                            rotatedX = -z;
                            rotatedZ = x;
                            break;
                        default:
                            rotatedX = x;
                            rotatedZ = z;
                            break;
                    }
                    positions.add(controllerPos.add(rotatedX, y, rotatedZ));
                }
            }
        }
        return positions;
    }

    public static BlockPos findController(IBlockAccess world, BlockPos partPos, Block controllerBlock)
    {
        int size = getStructureSize(controllerBlock);
        int radius = size - 1;
        for (int y = -radius; y <= 0; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos candidate = partPos.add(x, y, z);
                    IBlockState state = world.getBlockState(candidate);
                    if (state.getBlock() == controllerBlock
                        && getStructurePositions(candidate, getFacing(state), size).contains(partPos)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    public static boolean canPlaceParts(
        World world,
        BlockPos controllerPos,
        IBlockState controllerState,
        Block partBlock,
        EnumFacing placedAgainst,
        EntityPlayer player
    ) {
        int size = getStructureSize(controllerState.getBlock());
        for (BlockPos structurePos : getStructurePositions(controllerPos, getFacing(controllerState), size)) {
            if (!structurePos.equals(controllerPos)
                && !world.mayPlace(partBlock, structurePos, false, placedAgainst, player)) {
                return false;
            }
        }
        return true;
    }

    public static List<BlockPos> placeParts(
        World world,
        BlockPos controllerPos,
        IBlockState controllerState,
        Block partBlock
    ) {
        int size = getStructureSize(controllerState.getBlock());
        List<BlockPos> placed = new ArrayList<>(size * size * size - 1);
        for (BlockPos structurePos : getStructurePositions(controllerPos, getFacing(controllerState), size)) {
            if (structurePos.equals(controllerPos)) {
                continue;
            }
            if (!world.setBlockState(structurePos, partBlock.getDefaultState(), 2)) {
                removePlacedParts(world, placed, partBlock);
                return new ArrayList<>();
            }
            placed.add(structurePos);
        }
        return placed;
    }

    public static void finishPlacement(World world, List<BlockPos> partPositions, Block partBlock)
    {
        for (BlockPos partPos : partPositions) {
            world.notifyNeighborsOfStateChange(partPos, partBlock, false);
        }
    }

    public static void removePlacedParts(World world, List<BlockPos> positions, Block partBlock)
    {
        boolean previous = REMOVING.get();
        REMOVING.set(true);
        try {
            for (BlockPos position : positions) {
                if (world.getBlockState(position).getBlock() == partBlock) {
                    world.setBlockToAir(position);
                }
            }
        } finally {
            REMOVING.set(previous);
        }
    }

    public static void removePartsFromController(
        World world,
        BlockPos controllerPos,
        EnumFacing facing,
        Block partBlock,
        int size
    ) {
        if (REMOVING.get()) {
            return;
        }
        boolean previous = REMOVING.get();
        REMOVING.set(true);
        try {
            for (BlockPos structurePos : getStructurePositions(controllerPos, facing, size)) {
                if (!structurePos.equals(controllerPos) && world.getBlockState(structurePos).getBlock() == partBlock) {
                    world.setBlockToAir(structurePos);
                }
            }
        } finally {
            REMOVING.set(previous);
        }
    }

    public static void destroyControllerFromPart(
        World world,
        BlockPos partPos,
        Block controllerBlock,
        Block partBlock,
        boolean creativeHarvest
    ) {
        BlockPos controllerPos = findController(world, partPos, controllerBlock);
        if (controllerPos == null) {
            return;
        }
        IBlockState controllerState = world.getBlockState(controllerPos);
        boolean previous = REMOVING.get();
        REMOVING.set(true);
        try {
            int size = getStructureSize(controllerBlock);
            for (BlockPos structurePos : getStructurePositions(controllerPos, getFacing(controllerState), size)) {
                if (!structurePos.equals(controllerPos)
                    && !structurePos.equals(partPos)
                    && world.getBlockState(structurePos).getBlock() == partBlock) {
                    world.setBlockToAir(structurePos);
                }
            }
            world.destroyBlock(controllerPos, !creativeHarvest);
        } finally {
            REMOVING.set(previous);
        }
    }

    public static boolean isRemoving()
    {
        return REMOVING.get();
    }

    public static AxisAlignedBB getScaledCookingPotCollision(
        BlockPos controllerPos,
        BlockPos queriedPos,
        EnumFacing facing,
        int size
    ) {
        List<BlockPos> positions = getStructurePositions(controllerPos, facing, size);
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos position : positions) {
            minX = Math.min(minX, position.getX());
            maxX = Math.max(maxX, position.getX());
            minZ = Math.min(minZ, position.getZ());
            maxZ = Math.max(maxZ, position.getZ());
        }

        int yOffset = queriedPos.getY() - controllerPos.getY();
        double bodyHeight = size * 0.625D;
        if (yOffset < 0 || yOffset >= size || yOffset >= bodyHeight) {
            return Block.NULL_AABB;
        }
        double edgeInset = size * 0.125D;
        double minLocalX = queriedPos.getX() == minX ? edgeInset : 0.0D;
        double maxLocalX = queriedPos.getX() == maxX ? 1.0D - edgeInset : 1.0D;
        double minLocalZ = queriedPos.getZ() == minZ ? edgeInset : 0.0D;
        double maxLocalZ = queriedPos.getZ() == maxZ ? 1.0D - edgeInset : 1.0D;
        double maxLocalY = Math.min(1.0D, bodyHeight - yOffset);
        return new AxisAlignedBB(minLocalX, 0.0D, minLocalZ, maxLocalX, maxLocalY, maxLocalZ);
    }

    public static double getCenterX(BlockPos controllerPos, EnumFacing facing)
    {
        return getCenterX(controllerPos, facing, 2);
    }

    public static double getCenterX(BlockPos controllerPos, EnumFacing facing, int size)
    {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (BlockPos position : getStructurePositions(controllerPos, facing, size)) {
            min = Math.min(min, position.getX());
            max = Math.max(max, position.getX());
        }
        return (min + max + 1) / 2.0D;
    }

    public static double getCenterZ(BlockPos controllerPos, EnumFacing facing)
    {
        return getCenterZ(controllerPos, facing, 2);
    }

    public static double getCenterZ(BlockPos controllerPos, EnumFacing facing, int size)
    {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (BlockPos position : getStructurePositions(controllerPos, facing, size)) {
            min = Math.min(min, position.getZ());
            max = Math.max(max, position.getZ());
        }
        return (min + max + 1) / 2.0D;
    }

    public static int getStructureSize(Block block)
    {
        return block instanceof IGiantKitchenController
            ? Math.max(1, ((IGiantKitchenController) block).getStructureSize())
            : 2;
    }

    private static int rotateX(int x, int z, EnumFacing facing)
    {
        switch (facing) {
            case EAST:
                return -z;
            case SOUTH:
                return -x;
            case WEST:
                return z;
            default:
                return x;
        }
    }

    private static int rotateZ(int x, int z, EnumFacing facing)
    {
        switch (facing) {
            case EAST:
                return x;
            case SOUTH:
                return -z;
            case WEST:
                return -x;
            default:
                return z;
        }
    }
}
