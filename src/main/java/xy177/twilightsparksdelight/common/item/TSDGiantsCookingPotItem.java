package xy177.twilightsparksdelight.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenStructure;
import xy177.twilightsparksdelight.registry.TSDBlocks;

/**
 * Aligns a Giant's Cooking Pot placed on any top cell of a Giant's Stove.
 */
public final class TSDGiantsCookingPotItem extends BlockItem {
    public TSDGiantsCookingPotItem(Item.Properties properties) {
        super(TSDBlocks.GIANTS_COOKING_POT.get(), properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() == Direction.UP) {
            BlockPos stoveController = findStoveController(context);
            if (stoveController != null) {
                int size = TSDBlocks.GIANTS_STOVE.get().getStructureSize();
                if (context.getClickedPos().getY() == stoveController.getY() + size - 1) {
                    BlockPos topCell = stoveController.above(size - 1);
                    BlockHitResult alignedHit = new BlockHitResult(
                            context.getClickLocation(),
                            Direction.UP,
                            topCell,
                            context.isInside());
                    UseOnContext aligned = new UseOnContext(
                            context.getLevel(), context.getPlayer(), context.getHand(),
                            context.getItemInHand(), alignedHit);
                    return super.useOn(aligned);
                }
            }
        }
        return super.useOn(context);
    }

    private static BlockPos findStoveController(UseOnContext context) {
        BlockState clicked = context.getLevel().getBlockState(context.getClickedPos());
        if (clicked.is(TSDBlocks.GIANTS_STOVE.get())) {
            return context.getClickedPos();
        }
        if (clicked.is(TSDBlocks.GIANTS_STOVE_PART.get())) {
            return TSDGiantKitchenStructure.findController(
                    context.getLevel(), context.getClickedPos(), TSDBlocks.GIANTS_STOVE.get());
        }
        return null;
    }
}
