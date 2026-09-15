package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xy177.twilightsparksdelight.common.recipe.TSDDryingRecipe;
import xy177.twilightsparksdelight.registry.TSDBlocks;

final class TSDDryingCategory extends TSDJeiCategory<TSDDryingRecipe> {
    private final mezz.jei.api.gui.drawable.IDrawable arrow;
    TSDDryingCategory(IGuiHelper helper) {
        super(helper, TwilightSparksDelightJeiPlugin.DRYING, "twilight_spark_delight.jei.drying",
                new ItemStack(TSDBlocks.DRYING_RACK.get()), 116, 54);
        arrow = helper.createDrawable(new net.minecraft.resources.ResourceLocation("farmersdelight",
                "textures/gui/jei/cutting_board.png"), 47, 20, 24, 18);
    }
    @Override public void setRecipe(IRecipeLayoutBuilder layout, TSDDryingRecipe recipe, IFocusGroup focuses) {
        layout.addInputSlot(15, 12).setStandardSlotBackground().addIngredients(recipe.input());
        layout.addOutputSlot(85, 12).setStandardSlotBackground().addItemStack(recipe.result());
    }
    @Override public void draw(TSDDryingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double x, double y) {
        arrow.draw(graphics, 46, 11);
        centeredText(graphics, Component.translatable("twilight_spark_delight.jei.drying.time",
                recipe.dryingTicks() / 20), 37, 0x555555);
    }
}
