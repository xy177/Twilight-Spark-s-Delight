package xy177.twilightsparksdelight.integration.jei;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.recipe.KeepingItemShapelessRecipe;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

final class TSDWatchRecipeDecorator<R extends Recipe<?>> implements IRecipeCategoryDecorator<RecipeHolder<R>> {
    private final Set<IRecipeSlotDrawable> attached = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public void draw(RecipeHolder<R> recipe, IRecipeCategory<RecipeHolder<R>> category,
                     IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        if (!recipe.id().getNamespace().equals(TwilightSparksDelight.MOD_ID)
                || !(recipe.value() instanceof KeepingItemShapelessRecipe
                || recipe.value() instanceof CookingPotRecipe)) return;
        for (var view : slots.getSlotViews(RecipeIngredientRole.INPUT)) {
            if (!(view instanceof IRecipeSlotDrawable slot) || !attached.add(slot)) continue;
            // Decorator tooltips are skipped while hovering a slot; attach to that slot instead.
            slot.addTooltipCallback((display, tooltip) -> display.getDisplayedItemStack().ifPresent(stack -> {
                if (stack.is(TFItems.POCKET_WATCH.get())) {
                    tooltip.add(Component.translatable("twilight_spark_delight.jei.not_consumed"));
                }
            }));
        }
    }
}
