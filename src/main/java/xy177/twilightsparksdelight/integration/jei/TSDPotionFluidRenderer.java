package xy177.twilightsparksdelight.integration.jei;

import java.util.List;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;

/** Keeps the liquid presentation while using the actual potion's complete tooltip. */
final class TSDPotionFluidRenderer implements IIngredientRenderer<ItemStack> {
    @Override
    public void render(GuiGraphics graphics, ItemStack potion) {
        var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(new ResourceLocation("block/water_still"));
        int color = PotionUtils.getColor(potion);
        graphics.blit(0, 0, 0, 16, 16, sprite,
                (color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 1F);
    }

    @Override
    public List<Component> getTooltip(ItemStack potion, TooltipFlag flag) {
        var mc = Minecraft.getInstance();
        return potion.getTooltipLines(mc.player, flag);
    }
}
