package xy177.twilightsparksdelight.common.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

public final class Experiment250ReplicationRecipe extends CustomRecipe {
    public Experiment250ReplicationRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        return findMatch(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        Match match = findMatch(input);
        if (match == null) {
            return ItemStack.EMPTY;
        }
        Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(match.outputId());
        if (item == net.minecraft.world.item.Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, match.result().outputCount());
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.getContainerSize(), ItemStack.EMPTY);
        Match match = findMatch(input);
        if (match == null) {
            return remaining;
        }
        ItemStack experiment = input.getItem(match.experimentSlot()).copyWithCount(1);
        if (Experiment250Logic.consume(experiment, match.result())) {
            remaining.set(match.experimentSlot(), experiment);
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TSDRecipeSerializers.EXPERIMENT_250_REPLICATION.get();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    private static Match findMatch(CraftingContainer input) {
        if (TSDConfig.EXPERIMENT_WORKSTATION.get() != TSDConfig.ExperimentWorkstation.CRAFTING_TABLE) {
            return null;
        }
        int experimentSlot = -1;
        int inputSlot = -1;
        for (int slot = 0; slot < input.getContainerSize(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof Experiment250Item && experimentSlot < 0) {
                experimentSlot = slot;
            } else if (inputSlot < 0) {
                inputSlot = slot;
            } else {
                return null;
            }
        }
        if (experimentSlot < 0 || inputSlot < 0) {
            return null;
        }
        ItemStack experiment = input.getItem(experimentSlot);
        var outputId = Experiment250Logic.resolveTarget(experiment, input.getItem(inputSlot));
        var result = Experiment250Logic.calculate(experiment, outputId);
        return result.valid() ? new Match(experimentSlot, outputId, result) : null;
    }

    private record Match(int experimentSlot, net.minecraft.resources.ResourceLocation outputId,
                         Experiment250Logic.ReplicationResult result) {
    }

}
