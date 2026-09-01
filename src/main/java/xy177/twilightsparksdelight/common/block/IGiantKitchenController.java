package xy177.twilightsparksdelight.common.block;

import net.minecraft.block.Block;

public interface IGiantKitchenController
{
    Block getStructurePartBlock();

    default int getStructureSize()
    {
        return 2;
    }
}
