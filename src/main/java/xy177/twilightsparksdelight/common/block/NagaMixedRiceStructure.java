package xy177.twilightsparksdelight.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class NagaMixedRiceStructure
{
    private static final ThreadLocal<Boolean> REMOVING = new ThreadLocal<>();

    private NagaMixedRiceStructure()
    {
    }

    public static List<BlockPos> getStructurePositions(BlockPos controllerPos, EnumFacing facing)
    {
        List<BlockPos> positions = new ArrayList<>(9);
        for (int row = 0; row < 3; row++) {
            for (int column = -1; column <= 1; column++) {
                int x;
                int z;
                switch (facing) {
                    case SOUTH:
                        x = column;
                        z = -row;
                        break;
                    case EAST:
                        x = -row;
                        z = column;
                        break;
                    case WEST:
                        x = row;
                        z = -column;
                        break;
                    default:
                        x = column;
                        z = row;
                        break;
                }
                positions.add(controllerPos.add(x, 0, z));
            }
        }
        return positions;
    }

    public static void repairLegacyLayout(
        World world,
        BlockPos controllerPos,
        IBlockState controllerState,
        Block partBlock
    ) {
        if (world.isRemote || partBlock == null) {
            return;
        }
        EnumFacing facing = controllerState.getValue(BlockNagaMixedRice.FACING);
        if (facing != EnumFacing.EAST && facing != EnumFacing.WEST) {
            return;
        }

        Set<BlockPos> currentPositions = new HashSet<>(getStructurePositions(controllerPos, facing));
        List<BlockPos> missingPositions = new ArrayList<>();
        for (BlockPos position : currentPositions) {
            if (position.equals(controllerPos) || world.getBlockState(position).getBlock() == partBlock) {
                continue;
            }
            if (!world.getBlockState(position).getBlock().isReplaceable(world, position)) {
                return;
            }
            missingPositions.add(position);
        }

        boolean previous = isRemoving();
        REMOVING.set(true);
        try {
            for (BlockPos position : missingPositions) {
                world.setBlockState(position, partBlock.getDefaultState(), 3);
            }
            for (BlockPos legacyPosition : getLegacyStructurePositions(controllerPos, facing)) {
                if (!currentPositions.contains(legacyPosition)
                    && world.getBlockState(legacyPosition).getBlock() == partBlock
                    && findController(world, legacyPosition, controllerState.getBlock()) == null) {
                    world.setBlockToAir(legacyPosition);
                }
            }
        } finally {
            REMOVING.set(previous);
        }
    }

    private static List<BlockPos> getLegacyStructurePositions(BlockPos controllerPos, EnumFacing facing)
    {
        List<BlockPos> positions = new ArrayList<>(9);
        for (int row = 0; row < 3; row++) {
            for (int column = -1; column <= 1; column++) {
                int x = facing == EnumFacing.EAST ? row : -row;
                positions.add(controllerPos.add(x, 0, column));
            }
        }
        return positions;
    }

    public static BlockPos findController(IBlockAccess world, BlockPos partPos, Block controllerBlock)
    {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos candidate = partPos.add(x, 0, z);
                IBlockState candidateState = world.getBlockState(candidate);
                if (candidateState.getBlock() != controllerBlock) {
                    continue;
                }
                EnumFacing facing = candidateState.getValue(BlockNagaMixedRice.FACING);
                if (getStructurePositions(candidate, facing).contains(partPos)) {
                    return candidate;
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
        for (BlockPos structurePos : getStructurePositions(controllerPos, controllerState.getValue(BlockNagaMixedRice.FACING))) {
            if (!structurePos.equals(controllerPos)
                && !world.mayPlace(partBlock, structurePos, false, placedAgainst, player)) {
                return false;
            }
        }
        return true;
    }

    public static List<BlockPos> placeParts(World world, BlockPos controllerPos, IBlockState controllerState, Block partBlock)
    {
        List<BlockPos> placed = new ArrayList<>(8);
        for (BlockPos structurePos : getStructurePositions(controllerPos, controllerState.getValue(BlockNagaMixedRice.FACING))) {
            if (structurePos.equals(controllerPos)) {
                continue;
            }
            if (!world.setBlockState(structurePos, partBlock.getDefaultState(), 2)) {
                removePlacedParts(world, placed, partBlock);
                return Collections.emptyList();
            }
            placed.add(structurePos);
        }
        return placed;
    }

    public static void removePlacedParts(World world, List<BlockPos> positions, Block partBlock)
    {
        boolean previous = isRemoving();
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

    public static void finishPlacement(World world, List<BlockPos> positions, Block partBlock)
    {
        for (BlockPos position : positions) {
            world.notifyNeighborsOfStateChange(position, partBlock, false);
        }
    }

    public static void removePartsFromController(World world, BlockPos controllerPos, IBlockState state, Block partBlock)
    {
        if (partBlock == null || isRemoving()) {
            return;
        }
        boolean previous = isRemoving();
        REMOVING.set(true);
        try {
            for (BlockPos position : getStructurePositions(controllerPos, state.getValue(BlockNagaMixedRice.FACING))) {
                if (!position.equals(controllerPos) && world.getBlockState(position).getBlock() == partBlock) {
                    world.setBlockToAir(position);
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
        if (isRemoving()) {
            return;
        }
        BlockPos controllerPos = findController(world, partPos, controllerBlock);
        if (controllerPos == null) {
            return;
        }
        IBlockState controllerState = world.getBlockState(controllerPos);
        boolean previous = isRemoving();
        REMOVING.set(true);
        try {
            for (BlockPos position : getStructurePositions(controllerPos, controllerState.getValue(BlockNagaMixedRice.FACING))) {
                if (!position.equals(controllerPos)
                    && !position.equals(partPos)
                    && world.getBlockState(position).getBlock() == partBlock) {
                    world.setBlockToAir(position);
                }
            }
            world.destroyBlock(controllerPos, !creativeHarvest);
        } finally {
            REMOVING.set(previous);
        }
    }

    public static boolean isRemoving()
    {
        return Boolean.TRUE.equals(REMOVING.get());
    }
}

