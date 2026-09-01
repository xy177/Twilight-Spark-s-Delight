package xy177.twilightsparksdelight.common.registry;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.CrabItem;
import xy177.twilightsparksdelight.common.item.DoubleCrownIceCreamItem;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.item.MultiRandomCureFoodItem;
import xy177.twilightsparksdelight.common.item.PickledBrackenItem;
import xy177.twilightsparksdelight.common.item.RabbitPocketWatchItem;
import xy177.twilightsparksdelight.common.item.RandomCureFoodItem;
import xy177.twilightsparksdelight.common.item.TSDFoodItem;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDItems
{
    public static final String MILLION_POUND_MEAL_CRAFTED_TAG = "TsdMillionPoundMealCrafted";
    public static final String NAGA_MIXED_RICE_CRAFTED_TAG = "TsdNagaMixedRiceCrafted";
    public static final String NAGA_MIXED_RICE_INGREDIENT_TAG = "TsdNagaMixedRiceIngredient";
    public static final String NAGA_MIXED_RICE_HYDRA = "hydra";
    public static final String NAGA_MIXED_RICE_EXPERIMENT = "experiment";
    private static final List<Item> ITEMS = new ArrayList<>();
    private static final ResourceLocation COPPER_CUP_ID = new ResourceLocation("miners_delight", "copper_cup");
    private static final float LEG_SATURATION = 1.5F / (4.0F * 2.0F);
    private static final float COOKED_LEG_SATURATION = 3.5F / (6.0F * 2.0F);
    private static final float BEETLE_ORGAN_SATURATION = 3.0F / (5.0F * 2.0F);

    public static final Item LIVEROOT_CONE = register("liveroot_cone", new Item());
    public static final Item LIVEROOT_PIE_CRUST = register("liveroot_pie_crust", new Item());
    public static final Item LIVEROOT_DOUGH = register("liveroot_dough", new TSDFoodItem(2, 0.6F, false)
        .addEffect(new ResourceLocation("minecraft", "blindness"), 600, 0, 0.3F));
    public static final Item LIVEROOT_BREAD = register("liveroot_bread", new TSDFoodItem(6, 0.4167F, false));
    public static final Item RAW_WILD_BOAR_MEAT = register("raw_wild_boar_meat", new TSDFoodItem(3, 0.2667F, true));
    public static final Item RAW_BIGHORN_MUTTON = register("raw_bighorn_mutton", new TSDFoodItem(2, 0.4F, true));
    public static final Item COOKED_WILD_BOAR_MEAT = register("cooked_wild_boar_meat", new TSDFoodItem(7, 0.5143F, true));
    public static final Item COOKED_BIGHORN_MUTTON = register("cooked_bighorn_mutton", new TSDFoodItem(7, 0.5143F, true));
    public static final Item RAW_WILD_BOAR_MEAT_CUBES = register("raw_wild_boar_meat_cubes", new TSDFoodItem(2, 0.325F, true));
    public static final Item COOKED_WILD_BOAR_MEAT_CUBES = register("cooked_wild_boar_meat_cubes", new TSDFoodItem(4, 0.5125F, true));
    public static final Item RAW_BIGHORN_MUTTON_CHOP = register("raw_bighorn_mutton_chop", new TSDFoodItem(2, 0.325F, true));
    public static final Item COOKED_BIGHORN_MUTTON_CHOP = register("cooked_bighorn_mutton_chop", new TSDFoodItem(4, 0.5125F, true));
    public static final Item HERMIT_CRAB_LEG = register("hermit_crab_leg", new TSDFoodItem(4, LEG_SATURATION, true));
    public static final Item COOKED_HERMIT_CRAB_LEG = register("cooked_hermit_crab_leg", new TSDFoodItem(6, COOKED_LEG_SATURATION, true)
        .addEffect(new ResourceLocation("minecraft", "night_vision"), 3600, 0, 1.0F));
    public static final Item PINCH_BEETLE_LEG = register("pinch_beetle_leg", new TSDFoodItem(4, LEG_SATURATION, true));
    public static final Item COOKED_PINCH_BEETLE_LEG = register("cooked_pinch_beetle_leg", new TSDFoodItem(6, COOKED_LEG_SATURATION, true)
        .addEffect(new ResourceLocation("minecraft", "night_vision"), 3600, 0, 1.0F));
    public static final Item SLIME_BEETLE_LEG = register("slime_beetle_leg", new TSDFoodItem(4, LEG_SATURATION, true));
    public static final Item COOKED_SLIME_BEETLE_LEG = register("cooked_slime_beetle_leg", new TSDFoodItem(6, COOKED_LEG_SATURATION, true)
        .addEffect(new ResourceLocation("minecraft", "night_vision"), 3600, 0, 1.0F));
    public static final Item FIRE_BEETLE_LEG = register("fire_beetle_leg", new TSDFoodItem(4, LEG_SATURATION, true));
    public static final Item COOKED_FIRE_BEETLE_LEG = register("cooked_fire_beetle_leg", new TSDFoodItem(6, COOKED_LEG_SATURATION, true)
        .addEffect(new ResourceLocation("minecraft", "night_vision"), 3600, 0, 1.0F));
    public static final Item FIRE_BEETLE_FLAME_SAC = register("fire_beetle_flame_sac", new TSDFoodItem(5, BEETLE_ORGAN_SATURATION, false)
        .addEffect(new ResourceLocation("minecraft", "fire_resistance"), 3600, 0, 1.0F));
    public static final Item SLIME_BEETLE_HONEY_GLAND = register("slime_beetle_honey_gland", new TSDFoodItem(5, BEETLE_ORGAN_SATURATION, false)
        .addEffect(new ResourceLocation("minecraft", "regeneration"), 300, 0, 1.0F));
    public static final Item HERMIT_CRAB = register("hermit_crab", new CrabItem());
    public static final Item QUEST_RAM_MILK = register("quest_ram_milk", new RandomCureFoodItem(0, 0.0F, false, Items.GLASS_BOTTLE, EnumAction.DRINK).setAlwaysEdible().setMaxStackSize(16));
    public static final Item QUEST_RAM_CHEESE = register("quest_ram_cheese", new RandomCureFoodItem(6, 0.75F, false, Items.GLASS_BOTTLE, EnumAction.EAT));
    public static final Item TWILIGHT_SKEWER = register("twilight_skewer", new TSDFoodItem(12, 11.0F / (12.0F * 2.0F), false));
    public static final Item LABYRINTH_FLAVOR_SKEWER = register("labyrinth_flavor_skewer", new TSDFoodItem(14, 12.0F / (14.0F * 2.0F), false)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 3600, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "fire_resistance"), 5400, 0, 1.0F));
    public static final Item LABYRINTH_SASHIMI_MEDLEY = register("labyrinth_sashimi_medley", new TSDFoodItem(16, 10.0F / (16.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("minecraft", "night_vision"), 14400, 0, 1.0F)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 3600, 0, 1.0F));
    public static final Item LABYRINTH_A5_MUSHROOM_FLAVOR_DOUBLE_CHEESEBURGER = register("labyrinth_a5_mushroom_flavor_double_cheeseburger", new MultiRandomCureFoodItem(12, 18.6F / (12.0F * 2.0F), false, null, EnumAction.EAT, 3)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 5400, 0, 1.0F));
    public static final Item TORCHBERRY_SAUCE = register("torchberry_sauce", new TSDFoodItem(3, 2.2F / (3.0F * 2.0F), false, Items.BOWL, EnumAction.EAT));
    public static final Item LABYRINTH_TACO = register("labyrinth_taco", new TSDFoodItem(12, 14.4F / (12.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 3600, 0, 1.0F));
    public static final Item GELID_CRYSTAL = register("gelid_crystal", new TSDFoodItem(1, 0.0F, false)
        .addEffect(new ResourceLocation("twilightforest", "frosted"), 3600, 0, 1.0F));
    public static final Item LABYRINTH_MUSHROOM = register("labyrinth_mushroom", new TSDFoodItem(6, 0.6F, false)
        .addEffect(new ResourceLocation("minecraft", "nausea"), 60, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 1200, 0, 1.0F));
    public static final Item EXPERIMENT_PROTOTYPE = register("experiment_prototype", new Item());
    public static final Item EXPERIMENT_000 = register("experiment_000", new TSDFoodItem(3, 0.3F, true)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "symbiosis"), 1200, 0, 1.0F));
    public static final Item EXPERIMENT_234 = register("experiment_234", new TSDFoodItem(5, 0.0F, false)
        .addEffect(new ResourceLocation("minecraft", "hunger"), 1200, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "nausea"), 1200, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "sorrow"), 3600, 0, 1.0F));
    public static final Item ABYSS_PIE_SLICE = register("abyss_pie_slice", new TSDFoodItem(8, 10.0F / (8.0F * 2.0F), false)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "symbiosis"), 6000, 2, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "abyss_call"), 1800, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "night_vision"), 3600, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "sorrow"), 3600, 0, 1.0F));
    public static final Item EXPERIMENT_250 = register("experiment_250", new Experiment250Item());
    public static final Item FIERY_SLAG = register("fiery_slag", new Item());
    public static final Item RABBIT_POCKET_WATCH = register("rabbit_pocket_watch", new RabbitPocketWatchItem());
    public static final Item GRIDDLE_TENTACLE = register("griddle_tentacle", new TSDFoodItem(10, 12.5F / (10.0F * 2.0F), false)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "symbiosis"), 2400, 0, 1.0F));
    public static final Item SCOURGE_STEAK = register("scourge_steak", new TSDFoodItem(18, 16.0F / (18.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 3600, 0, 1.0F));
    public static final Item REDCAP_SPICE = register("redcap_spice", new Item());
    public static final Item DRINK_ME = register("drink_me", new TSDFoodItem(4, 2.0F / (4.0F * 2.0F), false, Items.GLASS_BOTTLE, EnumAction.DRINK)
        .addDisplayEffect(new ResourceLocation(TwilightSparksDelight.MODID, "shrink"), 3600, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "regeneration"), 600, 0, 1.0F));
    public static final Item EAT_ME = register("eat_me", new TSDFoodItem(5, 3.3F / (5.0F * 2.0F), false)
        .addDisplayEffect(new ResourceLocation(TwilightSparksDelight.MODID, "enlarge"), 3600, 0, 1.0F));
    public static final Item MINO_MINCE = register("mino_mince", new TSDFoodItem(2, 1.2F / (2.0F * 2.0F), true));
    public static final Item MINO_PATTY = register("mino_patty", new TSDFoodItem(6, 6.2F / (6.0F * 2.0F), true)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 600, 0, 1.0F));
    public static final Item TRAIL_RATIONS = register("trail_rations", new TSDFoodItem(12, 15.0F / (12.0F * 2.0F), false)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 2400, 0, 1.0F)
        .setUseDuration(15));
    public static final Item BRACKEN = register("bracken", new TSDFoodItem(3, 0.6F, false));
    public static final Item PICKLED_BRACKEN = register("pickled_bracken", new PickledBrackenItem());
    public static final Item BOWL_OF_TWILIGHT_BORSCHT = register("bowl_of_twilight_borscht", new TSDFoodItem(16, 23.0F / (16.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 7800, 0, 1.0F)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 4800, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 5400, 0, 1.0F));
    public static final Item TWILIGHT_BORSCHT_CUP = register("twilight_borscht_cup", new TSDFoodItem(8, 11.5F / (8.0F * 2.0F), false, null, EnumAction.EAT)
        .setContainerItem(COPPER_CUP_ID)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 5100, 0, 1.0F)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 3300, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 3600, 0, 1.0F));
    public static final Item PLATE_OF_TWILIGHT_BOAR_KNUCKLE = register("plate_of_twilight_boar_knuckle",
        new TSDFoodItem(18, 23.0F / (18.0F * 2.0F), false)
            .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 6000, 0, 1.0F)
            .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 6000, 1, 1.0F));
    public static final Item CREAM_OF_LABYRINTH_MUSHROOM_SOUP = register("cream_of_labyrinth_mushroom_soup", new MultiRandomCureFoodItem(14, 21.0F / (14.0F * 2.0F), false, Items.BOWL, EnumAction.EAT, 3)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 6000, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 2400, 0, 1.0F));
    public static final Item CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP = register("cream_of_labyrinth_mushroom_soup_cup", new MultiRandomCureFoodItem(7, 10.5F / (7.0F * 2.0F), false, null, EnumAction.EAT, 3)
        .setContainerItem(COPPER_CUP_ID)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 3900, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 1500, 0, 1.0F));
    public static final Item BOWL_OF_CHICKEN_AND_HYDRA_SOUP = register("bowl_of_chicken_and_hydra_soup", new TSDFoodItem(15, 23.0F / (15.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 6000, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "speed"), 3600, 1, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "strength"), 1800, 1, 1.0F));
    public static final Item CHICKEN_AND_HYDRA_SOUP_CUP = register("chicken_and_hydra_soup_cup", new TSDFoodItem(8, 11.0F / (8.0F * 2.0F), false, null, EnumAction.EAT)
        .setContainerItem(COPPER_CUP_ID)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 3900, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "speed"), 2400, 1, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "strength"), 1200, 1, 1.0F));
    public static final Item STIR_FRIED_BRACKEN = register("stir_fried_bracken", new TSDFoodItem(17, 19.0F / (17.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("minecraft", "fire_resistance"), 9600, 0, 1.0F));
    public static final Item HELMET_CRAB_LEG_SUSHI_ROLL = register("helmet_crab_leg_sushi_roll", new TSDFoodItem(12, 14.4F / (12.0F * 2.0F), false)
        .addEffect(new ResourceLocation("minecraft", "haste"), 5400, 1, 1.0F));
    public static final Item HELMET_CRAB_LEG_SUSHI = register("helmet_crab_leg_sushi", new TSDFoodItem(6, 6.0F / (6.0F * 2.0F), false)
        .addEffect(new ResourceLocation("minecraft", "haste"), 1800, 2, 1.0F));
    public static final Item TENTACLE_CHOW_MEIN = register("tentacle_chow_mein", new TSDFoodItem(16, 15.2F / (16.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 6000, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "symbiosis"), 6000, 1, 1.0F));
    public static final Item MILLION_POUND_MEAL = register("million_pound_meal", new TSDFoodItem(14, 19.0F / (14.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 3600, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 3600, 0, 1.0F)
        .addTooltip("twilight_spark_delight.tooltip.million_pound_meal.1", TextFormatting.GOLD)
        .addTooltip("twilight_spark_delight.tooltip.million_pound_meal.2", TextFormatting.GOLD));
    public static final Item TWILIGHT_CHEESE_FONDUE_COMPANION = register("twilight_cheese_fondue_companion", new TwilightCheeseFondueCompanionItem());
    public static final Item TWILIGHT_CHEESE_FONDUE_WITH_BREAD = register("twilight_cheese_fondue_with_bread", new MultiRandomCureFoodItem(16, 21.0F / (16.0F * 2.0F), false, null, EnumAction.EAT, 3)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 6000, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 5400, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "regeneration"), 600, 0, 1.0F));
    public static final Item TWILIGHT_SUPREME_SANDWICH = register("twilight_supreme_sandwich", new TSDFoodItem(15, 0.5334F, false));
    public static final Item DOUBLE_CROWN_ICE_CREAM = register("double_crown_ice_cream", new DoubleCrownIceCreamItem());
    public static final Item TWIN_RADIANCE_ICE_POP = register("twin_radiance_ice_pop", new TSDFoodItem(5, 3.2F / (5.0F * 2.0F), false)
        .addEffect(new ResourceLocation("minecraft", "resistance"), 3600, 0, 1.0F)
        .addEffect(new ResourceLocation("twilightforest", "frosted"), 3600, 0, 1.0F)
        .setAlwaysEdible());
    public static final Item SALT_ROASTED_HELMET_CRAB_CLAW = register("salt_roasted_helmet_crab_claw", new TSDFoodItem(12, 16.6F / (12.0F * 2.0F), false)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 3600, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "haste"), 5400, 1, 1.0F));
    public static final Item BOWL_OF_SALTED_CRAB_MEAT = register("bowl_of_salted_crab_meat", new TSDFoodItem(16, 21.0F / (16.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 6000, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "strength"), 5400, 0, 1.0F));
    public static final Item BOWL_OF_NAGA_MIXED_RICE = register("bowl_of_naga_mixed_rice", new TSDFoodItem(30, 30.0F / (30.0F * 2.0F), false, Items.BOWL, EnumAction.EAT)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 5400, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 6000, 1, 1.0F)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 6000, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "resistance"), 2400, 1, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "speed"), 2400, 2, 1.0F));
    public static final Item NAGA_MIXED_RICE_CUP = register("naga_mixed_rice_cup", new TSDFoodItem(15, 15.0F / (15.0F * 2.0F), false, null, EnumAction.EAT)
        .setContainerItem(COPPER_CUP_ID)
        .addEffect(new ResourceLocation("farmersdelight", "nourishment"), 3600, 0, 1.0F)
        .addEffect(new ResourceLocation(TwilightSparksDelight.MODID, "charge"), 3900, 1, 1.0F)
        .addEffect(new ResourceLocation("farmersdelight", "comfort"), 3900, 0, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "resistance"), 1500, 1, 1.0F)
        .addEffect(new ResourceLocation("minecraft", "speed"), 1500, 2, 1.0F));

    private TSDItems()
    {
    }

    public static void register(IForgeRegistry<Item> registry)
    {
        for (Item item : ITEMS) {
            registry.register(item);
        }
    }

    private static Item register(String name, Item item)
    {
        item.setRegistryName(TwilightSparksDelight.MODID, name);
        item.setUnlocalizedName(TwilightSparksDelight.MODID + "." + name);
        item.setCreativeTab(TwilightSparksDelight.CREATIVE_TAB);
        ITEMS.add(item);
        return item;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModelBakery.registerItemVariants(
            EXPERIMENT_250,
            new ResourceLocation(TwilightSparksDelight.MODID, "experiment_250"),
            new ResourceLocation(TwilightSparksDelight.MODID, "experiment_250_stage_2"),
            new ResourceLocation(TwilightSparksDelight.MODID, "experiment_250_stage_3")
        );
        for (Item item : ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
