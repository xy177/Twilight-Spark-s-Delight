package xy177.twilightsparksdelight.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import com.mojang.math.Axis;
import net.minecraft.world.item.ItemDisplayContext;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenStructure;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenController;
import xy177.twilightsparksdelight.common.tile.TSDGiantCookingPotBlockEntity;
import xy177.twilightsparksdelight.common.tile.TSDGiantStoveBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlocks;

/**
 * Renders the 4x4 kitchen models from legal 2x baked models.
 *
 * The model loader only accepts block-model coordinates in the normal
 * -16..32 range. The large models are therefore baked at half scale and
 * expanded here to preserve their four-block footprint.
 */
public final class TSDGiantKitchenRenderer<T extends net.minecraft.world.level.block.entity.BlockEntity>
        implements BlockEntityRenderer<T> {
    private final BlockRenderDispatcher blockRenderer;

    public TSDGiantKitchenRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof TSDGiantKitchenController kitchen)) return;
        int size = kitchen.getStructureSize();
        var bounds = getRenderBoundingBox(blockEntity).deflate(0.1);
        var pos = blockEntity.getBlockPos();
        var center = bounds.getCenter();
        Direction facing = TSDGiantKitchenStructure.controllerFacing(state);
        var north = state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING,
                Direction.NORTH);
        if (blockEntity.getLevel() != null) {
            packedLight = LevelRenderer.getLightColor(blockEntity.getLevel(),
                    net.minecraft.core.BlockPos.containing(center.x, bounds.maxY + 0.1, center.z));
        }
        poseStack.pushPose();
        poseStack.translate(center.x - pos.getX(), 0, center.z - pos.getZ());
        poseStack.mulPose(Axis.YP.rotationDegrees(switch (facing) {
            case EAST -> -90;
            case SOUTH -> 180;
            case WEST -> 90;
            default -> 0;
        }));
        poseStack.scale(size / 2.0F, size / 2.0F, size / 2.0F);
        poseStack.translate(-1, 0, -1);
        blockRenderer.getModelRenderer().renderModel(poseStack.last(),
                bufferSource.getBuffer(Sheets.cutoutBlockSheet()), north,
                blockRenderer.getBlockModel(north), 1, 1, 1, packedLight, packedOverlay);
        poseStack.popPose();

        if (blockEntity instanceof TSDGiantStoveBlockEntity stove) {
            var renderer = net.minecraft.client.Minecraft.getInstance().getItemRenderer();
            for (int slot = 0; slot < stove.getItems().getSlots(); slot++) {
                var stack = stove.getItems().getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                var offset = stove.getStoveItemOffset(slot);
                poseStack.pushPose();
                poseStack.translate(center.x - pos.getX(), size + 0.02, center.z - pos.getZ());
                poseStack.mulPose(Axis.YP.rotationDegrees(-facing.getOpposite().toYRot()));
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.translate(offset.x, offset.y, 0);
                poseStack.scale(0.75F, 0.75F, 0.75F);
                renderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                        poseStack, bufferSource, stove.getLevel(), (int) pos.asLong() + slot);
                poseStack.popPose();
            }
        }
    }

    public AABB getRenderBoundingBox(T entity) {
        return TSDGiantKitchenStructure.bounds(entity.getBlockPos(), entity.getBlockState()).inflate(0.1);
    }

    public static BlockEntityRenderer<TSDGiantStoveBlockEntity> stove(
            BlockEntityRendererProvider.Context context) {
        return new TSDGiantKitchenRenderer<>(context);
    }

    public static BlockEntityRenderer<TSDGiantCookingPotBlockEntity> cookingPot(
            BlockEntityRendererProvider.Context context) {
        return new TSDGiantKitchenRenderer<>(context);
    }
}
