package xy177.twilightsparksdelight.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xy177.twilightsparksdelight.common.food.ExtendedFoodData;

@Mixin(net.minecraft.world.effect.MobEffect.class)
public abstract class HungerMobEffectMixin {
    @Redirect(method = "applyEffectTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void tsd$unscaledHungerExhaustion(Player player, float amount) {
        ExtendedFoodData.applyHungerExhaustion(player, amount);
    }
}
