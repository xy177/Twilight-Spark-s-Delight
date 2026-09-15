package xy177.twilightsparksdelight.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

/** Retains the watch without changing unrelated crafting recipes. */
public final class KeepingItemShapelessRecipe extends ShapelessRecipe {
    private KeepingItemShapelessRecipe(ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(null),
                recipe.getIngredients());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
        NonNullList<ItemStack> result = super.getRemainingItems(input);
        for (int slot = 0; slot < input.getContainerSize(); slot++) {
            if (input.getItem(slot).is(TSDItems.RABBIT_POCKET_WATCH.get())) {
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
        @Override
        public KeepingItemShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new KeepingItemShapelessRecipe(BASE.fromJson(id, json));
        }

        @Override
        public KeepingItemShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new KeepingItemShapelessRecipe(BASE.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, KeepingItemShapelessRecipe recipe) {
            BASE.toNetwork(buffer, recipe);
        }
    }
}
