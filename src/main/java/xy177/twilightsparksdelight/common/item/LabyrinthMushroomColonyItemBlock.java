package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.block.BlockLabyrinthMushroomColony;

public class LabyrinthMushroomColonyItemBlock extends ItemBlock
{
    public LabyrinthMushroomColonyItemBlock(Block block)
    {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (newState.getBlock() instanceof BlockLabyrinthMushroomColony) {
            newState = newState.withProperty(BlockLabyrinthMushroomColony.AGE, BlockLabyrinthMushroomColony.MAX_AGE);
        }
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
