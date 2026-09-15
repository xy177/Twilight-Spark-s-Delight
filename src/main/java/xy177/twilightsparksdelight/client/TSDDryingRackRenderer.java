package xy177.twilightsparksdelight.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xy177.twilightsparksdelight.common.tile.TSDDryingRackBlockEntity;

public final class TSDDryingRackRenderer implements BlockEntityRenderer<TSDDryingRackBlockEntity> {
    public TSDDryingRackRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(TSDDryingRackBlockEntity rack, float partial, PoseStack pose,
                                 MultiBufferSource buffers, int light, int overlay) {
        if (rack.content().isEmpty()) return;
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(-rack.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        pose.translate(0, 0, -0.36);
        pose.scale(0.6F, 0.6F, 0.6F);
        Minecraft.getInstance().getItemRenderer().renderStatic(rack.content(), ItemDisplayContext.FIXED,
                light, overlay, pose, buffers, rack.getLevel(), 0);
        pose.popPose();
    }
}
