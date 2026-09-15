package xy177.twilightsparksdelight.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xy177.twilightsparksdelight.common.block.TSDColonyBlock;

/**
 * The colony item represents a mature colony when placed.
 */
public class TSDColonyItem extends BlockItem {
    public TSDColonyItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        return state == null ? null : state.setValue(TSDColonyBlock.AGE, 4);
    }
}
