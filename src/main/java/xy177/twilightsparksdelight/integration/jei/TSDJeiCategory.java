package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

abstract class TSDJeiCategory<T> implements IRecipeCategory<T> {
    private final RecipeType<T> type;
    private final Component title;
    private final IDrawable icon;
    private final int width;
    private final int height;

    TSDJeiCategory(IGuiHelper helper, RecipeType<T> type, String title, ItemStack icon, int width, int height) {
        this.type = type;
        this.title = Component.translatable(title);
        this.icon = helper.createDrawableItemStack(icon);
        this.width = width;
        this.height = height;
    }

    @Override public RecipeType<T> getRecipeType() { return type; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    protected void centeredText(GuiGraphics graphics, Component text, int y, int color) {
        var font = Minecraft.getInstance().font;
        // Long translations wrap instead of overflowing the recipe border.
        for (var line : font.split(text, width)) {
            graphics.drawString(font, line, (width - font.width(line)) / 2, y, color, false);
            y += font.lineHeight + 1;
        }
    }
}
