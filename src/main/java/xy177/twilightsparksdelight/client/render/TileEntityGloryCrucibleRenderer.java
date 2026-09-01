package xy177.twilightsparksdelight.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import xy177.twilightsparksdelight.common.tile.TileEntityGloryCrucible;

public class TileEntityGloryCrucibleRenderer extends TileEntitySpecialRenderer<TileEntityGloryCrucible>
{
    @Override
    public void render(TileEntityGloryCrucible tile, double x, double y, double z, float partialTicks,
        int destroyStage, float alpha)
    {
        if (tile.getLiquidLevel() <= 0) {
            return;
        }
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
            .getAtlasSprite(tile.getLiquidTexture().toString());
        int color = tile.getRenderedColor(partialTicks);
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GlStateManager.disableLighting();

        double min = 2.01D / 16.0D;
        double max = 13.99D / 16.0D;
        double surface = tile.getSurfaceHeight();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(min, surface, min).tex(sprite.getMinU(), sprite.getMinV()).color(red, green, blue, 0.9F).endVertex();
        buffer.pos(min, surface, max).tex(sprite.getMinU(), sprite.getMaxV()).color(red, green, blue, 0.9F).endVertex();
        buffer.pos(max, surface, max).tex(sprite.getMaxU(), sprite.getMaxV()).color(red, green, blue, 0.9F).endVertex();
        buffer.pos(max, surface, min).tex(sprite.getMaxU(), sprite.getMinV()).color(red, green, blue, 0.9F).endVertex();
        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
