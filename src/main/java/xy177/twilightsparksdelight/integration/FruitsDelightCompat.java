package xy177.twilightsparksdelight.integration;

import dev.xkmc.fruitsdelight.content.cauldrons.FDCauldronBlock;
import dev.xkmc.fruitsdelight.content.cauldrons.FDCauldronInteraction;
import dev.xkmc.fruitsdelight.content.cauldrons.JamCauldronBlock;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import xy177.twilightsparksdelight.common.tile.TSDGloryCrucibleBlockEntity;

public final class FruitsDelightCompat {
    public record Result(boolean matched, ItemStack output, ItemStack remainder) {
        private static final Result PASS = new Result(false, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    public static Result process(TSDGloryCrucibleBlockEntity crucible, ItemStack stack, boolean simulate) {
        if (!ModList.get().isLoaded("fruitsdelight") || stack.isEmpty() || crucible.isBrewing()
                || crucible.hasPotion()) return Result.PASS;
        BlockState state = crucible.getFruitState();
        if (state == null) {
            if (!crucible.isPlainWater() || crucible.getVolumeUnits() < 3000) return Result.PASS;
            state = Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
        }
        var interactions = state.getBlock() instanceof FDCauldronBlock cauldron
                ? cauldron.getInteractions() : CauldronInteraction.WATER;
        var interaction = interactions.map().get(stack.getItem());
        if (!(interaction instanceof FDCauldronInteraction action) || !action.pred().test(stack)
                || action.requiresHeat() && !crucible.isHeated(crucible.getLevel(), crucible.getBlockPos())) {
            return Result.PASS;
        }
        BlockState next = action.action().perform(state);
        if (next == null) return Result.PASS;
        boolean serving = !action.result().isEmpty();
        if (serving && crucible.getVolumeUnits() < TSDGloryCrucibleBlockEntity.BOTTLE_UNITS) {
            return Result.PASS;
        }
        ItemStack remainder = stack.copyWithCount(1).getCraftingRemainingItem();
        if (!simulate) {
            stack.shrink(1);
            if (serving) {
                crucible.consumeBottle();
                // The dependency's 1-3 property describes a single bucket. Our exact
                // volume remains authoritative, so each container removes one third.
                if (crucible.getVolumeUnits() > 0) crucible.setFruitState(state.setValue(JamCauldronBlock.LEVEL, 3));
            } else {
                crucible.setFruitState(next);
            }
            crucible.getLevel().playSound(null, crucible.getBlockPos(), action.sound(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
        }
        return new Result(true, action.result().copy(), remainder);
    }

    private FruitsDelightCompat() {}
}
