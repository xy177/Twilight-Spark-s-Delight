package xy177.twilightsparksdelight.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.tile.TileEntityGlassJar;

public class TileEntityGlassJarRenderer extends TileEntitySpecialRenderer<TileEntityGlassJar>
{
    @Override
    public void render(TileEntityGlassJar jar, double x, double y, double z, float partialTicks, int destroyStage,
        float alpha)
    {
        RenderState previousState = RenderState.capture();
        GlStateManager.pushMatrix();
        try {
            prepareRenderState();
            GlStateManager.translate(x + 0.5D, y, z + 0.5D);
            applyWobble(jar, partialTicks);
            renderDynamicParts(jar.getStoredItem(), jar.getLid(), jar.getItemRotation());
        } finally {
            GlStateManager.popMatrix();
            previousState.restore();
        }
    }

    public static void renderItemStack(ItemStack stack, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        RenderState previousState = RenderState.capture();
        GlStateManager.pushMatrix();
        try {
            prepareRenderState();
            minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            renderBlockModel(TSDBlocks.GLASS_JAR.getDefaultState());
            GlStateManager.translate(0.5D, 0.0D, 0.5D);
            renderDynamicParts(
                TileEntityGlassJar.getStoredItem(stack),
                TileEntityGlassJar.getLid(stack),
                TileEntityGlassJar.getItemRotation(stack)
            );
        } finally {
            GlStateManager.popMatrix();
            previousState.restore();
        }
    }

    private static void renderDynamicParts(ItemStack stored, ItemStack lid, int rotation)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (!stored.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 0.4375D, 0.0D);
            GlStateManager.rotate(-(rotation & 15) * 22.5F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            minecraft.getRenderItem().renderItem(stored, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        if (!lid.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 0.9375D, 0.0D);
            GlStateManager.scale(0.5F, 0.125F, 0.5F);
            GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            if (lid.getItem() instanceof ItemBlock) {
                Block lidBlock = ((ItemBlock) lid.getItem()).getBlock();
                try {
                    renderBlockModel(lidBlock.getStateFromMeta(lid.getMetadata()));
                } catch (RuntimeException ignored) {
                    renderBlockModel(lidBlock.getDefaultState());
                }
            }
            GlStateManager.popMatrix();
        }
    }

    private static void prepareRenderState()
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO
        );
        GlStateManager.enableRescaleNormal();
    }

    private static void renderBlockModel(IBlockState state)
    {
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        // renderBlockBrightness applies an unbalanced 90-degree Y rotation in 1.12.2.
        dispatcher.getBlockModelRenderer().renderModelBrightnessColor(
            state, dispatcher.getModelForState(state), 1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void applyWobble(TileEntityGlassJar jar, float partialTicks)
    {
        TileEntityGlassJar.WobbleStyle style = jar.getWobbleStyle();
        if (style == null || jar.getWorld() == null) {
            return;
        }
        float elapsed = (float) (jar.getWorld().getTotalWorldTime() - jar.getWobbleStartedAt()) + partialTicks;
        float progress = elapsed / (float) style.getDuration();
        if (progress < 0.0F || progress > 1.0F) {
            return;
        }
        float remaining = 1.0F - progress;
        if (style == TileEntityGlassJar.WobbleStyle.POSITIVE) {
            float cycle = progress * (float) Math.PI * 2.0F;
            GlStateManager.rotate((float) Math.sin(cycle) * remaining * 4.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate((float) Math.sin(cycle * 0.5F) * remaining * 3.0F, 0.0F, 0.0F, 1.0F);
        } else {
            GlStateManager.rotate((float) Math.sin(-progress * Math.PI * 3.0F) * remaining * 7.0F,
                0.0F, 1.0F, 0.0F);
        }
    }

    private static final class RenderState
    {
        private final boolean blendEnabled;
        private final boolean rescaleNormalEnabled;
        private final int blendSourceRgb;
        private final int blendDestinationRgb;
        private final int blendSourceAlpha;
        private final int blendDestinationAlpha;

        private RenderState(boolean blendEnabled, boolean rescaleNormalEnabled, int blendSourceRgb,
            int blendDestinationRgb, int blendSourceAlpha, int blendDestinationAlpha)
        {
            this.blendEnabled = blendEnabled;
            this.rescaleNormalEnabled = rescaleNormalEnabled;
            this.blendSourceRgb = blendSourceRgb;
            this.blendDestinationRgb = blendDestinationRgb;
            this.blendSourceAlpha = blendSourceAlpha;
            this.blendDestinationAlpha = blendDestinationAlpha;
        }

        private static RenderState capture()
        {
            return new RenderState(
                GL11.glIsEnabled(GL11.GL_BLEND),
                GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL),
                GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB),
                GL11.glGetInteger(GL14.GL_BLEND_DST_RGB),
                GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA),
                GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA)
            );
        }

        private void restore()
        {
            GlStateManager.tryBlendFuncSeparate(
                blendSourceRgb, blendDestinationRgb, blendSourceAlpha, blendDestinationAlpha);
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            if (rescaleNormalEnabled) {
                GlStateManager.enableRescaleNormal();
            } else {
                GlStateManager.disableRescaleNormal();
            }
        }
    }
}
