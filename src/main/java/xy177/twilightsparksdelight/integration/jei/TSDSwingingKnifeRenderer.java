package xy177.twilightsparksdelight.integration.jei;

import java.util.List;
import com.mojang.math.Axis;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Animate the ingredient itself so both JEI and its EMI bridge retain the motion. */
final class TSDSwingingKnifeRenderer implements IIngredientRenderer<ItemStack> {
    @Override
    public void render(GuiGraphics graphics, ItemStack stack) {
        float phase = (Util.getMillis() % 1400L) / 1400F;
        float angle = -20 + 65 * (float) Math.sin(phase * Math.PI * 2);
        graphics.pose().pushPose();
        graphics.pose().translate(12, 12, 0);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(angle));
        graphics.renderItem(stack, -8, -8);
        graphics.pose().popPose();
    }

    @Override public int getWidth() { return 24; }
    @Override public int getHeight() { return 24; }

    @Override
    public List<Component> getTooltip(ItemStack stack, TooltipFlag flag) {
        var mc = Minecraft.getInstance();
        return stack.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, flag);
    }
}
