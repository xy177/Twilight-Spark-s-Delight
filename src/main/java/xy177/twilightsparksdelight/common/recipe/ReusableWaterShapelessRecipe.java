package xy177.twilightsparksdelight.common.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

/**
 * The water ingredient is reusable: its full item NBT survives crafting.
 */
public final class ReusableWaterShapelessRecipe extends CustomRecipe {
    public ReusableWaterShapelessRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        int flour = -1;
        int water = -1;
        for (int slot = 0; slot < input.getContainerSize(); slot++) {
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
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        return matches(input, null)
                ? TSDItems.LIVEROOT_DOUGH.get().getDefaultInstance()
                : ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.getContainerSize(), ItemStack.EMPTY);
        for (int slot = 0; slot < input.getContainerSize(); slot++) {
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
                net.minecraftforge.common.crafting.CompoundIngredient.of(
                        Ingredient.of(Items.WATER_BUCKET),
                        net.minecraftforge.common.crafting.PartialNBTIngredient.of(Items.POTION,
                                PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER).getTag())));
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return new ItemStack(TSDItems.LIVEROOT_DOUGH.get());
    }

    private static boolean isWaterContainer(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET) || isWaterBottle(stack);
    }

    private static boolean isWaterBottle(ItemStack stack) {
        if (!stack.is(Items.POTION)) {
            return false;
        }
        return PotionUtils.getPotion(stack) == Potions.WATER;
    }
}
