package xy177.twilightsparksdelight.mixin;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xy177.twilightsparksdelight.common.effect.SizeEffectHelper;

@Mixin(Player.class)
public abstract class PlayerDimensionsMixin {
    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void tsd$scaledDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        float scale = SizeEffectHelper.getScale((Player) (Object) this);
        if (scale != 1.0F) cir.setReturnValue(cir.getReturnValue().scale(scale));
    }

    @Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
    private void tsd$scaledEyes(Pose pose, EntityDimensions size, CallbackInfoReturnable<Float> cir) {
        float scale = SizeEffectHelper.getScale((Player) (Object) this);
        if (scale != 1.0F) cir.setReturnValue(cir.getReturnValue() * scale);
    }
}
