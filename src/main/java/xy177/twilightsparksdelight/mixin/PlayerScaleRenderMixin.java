package xy177.twilightsparksdelight.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xy177.twilightsparksdelight.common.effect.SizeEffectHelper;

@Mixin(PlayerRenderer.class)
public abstract class PlayerScaleRenderMixin {
    @Inject(method = "scale(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
            at = @At("TAIL"))
    private void tsd$scaleModel(AbstractClientPlayer player, PoseStack poses, float partialTick, CallbackInfo ci) {
        float scale = SizeEffectHelper.getScale(player);
        poses.scale(scale, scale, scale);
    }
}
