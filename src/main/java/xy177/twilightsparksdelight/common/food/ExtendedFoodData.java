package xy177.twilightsparksdelight.common.food;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import xy177.twilightsparksdelight.TSDConfig;

/** Extra food policy around the original FoodData, including other mods' subclasses and hooks. */
public final class ExtendedFoodData {
    private static final ThreadLocal<Boolean> HUNGER_EXHAUSTION = ThreadLocal.withInitial(() -> false);

    public static void attach(Player player) {
        ((OwnerAccess) player.getFoodData()).tsd$setOwner(player);
    }

    public static float exhaustionMultiplier(Player player) {
        return !HUNGER_EXHAUSTION.get() && ExtendedFoodProgression.shouldUseExtraDrain(player)
                ? (float) (1 + TSDConfig.EXTENDED_FOOD_EXTRA_CONSUMPTION_MULTIPLIER.get()) : 1;
    }

    public static void resetForRespawn(Player player, boolean extendedCap, float originalSaturation) {
        var data = player.getFoodData();
        CompoundTag saved = new CompoundTag();
        data.addAdditionalSaveData(saved);
        int food = extendedCap ? ExtendedFoodProgression.getMaxFood(player) : ExtendedFoodProgression.BASE_CAP;
        saved.putInt("foodLevel", food);
        saved.putFloat("foodSaturationLevel", Math.min(food, Math.max(0, originalSaturation)));
        saved.putFloat("foodExhaustionLevel", 0);
        saved.putInt("foodTickTimer", 0);
        data.readAdditionalSaveData(saved);
        attach(player);
    }

    public static void copy(FoodData source, FoodData target) {
        CompoundTag saved = new CompoundTag();
        source.addAdditionalSaveData(saved);
        target.readAdditionalSaveData(saved);
    }

    public static void applyHungerExhaustion(Player player, float amount) {
        boolean previous = HUNGER_EXHAUSTION.get();
        HUNGER_EXHAUSTION.set(true);
        try {
            player.causeFoodExhaustion(amount);
        } finally {
            HUNGER_EXHAUSTION.set(previous);
        }
    }

    public interface OwnerAccess {
        void tsd$setOwner(Player player);
    }

    private ExtendedFoodData() {}
}
