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
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;

public class Experiment250ReplicationRecipeCategory implements IRecipeCategory<Experiment250ReplicationRecipeWrapper>
{
    private static final ResourceLocation COOKING_POT_JEI = new ResourceLocation(
        "farmersdelight",
        "textures/gui/jei/cooking_pot.png"
    );
    private static final ResourceLocation COOKING_POT_GUI = new ResourceLocation(
        "farmersdelight",
        "textures/gui/cooking_pot.png"
    );
    private static final ResourceLocation CUTTING_BOARD_JEI = new ResourceLocation(
        "farmersdelight",
        "textures/gui/jei/cutting_board.png"
    );
    private static final ResourceLocation CRAFTING_JEI = new ResourceLocation(
        "jei",
        "textures/gui/gui_vanilla.png"
    );
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable heatIndicator;
    private final IDrawableAnimated arrow;
    private final String workstationMode;

    public Experiment250ReplicationRecipeCategory(IGuiHelper helper)
    {
        this.workstationMode = TSDConfig.experiment250WorkstationMode;
        this.background = "cutting_board".equals(workstationMode)
            ? helper.createDrawable(CUTTING_BOARD_JEI, 0, 0, 117, 57, 0, 76, 28, 29)
            : "cooking_pot".equals(workstationMode)
                ? helper.createDrawable(COOKING_POT_JEI, 0, 0, 116, 56, 0, 77, 29, 29)
                : helper.createDrawable(CRAFTING_JEI, 0, 60, 116, 54, 0, 79, 29, 29);
        this.icon = helper.createDrawableIngredient(Experiment250Item.createStack(TSDItems.EXPERIMENT_250, 1, 0.0D));
        this.slot = helper.createDrawable(CUTTING_BOARD_JEI, 0, 58, 18, 18);
        this.heatIndicator = helper.createDrawable(COOKING_POT_GUI, 176, 0, 17, 15);
        this.arrow = helper.drawableBuilder(COOKING_POT_GUI, 176, 15, 24, 17)
            .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public String getUid()
    {
        return TSDJeiRecipeTypes.EXPERIMENT_250_REPLICATION;
    }

    @Override
    public String getTitle()
    {
        return I18n.format("twilight_spark_delight.jei.experiment_250.replication.title");
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
    public void setRecipe(IRecipeLayout layout, Experiment250ReplicationRecipeWrapper wrapper, IIngredients ingredients)
    {
        if ("cutting_board".equals(workstationMode)) {
            layout.getItemStacks().init(0, true, 44, 8);
            layout.getItemStacks().init(1, true, 44, 27);
            layout.getItemStacks().init(2, false, 114, 20);
            layout.getItemStacks().setBackground(2, slot);
        } else if ("cooking_pot".equals(workstationMode)) {
            layout.getItemStacks().init(0, true, 29, 0);
            layout.getItemStacks().init(1, true, 47, 0);
            layout.getItemStacks().init(2, false, 123, 10);
        } else {
            layout.getItemStacks().init(0, true, 29, 0);
            layout.getItemStacks().init(1, true, 47, 0);
            layout.getItemStacks().init(2, false, 123, 18);
        }
        layout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
        layout.getItemStacks().set(1, ingredients.getInputs(ItemStack.class).get(1));
        layout.getItemStacks().set(2, ingredients.getOutputs(ItemStack.class).get(0));
        layout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 1 && TSDConfig.experiment250BindingModeEnabled) {
                tooltip.add(I18n.format("twilight_spark_delight.jei.experiment_250.binding_input"));
                tooltip.add(I18n.format("twilight_spark_delight.jei.experiment_250.binding_output"));
            }
        });
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        if ("cooking_pot".equals(workstationMode)) {
            arrow.draw(minecraft, 89, 9);
            heatIndicator.draw(minecraft, 47, 39);
        }
    }
}
