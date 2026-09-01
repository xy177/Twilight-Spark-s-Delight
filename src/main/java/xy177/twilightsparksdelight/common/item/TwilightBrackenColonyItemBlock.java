package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.block.BlockTwilightBrackenColony;

public class TwilightBrackenColonyItemBlock extends ItemBlock
{
    public TwilightBrackenColonyItemBlock(Block block)
    {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
        float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (newState.getBlock() instanceof BlockTwilightBrackenColony) {
            newState = newState.withProperty(BlockTwilightBrackenColony.AGE, BlockTwilightBrackenColony.MAX_AGE);
        }
        return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }
}
