package xy177.twilightsparksdelight.common.tile;

import xy177.twilightsparksdelight.common.config.TSDConfig;

public class TileEntityGiantsCookingPot extends TileEntityGiantCookingPot
{
    @Override
    protected int getBatchLimit()
    {
        return Math.max(1, Math.min(16, TSDConfig.giantsCookingPotBatchCount));
    }

    @Override
    public String getName()
    {
        return "twilight_spark_delight.container.giants_cooking_pot";
    }

    @Override
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox()
    {
        return new net.minecraft.util.math.AxisAlignedBB(this.pos.add(-4, -2, -4), this.pos.add(5, 5, 5));
    }
}
