package xy177.twilightsparksdelight;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.resources.ResourceLocation;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDCreativeTab;
import xy177.twilightsparksdelight.registry.TSDEffects;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDFluids;
import xy177.twilightsparksdelight.registry.TSDComponents;
import xy177.twilightsparksdelight.registry.TSDTriggers;
import xy177.twilightsparksdelight.registry.TSDRecipeSerializers;
import xy177.twilightsparksdelight.registry.TSDEntities;
import xy177.twilightsparksdelight.common.event.TSDEffectEvents;
import xy177.twilightsparksdelight.common.event.TSDFoodEvents;
import xy177.twilightsparksdelight.common.event.TSDLootEvents;
import xy177.twilightsparksdelight.common.event.TSDExtendedFoodEvents;
import xy177.twilightsparksdelight.common.event.TSDSizeEffectEvents;
import xy177.twilightsparksdelight.common.event.TSDExperiment250Events;
import xy177.twilightsparksdelight.common.event.TSDExperiment250ScepterEvents;
import xy177.twilightsparksdelight.common.event.TSDExperiment250PedestalEvents;
import xy177.twilightsparksdelight.common.event.TSDMossSpreadEvents;
import xy177.twilightsparksdelight.common.event.TSDPicklingEvents;
import xy177.twilightsparksdelight.common.event.TSDBrackenPacificationEvents;
import xy177.twilightsparksdelight.common.event.TSDRabbitPocketWatchEvents;
import xy177.twilightsparksdelight.common.event.TSDArmoredGiantEvents;
import xy177.twilightsparksdelight.common.event.TSDInteractionEvents;
import xy177.twilightsparksdelight.common.event.TSDPickupEvents;
import xy177.twilightsparksdelight.common.event.TSDCookingPotEvents;
import xy177.twilightsparksdelight.common.event.TSDFondueEvents;
import xy177.twilightsparksdelight.common.command.TSDCommands;
import net.minecraftforge.common.MinecraftForge;

/**
 * Common entry point for the Forge 1.20.1 edition.
 *
 * Content registration stays isolated from the legacy Forge implementation.
 */
@Mod(TwilightSparksDelight.MOD_ID)
public final class TwilightSparksDelight {
    public static final String MOD_ID = "twilight_spark_delight";
    public static final String MOD_NAME = "Twilight Spark's Delight";
    public static final String MOD_NAME_ZH = "暮光乐事";
    public static final String AUTHOR = "xy177";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public TwilightSparksDelight() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TSDBlocks.BLOCKS.register(modEventBus);
        TSDItems.ITEMS.register(modEventBus);
        TSDCreativeTab.TABS.register(modEventBus);
        TSDEffects.EFFECTS.register(modEventBus);
        modEventBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(TSDTriggers::register));
        TSDRecipeSerializers.SERIALIZERS.register(modEventBus);
        TSDRecipeSerializers.TYPES.register(modEventBus);
        xy177.twilightsparksdelight.registry.TSDLootModifiers.SERIALIZERS.register(modEventBus);
        TSDFluids.FLUID_TYPES.register(modEventBus);
        TSDFluids.FLUIDS.register(modEventBus);
        TSDBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        TSDEntities.ENTITIES.register(modEventBus);
        xy177.twilightsparksdelight.network.TSDNetwork.register();
        MinecraftForge.EVENT_BUS.register(xy177.twilightsparksdelight.common.event.TSDCapabilityEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDEffectEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDFoodEvents.class);
        MinecraftForge.EVENT_BUS.register(xy177.twilightsparksdelight.integration.TwilightDelightCompat.class);
        MinecraftForge.EVENT_BUS.register(xy177.twilightsparksdelight.integration.CopperCupCompat.class);
        MinecraftForge.EVENT_BUS.register(TSDLootEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDExtendedFoodEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDSizeEffectEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDExperiment250Events.class);
        MinecraftForge.EVENT_BUS.register(TSDExperiment250PedestalEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDMossSpreadEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDPicklingEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDBrackenPacificationEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDRabbitPocketWatchEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDArmoredGiantEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDInteractionEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDPickupEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDFondueEvents.class);
        MinecraftForge.EVENT_BUS.register(xy177.twilightsparksdelight.common.event.TSDExperiment250CuttingEvents.class);
        MinecraftForge.EVENT_BUS.register(TSDCommands.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TSDConfig.SPEC);
        LOGGER.info("{} is loading for Minecraft 1.20.1 Forge", MOD_NAME);
    }
}
