package xy177.twilightsparksdelight.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.common.registry.TSDItems;

public class Experiment250ReplicationRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        return "crafting_table".equals(TSDConfig.experiment250WorkstationMode)
            && findMatch(inventory).isValid();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        Match match = findMatch(inventory);
        if (!match.isValid()) {
            return ItemStack.EMPTY;
        }
        Item output = ForgeRegistries.ITEMS.getValue(match.outputId);
        return output == null ? ItemStack.EMPTY : new ItemStack(output, match.result.getOutputCount());
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory)
    {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        Match match = findMatch(inventory);
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                remaining.set(slot, ForgeHooks.getContainerItem(stack));
            }
        }
        if (match.isValid()) {
            ItemStack experiment250 = inventory.getStackInSlot(match.experimentSlot).copy();
            experiment250.setCount(1);
            if (Experiment250Logic.consumeReplicationActivity(experiment250, match.result)) {
                remaining.set(match.experimentSlot, experiment250);
            }
        }
        return remaining;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.create();
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width * height >= 2;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    private static Match findMatch(InventoryCrafting inventory)
    {
        int experimentSlot = -1;
        int inputSlot = -1;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() == TSDItems.EXPERIMENT_250 && experimentSlot < 0) {
                experimentSlot = slot;
            } else if (inputSlot < 0) {
                inputSlot = slot;
            } else {
                return Match.NONE;
            }
        }
        if (experimentSlot < 0 || inputSlot < 0) {
            return Match.NONE;
        }
        ItemStack experiment250 = inventory.getStackInSlot(experimentSlot);
        ResourceLocation outputId = Experiment250Logic.resolveReplicationTarget(
            experiment250,
            inventory.getStackInSlot(inputSlot)
        );
        Experiment250Logic.ReplicationResult result = Experiment250Logic.calculateReplication(experiment250, outputId);
        return result.isValid() ? new Match(experimentSlot, outputId, result) : Match.NONE;
    }

    private static final class Match
    {
        private static final Match NONE = new Match(-1, null, null);
        private final int experimentSlot;
        private final ResourceLocation outputId;
        private final Experiment250Logic.ReplicationResult result;

        private Match(int experimentSlot, ResourceLocation outputId, Experiment250Logic.ReplicationResult result)
        {
            this.experimentSlot = experimentSlot;
            this.outputId = outputId;
            this.result = result;
        }

        private boolean isValid()
        {
            return experimentSlot >= 0 && outputId != null && result != null && result.isValid();
        }
    }
}
