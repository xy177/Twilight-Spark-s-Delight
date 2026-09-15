package xy177.twilightsparksdelight.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import xy177.twilightsparksdelight.common.tile.TSDGloryCrucibleBlockEntity;

public final class TSDGloryCrucibleRenderer implements BlockEntityRenderer<TSDGloryCrucibleBlockEntity> {
    public TSDGloryCrucibleRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TSDGloryCrucibleBlockEntity crucible, float partialTick, PoseStack poses,
                        MultiBufferSource buffers, int light, int overlay) {
        if (crucible.getVolumeUnits() <= 0) return;
        var fluid = crucible.getFluid();
        var extension = IClientFluidTypeExtensions.of(fluid.getFluid());
        var texture = extension.getStillTexture(fluid);
        if (texture == null) return;
        var mc = Minecraft.getInstance();
        var sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        int color = extension.getTintColor(fluid);
        if (crucible.hasPotion()) color = crucible.getPotionColor(partialTick);
        else if (crucible.getFruitState() != null) {
            color = mc.getBlockColors().getColor(crucible.getFruitState(),
                    crucible.getLevel(), crucible.getBlockPos(), 0) | 0xFF000000;
        } else if (fluid.getFluid() == net.minecraft.world.level.material.Fluids.WATER) {
            color = net.minecraft.client.renderer.BiomeColors.getAverageWaterColor(
                    crucible.getLevel(), crucible.getBlockPos()) | 0xFF000000;
        }
        var vertices = buffers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));
        var pose = poses.last();
        float y = (float) crucible.getSurfaceHeight();
        float min = 2.01F / 16, max = 13.99F / 16;
        float u0 = sprite.getU(min * 16), u1 = sprite.getU(max * 16);
        float v0 = sprite.getV(min * 16), v1 = sprite.getV(max * 16);
        vertices.vertex(pose.pose(), min, y, min).color(color).uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0, 1, 0).endVertex();
        vertices.vertex(pose.pose(), min, y, max).color(color).uv(u0, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0, 1, 0).endVertex();
        vertices.vertex(pose.pose(), max, y, max).color(color).uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0, 1, 0).endVertex();
        vertices.vertex(pose.pose(), max, y, min).color(color).uv(u1, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0, 1, 0).endVertex();
    }
}
