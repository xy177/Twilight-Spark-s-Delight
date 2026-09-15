package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import xy177.twilightsparksdelight.registry.TSDBlocks;

final class TSDGloryCrucibleCategory
        extends TSDJeiCategory<TSDJeiDynamicRecipes.GloryCrucible> {
    private final boolean brewing;

    TSDGloryCrucibleCategory(IGuiHelper helper, boolean brewing) {
        super(helper, brewing ? TwilightSparksDelightJeiPlugin.CRUCIBLE_BREWING
                        : TwilightSparksDelightJeiPlugin.CRUCIBLE_HEATING,
                brewing ? "twilight_spark_delight.jei.glory_crucible.brewing.title"
                        : "twilight_spark_delight.jei.glory_crucible.heating.title",
                TSDBlocks.GLORY_CRUCIBLE.get().asItem().getDefaultInstance(), 125, 48);
        this.brewing = brewing;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, TSDJeiDynamicRecipes.GloryCrucible recipe,
                          IFocusGroup focuses) {
        if (brewing) {
            layout.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks(recipe.reagents());
            potionSlot(layout, RecipeIngredientRole.INPUT, 50, recipe.inputs().get(0));
            potionSlot(layout, RecipeIngredientRole.OUTPUT, 108, recipe.outputs().get(0));
        } else {
            layout.addInputSlot(1, 1).setStandardSlotBackground().addItemStacks(recipe.inputs());
            layout.addOutputSlot(108, 1).setStandardSlotBackground().addItemStacks(recipe.outputs());
            layout.addSlot(RecipeIngredientRole.INPUT, 50, 1)
                    .setStandardSlotBackground().setFluidRenderer(1000, false, 16, 16)
                    .addIngredients(ForgeTypes.FLUID_STACK,
                            recipe.fluids().stream().map(fluid -> new FluidStack(fluid, 1000)).toList());
        }
    }

    private static void potionSlot(IRecipeLayoutBuilder layout, RecipeIngredientRole role,
                                   int x, ItemStack potion) {
        // Keep the real potion identity for recipe lookup and the EMI bridge.
        // Water with POTION_CONTENTS is still water to other ingredient renderers.
        layout.addSlot(role, x, 1).setStandardSlotBackground()
                .setSlotName(role == RecipeIngredientRole.INPUT ? "potion_input" : "potion_output")
                .setCustomRenderer(VanillaTypes.ITEM_STACK, new TSDPotionFluidRenderer())
                .addItemStack(potion);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
                                  TSDJeiDynamicRecipes.GloryCrucible recipe, IFocusGroup focuses) {
        // Same positions as JEI's anvil category, without registering as an anvil recipe.
        builder.addRecipePlusSign().setPosition(27, 3);
        builder.addRecipeArrow().setPosition(76, 1);
    }

    @Override
    public void draw(TSDJeiDynamicRecipes.GloryCrucible recipe, IRecipeSlotsView slots,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        if (brewing) {
            centeredText(graphics, Component.translatable(
                    "twilight_spark_delight.jei.glory_crucible.heat_source_required"), 27, 0xAA0000);
        } else {
            centeredText(graphics, Component.translatable(
                    "twilight_spark_delight.jei.glory_crucible.fuel_not_consumed"), 27, 0xAA0000);
        }
    }
}
