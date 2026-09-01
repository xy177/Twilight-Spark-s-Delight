package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.block.BlockTwilightBoarKnuckle;
import xy177.twilightsparksdelight.common.block.TwilightBoarKnuckleStructure;

import java.util.List;

public class TwilightBoarKnuckleItemBlock extends ItemBlock
{
    public TwilightBoarKnuckleItemBlock(Block block)
    {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
        float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (!(newState.getBlock() instanceof BlockTwilightBoarKnuckle)) {
            return false;
        }
        BlockTwilightBoarKnuckle controller = (BlockTwilightBoarKnuckle) newState.getBlock();
        EnumFacing facing = newState.getValue(BlockTwilightBoarKnuckle.FACING);
        BlockPos controllerPos = pos.offset(facing.rotateYCCW());
        if (!world.mayPlace(controller, controllerPos, false, side, player)) {
            return false;
        }
        if (!TwilightBoarKnuckleStructure.canPlaceParts(world, controllerPos, newState,
            controller.getStructurePartBlock(), side, player)) {
            return false;
        }
        List<BlockPos> placed = TwilightBoarKnuckleStructure.placeParts(world, controllerPos, newState,
            controller.getStructurePartBlock());
        if (placed.size() != 1) {
            return false;
        }
        if (!super.placeBlockAt(stack, player, world, controllerPos, side, hitX, hitY, hitZ, newState)) {
            TwilightBoarKnuckleStructure.removePlacedParts(world, placed, controller.getStructurePartBlock());
            return false;
        }
        TwilightBoarKnuckleStructure.finishPlacement(world, placed, controller.getStructurePartBlock());
        return true;
    }
}
