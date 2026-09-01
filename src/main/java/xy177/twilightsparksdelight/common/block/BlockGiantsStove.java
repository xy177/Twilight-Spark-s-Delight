package xy177.twilightsparksdelight.common.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantsStove;

public class BlockGiantsStove extends BlockGiantStove
{
    @Override
    public int getStructureSize()
    {
        return 4;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityGiantsStove();
    }
}
