package xy177.twilightsparksdelight.client;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.client.event.EntityRenderersEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.registry.TSDEntities;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;

@EventBusSubscriber(modid = TwilightSparksDelight.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TSDClientEvents {
    private TSDClientEvents() {
    }

    public static void showExperimentActivation(net.minecraft.world.item.ItemStack stack) {
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.player == null) return;
        minecraft.gameRenderer.displayItemActivation(stack);
        minecraft.particleEngine.createTrackingEmitter(minecraft.player,
                net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING, 30);
        minecraft.level.playLocalSound(minecraft.player.getX(), minecraft.player.getY(),
                minecraft.player.getZ(), net.minecraft.sounds.SoundEvents.TOTEM_USE,
                minecraft.player.getSoundSource(), 1.0F, 1.0F, false);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TSDBlockEntities.MASON_JAR.get(), TSDMasonJarRenderer::new);
        event.registerBlockEntityRenderer(TSDBlockEntities.DRYING_RACK.get(), TSDDryingRackRenderer::new);
        event.registerEntityRenderer(TSDEntities.PICKLED_BRACKEN.get(), ThrownItemRenderer::new);
        event.registerBlockEntityRenderer(TSDBlockEntities.GIANT_STOVE.get(),
                TSDGiantKitchenRenderer::stove);
        event.registerBlockEntityRenderer(TSDBlockEntities.GIANT_COOKING_POT.get(),
                TSDGiantKitchenRenderer::cookingPot);
        event.registerBlockEntityRenderer(TSDBlockEntities.LARGE_FEAST.get(), TSDLargeFeastRenderer::new);
        event.registerBlockEntityRenderer(TSDBlockEntities.GLORY_CRUCIBLE.get(), TSDGloryCrucibleRenderer::new);
    }

    @SubscribeEvent
    public static void clientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.renderer.item.ItemProperties.register(
                    xy177.twilightsparksdelight.registry.TSDItems.EXPERIMENT_250.get(),
                    TwilightSparksDelight.id("activity_stage"), (stack, level, entity, seed) ->
                            xy177.twilightsparksdelight.common.item.Experiment250Item.getActivityStage(stack));
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(TSDExtraFoodHud.class);
            if (net.minecraftforge.fml.ModList.get().isLoaded("appleskin")) {
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
                        xy177.twilightsparksdelight.integration.appleskin.TSDAppleSkinCompat.class);
            }
        });
    }
}
