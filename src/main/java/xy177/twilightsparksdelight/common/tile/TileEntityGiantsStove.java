package xy177.twilightsparksdelight.common.tile;

import xy177.twilightsparksdelight.common.config.TSDConfig;

public class TileEntityGiantsStove extends TileEntityGiantStove
{
    public TileEntityGiantsStove()
    {
        super(16);
    }

    @Override
    protected int getProcessingSlotLimit()
    {
        return Math.max(1, Math.min(16, TSDConfig.giantsStoveBatchCount));
    }

    @Override
    public int getKitchenStructureSize()
    {
        return 4;
    }

    @Override
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox()
    {
        return new net.minecraft.util.math.AxisAlignedBB(this.pos.add(-4, 0, -4), this.pos.add(5, 5, 5));
    }
}
