package xy177.twilightsparksdelight.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.food.ExtendedFoodData;
import xy177.twilightsparksdelight.common.food.ExtendedFoodProgression;

@Mixin(FoodData.class)
public abstract class ExtendedFoodDataMixin implements ExtendedFoodData.OwnerAccess {
    @Unique private Player tsd$owner;
    @Unique private boolean tsd$previouslyEnabled;
    @Unique private boolean tsd$clamping;

    @Override
    public void tsd$setOwner(Player player) {
        tsd$owner = player;
        if (tsd$clamping) return;
        boolean enabled = TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get();
        if (enabled || tsd$previouslyEnabled) {
            // Public setters re-enter through the RETURN injection below.
            tsd$clamping = true;
            try {
                FoodData data = (FoodData) (Object) this;
                int cap = enabled ? ExtendedFoodProgression.getMaxFood(player) : 20;
                int food = Math.max(0, Math.min(cap, data.getFoodLevel()));
                float saturation = Math.max(0, Math.min(food, data.getSaturationLevel()));
                if (food != data.getFoodLevel()) {
                    data.setFoodLevel(food);
                }
                if (saturation != data.getSaturationLevel()) {
                    data.setSaturation(saturation);
                }
            } finally {
                tsd$clamping = false;
            }
        }
        tsd$previouslyEnabled = enabled;
    }

    @ModifyConstant(method = {"eat(IF)V", "needsFood"}, constant = @Constant(intValue = 20))
    private int tsd$foodCap(int original) {
        return tsd$enabled() ? ExtendedFoodProgression.getMaxFood(tsd$owner) : original;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tsd$bindOwner(Player player, CallbackInfo callback) {
        tsd$setOwner(player);
    }

    @Inject(method = {"setFoodLevel", "setSaturation", "readAdditionalSaveData"}, at = @At("RETURN"))
    private void tsd$clampChanges(CallbackInfo callback) {
        if (tsd$enabled()) tsd$setOwner(tsd$owner);
    }

    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true)
    private float tsd$extraExhaustion(float amount) {
        return tsd$enabled() ? amount * ExtendedFoodData.exhaustionMultiplier(tsd$owner) : amount;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"), index = 0)
    private float tsd$extraNaturalRegeneration(float amount) {
        return tsd$enabled()
                ? amount * (float) (1 + ExtendedFoodProgression.getExtraBenefit(tsd$owner)) : amount;
    }

    @Unique
    private boolean tsd$enabled() {
        return tsd$owner != null && TSDConfig.EXTENDED_FOOD_STATS_ENABLED.get();
    }
}
