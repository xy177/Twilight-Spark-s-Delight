package xy177.twilightsparksdelight.client.render;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import xy177.twilightsparksdelight.common.tile.TileEntityNagaMixedRice;

public class TileEntityNagaMixedRiceRenderer extends TileEntitySpecialRenderer<TileEntityNagaMixedRice>
{
    @Override
    public boolean isGlobalRenderer(TileEntityNagaMixedRice tile)
    {
        return true;
    }

    @Override
    public void render(
        TileEntityNagaMixedRice tile,
        double x,
        double y,
        double z,
        float partialTicks,
        int destroyStage,
        float alpha
    ) {
        if (tile.getWorld() == null) {
            return;
        }
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        state = state.getActualState(tile.getWorld(), tile.getPos());
        EnumFacing facing = state.getValue(BlockFeast.FACING);
        int light = tile.getWorld().getCombinedLight(tile.getPos().up(2), 0);

        GlStateManager.pushMatrix();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.disableCull();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        double centerX = 0.5D - facing.getFrontOffsetX();
        double centerZ = 0.5D - facing.getFrontOffsetZ();
        GlStateManager.translate(x + centerX, y, z + centerZ);
        switch (facing) {
            case EAST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            default:
                break;
        }
        GlStateManager.translate(-0.5D, 0.0D, -0.5D);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light & 65535, light >> 16);
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState(state);
        // Do not call BlockRendererDispatcher.renderBlockBrightness here: ENTITYBLOCK_ANIMATED
        // routes that method to ChestRenderer instead of the block's baked model.
        // BlockModelRenderer applies an inventory-style +90 degree rotation internally.
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        dispatcher.getBlockModelRenderer().renderModelBrightness(model, state, 1.0F, true);
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}

