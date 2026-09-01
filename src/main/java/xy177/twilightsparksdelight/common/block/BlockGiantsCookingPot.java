package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockStove;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantsCookingPot;

public class BlockGiantsCookingPot extends BlockGiantCookingPot
{
    @Override
    public int getStructureSize()
    {
        return 4;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityGiantsCookingPot();
    }

    @Override
    public IBlockState getStateForPlacement(
        World world,
        BlockPos pos,
        EnumFacing side,
        float hitX,
        float hitY,
        float hitZ,
        int meta,
        EntityLivingBase placer
    ) {
        IBlockState placedState = super.getStateForPlacement(
            world, pos, side, hitX, hitY, hitZ, meta, placer
        );
        IBlockState belowState = world.getBlockState(pos.down(getStructureSize()));
        if (belowState.getBlock() instanceof BlockGiantsStove) {
            return placedState.withProperty(FACING, belowState.getValue(BlockStove.FACING));
        }
        return placedState;
    }
}
