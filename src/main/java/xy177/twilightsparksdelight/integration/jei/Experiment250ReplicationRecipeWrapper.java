package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Experiment250ReplicationRecipeWrapper implements IRecipeWrapper
{
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.##");
    private final ResourceLocation meatId;
    private ItemStack displayedExperiment250 = ItemStack.EMPTY;
    private ItemStack displayedOutput = ItemStack.EMPTY;

    public Experiment250ReplicationRecipeWrapper(ResourceLocation meatId)
    {
        this.meatId = meatId;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        Item meat = ForgeRegistries.ITEMS.getValue(meatId);
        if (meat == null) {
            return;
        }
        displayedExperiment250 = Experiment250Item.createStack(
            TSDItems.EXPERIMENT_250,
            Experiment250Item.MIN_LEVEL,
            Experiment250Item.getCapacity(Experiment250Item.MIN_LEVEL)
        );
        displayedOutput = new ItemStack(
            meat,
            Experiment250Logic.getMaximumReplicationOutput(Experiment250Item.MIN_LEVEL)
        );
        List<ItemStack> source = TSDConfig.experiment250BindingModeEnabled
            ? new ArrayList<>(OreDictionary.getOres("foodDough", false))
            : Collections.singletonList(new ItemStack(meat));
        ingredients.setInputLists(ItemStack.class, Arrays.asList(
            Collections.singletonList(displayedExperiment250),
            source
        ));
        ingredients.setOutput(ItemStack.class, displayedOutput);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        updateRotatingStacks();
        FontRenderer font = minecraft.fontRenderer;
        font.drawString(I18n.format("twilight_spark_delight.jei.experiment_250.table.level"), 4, 60, 0x555555);
        font.drawString(I18n.format("twilight_spark_delight.jei.experiment_250.table.each"), 34, 60, 0x555555);
        font.drawString(I18n.format("twilight_spark_delight.jei.experiment_250.table.max"), 80, 60, 0x555555);
        font.drawString(I18n.format("twilight_spark_delight.jei.experiment_250.table.batch"), 118, 60, 0x555555);
        for (int level = 1; level <= Experiment250Item.MAX_LEVEL; level++) {
            int y = 72 + (level - 1) * 10;
            double costPerCopy = Experiment250Logic.getReplicationCostPerCopy(level, meatId);
            int maximumOutput = Experiment250Logic.getMaximumReplicationOutput(level);
            font.drawString(Integer.toString(level), 4, y, 0x404040);
            font.drawString(NUMBER_FORMAT.format(costPerCopy), 34, y, 0x683A7A);
            font.drawString(Integer.toString(maximumOutput), 80, y, 0x404040);
            font.drawString(NUMBER_FORMAT.format((maximumOutput - 1) * costPerCopy), 118, y, 0x683A7A);
        }
    }

    private void updateRotatingStacks()
    {
        if (displayedExperiment250.isEmpty() || displayedOutput.isEmpty()) {
            return;
        }
        int level = (int) ((System.currentTimeMillis() / 1000L) % Experiment250Item.MAX_LEVEL)
            + Experiment250Item.MIN_LEVEL;
        Experiment250Item.setLevel(displayedExperiment250, level);
        Experiment250Item.setActivity(displayedExperiment250, Experiment250Item.getCapacity(level));
        displayedOutput.setCount(Experiment250Logic.getMaximumReplicationOutput(level));
    }
}
