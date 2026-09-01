package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GloryCrucibleFluidRenderer implements IIngredientRenderer<FluidStack>
{
    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, FluidStack ingredient)
    {
        if (ingredient == null || ingredient.getFluid() == null) {
            return;
        }
        ResourceLocation still = ingredient.getFluid().getStill(ingredient);
        if (still == null) {
            return;
        }
        TextureAtlasSprite sprite = minecraft.getTextureMapBlocks().getAtlasSprite(still.toString());
        int color = ingredient.getFluid() == net.minecraftforge.fluids.FluidRegistry.WATER
            ? PotionPreviewFluid.getColor(ingredient)
            : ingredient.getFluid().getColor(ingredient);
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        );
        GlStateManager.color(red, green, blue, 1.0F);
        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        buffer.pos(xPosition, yPosition + 16, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        buffer.pos(xPosition + 16, yPosition + 16, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(xPosition + 16, yPosition, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(xPosition, yPosition, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, FluidStack ingredient, ITooltipFlag tooltipFlag)
    {
        if (ingredient == null) {
            return Collections.emptyList();
        }
        List<String> tooltip = new ArrayList<>();
        tooltip.add(TextFormatting.RESET + PotionPreviewFluid.getLocalizedName(ingredient));
        ItemStack potion = PotionPreviewFluid.getPotionStack(ingredient);
        if (!potion.isEmpty()) {
            PotionUtils.addPotionTooltip(potion, tooltip, 1.0F);
        }
        return tooltip;
    }
}
