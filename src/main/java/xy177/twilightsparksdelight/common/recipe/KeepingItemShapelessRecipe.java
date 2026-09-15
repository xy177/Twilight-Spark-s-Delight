package xy177.twilightsparksdelight.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

/** Retains the upstream watch without changing unrelated Twilight Forest recipes. */
public final class KeepingItemShapelessRecipe extends ShapelessRecipe {
    private KeepingItemShapelessRecipe(ShapelessRecipe recipe) {
        super(recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> result = super.getRemainingItems(input);
        for (int slot = 0; slot < input.size(); slot++) {
            if (input.getItem(slot).is(TFItems.POCKET_WATCH.get())) {
                result.set(slot, input.getItem(slot).copyWithCount(1));
            }
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TSDRecipeSerializers.KEEPING_ITEM_SHAPELESS.get();
    }

    public static final class Serializer implements RecipeSerializer<KeepingItemShapelessRecipe> {
        private static final ShapelessRecipe.Serializer BASE = new ShapelessRecipe.Serializer();
        private static final MapCodec<KeepingItemShapelessRecipe> CODEC =
                BASE.codec().xmap(KeepingItemShapelessRecipe::new, recipe -> recipe);
        private static final StreamCodec<RegistryFriendlyByteBuf, KeepingItemShapelessRecipe> STREAM_CODEC =
                BASE.streamCodec().map(KeepingItemShapelessRecipe::new, recipe -> recipe);

        @Override
        public MapCodec<KeepingItemShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, KeepingItemShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
