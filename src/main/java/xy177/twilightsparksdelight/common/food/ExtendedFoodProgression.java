package xy177.twilightsparksdelight.common.food;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import xy177.twilightsparksdelight.TSDConfig;

/**
 * Calculates the part of the extended food bar a player has unlocked.
 * Advancement names intentionally match the legacy edition's progression.
 */
public final class ExtendedFoodProgression {
    public static final int BASE_CAP = 20;
    public static final int FULL_CAP = 40;
    public static final String CLIENT_CAP = "twilight_spark_delight.food_cap";
    private static final int CAP_PER_ADVANCEMENT = 2;

    private static final ResourceLocation[] PROGRESSION = {
            id("progress_naga"),
            id("progress_lich"),
            id("progress_knights"),
            id("progress_labyrinth"),
            id("progress_yeti"),
            id("progress_ur_ghast"),
            id("progress_hydra"),
            id("progress_glacier"),
            id("quest_ram"),
            id("progress_merge")
    };

    private ExtendedFoodProgression() {
    }

    public static int getMaxFood(Player player) {
        if (!TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get()) return BASE_CAP;
        if (player != null && player.level().isClientSide && player.getPersistentData().contains(CLIENT_CAP)) {
            return Math.max(BASE_CAP, Math.min(FULL_CAP, player.getPersistentData().getInt(CLIENT_CAP)));
        }
        if (!TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get()
                || !TSDConfig.EXTENDED_FOOD_PROGRESSIVE_CAPS_ENABLED.get()
                || !(player instanceof ServerPlayer serverPlayer)) {
            return TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get() ? FULL_CAP : BASE_CAP;
        }

        int completed = 0;
        for (ResourceLocation id : PROGRESSION) {
            Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(id);
            if (advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                completed++;
            }
        }
        return Math.min(FULL_CAP, BASE_CAP + completed * CAP_PER_ADVANCEMENT);
    }

    public static float getMaxSaturation(Player player) {
        return getMaxFood(player);
    }

    public static boolean isInExtraRange(Player player) {
        return player != null && (player.getFoodData().getFoodLevel() > BASE_CAP
                || player.getFoodData().getSaturationLevel() > BASE_CAP);
    }

    public static boolean shouldUseExtraDrain(Player player) {
        return TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get()
                && TSDConfig.EXTENDED_FOOD_EXTRA_CONSUMPTION_ENABLED.get()
                && player != null
                && isInExtraRange(player);
    }

    public static double getExtraBenefit(Player player) {
        return TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get()
                && TSDConfig.EXTENDED_FOOD_EXTRA_CONSUMPTION_ENABLED.get() && isInExtraRange(player)
                ? TSDConfig.EXTENDED_FOOD_EXTRA_BENEFIT_MULTIPLIER.get()
                : 0.0D;
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation("twilightforest", path);
    }
}
