package xy177.twilightsparksdelight.integration.jei;

import java.util.List;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;

/** A shared block-space transform puts the plant directly on the soil surface. */
final class TSDHarvestPlantRenderer implements IIngredientRenderer<ItemStack> {
    @Override
    public void render(GuiGraphics graphics, ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem plant)) return;
        var mc = Minecraft.getInstance();
        var pose = graphics.pose();
        graphics.flush();
        pose.pushPose();
        try {
            pose.translate(22, 34, 150);
            pose.scale(18, -18, 18);
            pose.mulPose(Axis.XP.rotationDegrees(25));
            pose.mulPose(Axis.YP.rotationDegrees(45));
            pose.translate(-0.5, 0, -0.5);
            Lighting.setupFor3DItems();
            var buffers = mc.renderBuffers().bufferSource();
            mc.getBlockRenderer().renderSingleBlock(Blocks.GRASS_BLOCK.defaultBlockState(), pose, buffers,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            pose.translate(0, 1, 0);
            mc.getBlockRenderer().renderSingleBlock(plant.getBlock().defaultBlockState(), pose, buffers,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            buffers.endBatch();
        } finally {
            pose.popPose();
            Lighting.setupFor3DItems();
        }
    }

    @Override public int getWidth() { return 44; }
    @Override public int getHeight() { return 52; }

    @Override
    public List<Component> getTooltip(ItemStack stack, TooltipFlag flag) {
        var mc = Minecraft.getInstance();
        return stack.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, flag);
    }
}
