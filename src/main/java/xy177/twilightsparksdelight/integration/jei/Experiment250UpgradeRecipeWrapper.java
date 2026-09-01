package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import twilightforest.item.TFItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Experiment250UpgradeRecipeWrapper implements IRecipeWrapper
{
    private final int inputLevel;
    private final int inputCount;
    private final int outputLevel;
    private final int outputCount;

    public Experiment250UpgradeRecipeWrapper(int inputLevel, int inputCount, int outputLevel, int outputCount)
    {
        this.inputLevel = inputLevel;
        this.inputCount = inputCount;
        this.outputLevel = outputLevel;
        this.outputCount = outputCount;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<List<ItemStack>> inputs = new ArrayList<>();
        for (int index = 0; index < inputCount; index++) {
            inputs.add(Collections.singletonList(createLevelStack(inputLevel)));
        }
        inputs.add(Collections.singletonList(new ItemStack(TFItems.transformation_powder)));
        ItemStack result = createLevelStack(outputLevel);
        result.setCount(outputCount);
        List<ItemStack> outputs = new ArrayList<>();
        outputs.add(result);
        outputs.add(result.copy());
        ingredients.setInputLists(ItemStack.class, inputs);
        ingredients.setOutputs(ItemStack.class, outputs);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        FontRenderer font = minecraft.fontRenderer;
        String levelText = I18n.format(
            "twilight_spark_delight.jei.experiment_250.level_conversion",
            inputLevel,
            outputLevel
        );
        font.drawString(levelText, 2, 56, 0x404040);
        font.drawString(
            TextFormatting.DARK_PURPLE + I18n.format("twilight_spark_delight.jei.experiment_250.activity_inherited"),
            2,
            68,
            0x404040
        );
    }

    public int getInputCount()
    {
        return inputCount;
    }

    private static ItemStack createLevelStack(int level)
    {
        return Experiment250Item.createStack(TSDItems.EXPERIMENT_250, level, 0.0D);
    }
}
