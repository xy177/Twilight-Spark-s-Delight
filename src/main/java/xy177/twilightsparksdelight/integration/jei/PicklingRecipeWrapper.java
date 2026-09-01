package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PicklingRecipeWrapper implements IRecipeWrapper
{
    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(ItemStack.class, Arrays.asList(
            Collections.singletonList(new ItemStack(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR)),
            getAcceleratorStacks()
        ));
        ingredients.setOutput(ItemStack.class, new ItemStack(TSDBlocks.PICKLED_BRACKEN_JAR));
    }

    public List<ItemStack> getAcceleratorStacks()
    {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(displayStack(new ItemStack(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR), "twilight_spark_delight.jei.pickling.clean_area"));
        stacks.add(displayStack(new ItemStack(TSDBlocks.PICKLED_BRACKEN_JAR), "twilight_spark_delight.jei.pickling.clean_area"));
        add(stacks, "twilightforest:magic_log_core");
        stacks.add(new ItemStack(TSDBlocks.LABYRINTH_MUSHROOM_COLONY));
        add(stacks, "twilightforest:peacock_fan", "twilight_spark_delight.jei.pickling.peacock_fan");
        return stacks;
    }

    private static void add(List<ItemStack> stacks, String id)
    {
        add(stacks, id, null);
    }

    private static void add(List<ItemStack> stacks, String id, String tooltipKey)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item != null) {
            ItemStack stack = new ItemStack(item);
            stacks.add(tooltipKey == null ? stack : displayStack(stack, tooltipKey));
        }
    }

    private static ItemStack displayStack(ItemStack stack, String tooltipKey)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("TsdPicklingJeiHint", tooltipKey);
        stack.setTagCompound(tag);
        return stack;
    }
}
