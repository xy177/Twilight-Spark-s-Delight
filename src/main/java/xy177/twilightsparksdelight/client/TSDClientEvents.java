package xy177.twilightsparksdelight.client;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.registry.TSDEntities;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;

@EventBusSubscriber(modid = TwilightSparksDelight.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TSDClientEvents {
    private TSDClientEvents() {
    }

    @SubscribeEvent
    public static void registerFluidExtensions(
            net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
        event.registerFluidType(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
            @Override
            public net.minecraft.resources.ResourceLocation getStillTexture() {
                return TwilightSparksDelight.id("block/fiery_essence_still");
            }

            @Override
            public net.minecraft.resources.ResourceLocation getFlowingTexture() {
                return TwilightSparksDelight.id("block/fiery_essence_flow");
            }
        }, xy177.twilightsparksdelight.registry.TSDFluids.FIERY_BLOOD_TYPE.get(),
                xy177.twilightsparksdelight.registry.TSDFluids.FIERY_TEARS_TYPE.get());
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
        event.registerEntityRenderer(TSDEntities.PICKLED_BRACKEN.get(), ThrownItemRenderer::new);
        event.registerBlockEntityRenderer(TSDBlockEntities.GIANT_STOVE.get(),
                TSDGiantKitchenRenderer::stove);
        event.registerBlockEntityRenderer(TSDBlockEntities.GIANT_COOKING_POT.get(),
                TSDGiantKitchenRenderer::cookingPot);
        event.registerBlockEntityRenderer(TSDBlockEntities.LARGE_FEAST.get(), TSDLargeFeastRenderer::new);
        event.registerBlockEntityRenderer(TSDBlockEntities.GLORY_CRUCIBLE.get(), TSDGloryCrucibleRenderer::new);
    }

    @SubscribeEvent
    public static void clientSetup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.renderer.item.ItemProperties.register(
                    xy177.twilightsparksdelight.registry.TSDItems.EXPERIMENT_250.get(),
                    TwilightSparksDelight.id("activity_stage"), (stack, level, entity, seed) ->
                            xy177.twilightsparksdelight.common.item.Experiment250Item.getActivityStage(stack));
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(TSDExtraFoodHud.class);
            if (net.neoforged.fml.ModList.get().isLoaded("appleskin")) {
                net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(
                        xy177.twilightsparksdelight.integration.appleskin.TSDAppleSkinCompat.class);
            }
        });
    }
}
