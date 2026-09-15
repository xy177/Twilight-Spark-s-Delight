package xy177.twilightsparksdelight.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import xy177.twilightsparksdelight.common.item.TSDMasonJarItem;
import xy177.twilightsparksdelight.common.tile.TSDMasonJarBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import twilightforest.init.TFBlocks;

public final class TSDMasonJarRenderer implements BlockEntityRenderer<TSDMasonJarBlockEntity> {
    public TSDMasonJarRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TSDMasonJarBlockEntity jar, float partial, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5, 0, 0.5);
        float age = jar.getLevel() == null ? 10 : jar.getLevel().getGameTime() - jar.wobbleStarted + partial;
        if (age >= 0 && age < 10) {
            float angle = (float) Math.sin(age * Math.PI * 0.3) * (1 - age / 10) * 7;
            pose.mulPose((jar.positiveWobble ? Axis.XP : Axis.YP).rotationDegrees(angle));
        }
        renderContents(jar.content(), jar.lid(), jar.rotation(), pose, buffers, light, overlay);
        pose.popPose();
    }

    private static void renderContents(ItemStack stored, ItemStack lid, int rotation, PoseStack pose,
                                       MultiBufferSource buffers, int light, int overlay) {
        var mc = Minecraft.getInstance();
        if (!stored.isEmpty() && !(stored.getItem() instanceof TSDMasonJarItem)) {
            pose.pushPose();
            pose.translate(0, 0.4375, 0);
            pose.mulPose(Axis.YP.rotationDegrees(-rotation * 22.5F));
            pose.scale(0.5F, 0.5F, 0.5F);
            mc.getItemRenderer().renderStatic(stored, ItemDisplayContext.FIXED, light, overlay, pose, buffers, mc.level, 0);
            pose.popPose();
        }
        if (lid.getItem() instanceof BlockItem block) {
            pose.pushPose();
            pose.translate(-0.25, 0.875, -0.25);
            pose.scale(0.5F, 0.125F, 0.5F);
            mc.getBlockRenderer().renderSingleBlock(block.getBlock().defaultBlockState(), pose, buffers, light, overlay);
            pose.popPose();
        }
    }

    public static final class ItemRenderer extends BlockEntityWithoutLevelRenderer {
        public ItemRenderer() {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

        @Override
        public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose,
                                 MultiBufferSource buffers, int light, int overlay) {
            pose.pushPose();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                    TSDBlocks.MASON_JAR.get().defaultBlockState(), pose, buffers, light, overlay);
            pose.translate(0.5, 0, 0.5);
            var data = stack.getTagElement("BlockEntityTag");
            ItemStack lid = data == null ? ItemStack.EMPTY : ItemStack.of(data.getCompound("Lid"));
            if (lid.isEmpty()) lid = TFBlocks.TWILIGHT_OAK_LOG.get().asItem().getDefaultInstance();
            renderContents(TSDMasonJarItem.content(stack), lid, 0, pose, buffers, light, overlay);
            pose.popPose();
        }
    }
}
