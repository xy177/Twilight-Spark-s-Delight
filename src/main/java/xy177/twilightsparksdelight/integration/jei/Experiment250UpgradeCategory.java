package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.registry.TSDItems;

final class Experiment250UpgradeCategory extends TSDJeiCategory<Experiment250JeiRecipes.Upgrade> {
    static final ResourceLocation POT = new ResourceLocation(
            "farmersdelight", "textures/gui/jei/cooking_pot.png");
    static final ResourceLocation GUI = new ResourceLocation(
            "farmersdelight", "textures/gui/cooking_pot.png");
    private final IDrawable background;
    private final IDrawable heat;
    private final IDrawable arrow;

    Experiment250UpgradeCategory(IGuiHelper helper) {
        super(helper, TwilightSparksDelightJeiPlugin.CULTIVATION,
                "twilight_spark_delight.jei.experiment_250.upgrade.title",
                new ItemStack(TSDItems.EXPERIMENT_250.get()), 116, 84);
        background = helper.createDrawable(POT, 0, 0, 116, 56);
        heat = helper.createDrawable(GUI, 176, 0, 17, 15);
        arrow = helper.createAnimatedRecipeArrow(100);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, Experiment250JeiRecipes.Upgrade recipe, IFocusGroup focuses) {
        for (int slot = 0; slot < recipe.inputCount(); slot++) {
            layout.addInputSlot(1 + slot % 3 * 18, 1 + slot / 3 * 18).addItemStack(recipe.input());
        }
        layout.addInputSlot(63, 39).addItemStack(new ItemStack(TFItems.TRANSFORMATION_POWDER.get()));
        layout.addOutputSlot(95, 10).addItemStack(recipe.output());
        layout.addOutputSlot(95, 39).addItemStack(recipe.output());
    }

    @Override
    public void draw(Experiment250JeiRecipes.Upgrade recipe, IRecipeSlotsView slots,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
        arrow.draw(graphics, 60, 9);
        heat.draw(graphics, 18, 39);
        centeredText(graphics, Component.translatable(
                "twilight_spark_delight.jei.experiment_250.activity_inherited"), 62, 0x555555);
    }
}
