package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.block.GiantKitchenStructure;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;

public class GiantsCookingPotItemBlock extends GiantCookingPotItemBlock
{
    public GiantsCookingPotItemBlock(Block block)
    {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(
        EntityPlayer player,
        World world,
        BlockPos pos,
        EnumHand hand,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ
    ) {
        if (facing == EnumFacing.UP) {
            IBlockState clickedState = world.getBlockState(pos);
            BlockPos stovePos = null;
            if (clickedState.getBlock() == TSDBlocks.GIANTS_STOVE) {
                stovePos = pos;
            } else if (clickedState.getBlock() == TSDBlocks.GIANTS_STOVE_PART) {
                stovePos = GiantKitchenStructure.findController(world, pos, TSDBlocks.GIANTS_STOVE);
            }
            int size = GiantKitchenStructure.getStructureSize(TSDBlocks.GIANTS_STOVE);
            if (stovePos != null && pos.getY() == stovePos.getY() + size - 1) {
                BlockPos alignedTop = stovePos.up(size - 1);
                return super.onItemUse(player, world, alignedTop, hand, EnumFacing.UP, hitX, hitY, hitZ);
            }
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
}
