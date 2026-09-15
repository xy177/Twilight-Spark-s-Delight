package xy177.twilightsparksdelight;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
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
import net.neoforged.neoforge.common.NeoForge;

/**
 * Common entry point for the NeoForge 1.21.1 edition.
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
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public TwilightSparksDelight(IEventBus modEventBus, ModContainer modContainer) {
        TSDBlocks.BLOCKS.register(modEventBus);
        TSDItems.ITEMS.register(modEventBus);
        TSDCreativeTab.TABS.register(modEventBus);
        TSDEffects.EFFECTS.register(modEventBus);
        TSDComponents.COMPONENTS.register(modEventBus);
        TSDTriggers.TRIGGERS.register(modEventBus);
        TSDRecipeSerializers.SERIALIZERS.register(modEventBus);
        TSDFluids.FLUID_TYPES.register(modEventBus);
        TSDFluids.FLUIDS.register(modEventBus);
        TSDBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        TSDEntities.ENTITIES.register(modEventBus);
        modEventBus.addListener(TwilightSparksDelight::registerCapabilities);
        modEventBus.addListener(TwilightSparksDelight::registerPayloads);
        modEventBus.addListener(TSDFoodEvents::modifyDefaultComponents);
        NeoForge.EVENT_BUS.register(TSDEffectEvents.class);
        NeoForge.EVENT_BUS.register(TSDFoodEvents.class);
        NeoForge.EVENT_BUS.register(xy177.twilightsparksdelight.integration.TwilightDelightCompat.class);
        NeoForge.EVENT_BUS.register(xy177.twilightsparksdelight.integration.CopperCupCompat.class);
        NeoForge.EVENT_BUS.register(TSDLootEvents.class);
        NeoForge.EVENT_BUS.register(TSDExtendedFoodEvents.class);
        NeoForge.EVENT_BUS.register(TSDSizeEffectEvents.class);
        NeoForge.EVENT_BUS.register(TSDExperiment250Events.class);
        NeoForge.EVENT_BUS.register(TSDExperiment250PedestalEvents.class);
        NeoForge.EVENT_BUS.register(TSDMossSpreadEvents.class);
        NeoForge.EVENT_BUS.register(TSDPicklingEvents.class);
        NeoForge.EVENT_BUS.register(TSDBrackenPacificationEvents.class);
        NeoForge.EVENT_BUS.register(TSDRabbitPocketWatchEvents.class);
        NeoForge.EVENT_BUS.register(TSDArmoredGiantEvents.class);
        NeoForge.EVENT_BUS.register(TSDInteractionEvents.class);
        NeoForge.EVENT_BUS.register(TSDPickupEvents.class);
        NeoForge.EVENT_BUS.register(TSDFondueEvents.class);
        NeoForge.EVENT_BUS.register(xy177.twilightsparksdelight.common.event.TSDExperiment250CuttingEvents.class);
        NeoForge.EVENT_BUS.register(TSDCommands.class);
        modContainer.registerConfig(ModConfig.Type.SERVER, TSDConfig.SPEC);
        LOGGER.info("{} {} is loading for Minecraft 1.21.1 NeoForge",
                MOD_NAME, modContainer.getModInfo().getVersion());
    }

    private static void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(xy177.twilightsparksdelight.network.FoodStatePayload.TYPE,
                xy177.twilightsparksdelight.network.FoodStatePayload.CODEC,
                xy177.twilightsparksdelight.network.FoodStatePayload::handle);
        event.registrar("1").playToClient(xy177.twilightsparksdelight.network.ExperimentActivationPayload.TYPE,
                xy177.twilightsparksdelight.network.ExperimentActivationPayload.CODEC,
                xy177.twilightsparksdelight.network.ExperimentActivationPayload::handle);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        xy177.twilightsparksdelight.integration.CuriosCompat.registerCapabilities(event);
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                TSDBlockEntities.GLORY_CRUCIBLE.get(),
                (blockEntity, side) -> blockEntity);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TSDBlockEntities.GIANT_COOKING_POT.get(),
                (pot, side) -> new vectorwing.farmersdelight.common.block.entity.inventory.CookingPotItemHandler(
                        pot.getInventory(), side == net.minecraft.core.Direction.UP
                                ? net.minecraft.core.Direction.UP : net.minecraft.core.Direction.DOWN));
    }
}
