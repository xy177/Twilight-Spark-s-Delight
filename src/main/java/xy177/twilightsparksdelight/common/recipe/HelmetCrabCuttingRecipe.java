package xy177.twilightsparksdelight.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import java.util.ArrayList;
import java.util.List;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

/** Shares the configured roll chances between the real cutting board and JEI. */
public final class HelmetCrabCuttingRecipe extends CuttingBoardRecipe {
    private HelmetCrabCuttingRecipe(CuttingBoardRecipe recipe) {
        super(recipe.getGroup(), recipe.getIngredients().getFirst(), recipe.getTool(),
                recipe.getRollableResults(), recipe.getSoundEvent());
    }

    @Override
    public NonNullList<ChanceResult> getRollableResults() {
        NonNullList<ChanceResult> results = NonNullList.create();
        results.addAll(super.getRollableResults());
        if (results.size() >= 3) {
            results.set(1, new ChanceResult(results.get(1).stack(),
                    TSDConfig.HELMET_CRAB_CUTTING_EXTRA_LEGS_CHANCE.get().floatValue()));
            results.set(2, new ChanceResult(results.get(2).stack(),
                    TSDConfig.HELMET_CRAB_CUTTING_ARMOR_CHANCE.get().floatValue()));
        }
        return results;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TSDRecipeSerializers.HELMET_CRAB_CUTTING.get();
    }

    @Override
    public List<ItemStack> rollResults(RandomSource random, int fortuneLevel, RecipeWrapper inventory) {
        List<ItemStack> results = new ArrayList<>();
        double fortune = vectorwing.farmersdelight.common.Configuration.CUTTING_BOARD_FORTUNE_BONUS.get()
                * fortuneLevel;
        // Each optional bundle is one roll: three legs or five clusters, not a per-item roll.
        for (ChanceResult result : getRollableResults()) {
            if (random.nextDouble() < result.chance() + fortune) results.add(result.stack().copy());
        }
        return results;
    }

    public static final class Serializer implements RecipeSerializer<HelmetCrabCuttingRecipe> {
        private static final CuttingBoardRecipe.Serializer BASE = new CuttingBoardRecipe.Serializer();
        private static final MapCodec<HelmetCrabCuttingRecipe> CODEC =
                BASE.codec().xmap(HelmetCrabCuttingRecipe::new, recipe -> recipe);
        private static final StreamCodec<RegistryFriendlyByteBuf, HelmetCrabCuttingRecipe> STREAM_CODEC =
                BASE.streamCodec().map(HelmetCrabCuttingRecipe::new, recipe -> recipe);

        @Override public MapCodec<HelmetCrabCuttingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, HelmetCrabCuttingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
