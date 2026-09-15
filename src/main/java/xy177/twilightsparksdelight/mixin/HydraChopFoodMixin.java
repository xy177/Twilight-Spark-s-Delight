package xy177.twilightsparksdelight.mixin;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.init.TFItems;

@Mixin(Item.class)
public abstract class HydraChopFoodMixin {
    @Inject(method = "getFoodProperties", at = @At("RETURN"), cancellable = true)
    private void tsd$hydraNutrition(CallbackInfoReturnable<FoodProperties> cir) {
        if (!TFItems.HYDRA_CHOP.isPresent() || (Object) this != TFItems.HYDRA_CHOP.get()) return;
        FoodProperties original = cir.getReturnValue();
        if (original == null) return;
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(16).saturationMod(0.5F);
        if (original.isMeat()) builder.meat();
        if (original.canAlwaysEat()) builder.alwaysEat();
        if (original.isFastFood()) builder.fast();
        original.getEffects().forEach(effect -> builder.effect(
                () -> new net.minecraft.world.effect.MobEffectInstance(effect.getFirst()), effect.getSecond()));
        cir.setReturnValue(builder.build());
    }
}
