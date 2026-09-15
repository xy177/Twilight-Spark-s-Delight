package xy177.twilightsparksdelight.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDItems;
import vectorwing.farmersdelight.common.registry.ModSounds;

public final class TSDTwilightBorschtBlock extends TSDFeastBlock {
    public TSDTwilightBorschtBlock(Properties properties) {
        super(properties, TSDItems.BOWL_OF_TWILIGHT_BORSCHT, 6, false);
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,
            net.minecraft.world.level.BlockGetter level, BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context) {
        return net.minecraft.world.phys.shapes.Shapes.block();
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           net.minecraft.world.InteractionHand hand,
                                           BlockHitResult hit) {
        boolean cup = xy177.twilightsparksdelight.integration.CopperCupCompat.isCup(held);
        if (!held.is(Items.BOWL) && !cup) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (cup && held.getCount() < 2) return ItemInteractionResult.SUCCESS;
        if (!level.isClientSide) {
            if (!player.getAbilities().instabuild) held.shrink(cup ? 2 : 1);
            var serving = new ItemStack(cup ? TSDItems.TWILIGHT_BORSCHT_CUP.get()
                    : TSDItems.BOWL_OF_TWILIGHT_BORSCHT.get(), cup ? 2 : 1);
            if (!player.getInventory().add(serving)) player.drop(serving, false);
            int next = state.getValue(SERVINGS) - 1;
            if (next <= 0) {
                level.setBlock(pos, TSDBlocks.GLORY_CRUCIBLE.get().defaultBlockState(), Block.UPDATE_ALL);
            } else {
                level.setBlock(pos, state.setValue(SERVINGS, next), Block.UPDATE_ALL);
            }
            level.playSound(null, pos, ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state,
            net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        return java.util.List.of(new ItemStack(state.getValue(SERVINGS) == maxServings
                ? asItem() : TSDBlocks.GLORY_CRUCIBLE.get().asItem()));
    }
}
