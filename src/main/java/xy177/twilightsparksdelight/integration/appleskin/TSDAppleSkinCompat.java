package xy177.twilightsparksdelight.integration.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import squeek.appleskin.api.food.FoodValues;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import squeek.appleskin.ModConfig;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.helpers.FoodHelper;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.client.TSDExtraFoodHud;
import xy177.twilightsparksdelight.common.food.ExtendedFoodProgression;

/** Loaded only on the client when AppleSkin is installed. */
public final class TSDAppleSkinCompat {
    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void afterOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.FOOD_LEVEL.id())) return;
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (!TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get() || player == null || mc.options.hideGui
                || mc.gameMode == null || !mc.gameMode.canHurtPlayer()
                || (player.getVehicle() != null && player.getVehicle().showVehicleHealth())) return;
        boolean saturation = ModConfig.SHOW_SATURATION_OVERLAY.get();
        boolean hunger = ModConfig.SHOW_FOOD_VALUES_OVERLAY.get();
        if (!saturation && !hunger) return;
        var graphics = event.getGuiGraphics();
        int left = graphics.guiWidth() / 2 + 91;
        int top = graphics.guiHeight() - ((net.minecraftforge.client.gui.overlay.ForgeGui) mc.gui).rightHeight + 10;
        var data = player.getFoodData();
        int currentExtra = Math.max(0, data.getFoodLevel() - 20);
        float currentSaturation = Math.max(0, data.getSaturationLevel() - 20);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        try {
            if (saturation) {
                TSDExtraFoodHud.renderSaturation(graphics, left, top, currentExtra, currentSaturation);
            }
            FoodValues food = previewFood(player);
            if (food == null) return;
            int targetFood = Math.min(ExtendedFoodProgression.getMaxFood(player),
                    data.getFoodLevel() + food.hunger);
            int targetExtra = Math.max(0, targetFood - 20);
            float targetSaturation = Math.max(0, Math.min(targetFood,
                    data.getSaturationLevel() + food.getSaturationIncrement()) - 20);
            float alpha = 0.3F + 0.25F * (float) Math.sin(mc.gui.getGuiTicks() / 5.0D);
            RenderSystem.setShaderColor(1, 1, 1, alpha);
            if (hunger) {
                TSDExtraFoodHud.renderFoodPreview(graphics, player, left, top, currentExtra, targetExtra);
            }
            if (saturation) {
                TSDExtraFoodHud.renderSaturationPreview(graphics, left, top, currentExtra,
                        currentSaturation, targetExtra, targetSaturation);
            }
        } finally {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    private static FoodValues previewFood(Player player) {
        if (!ModConfig.SHOW_FOOD_VALUES_OVERLAY.get()) return null;
        var stack = player.getMainHandItem();
        if (!FoodHelper.canConsume(stack, player)
                && ModConfig.SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND.get()) {
            stack = player.getOffhandItem();
        }
        return FoodHelper.canConsume(stack, player) ? FoodHelper.getModifiedFoodValues(stack, player) : null;
    }

    private TSDAppleSkinCompat() {}
}
