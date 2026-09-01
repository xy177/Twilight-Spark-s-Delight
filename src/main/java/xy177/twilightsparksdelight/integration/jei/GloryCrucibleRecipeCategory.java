package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;

public final class GloryCrucibleRecipeCategory implements IRecipeCategory<GloryCrucibleRecipeWrapper>
{
    private static final ResourceLocation VANILLA_GUI = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final String uid;
    private final String titleKey;

    public GloryCrucibleRecipeCategory(IGuiHelper helper, String uid, String titleKey)
    {
        this.uid = uid;
        this.titleKey = titleKey;
        background = helper.drawableBuilder(VANILLA_GUI, 0, 168, 125, 18)
            .addPadding(0, 20, 0, 0)
            .build();
        icon = helper.createDrawableIngredient(new ItemStack(TSDBlocks.GLORY_CRUCIBLE));
    }

    @Override
    public String getUid()
    {
        return uid;
    }

    @Override
    public String getTitle()
    {
        return I18n.format(titleKey);
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
    public void setRecipe(IRecipeLayout layout, GloryCrucibleRecipeWrapper wrapper, IIngredients ingredients)
    {
        layout.getItemStacks().init(0, true, 0, 0);
        layout.getItemStacks().init(1, false, 107, 0);
        layout.getItemStacks().set(0, wrapper.getItemInputs());
        if (wrapper.getItemOutput() != null) {
            layout.getItemStacks().set(1, wrapper.getItemOutput());
        }

        IGuiFluidStackGroup fluids = layout.getFluidStacks();
        GloryCrucibleFluidRenderer renderer = new GloryCrucibleFluidRenderer();
        fluids.init(0, true, renderer, 50, 1, 16, 16, 0, 0);
        fluids.init(1, false, renderer, 108, 1, 16, 16, 0, 0);
        fluids.set(0, wrapper.getFluidInputs());
        if (wrapper.getFluidOutput() != null) {
            fluids.set(1, wrapper.getFluidOutput());
        }
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {
        String text;
        if (TSDJeiRecipeTypes.GLORY_CRUCIBLE_HEATING.equals(uid)) {
            text = I18n.format("twilight_spark_delight.jei.glory_crucible.fuel_not_consumed");
        } else if (TSDJeiRecipeTypes.GLORY_CRUCIBLE_BREWING.equals(uid)) {
            text = I18n.format("twilight_spark_delight.jei.glory_crucible.heat_source_required");
        } else {
            return;
        }
        int x = (background.getWidth() - minecraft.fontRenderer.getStringWidth(text)) / 2;
        minecraft.fontRenderer.drawString(text, x, 27, 0xAA0000);
    }
}
