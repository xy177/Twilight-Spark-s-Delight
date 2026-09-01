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
import java.util.List;

public final class TwilightBoarKnuckleStructure
{
    private static final ThreadLocal<Boolean> REMOVING = new ThreadLocal<>();

    private TwilightBoarKnuckleStructure()
    {
    }

    public static List<BlockPos> getStructurePositions(BlockPos controllerPos, EnumFacing facing)
    {
        List<BlockPos> positions = new ArrayList<>(2);
        positions.add(controllerPos);
        // The controller is the right-hand cell when viewed from the player.
        // The second cell therefore lies to the player's left, perpendicular
        // to the feast's front direction.
        positions.add(controllerPos.offset(facing.rotateY()));
        return positions;
    }

    public static BlockPos findController(IBlockAccess world, BlockPos partPos, Block controllerBlock)
    {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos candidate = partPos.add(x, 0, z);
                IBlockState candidateState = world.getBlockState(candidate);
                if (candidateState.getBlock() != controllerBlock) {
                    continue;
                }
                EnumFacing facing = candidateState.getValue(BlockTwilightBoarKnuckle.FACING);
                if (getStructurePositions(candidate, facing).contains(partPos)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    public static boolean canPlaceParts(World world, BlockPos controllerPos, IBlockState controllerState,
        Block partBlock, EnumFacing placedAgainst, EntityPlayer player)
    {
        for (BlockPos structurePos : getStructurePositions(controllerPos,
            controllerState.getValue(BlockTwilightBoarKnuckle.FACING))) {
            if (!structurePos.equals(controllerPos)
                && !world.mayPlace(partBlock, structurePos, false, placedAgainst, player)) {
                return false;
            }
        }
        return true;
    }

    public static List<BlockPos> placeParts(World world, BlockPos controllerPos, IBlockState controllerState,
        Block partBlock)
    {
        List<BlockPos> placed = new ArrayList<>(1);
        for (BlockPos structurePos : getStructurePositions(controllerPos,
            controllerState.getValue(BlockTwilightBoarKnuckle.FACING))) {
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

    public static void removePartsFromController(World world, BlockPos controllerPos, IBlockState state,
        Block partBlock)
    {
        if (partBlock == null || isRemoving()) {
            return;
        }
        boolean previous = isRemoving();
        REMOVING.set(true);
        try {
            for (BlockPos position : getStructurePositions(controllerPos,
                state.getValue(BlockTwilightBoarKnuckle.FACING))) {
                if (!position.equals(controllerPos) && world.getBlockState(position).getBlock() == partBlock) {
                    world.setBlockToAir(position);
                }
            }
        } finally {
            REMOVING.set(previous);
        }
    }

    public static void destroyControllerFromPart(World world, BlockPos partPos, Block controllerBlock,
        Block partBlock, boolean creativeHarvest)
    {
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
            for (BlockPos position : getStructurePositions(controllerPos,
                controllerState.getValue(BlockTwilightBoarKnuckle.FACING))) {
                if (!position.equals(controllerPos) && !position.equals(partPos)
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
