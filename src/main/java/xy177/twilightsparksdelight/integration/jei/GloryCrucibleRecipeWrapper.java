package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;

public final class GloryCrucibleRecipeWrapper implements IRecipeWrapper
{
    private final List<ItemStack> itemInputs;
    private final List<FluidStack> fluidInputs;
    private final ItemStack itemOutput;
    private final FluidStack fluidOutput;

    private GloryCrucibleRecipeWrapper(List<ItemStack> itemInputs, List<FluidStack> fluidInputs,
        ItemStack itemOutput, FluidStack fluidOutput)
    {
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutput = itemOutput;
        this.fluidOutput = fluidOutput;
    }

    public static GloryCrucibleRecipeWrapper heating(ItemStack input, ItemStack output)
    {
        return new GloryCrucibleRecipeWrapper(
            Collections.singletonList(input.copy()),
            GloryCrucibleJeiData.getHeatingFluids(),
            output.copy(),
            null
        );
    }

    public static GloryCrucibleRecipeWrapper brewing(List<ItemStack> reagents, FluidStack input, FluidStack output)
    {
        return new GloryCrucibleRecipeWrapper(
            copyItems(reagents),
            Collections.singletonList(input),
            null,
            output
        );
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(ItemStack.class, Collections.singletonList(itemInputs));
        ingredients.setInputLists(FluidStack.class, Collections.singletonList(fluidInputs));
        if (itemOutput != null) {
            ingredients.setOutput(ItemStack.class, itemOutput);
        } else {
            ingredients.setOutput(FluidStack.class, fluidOutput);
        }
    }

    public List<ItemStack> getItemInputs()
    {
        return itemInputs;
    }

    public List<FluidStack> getFluidInputs()
    {
        return fluidInputs;
    }

    public ItemStack getItemOutput()
    {
        return itemOutput;
    }

    public FluidStack getFluidOutput()
    {
        return fluidOutput;
    }

    private static List<ItemStack> copyItems(List<ItemStack> stacks)
    {
        java.util.ArrayList<ItemStack> copies = new java.util.ArrayList<>();
        for (ItemStack stack : stacks) {
            copies.add(stack.copy());
        }
        return copies;
    }
}
