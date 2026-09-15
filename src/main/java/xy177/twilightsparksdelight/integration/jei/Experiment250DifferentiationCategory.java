package xy177.twilightsparksdelight.integration.jei;

import java.text.DecimalFormat;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.registry.TSDItems;

final class Experiment250DifferentiationCategory extends TSDJeiCategory<Experiment250JeiRecipes.Differentiation> {
    private static final DecimalFormat NUMBER = new DecimalFormat("0.##");
    private static final int WIDTH = 268;
    private static final int HEIGHT = 98;
    private static final int STATION_X = (WIDTH - 174) / 2;
    private static final int TABLE_TOP = 59;
    private static final int ROW_HEIGHT = 10;
    private static final int LABEL_WIDTH = 48;
    private static final int COLUMN_WIDTH = 36;
    private static final String[] TABLE_KEYS = {"level", "max", "each", "batch"};
    private final IDrawable pot;
    private final IDrawable board;
    private final IDrawable slot;
    private final IDrawable arrow;
    private final IDrawable heat;
    private final TSDConfig.ExperimentWorkstation mode;

    Experiment250DifferentiationCategory(IGuiHelper helper) {
        super(helper, TwilightSparksDelightJeiPlugin.DIFFERENTIATION,
                "twilight_spark_delight.jei.experiment_250.replication.title",
                new ItemStack(TSDItems.EXPERIMENT_250.get()), WIDTH, HEIGHT);
        mode = TSDConfig.EXPERIMENT_WORKSTATION.get();
        pot = helper.createDrawable(Experiment250UpgradeCategory.POT, 0, 0, 116, 56);
        board = helper.createDrawable(new ResourceLocation(
                "farmersdelight", "textures/gui/jei/cutting_board.png"), 0, 0, 117, 57);
        slot = helper.getSlotDrawable();
        arrow = helper.createAnimatedRecipeArrow(100);
        heat = helper.createDrawable(Experiment250UpgradeCategory.GUI, 176, 0, 17, 15);
    }

    @Override
    public void onDisplayedIngredientsUpdate(Experiment250JeiRecipes.Differentiation recipe,
            List<mezz.jei.api.gui.ingredient.IRecipeSlotDrawable> slots, IFocusGroup focuses) {
        int level = slots.stream().filter(slot -> slot.getSlotName().filter("experiment"::equals).isPresent())
                .findFirst().flatMap(slot -> slot.getDisplayedItemStack())
                .map(Experiment250Item::getLevel).orElse(1);
        ItemStack output = recipe.rows().get(level - 1).output();
        for (var slot : slots) {
            if (slot.getRole() == mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT) {
                // JEI identifies the same meat at different counts as one ingredient.
                slot.createDisplayOverrides().addItemStack(output);
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, Experiment250JeiRecipes.Differentiation recipe,
                          IFocusGroup focuses) {
        boolean cutting = mode == TSDConfig.ExperimentWorkstation.CUTTING_BOARD;
        boolean cooking = mode == TSDConfig.ExperimentWorkstation.COOKING_POT;
        var experiment = layout.addInputSlot(STATION_X + (cutting ? 45 : 30), cutting ? 9 : 1)
                .addItemStacks(recipe.rows().stream().map(Experiment250JeiRecipes.Row::experiment).toList())
                .setSlotName("experiment");
        var input = layout.addInputSlot(STATION_X + (cutting ? 45 : 48), cutting ? 28 : 1);
        if (TSDConfig.EXPERIMENT_BINDING_MODE_ENABLED.get()) {
            input.addIngredients(Ingredient.of(
                    xy177.twilightsparksdelight.common.experiment.Experiment250Logic.DOUGH_TAG));
            input.addTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable(
                    "twilight_spark_delight.jei.experiment_250.binding_output")));
        } else {
            input.addItemStack(new ItemStack(BuiltInRegistries.ITEM.get(recipe.item())));
        }
        List<ItemStack> outputs = recipe.rows().stream().map(Experiment250JeiRecipes.Row::output).toList();
        var output = layout.addOutputSlot(STATION_X + (cutting ? 115 : 124), cutting ? 21 : cooking ? 10 : 19)
                .addItemStacks(outputs).setSlotName("result");
        if (cutting) {
            output.setStandardSlotBackground();
        }
        if (cooking) {
            layout.addOutputSlot(STATION_X + 124, 39).addItemStacks(outputs);
        }
        // Output counts are not part of JEI's ingredient identity, so JEI may
        // merge two levels into one display entry. The displayed output is
        // replaced in onDisplayedIngredientsUpdate using the current level.
        experiment.addTooltipCallback((view, tooltip) -> {
            tooltip.add(Component.translatable("twilight_spark_delight.jei.not_consumed"));
            view.getDisplayedItemStack().ifPresent(stack -> {
                var row = recipe.rows().get(Experiment250Item.getLevel(stack) - 1);
                tooltip.add(Component.translatable("twilight_spark_delight.jei.experiment_250.cost_per_copy",
                        NUMBER.format(row.cost())));
            });
        });
    }

    @Override
    public void draw(Experiment250JeiRecipes.Differentiation recipe, IRecipeSlotsView slots,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.pose().pushPose();
        graphics.pose().translate(STATION_X, 0, 0);
        switch (mode) {
            case COOKING_POT -> {
                pot.draw(graphics, 29, 0);
                arrow.draw(graphics, 89, 9);
                heat.draw(graphics, 47, 39);
            }
            case CUTTING_BOARD -> board.draw(graphics, 29, 0);
            case CRAFTING_TABLE -> {
                for (int cell = 0; cell < 9; cell++) {
                    slot.draw(graphics, 29 + cell % 3 * 18, cell / 3 * 18);
                }
                arrow.draw(graphics, 90, 18);
                slot.draw(graphics, 123, 18);
            }
        }
        graphics.pose().popPose();
        for (int row = 0; row < TABLE_KEYS.length; row++) {
            drawCell(graphics, label(row).getString(), 4, TABLE_TOP + row * ROW_HEIGHT,
                    LABEL_WIDTH - 8, 0x555555, false);
        }
        int displayed = slots.findSlotByName("experiment").flatMap(view -> view.getDisplayedItemStack())
                .map(Experiment250Item::getLevel).orElse(1);
        for (var level : recipe.rows()) {
            int x = LABEL_WIDTH + (level.level() - 1) * COLUMN_WIDTH;
            int color = level.level() == displayed ? 0x227733 : 0x555555;
            for (int row = 0; row < TABLE_KEYS.length; row++) {
                drawCell(graphics, value(level, row), x + 2, TABLE_TOP + row * ROW_HEIGHT,
                        COLUMN_WIDTH - 4, color, true);
            }
        }
    }

    @Override
    public void getTooltip(mezz.jei.api.gui.builder.ITooltipBuilder tooltip,
                           Experiment250JeiRecipes.Differentiation recipe, IRecipeSlotsView slots,
                           double mouseX, double mouseY) {
        int row = (int) ((mouseY - TABLE_TOP) / ROW_HEIGHT);
        if (mouseY < TABLE_TOP || row >= TABLE_KEYS.length || mouseX < 4 || mouseX >= WIDTH - 4) return;
        if (mouseX < LABEL_WIDTH) {
            tooltip.add(label(row));
            return;
        }
        int column = (int) ((mouseX - LABEL_WIDTH) / COLUMN_WIDTH);
        if (column >= recipe.rows().size()) return;
        var level = recipe.rows().get(column);
        tooltip.add(Component.translatable("twilight_spark_delight.jei.experiment_250.level", level.level()));
        tooltip.add(label(row).append(": ").append(value(level, row)));
    }

    private static net.minecraft.network.chat.MutableComponent label(int row) {
        return Component.translatable("twilight_spark_delight.jei.experiment_250.table." + TABLE_KEYS[row]);
    }

    private static String value(Experiment250JeiRecipes.Row level, int row) {
        return switch (row) {
            case 0 -> Integer.toString(level.level());
            case 1 -> Integer.toString(level.output().getCount());
            case 2 -> NUMBER.format(level.cost());
            default -> NUMBER.format(level.batchCost());
        };
    }

    private static void drawCell(GuiGraphics graphics, String text, int x, int y,
                                 int width, int color, boolean centered) {
        var font = Minecraft.getInstance().font;
        if (font.width(text) > width) {
            text = font.plainSubstrByWidth(text, width - font.width("...")) + "...";
        }
        graphics.drawString(font, text, centered ? x + (width - font.width(text)) / 2 : x, y, color, false);
    }
}
