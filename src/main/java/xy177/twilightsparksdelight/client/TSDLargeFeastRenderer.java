package xy177.twilightsparksdelight.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import xy177.twilightsparksdelight.common.block.TSDLargeStageFeastBlock;
import xy177.twilightsparksdelight.common.block.TSDNagaMixedRiceBlock;
import xy177.twilightsparksdelight.common.tile.TSDLargeFeastBlockEntity;

public final class TSDLargeFeastRenderer implements BlockEntityRenderer<TSDLargeFeastBlockEntity> {
    private final BlockRenderDispatcher blocks;

    public TSDLargeFeastRenderer(BlockEntityRendererProvider.Context context) {
        blocks = context.getBlockRenderDispatcher();
    }

    public AABB getRenderBoundingBox(TSDLargeFeastBlockEntity entity) {
        var state = entity.getBlockState();
        return ((TSDLargeStageFeastBlock) state.getBlock()).structureBounds(entity.getBlockPos(), state)
                .inflate(0.25);
    }

    @Override
    public void render(TSDLargeFeastBlockEntity entity, float partialTick, PoseStack poses,
            MultiBufferSource buffers, int light, int overlay) {
        var state = entity.getBlockState();
        Direction facing = state.getValue(TSDLargeStageFeastBlock.FACING);
        var north = state.setValue(TSDLargeStageFeastBlock.FACING, Direction.NORTH);
        poses.pushPose();
        applyModelTransform(poses, state);
        if (entity.getLevel() != null) {
            light = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos().above());
        }
        // Animated blocks must render the baked model directly instead of dispatching to an item renderer.
        blocks.getModelRenderer().renderModel(poses.last(), buffers.getBuffer(Sheets.cutoutBlockSheet()),
                north, blocks.getBlockModel(north), 1, 1, 1, light, overlay);
        poses.popPose();
    }

    public static void applyModelTransform(PoseStack poses, net.minecraft.world.level.block.state.BlockState state) {
        Direction facing = state.getValue(TSDLargeStageFeastBlock.FACING);
        poses.translate(0.5, 0, 0.5);
        float rotation = switch (facing) {
            case EAST -> -90;
            case SOUTH -> 180;
            case WEST -> 90;
            default -> 0;
        };
        poses.mulPose(Axis.YP.rotationDegrees(rotation));
        poses.translate(-0.5, 0, -0.5);
        if (state.getBlock() instanceof TSDNagaMixedRiceBlock) {
            poses.translate(0, 0, 1);
        } else {
            poses.translate(2, 0, 1.0 / 16.0);
            poses.mulPose(Axis.YP.rotationDegrees(-90));
        }
    }
}
