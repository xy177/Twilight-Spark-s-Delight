package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.block.GiantKitchenStructure;
import xy177.twilightsparksdelight.common.block.IGiantKitchenController;

import java.util.List;

final class GiantKitchenItemPlacement
{
    private GiantKitchenItemPlacement()
    {
    }

    static boolean canPlace(
        World world,
        BlockPos pos,
        IBlockState state,
        EntityPlayer player,
        EnumFacing side
    ) {
        Block controllerBlock = state.getBlock();
        if (!(controllerBlock instanceof IGiantKitchenController)) {
            return false;
        }
        Block partBlock = ((IGiantKitchenController) controllerBlock).getStructurePartBlock();
        return partBlock != null
            && GiantKitchenStructure.canPlaceParts(world, pos, state, partBlock, side, player);
    }

    static List<BlockPos> placeParts(World world, BlockPos pos, IBlockState state)
    {
        Block controllerBlock = state.getBlock();
        if (!(controllerBlock instanceof IGiantKitchenController)) {
            return java.util.Collections.emptyList();
        }
        Block partBlock = ((IGiantKitchenController) controllerBlock).getStructurePartBlock();
        if (partBlock == null) {
            return java.util.Collections.emptyList();
        }
        return GiantKitchenStructure.placeParts(world, pos, state, partBlock);
    }

    static int requiredPartCount(IBlockState state)
    {
        int size = GiantKitchenStructure.getStructureSize(state.getBlock());
        return size * size * size - 1;
    }

    static void finishPlacement(World world, IBlockState state, List<BlockPos> placed)
    {
        Block controllerBlock = state.getBlock();
        if (controllerBlock instanceof IGiantKitchenController) {
            Block partBlock = ((IGiantKitchenController) controllerBlock).getStructurePartBlock();
            GiantKitchenStructure.finishPlacement(world, placed, partBlock);
        }
    }

    static void rollback(World world, IBlockState state, List<BlockPos> placed)
    {
        Block controllerBlock = state.getBlock();
        if (controllerBlock instanceof IGiantKitchenController) {
            Block partBlock = ((IGiantKitchenController) controllerBlock).getStructurePartBlock();
            GiantKitchenStructure.removePlacedParts(world, placed, partBlock);
        }
    }
}
