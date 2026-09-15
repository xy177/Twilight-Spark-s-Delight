package xy177.twilightsparksdelight.integration.jei;

import java.util.List;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.TwilightSparksDelight;

final class TSDPicklingCategory extends TSDJeiCategory<TSDJeiDynamicRecipes.Pickling> {
    private final IDrawable background;
    private final IDrawable catalystSlot;

    TSDPicklingCategory(IGuiHelper helper) {
        super(helper, TwilightSparksDelightJeiPlugin.PICKLING,
                "twilight_spark_delight.jei.pickling.title",
                TSDBlocks.PICKLED_BRACKEN_JAR.get().asItem().getDefaultInstance(), 118, 80);
        var texture = TwilightSparksDelight.id("textures/gui/jei/pickling.png");
        background = helper.createDrawable(texture, 0, 0, 118, 80);
        catalystSlot = helper.createDrawable(texture, 119, 0, 18, 18);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, TSDJeiDynamicRecipes.Pickling recipe,
                          IFocusGroup focuses) {
        layout.addInputSlot(9, 26).addItemStack(recipe.unripe());
        layout.addOutputSlot(93, 26).addItemStack(recipe.finished());
        var catalysts = layout.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.CATALYST, 64, 54)
                .setBackground(catalystSlot, -1, -1)
                .addItemStacks(recipe.catalysts())
                .setSlotName("catalyst");
        catalysts.addRichTooltipCallback((view, tooltip) -> {
            displayedCatalyst(view).ifPresent(stack -> {
                if (stack.is(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR.get().asItem())
                        || stack.is(TSDBlocks.PICKLED_BRACKEN_JAR.get().asItem())) {
                    tooltip.add(Component.translatable(
                            "twilight_spark_delight.jei.pickling.clean_area"));
                } else if (stack.is(TFItems.PEACOCK_FEATHER_FAN.get())) {
                    tooltip.add(Component.translatable(
                            "twilight_spark_delight.jei.pickling.peacock_fan"));
                }
            });
        });
    }

    private static java.util.Optional<ItemStack> displayedCatalyst(
            mezz.jei.api.gui.ingredient.IRecipeSlotView view) {
        // EMI 1.1.x cycles this flat item list once per second, but its JEI slot
        // view reports the first alternative. Match the item actually on screen.
        if (view.getClass().getName().equals("dev.emi.emi.jemi.impl.JemiRecipeSlot")) {
            var stacks = view.getItemStacks().toList();
            if (!stacks.isEmpty()) {
                int index = (int) (System.currentTimeMillis() / 1000L % stacks.size());
                return java.util.Optional.of(stacks.get(index));
            }
        }
        return view.getDisplayedItemStack();
    }

    @Override
    public void draw(TSDJeiDynamicRecipes.Pickling recipe, IRecipeSlotsView slots,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        background.draw(graphics);
    }

    @Override
    public void getTooltip(mezz.jei.api.gui.builder.ITooltipBuilder tooltip,
                           TSDJeiDynamicRecipes.Pickling recipe, IRecipeSlotsView slots,
                           double mouseX, double mouseY) {
        if (mouseY < 38 || mouseY >= 49) return;
        if (mouseX >= 40 && mouseX < 51) {
            tooltip.add(Component.translatable("twilight_spark_delight.jei.pickling.shade"));
        } else if (mouseX >= 53 && mouseX < 64) {
            for (String key : List.of("structures", "structure_hills", "structure_mushroom_tower",
                    "structure_labyrinth", "structure_knight_stronghold", "structure_quest_grove")) {
                tooltip.add(Component.translatable("twilight_spark_delight.jei.pickling." + key));
            }
        } else if (mouseX >= 67 && mouseX < 78) {
            tooltip.add(Component.translatable("twilight_spark_delight.jei.pickling.accelerators"));
        }
    }
}
