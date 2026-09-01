package xy177.twilightsparksdelight.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.effect.ShrinkEffectHelper;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID, value = Side.CLIENT)
public final class TSDShrinkClientEvents
{
    private static final float VANILLA_THIRD_PERSON_DISTANCE = 4.0F;

    private TSDShrinkClientEvents()
    {
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        float scale = ShrinkEffectHelper.getScale(event.getEntityPlayer());
        if (Math.abs(scale - 1.0F) < 0.001F) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(event.getX(), event.getY(), event.getZ());
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-event.getX(), -event.getY(), -event.getZ());
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event)
    {
        if (ShrinkEffectHelper.hasScaleEffect(event.getEntityPlayer())) {
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public static void onFovModifier(EntityViewRenderEvent.FOVModifier event)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.gameSettings.thirdPersonView != 0) {
            return;
        }
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        float scale = ShrinkEffectHelper.getScale((EntityPlayer) event.getEntity());
        if (scale < 1.0F) {
            event.setFOV(event.getFOV() * (1.0F + (1.0F - scale) * 0.10F));
        } else if (scale > 1.0F) {
            event.setFOV(event.getFOV() / Math.min(2.0F, 1.0F + (scale - 1.0F) * 0.15F));
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.gameSettings.thirdPersonView <= 0 || !(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        float scale = Math.max(0.1F, Math.min(32.0F, ShrinkEffectHelper.getScale((EntityPlayer) event.getEntity())));
        if (Math.abs(scale - 1.0F) < 0.001F) {
            return;
        }
        float targetDistance = scale < 1.0F
            ? VANILLA_THIRD_PERSON_DISTANCE * (0.65F + scale * 0.35F)
            : VANILLA_THIRD_PERSON_DISTANCE * scale;
        float direction = minecraft.gameSettings.thirdPersonView == 2 ? 1.0F : -1.0F;
        GlStateManager.translate(0.0F, 0.0F, direction * (targetDistance - VANILLA_THIRD_PERSON_DISTANCE));
    }
}
