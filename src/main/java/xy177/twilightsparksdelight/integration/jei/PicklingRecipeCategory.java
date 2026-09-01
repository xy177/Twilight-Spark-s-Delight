package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PicklingRecipeCategory implements IRecipeCategory<PicklingRecipeWrapper>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(TwilightSparksDelight.MODID, "textures/gui/jei/pickling.png");
    private static final int WIDTH = 118;
    private static final int HEIGHT = 80;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotIcon;

    public PicklingRecipeCategory(IGuiHelper helper)
    {
        this.background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(new ItemStack(TSDBlocks.PICKLED_BRACKEN_JAR));
        this.slotIcon = helper.createDrawable(TEXTURE, 119, 0, 22, 22);
    }

    @Override
    public String getUid()
    {
        return TSDJeiRecipeTypes.PICKLING;
    }

    @Override
    public String getTitle()
    {
        return I18n.format("twilight_spark_delight.jei.pickling.title");
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
    public void setRecipe(IRecipeLayout recipeLayout, PicklingRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        recipeLayout.getItemStacks().init(0, true, 8, 25);

        recipeLayout.getItemStacks().init(1, false, 92, 25);

        recipeLayout.getItemStacks().init(2, true, 63, 53);
        recipeLayout.getItemStacks().set(0, new ItemStack(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR));
        recipeLayout.getItemStacks().set(1, new ItemStack(TSDBlocks.PICKLED_BRACKEN_JAR));
        recipeLayout.getItemStacks().set(2, recipeWrapper.getAcceleratorStacks());

        recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 2) {
                tooltip.add(TextFormatting.GRAY + I18n.format("twilight_spark_delight.jei.pickling.accepted"));
                String hintKey = getJeiHintKey(ingredient);
                if (hintKey != null) {
                    tooltip.add(TextFormatting.GRAY + I18n.format(hintKey));
                }
            }
        });
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        slotIcon.draw(minecraft, 63, 53);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY)
    {
        if (isCursorInsideBounds(40, 38, 11, 11, mouseX, mouseY)) {
            return Collections.singletonList(I18n.format("twilight_spark_delight.jei.pickling.shade"));
        }
        if (isCursorInsideBounds(53, 38, 11, 11, mouseX, mouseY)) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(I18n.format("twilight_spark_delight.jei.pickling.structures"));
            tooltip.add(TextFormatting.GRAY + I18n.format("twilight_spark_delight.jei.pickling.structure_hills"));
            tooltip.add(TextFormatting.GRAY + I18n.format("twilight_spark_delight.jei.pickling.structure_mushroom_tower"));
            tooltip.add(TextFormatting.GRAY + I18n.format("twilight_spark_delight.jei.pickling.structure_labyrinth"));
            tooltip.add(TextFormatting.GRAY + I18n.format("twilight_spark_delight.jei.pickling.structure_knight_stronghold"));
            tooltip.add(TextFormatting.GRAY + I18n.format("twilight_spark_delight.jei.pickling.structure_quest_grove"));
            return tooltip;
        }
        if (isCursorInsideBounds(67, 38, 11, 11, mouseX, mouseY)) {
            return Collections.singletonList(I18n.format("twilight_spark_delight.jei.pickling.accelerators"));
        }
        return Collections.emptyList();
    }

    private static boolean isCursorInsideBounds(int x, int y, int width, int height, int mouseX, int mouseY)
    {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static String getJeiHintKey(ItemStack stack)
    {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("TsdPicklingJeiHint", 8) ? tag.getString("TsdPicklingJeiHint") : null;
    }
}
