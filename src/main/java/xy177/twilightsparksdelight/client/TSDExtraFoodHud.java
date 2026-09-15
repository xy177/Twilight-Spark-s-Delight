package xy177.twilightsparksdelight.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import com.mojang.blaze3d.systems.RenderSystem;
import xy177.twilightsparksdelight.TSDConfig;

/**
 * Renders the part of the food bar above vanilla's twenty-point range.
 *
 * <p>The layer is ordered immediately after the vanilla food layer. The
 * vanilla GUI exposes its current right-side height, so the overlay follows
 * armor, vehicles, and other vanilla HUD rows instead of using a fixed y
 * coordinate.</p>
 */
public final class TSDExtraFoodHud {
    private static final ResourceLocation EXTRA_HUNGER =
            new ResourceLocation("twilight_spark_delight", "textures/gui/extra_hunger_icons.png");
    private static final ResourceLocation EXTRA_SATURATION =
            new ResourceLocation("twilight_spark_delight", "textures/gui/extra_saturation_icons.png");
    private static final int ICON_SIZE = 9;

    private TSDExtraFoodHud() {
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
    public static void afterFood(net.minecraftforge.client.event.RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id().equals(net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.FOOD_LEVEL.id())) {
            render(event.getGuiGraphics());
        }
    }

    public static void render(GuiGraphics graphics) {
        if (!TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || (player.getVehicle() != null && player.getVehicle().showVehicleHealth())
                || minecraft.options.hideGui || minecraft.gameMode == null || !minecraft.gameMode.canHurtPlayer()) {
            return;
        }

        FoodData food = player.getFoodData();
        int extraFood = Math.max(0, food.getFoodLevel() - 20);
        float extraSaturation = Math.min(extraFood,
                Math.max(0.0F, food.getSaturationLevel() - 20.0F));
        if (extraFood <= 0 && extraSaturation <= 0.0F) {
            return;
        }

        int left = graphics.guiWidth() / 2 + 91;
        int top = graphics.guiHeight() - ((net.minecraftforge.client.gui.overlay.ForgeGui) minecraft.gui).rightHeight + 10;

        RenderSystem.enableBlend();
        renderFood(graphics, player, left, top, extraFood);
        RenderSystem.disableBlend();
    }

    private static void renderFood(GuiGraphics graphics, Player player, int left, int top, int extraFood) {
        boolean hunger = player.hasEffect(MobEffects.HUNGER);
        for (int index = 0; index < 10; index++) {
            int threshold = index * 2 + 1;
            if (threshold > extraFood) {
                continue;
            }
            int x = left - index * 8 - ICON_SIZE;
            int u = threshold == extraFood
                    ? (hunger ? 27 : 9)
                    : (hunger ? 36 : 18);
            draw(graphics, EXTRA_HUNGER, x, top, u, 0);
        }
    }

    public static void renderSaturation(GuiGraphics graphics, int left, int top,
                                         int extraFood, float extraSaturation) {
        for (int index = 0; index < 10; index++) {
            int slotFood = Math.min(2, Math.max(0, extraFood - index * 2));
            if (slotFood <= 0) {
                continue;
            }
            float slotSaturation = Math.min(slotFood,
                    Math.max(0.0F, extraSaturation - index * 2));
            if (slotSaturation <= 0.0F) {
                continue;
            }

            int x = left - index * 8 - ICON_SIZE;
            float normalized = slotFood == 1 ? Math.min(0.5F, slotSaturation / 2.0F) : slotSaturation / 2.0F;
            draw(graphics, EXTRA_SATURATION, x, top, saturationU(normalized), 0);
        }
    }

    public static void renderFoodPreview(GuiGraphics graphics, Player player, int left, int top,
                                        int currentExtra, int targetExtra) {
        boolean hunger = player.hasEffect(MobEffects.HUNGER);
        for (int index = Math.max(0, currentExtra / 2); index < 10; index++) {
            int current = Math.min(2, Math.max(0, currentExtra - index * 2));
            int target = Math.min(2, Math.max(0, targetExtra - index * 2));
            if (target <= current) continue;
            int u = target == 1 ? (hunger ? 27 : 9) : (hunger ? 36 : 18);
            draw(graphics, EXTRA_HUNGER, left - index * 8 - ICON_SIZE, top, u, 0);
        }
    }

    public static void renderSaturationPreview(GuiGraphics graphics, int left, int top,
            int currentExtraFood, float currentExtraSaturation, int targetExtraFood, float targetExtraSaturation) {
        for (int index = 0; index < 10; index++) {
            int currentFood = Math.min(2, Math.max(0, currentExtraFood - index * 2));
            int targetFood = Math.min(2, Math.max(0, targetExtraFood - index * 2));
            float current = Math.min(currentFood, Math.max(0, currentExtraSaturation - index * 2));
            float target = Math.min(targetFood, Math.max(0, targetExtraSaturation - index * 2));
            if (target <= current) continue;
            draw(graphics, EXTRA_SATURATION, left - index * 8 - ICON_SIZE, top, saturationU(target / 2), 0);
        }
    }

    private static int saturationU(float fill) {
        if (fill >= 1.0F) {
            return 27;
        }
        if (fill > 0.5F) {
            return 18;
        }
        if (fill > 0.25F) {
            return 9;
        }
        return 0;
    }

    private static void draw(GuiGraphics graphics, ResourceLocation texture,
                             int x, int y, int u, int v) {
        graphics.blit(texture, x, y, u, v, ICON_SIZE, ICON_SIZE);
    }
}
