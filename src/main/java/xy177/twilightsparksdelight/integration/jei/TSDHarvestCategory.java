package xy177.twilightsparksdelight.integration.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import twilightforest.init.TFBlocks;
import xy177.twilightsparksdelight.common.event.TSDLootEvents;
import xy177.twilightsparksdelight.registry.TSDItems;

/** Harvesting is a world action, not a cutting-board recipe. */
final class TSDHarvestCategory extends TSDJeiCategory<TSDHarvestCategory.Harvest> {
    record Harvest(ItemStack plant, ItemStack result) {}
    private final IDrawable arrow;

    TSDHarvestCategory(IGuiHelper helper) {
        super(helper, TwilightSparksDelightJeiPlugin.HARVESTING, "twilight_spark_delight.jei.harvesting.title",
                new ItemStack(TSDItems.BRACKEN.get()), 140, 62);
        arrow = helper.createDrawable(ResourceLocation.fromNamespaceAndPath("farmersdelight",
                "textures/gui/jei/cutting_board.png"), 47, 20, 24, 18);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, Harvest recipe, IFocusGroup focuses) {
        layout.addInputSlot(2, 2).setSlotName("plant")
                .setCustomRenderer(VanillaTypes.ITEM_STACK, new TSDHarvestPlantRenderer())
                .addItemStack(recipe.plant());
        var knives = new ArrayList<ItemStack>();
        BuiltInRegistries.ITEM.forEach(item -> {
            var stack = item.getDefaultInstance();
            if (TSDLootEvents.isKnife(stack)) knives.add(stack);
        });
        layout.addInputSlot(59, 37).setSlotName("knife")
                .setCustomRenderer(VanillaTypes.ITEM_STACK, new TSDSwingingKnifeRenderer())
                .addItemStacks(knives);
        layout.addOutputSlot(108, 21).setStandardSlotBackground().addItemStack(recipe.result())
                .addRichTooltipCallback((slot, tooltip) ->
                        tooltip.add(Component.translatable("twilight_spark_delight.jei.extra_by_looting")));
    }

    @Override
    public void draw(Harvest recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 59, 20);
    }

    static List<Harvest> recipes() {
        return List.of(new Harvest(new ItemStack(TFBlocks.FIDDLEHEAD.get()), new ItemStack(TSDItems.BRACKEN.get())));
    }
}
