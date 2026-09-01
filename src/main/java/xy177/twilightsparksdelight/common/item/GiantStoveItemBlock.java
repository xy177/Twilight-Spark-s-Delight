package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class GiantStoveItemBlock extends ItemBlock
{
    public GiantStoveItemBlock(Block block)
    {
        super(block);
    }

    @Override
    public boolean placeBlockAt(
        ItemStack stack,
        EntityPlayer player,
        World world,
        BlockPos pos,
        EnumFacing side,
        float hitX,
        float hitY,
        float hitZ,
        IBlockState newState
    ) {
        if (!GiantKitchenItemPlacement.canPlace(world, pos, newState, player, side)) {
            return false;
        }
        List<BlockPos> placed = GiantKitchenItemPlacement.placeParts(world, pos, newState);
        if (placed.size() != GiantKitchenItemPlacement.requiredPartCount(newState)) {
            return false;
        }
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            GiantKitchenItemPlacement.rollback(world, newState, placed);
            return false;
        }
        GiantKitchenItemPlacement.finishPlacement(world, world.getBlockState(pos), placed);
        return true;
    }
}
