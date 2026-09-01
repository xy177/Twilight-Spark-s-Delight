package xy177.twilightsparksdelight.client.render;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockStove;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import xy177.twilightsparksdelight.common.block.GiantKitchenStructure;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantStove;

public class TileEntityGiantStoveRenderer extends TileEntitySpecialRenderer<TileEntityGiantStove>
{
    @Override
    public void render(
        TileEntityGiantStove tile,
        double x,
        double y,
        double z,
        float partialTicks,
        int destroyStage,
        float alpha
    ) {
        EnumFacing facing = EnumFacing.NORTH;
        if (tile.getWorld().getBlockState(tile.getPos()).getBlock() instanceof BlockStove) {
            facing = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockStove.FACING).getOpposite();
        }
        int size = tile.getKitchenStructureSize();
        double centerOffsetX = GiantKitchenStructure.getCenterX(tile.getPos(), facing.getOpposite(), size) - tile.getPos().getX();
        double centerOffsetZ = GiantKitchenStructure.getCenterZ(tile.getPos(), facing.getOpposite(), size) - tile.getPos().getZ();

        for (int slot = 0; slot < tile.getSizeInventory(); slot++) {
            ItemStack stack = tile.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + centerOffsetX, y + size + 0.02D, z + centerOffsetZ);
            GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(tile.getRenderOffsetX(slot), tile.getRenderOffsetZ(slot), 0.0D);
            float itemScale = 0.75F;
            GlStateManager.scale(itemScale, itemScale, itemScale);
            int light = tile.getWorld().getCombinedLight(tile.getPos().up(size), 0);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light & 65535, light >> 16);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }
}
