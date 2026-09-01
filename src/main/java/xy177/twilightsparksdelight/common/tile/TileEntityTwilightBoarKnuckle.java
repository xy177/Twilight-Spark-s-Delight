package xy177.twilightsparksdelight.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import xy177.twilightsparksdelight.common.block.BlockTwilightBoarKnuckle;
import xy177.twilightsparksdelight.common.block.TwilightBoarKnuckleStructure;

import java.util.List;

public class TileEntityTwilightBoarKnuckle extends TileEntitySharedFeast
{
    @Override
    public void setServings(int servings)
    {
        super.setServings(BlockTwilightBoarKnuckle.clampStage(servings));
    }

    @Override
    public void initializeFromBlockDefault(int servings)
    {
        super.initializeFromBlockDefault(BlockTwilightBoarKnuckle.clampStage(servings));
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (this.world == null) {
            return super.getRenderBoundingBox();
        }
        IBlockState state = this.world.getBlockState(this.pos);
        if (!(state.getBlock() instanceof BlockTwilightBoarKnuckle)) {
            return super.getRenderBoundingBox();
        }
        EnumFacing facing = state.getValue(BlockTwilightBoarKnuckle.FACING);
        List<BlockPos> positions = TwilightBoarKnuckleStructure.getStructurePositions(this.pos, facing);
        int minX = this.pos.getX();
        int minZ = this.pos.getZ();
        int maxX = this.pos.getX();
        int maxZ = this.pos.getZ();
        for (BlockPos position : positions) {
            minX = Math.min(minX, position.getX());
            minZ = Math.min(minZ, position.getZ());
            maxX = Math.max(maxX, position.getX());
            maxZ = Math.max(maxZ, position.getZ());
        }
        return new AxisAlignedBB(
            minX - 0.25D,
            this.pos.getY() - 0.25D,
            minZ - 0.25D,
            maxX + 1.25D,
            this.pos.getY() + 1.25D,
            maxZ + 1.25D
        );
    }
}
