package xy177.twilightsparksdelight.common.tile;

import xy177.twilightsparksdelight.common.block.BlockTwilightBorscht;

public class TileEntityTwilightBorscht extends TileEntitySharedFeast
{
    @Override
    public void setServings(int servings)
    {
        super.setServings(BlockTwilightBorscht.clampLevel(servings));
    }

    @Override
    public void initializeFromBlockDefault(int servings)
    {
        super.initializeFromBlockDefault(BlockTwilightBorscht.clampLevel(servings));
    }
}
