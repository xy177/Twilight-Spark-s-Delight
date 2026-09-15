package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface TSDGiantKitchenController {
    default int getStructureSize() {
        return 2;
    }

    ItemInteractionResult useOnPart(ItemStack held, BlockState state, Level level, BlockPos pos,
                                    Player player, InteractionHand hand, BlockHitResult hit);
}
