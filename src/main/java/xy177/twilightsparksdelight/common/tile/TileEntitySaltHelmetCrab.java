package xy177.twilightsparksdelight.common.tile;

import xy177.twilightsparksdelight.common.block.BlockSaltHelmetCrab;

public class TileEntitySaltHelmetCrab extends TileEntitySharedFeast
{
    @Override
    public void setServings(int servings)
    {
        super.setServings(BlockSaltHelmetCrab.clampServings(servings));
    }

    @Override
    public void initializeFromBlockDefault(int servings)
    {
        super.initializeFromBlockDefault(BlockSaltHelmetCrab.clampServings(servings));
    }
}
