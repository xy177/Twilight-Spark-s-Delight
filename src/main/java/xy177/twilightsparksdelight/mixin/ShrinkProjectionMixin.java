package xy177.twilightsparksdelight.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import xy177.twilightsparksdelight.common.effect.SizeEffectHelper;

@Mixin(GameRenderer.class)
public abstract class ShrinkProjectionMixin {
    @ModifyConstant(method = "getProjectionMatrix", constant = @Constant(floatValue = 0.05F))
    private float tsd$scaledNearPlane(float original) {
        var player = Minecraft.getInstance().player;
        return player != null && SizeEffectHelper.getScale(player) < 1
                ? original * Math.min(1, SizeEffectHelper.getScale(player)) : original;
    }
}
