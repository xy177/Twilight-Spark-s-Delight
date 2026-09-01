package xy177.twilightsparksdelight.client.render;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class GlassJarItemStackRenderer extends TileEntityItemStackRenderer
{
    @Override
    public void renderByItem(ItemStack stack, float partialTicks)
    {
        TileEntityGlassJarRenderer.renderItemStack(stack, partialTicks);
    }
}
