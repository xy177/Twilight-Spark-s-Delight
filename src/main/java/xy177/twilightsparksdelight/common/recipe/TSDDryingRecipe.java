package xy177.twilightsparksdelight.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

public record TSDDryingRecipe(ResourceLocation id, Ingredient input, ItemStack result, int dryingTicks)
        implements Recipe<Container> {
    @Override public boolean matches(Container container, Level level) { return input.test(container.getItem(0)); }
    @Override public ItemStack assemble(Container container, RegistryAccess registries) { return result.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }
    @Override public ItemStack getResultItem(RegistryAccess registries) { return result; }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return TSDRecipeSerializers.DRYING.get(); }
    @Override public RecipeType<?> getType() { return TSDRecipeSerializers.DRYING_TYPE.get(); }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY, input); }

    public static final class Serializer implements RecipeSerializer<TSDDryingRecipe> {
        @Override
        public TSDDryingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new TSDDryingRecipe(id, Ingredient.fromJson(json.get("input")),
                    ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                    Math.max(1, GsonHelper.getAsInt(json, "filter_time", 600)));
        }

        @Override
        public TSDDryingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new TSDDryingRecipe(id, Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readVarInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TSDDryingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.dryingTicks);
        }
    }
}
