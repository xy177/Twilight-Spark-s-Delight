package xy177.twilightsparksdelight.client.render;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockStove;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import xy177.twilightsparksdelight.common.block.GiantKitchenStructure;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantStove;

public class TileEntityGiantsKitchenRenderer<T extends net.minecraft.tileentity.TileEntity> extends TileEntitySpecialRenderer<T>
{
    private static final TileEntityGiantStoveRenderer STOVE_ITEM_RENDERER = new TileEntityGiantStoveRenderer();
    private final boolean stove;

    public TileEntityGiantsKitchenRenderer(boolean stove)
    {
        this.stove = stove;
    }

    @Override
    public boolean isGlobalRenderer(T tile)
    {
        return true;
    }

    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (tile.getWorld() == null) {
            return;
        }
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if ((this.stove && !(state.getBlock() instanceof BlockStove))
            || (!this.stove && !(state.getBlock() instanceof BlockCookingPot))) {
            return;
        }
        EnumFacing facing = this.stove
            ? state.getValue(BlockStove.FACING)
            : state.getValue(BlockCookingPot.FACING);
        boolean alternate = this.stove
            ? state.getValue(BlockStove.LIT)
            : state.getValue(BlockCookingPot.SUPPORT);
        IBlockState renderState = this.stove
            ? TSDBlocks.GIANT_STOVE.getDefaultState()
                .withProperty(BlockStove.FACING, facing)
                .withProperty(BlockStove.LIT, alternate)
            : TSDBlocks.GIANT_COOKING_POT.getDefaultState()
                .withProperty(BlockCookingPot.FACING, facing)
                .withProperty(BlockCookingPot.SUPPORT, alternate);

        double offsetX = 0.0D;
        double offsetZ = 0.0D;
        if (facing == EnumFacing.EAST || facing == EnumFacing.SOUTH) {
            offsetX = -1.0D;
        }
        if (facing == EnumFacing.SOUTH || facing == EnumFacing.WEST) {
            offsetZ = -1.0D;
        }

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + offsetX, y, z + offsetZ);
        GlStateManager.scale(2.0D, 2.0D, 2.0D);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        int size = GiantKitchenStructure.getStructureSize(state.getBlock());
        BlockPos lightPos = tile.getPos().up(size);
        int light = tile.getWorld().getCombinedLight(lightPos, 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light & 65535, light >> 16);
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        // 1.12's brightness renderer applies an inventory-style +90 degree rotation internally.
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        dispatcher.renderBlockBrightness(renderState, 1.0F);
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        if (this.stove && tile instanceof TileEntityGiantStove) {
            STOVE_ITEM_RENDERER.render(
                (TileEntityGiantStove) tile,
                x,
                y,
                z,
                partialTicks,
                destroyStage,
                alpha
            );
        }
    }
}
