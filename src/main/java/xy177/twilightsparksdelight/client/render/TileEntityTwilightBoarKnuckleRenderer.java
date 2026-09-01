package xy177.twilightsparksdelight.client.render;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import xy177.twilightsparksdelight.common.tile.TileEntityTwilightBoarKnuckle;

public class TileEntityTwilightBoarKnuckleRenderer extends TileEntitySpecialRenderer<TileEntityTwilightBoarKnuckle>
{
    private static final double MODEL_WIDTH = 15.0D / 16.0D;
    private static final double MODEL_LENGTH = 2.0D;

    @Override
    public boolean isGlobalRenderer(TileEntityTwilightBoarKnuckle tile)
    {
        return true;
    }

    @Override
    public void render(TileEntityTwilightBoarKnuckle tile, double x, double y, double z, float partialTicks,
        int destroyStage, float alpha)
    {
        if (tile.getWorld() == null) {
            return;
        }
        IBlockState state = tile.getWorld().getBlockState(tile.getPos()).getActualState(tile.getWorld(), tile.getPos());
        EnumFacing facing = state.getValue(BlockFeast.FACING);
        int light = tile.getWorld().getCombinedLight(tile.getPos().up(), 0);

        GlStateManager.pushMatrix();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.disableCull();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        EnumFacing left = facing.rotateY();
        // The baked model uses local X for the player-facing width and local
        // Z for the two-cell length. The controller is the right-hand cell,
        // so negative world directions need the origin moved to the far edge
        // of the controller cell before the model is rotated.
        double originX = x;
        double originZ = z;
        if (facing.getFrontOffsetX() < 0 || left.getFrontOffsetX() < 0) {
            originX += 1.0D;
        }
        if (facing.getFrontOffsetZ() < 0 || left.getFrontOffsetZ() < 0) {
            originZ += 1.0D;
        }

        // Rotate the food around the centre of its own 15/16 x 2 block model,
        // rather than around the controller block corner. This reverses the
        // food's presentation without moving it into a different cell.
        originX += facing.getFrontOffsetX() * MODEL_WIDTH + left.getFrontOffsetX() * MODEL_LENGTH;
        originZ += facing.getFrontOffsetZ() * MODEL_WIDTH + left.getFrontOffsetZ() * MODEL_LENGTH;
        GlStateManager.translate(originX, y, originZ);

        float modelRotation;
        switch (facing) {
            case EAST:
                modelRotation = 90.0F;
                break;
            case SOUTH:
                modelRotation = 0.0F;
                break;
            case WEST:
                modelRotation = -90.0F;
                break;
            default:
                modelRotation = 180.0F;
                break;
        }
        GlStateManager.rotate(modelRotation, 0.0F, 1.0F, 0.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light & 65535, light >> 16);
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState(state);
        dispatcher.getBlockModelRenderer().renderModelBrightness(model, state, 1.0F, true);
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}
