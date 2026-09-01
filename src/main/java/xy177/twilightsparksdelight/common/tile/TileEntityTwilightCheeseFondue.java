package xy177.twilightsparksdelight.common.tile;

import xy177.twilightsparksdelight.common.block.BlockTwilightCheeseFondue;

public class TileEntityTwilightCheeseFondue extends TileEntitySharedFeast
{
    @Override
    public void setServings(int servings)
    {
        super.setServings(BlockTwilightCheeseFondue.clampServings(servings));
    }

    @Override
    public void initializeFromBlockDefault(int servings)
    {
        super.initializeFromBlockDefault(BlockTwilightCheeseFondue.clampServings(servings));
    }
}
