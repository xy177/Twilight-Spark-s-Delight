package xy177.twilightsparksdelight;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceApi;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import xy177.twilightsparksdelight.common.TSDCreativeTab;
import xy177.twilightsparksdelight.common.command.CommandGiveExperiment250;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.entity.EntityThrownPickledBracken;
import xy177.twilightsparksdelight.common.event.TSDAdvancements;
import xy177.twilightsparksdelight.common.network.TSDNetwork;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDFluids;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.registry.TSDOreDictionary;
import xy177.twilightsparksdelight.common.registry.TSDRecipes;
import xy177.twilightsparksdelight.common.tile.TileEntitySaltHelmetCrab;
import xy177.twilightsparksdelight.common.tile.TileEntityGloryCrucible;
import xy177.twilightsparksdelight.common.tile.TileEntityGlassJar;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantCookingPot;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantStove;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantsCookingPot;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantsStove;
import xy177.twilightsparksdelight.common.tile.TileEntityTwilightCheeseFondue;
import xy177.twilightsparksdelight.common.tile.TileEntityTwilightBorscht;
import xy177.twilightsparksdelight.common.tile.TileEntityTwilightBoarKnuckle;
import xy177.twilightsparksdelight.common.tile.TileEntityUnripePickledBrackenJar;
import xy177.twilightsparksdelight.common.tile.TileEntityNagaMixedRice;

@Mod(
    modid = TwilightSparksDelight.MODID,
    name = TwilightSparksDelight.NAME,
    version = TwilightSparksDelight.VERSION,
    dependencies = "required-after:farmersdelight;required-after:twilightforest;after:futuremc;after:farmers_future_delight;after:miners_delight_bridge;after:twilightdelight;after:jei;after:tconstruct;after:baubles",
    acceptedMinecraftVersions = "[1.12.2]"
)
@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public class TwilightSparksDelight
{
    public static final String MODID = "twilight_spark_delight";
    public static final String NAME = "Twilight Spark's Delight";
    public static final String VERSION = "1.0.0";
    public static final CreativeTabs CREATIVE_TAB = TSDCreativeTab.INSTANCE;

    public static Logger logger;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        TSDFluids.register();
        TSDConfig.load(event.getSuggestedConfigurationFile());
        TSDNetwork.init();
        TSDAdvancements.init();
        GameRegistry.registerTileEntity(TileEntityTwilightCheeseFondue.class, MODID + ":twilight_cheese_fondue");
        GameRegistry.registerTileEntity(TileEntityTwilightBorscht.class, MODID + ":twilight_borscht");
        GameRegistry.registerTileEntity(TileEntitySaltHelmetCrab.class, MODID + ":salt_helmet_crab");
        GameRegistry.registerTileEntity(TileEntityUnripePickledBrackenJar.class, MODID + ":unripe_pickled_bracken_jar");
        GameRegistry.registerTileEntity(TileEntityGlassJar.class, MODID + ":glass_jar");
        GameRegistry.registerTileEntity(TileEntityGloryCrucible.class, MODID + ":glory_crucible");
        GameRegistry.registerTileEntity(TileEntityGiantStove.class, MODID + ":giant_stove");
        GameRegistry.registerTileEntity(TileEntityGiantCookingPot.class, MODID + ":giant_cooking_pot");
        GameRegistry.registerTileEntity(TileEntityGiantsStove.class, MODID + ":giants_stove");
        GameRegistry.registerTileEntity(TileEntityGiantsCookingPot.class, MODID + ":giants_cooking_pot");
        GameRegistry.registerTileEntity(TileEntityNagaMixedRice.class, MODID + ":naga_mixed_rice");
        GameRegistry.registerTileEntity(TileEntityTwilightBoarKnuckle.class, MODID + ":twilight_boar_knuckle");
        EntityRegistry.registerModEntity(new net.minecraft.util.ResourceLocation(MODID, "pickled_bracken"), EntityThrownPickledBracken.class, "pickled_bracken", 1, this, 64, 10, true);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        HeatSourceApi.registerDirectHeatSourcePredicate(MODID + ":giant_stove_parts", (world, pos, state) ->
            state.getBlock() == TSDBlocks.GIANT_STOVE_PART && TSDBlocks.GIANT_STOVE_PART.isStoveLit(world, pos));
        HeatSourceApi.registerDirectHeatSourcePredicate(MODID + ":giants_stove_parts", (world, pos, state) ->
            state.getBlock() == TSDBlocks.GIANTS_STOVE_PART && TSDBlocks.GIANTS_STOVE_PART.isStoveLit(world, pos));
        TSDRecipes.registerRuntimeRecipes();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandGiveExperiment250());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        TSDItems.register(event.getRegistry());
        TSDBlocks.registerItemBlocks(event.getRegistry());
        TSDOreDictionary.register();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        TSDBlocks.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        TSDRecipes.registerCraftingRecipes(event.getRegistry());
    }
}
