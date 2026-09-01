package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;

public class Experiment250UpgradeRecipeCategory implements IRecipeCategory<Experiment250UpgradeRecipeWrapper>
{
    private static final ResourceLocation COOKING_POT_JEI = new ResourceLocation(
        "farmersdelight",
        "textures/gui/jei/cooking_pot.png"
    );
    private static final ResourceLocation COOKING_POT_GUI = new ResourceLocation(
        "farmersdelight",
        "textures/gui/cooking_pot.png"
    );
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable heatIndicator;
    private final IDrawableAnimated arrow;

    public Experiment250UpgradeRecipeCategory(IGuiHelper helper)
    {
        this.background = helper.createDrawable(COOKING_POT_JEI, 0, 0, 116, 56, 0, 24, 0, 0);
        this.icon = helper.createDrawableIngredient(Experiment250Item.createStack(TSDItems.EXPERIMENT_250, 6, 0.0D));
        this.heatIndicator = helper.createDrawable(COOKING_POT_GUI, 176, 0, 17, 15);
        this.arrow = helper.drawableBuilder(COOKING_POT_GUI, 176, 15, 24, 17)
            .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public String getUid()
    {
        return TSDJeiRecipeTypes.EXPERIMENT_250_UPGRADE;
    }

    @Override
    public String getTitle()
    {
        return I18n.format("twilight_spark_delight.jei.experiment_250.upgrade.title");
    }

    @Override
    public String getModName()
    {
        return TwilightSparksDelight.NAME;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout layout, Experiment250UpgradeRecipeWrapper wrapper, IIngredients ingredients)
    {
        for (int index = 0; index < 6; index++) {
            layout.getItemStacks().init(index, true, (index % 3) * 18, (index / 3) * 18);
        }
        int inputCount = wrapper.getInputCount();
        for (int index = 0; index < inputCount; index++) {
            layout.getItemStacks().set(index, ingredients.getInputs(ItemStack.class).get(index));
        }
        layout.getItemStacks().init(6, true, 62, 38);
        layout.getItemStacks().set(6, ingredients.getInputs(ItemStack.class).get(inputCount));

        layout.getItemStacks().init(7, false, 94, 10);
        layout.getItemStacks().init(8, false, 94, 38);
        layout.getItemStacks().set(7, ingredients.getOutputs(ItemStack.class).get(0));
        layout.getItemStacks().set(8, ingredients.getOutputs(ItemStack.class).get(1));
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        arrow.draw(minecraft, 60, 9);
        heatIndicator.draw(minecraft, 18, 39);
    }
}
