package xy177.twilightsparksdelight.common.recipe;

import java.util.List;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;

public final class Experiment250ReplicationRecipe implements CraftingRecipe {
    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findMatch(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
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
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
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

    private static Match findMatch(CraftingInput input) {
        if (TSDConfig.EXPERIMENT_WORKSTATION.get() != TSDConfig.ExperimentWorkstation.CRAFTING_TABLE) {
            return null;
        }
        int experimentSlot = -1;
        int inputSlot = -1;
        for (int slot = 0; slot < input.size(); slot++) {
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

    public static final class Serializer implements RecipeSerializer<Experiment250ReplicationRecipe> {
        private static final Experiment250ReplicationRecipe INSTANCE = new Experiment250ReplicationRecipe();
        private static final MapCodec<Experiment250ReplicationRecipe> CODEC =
                MapCodec.unit(INSTANCE);
        private static final StreamCodec<RegistryFriendlyByteBuf, Experiment250ReplicationRecipe> STREAM_CODEC =
                StreamCodec.unit(INSTANCE);

        @Override
        public MapCodec<Experiment250ReplicationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, Experiment250ReplicationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
