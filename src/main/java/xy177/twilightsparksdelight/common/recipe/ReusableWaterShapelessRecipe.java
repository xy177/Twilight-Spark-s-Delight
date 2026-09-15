package xy177.twilightsparksdelight.common.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

/**
 * The water ingredient is reusable: its full stack components survive crafting.
 */
public final class ReusableWaterShapelessRecipe extends CustomRecipe {
    public ReusableWaterShapelessRecipe() {
        super(CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int flour = -1;
        int water = -1;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(TSDItems.LIVEROOT_FLOUR.get()) && flour < 0) {
                flour = slot;
            } else if (isWaterContainer(stack) && water < 0) {
                water = slot;
            } else {
                return false;
            }
        }
        return flour >= 0 && water >= 0;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return matches(input, null)
                ? TSDItems.LIVEROOT_DOUGH.get().getDefaultInstance()
                : ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (isWaterContainer(stack)) {
                remaining.set(slot, stack.copyWithCount(1));
            }
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TSDRecipeSerializers.REUSABLE_WATER_SHAPELESS.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(
                Ingredient.EMPTY,
                Ingredient.of(TSDItems.LIVEROOT_FLOUR.get()),
                net.neoforged.neoforge.common.crafting.CompoundIngredient.of(
                        Ingredient.of(Items.WATER_BUCKET),
                        net.neoforged.neoforge.common.crafting.DataComponentIngredient.of(false,
                                DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER), Items.POTION)));
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(TSDItems.LIVEROOT_DOUGH.get());
    }

    private static boolean isWaterContainer(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET) || isWaterBottle(stack);
    }

    private static boolean isWaterBottle(ItemStack stack) {
        if (!stack.is(Items.POTION)) {
            return false;
        }
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.is(Potions.WATER);
    }

    public static final class Serializer implements RecipeSerializer<ReusableWaterShapelessRecipe> {
        private static final ReusableWaterShapelessRecipe INSTANCE = new ReusableWaterShapelessRecipe();
        private static final com.mojang.serialization.MapCodec<ReusableWaterShapelessRecipe> CODEC =
                com.mojang.serialization.MapCodec.unit(INSTANCE);
        private static final net.minecraft.network.codec.StreamCodec<
                net.minecraft.network.RegistryFriendlyByteBuf, ReusableWaterShapelessRecipe> STREAM_CODEC =
                net.minecraft.network.codec.StreamCodec.unit(INSTANCE);

        @Override
        public com.mojang.serialization.MapCodec<ReusableWaterShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public net.minecraft.network.codec.StreamCodec<
                net.minecraft.network.RegistryFriendlyByteBuf, ReusableWaterShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
