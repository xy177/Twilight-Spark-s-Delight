package xy177.twilightsparksdelight.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At;
import xy177.twilightsparksdelight.common.effect.SizeEffectHelper;

@Mixin(Camera.class)
public abstract class ShrinkCameraMixin {
    @ModifyArg(method = "setup", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"), index = 0)
    private double tsd$thirdPersonDistance(double distance) {
        return currentEntity() instanceof Player player
                ? distance * SizeEffectHelper.getScale(player) : distance;
    }

    @ModifyConstant(method = "getNearPlane", constant = @Constant(doubleValue = 0.05000000074505806D))
    private double tsd$scaledNearPlane(double original) {
        return original * tsd$cameraScale();
    }

    @ModifyConstant(method = "getMaxZoom", constant = @Constant(floatValue = 0.1F))
    private float tsd$scaledCollisionPadding(float original) {
        return original * tsd$cameraScale();
    }

    private float tsd$cameraScale() {
        return currentEntity() instanceof Player player && SizeEffectHelper.getScale(player) < 1
                ? Math.min(1, SizeEffectHelper.getScale(player)) : 1;
    }

    private Entity currentEntity() {
        return ((Camera) (Object) this).getEntity();
    }
}
