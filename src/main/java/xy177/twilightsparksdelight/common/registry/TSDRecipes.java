package xy177.twilightsparksdelight.common.registry;

import com.wdcftgg.farmersdelightlegacy.api.recipe.CookingPotRecipeApi;
import com.wdcftgg.farmersdelightlegacy.api.recipe.CuttingBoardRecipeApi;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HarvestDropRecipeApi;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropOutput;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropRecipeApi;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.HuntingDropRecipeManager.HuntingTargetMatcher;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.CampfireCookingRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import twilightforest.entity.boss.EntityTFKnightPhantom;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.item.GlassJarItemBlock;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.recipe.ReusableWaterShapelessOreRecipe;
import xy177.twilightsparksdelight.common.recipe.KeepingItemShapelessOreRecipe;
import xy177.twilightsparksdelight.common.recipe.Experiment250ReplicationRecipe;
import xy177.twilightsparksdelight.common.tile.TileEntityGlassJar;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TSDRecipes
{
    private static boolean runtimeRegistered;

    private TSDRecipes()
    {
    }

    public static void registerRuntimeRecipes()
    {
        if (runtimeRegistered) {
            return;
        }
        runtimeRegistered = true;

        registerFurnaceRecipes();
        registerCampfireRecipes();
        registerCuttingBoardRecipes();
        registerCookingPotRecipes();
        registerDryingRackRecipes();
        registerHuntingDropRecipes();
        registerHarvestDropRecipes();
    }

    public static void registerCraftingRecipes(IForgeRegistry<IRecipe> registry)
    {
        registerGlassJarRecipes(registry);
        registerLiverootDoughRecipe(registry);
        registerTwilightSupremeSandwichRecipe(registry);
        registerTwilightSkewerRecipe(registry);
        registerLabyrinthFlavorSkewerRecipe(registry);
        registerLabyrinthTacoRecipe(registry);
        registerTwilightCheeseFondueRecipe(registry);
        registerTwinRadianceIcePopRecipe(registry);
        registerHelmetCrabLegSushiRollRecipe(registry);
        registerTrailRationsRecipes(registry);
        registerTrophyPedestalRecipe(registry);
        registerGloryCrucibleRecipe(registry);
        registerGiantKitchenRecipes(registry);
        registerCrateRecipes(registry);
        Experiment250ReplicationRecipe experiment250Replication = new Experiment250ReplicationRecipe();
        experiment250Replication.setRegistryName(TwilightSparksDelight.MODID, "experiment_250_replication");
        registry.register(experiment250Replication);
    }

    private static void registerGlassJarRecipes(IForgeRegistry<IRecipe> registry)
    {
        ItemStack glassJarOutput = GlassJarItemBlock.createEmptyJar();
        glassJarOutput.setCount(4);
        ShapedOreRecipe glassJar = new ShapedOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "glass_jar"),
            glassJarOutput,
            "GLG",
            "G G",
            "GGG",
            'G', Blocks.GLASS,
            'L', new ItemStack(TFBlocks.twilight_log, 1, 0)
        );
        glassJar.setRegistryName(TwilightSparksDelight.MODID, "glass_jar");
        registry.register(glassJar);

        if (registry instanceof IForgeRegistryModifiable) {
            ((IForgeRegistryModifiable<IRecipe>) registry).remove(new ResourceLocation("twilightforest", "firefly_jar"));
        }

        ShapelessOreRecipe fireflyJar = new ShapelessOreRecipe(
            new ResourceLocation("twilightforest", "firefly_jar"),
            TileEntityGlassJar.createJarStack(
                new ItemStack(TFBlocks.firefly),
                TileEntityGlassJar.createDefaultLid(),
                0
            ),
            new ItemStack(TFBlocks.firefly),
            new ItemStack(TSDBlocks.GLASS_JAR, 1, 0)
        );
        fireflyJar.setRegistryName("twilightforest", "firefly_jar");
        registry.register(fireflyJar);

        ShapelessOreRecipe cicadaJar = new ShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "cicada_jar"),
            TileEntityGlassJar.createJarStack(
                new ItemStack(TFBlocks.cicada),
                TileEntityGlassJar.createCicadaLid(),
                0
            ),
            new ItemStack(TFBlocks.cicada),
            new ItemStack(TSDBlocks.GLASS_JAR, 1, 0)
        );
        cicadaJar.setRegistryName(TwilightSparksDelight.MODID, "cicada_jar");
        registry.register(cicadaJar);
    }

    private static void registerGiantKitchenRecipes(IForgeRegistry<IRecipe> registry)
    {
        Item giantCobblestone = item("twilightforest:giant_cobblestone");
        if (giantCobblestone == null) {
            return;
        }

        ShapedOreRecipe giantStove = new ShapedOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "giant_stove"),
            new ItemStack(TSDBlocks.GIANT_STOVE),
            "SSS",
            "SGS",
            "SSS",
            'S', new ItemStack(ModBlocks.STOVE),
            'G', new ItemStack(giantCobblestone)
        );
        giantStove.setRegistryName(TwilightSparksDelight.MODID, "giant_stove");
        registry.register(giantStove);

        ShapedOreRecipe giantCookingPot = new ShapedOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "giant_cooking_pot"),
            new ItemStack(TSDBlocks.GIANT_COOKING_POT),
            "PPP",
            "PGP",
            "PPP",
            'P', new ItemStack(ModBlocks.COOKING_POT),
            'G', new ItemStack(giantCobblestone)
        );
        giantCookingPot.setRegistryName(TwilightSparksDelight.MODID, "giant_cooking_pot");
        registry.register(giantCookingPot);
    }

    private static void registerCrateRecipes(IForgeRegistry<IRecipe> registry)
    {
        registerCrateRecipe(
            registry,
            "labyrinth_mushroom_crate",
            new ItemStack(TSDItems.LABYRINTH_MUSHROOM),
            new ItemStack(TSDBlocks.LABYRINTH_MUSHROOM_CRATE)
        );
        registerCrateRecipe(
            registry,
            "bracken_crate",
            new ItemStack(TSDItems.BRACKEN),
            new ItemStack(TSDBlocks.BRACKEN_CRATE)
        );
    }

    private static void registerCrateRecipe(IForgeRegistry<IRecipe> registry, String name, ItemStack ingredient,
        ItemStack crate)
    {
        ShapedOreRecipe packing = new ShapedOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, name),
            crate,
            "MMM",
            "MMM",
            "MMM",
            'M', ingredient
        );
        packing.setRegistryName(TwilightSparksDelight.MODID, name);
        registry.register(packing);

        ShapelessOreRecipe unpacking = new ShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, name + "_unpacking"),
            copyWithCount(ingredient, 9),
            crate
        );
        unpacking.setRegistryName(TwilightSparksDelight.MODID, name + "_unpacking");
        registry.register(unpacking);
    }

    private static void registerFurnaceRecipes()
    {
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.LIVEROOT_DOUGH), new ItemStack(TSDItems.LIVEROOT_BREAD), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.RAW_WILD_BOAR_MEAT), new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.RAW_BIGHORN_MUTTON), new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.RAW_WILD_BOAR_MEAT_CUBES), new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT_CUBES), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.RAW_BIGHORN_MUTTON_CHOP), new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON_CHOP), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.HERMIT_CRAB_LEG), new ItemStack(TSDItems.COOKED_HERMIT_CRAB_LEG), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.PINCH_BEETLE_LEG), new ItemStack(TSDItems.COOKED_PINCH_BEETLE_LEG), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.SLIME_BEETLE_LEG), new ItemStack(TSDItems.COOKED_SLIME_BEETLE_LEG), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.FIRE_BEETLE_LEG), new ItemStack(TSDItems.COOKED_FIRE_BEETLE_LEG), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.QUEST_RAM_MILK), new ItemStack(TSDItems.QUEST_RAM_CHEESE), 0.35F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(TSDItems.MINO_MINCE), new ItemStack(TSDItems.MINO_PATTY), 0.35F);
    }

    private static void registerCampfireRecipes()
    {
        CampfireCookingRecipeManager.registerScriptRecipe(id("liveroot_bread_campfire"), inputs(id("liveroot_dough")), new ItemStack(TSDItems.LIVEROOT_BREAD), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("cooked_wild_boar_meat_campfire"), inputs(id("raw_wild_boar_meat")), new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("cooked_bighorn_mutton_campfire"), inputs(id("raw_bighorn_mutton")), new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("cooked_wild_boar_meat_cubes_campfire"), inputs(id("raw_wild_boar_meat_cubes")), new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT_CUBES), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("cooked_bighorn_mutton_chop_campfire"), inputs(id("raw_bighorn_mutton_chop")), new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON_CHOP), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("cooked_hermit_crab_leg_campfire"), inputs(id("hermit_crab_leg")), new ItemStack(TSDItems.COOKED_HERMIT_CRAB_LEG), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("cooked_pinch_beetle_leg_campfire"), inputs(id("pinch_beetle_leg")), new ItemStack(TSDItems.COOKED_PINCH_BEETLE_LEG), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("cooked_slime_beetle_leg_campfire"), inputs(id("slime_beetle_leg")), new ItemStack(TSDItems.COOKED_SLIME_BEETLE_LEG), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("cooked_fire_beetle_leg_campfire"), inputs(id("fire_beetle_leg")), new ItemStack(TSDItems.COOKED_FIRE_BEETLE_LEG), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("quest_ram_cheese_campfire"), inputs(id("quest_ram_milk")), new ItemStack(TSDItems.QUEST_RAM_CHEESE), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("mino_patty_campfire"), inputs(id("mino_mince")), new ItemStack(TSDItems.MINO_PATTY), 600);
        CampfireCookingRecipeManager.registerScriptRecipe(id("griddle_tentacle_campfire"), inputs(id("experiment_000")), new ItemStack(TSDItems.GRIDDLE_TENTACLE), 600);
    }

    private static void registerCuttingBoardRecipes()
    {
        CuttingBoardRecipeApi.registerRecipe(
            id("liveroot_cone_from_liveroot"),
            "twilightforest:liveroot",
            null,
            id("liveroot_cone"),
            2,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("liveroot_cone_from_liveroots"),
            "twilightforest:root@1",
            null,
            id("liveroot_cone"),
            12,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("quest_ram_milk_from_trophy"),
            "minecraft:glass_bottle",
            "twilightforest:trophy@8",
            id("quest_ram_milk"),
            1,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("hermit_crab_from_anvil"),
            inputs(id("hermit_crab")),
            inputs("minecraft:anvil@*"),
            inputs(id("hermit_crab_leg"), id("hermit_crab_leg"), "twilightforest:armor_shard_cluster"),
            new int[] {3, 3, 5},
            new float[] {1.0F, TSDConfig.hermitCrabAnvilBonusLegChance, TSDConfig.hermitCrabAnvilArmorShardClusterChance}
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("gelid_crystal_from_snow_queen_trophy"),
            "minecraft:packed_ice",
            "twilightforest:trophy@5",
            id("gelid_crystal"),
            1,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("experiment_prototype_from_knight_phantom_trophy"),
            "twilightforest:knightmetal_ingot",
            "twilightforest:trophy@4",
            id("experiment_prototype"),
            1,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("experiment_000_from_experiment_prototype"),
            inputs(id("experiment_prototype")),
            inputs("ore:toolKnife"),
            inputs(id("experiment_000"), "twilightforest:armor_shard"),
            new int[] {2, 9},
            new float[] {1.0F, 1.0F}
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("labyrinth_mushroom_from_colony"),
            id("labyrinth_mushroom_colony"),
            "ore:toolKnife",
            id("labyrinth_mushroom"),
            5,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("raw_wild_boar_meat_cubes"),
            id("raw_wild_boar_meat"),
            "ore:toolKnife",
            id("raw_wild_boar_meat_cubes"),
            2,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("raw_bighorn_mutton_chop"),
            id("raw_bighorn_mutton"),
            "ore:toolKnife",
            id("raw_bighorn_mutton_chop"),
            2,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("bracken_from_twilight_bracken_colony"),
            id("twilight_bracken_colony"),
            "ore:toolKnife",
            id("bracken"),
            5,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("helmet_crab_leg_sushi"),
            id("helmet_crab_leg_sushi_roll"),
            "ore:toolKnife",
            id("helmet_crab_leg_sushi"),
            3,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("eat_me_from_experiment_115"),
            "twilightforest:experiment_115",
            id("redcap_spice"),
            id("eat_me"),
            1,
            1.0F
        );
        CuttingBoardRecipeApi.registerRecipe(
            id("mino_mince"),
            getMinoMinceInput(),
            "ore:toolKnife",
            id("mino_mince"),
            2,
            1.0F
        );
    }

    private static String getMinoMinceInput()
    {
        String twilightDelightMeefSlice = "twilightdelight:raw_meef_slice";
        if (Loader.isModLoaded("twilightdelight") && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(twilightDelightMeefSlice))) {
            return twilightDelightMeefSlice;
        }
        return "twilightforest:raw_meef";
    }

    private static void registerCookingPotRecipes()
    {
        CookingPotRecipeApi.registerRecipe(
            id("experiment_250"),
            inputs(id("experiment_234"), id("experiment_000")),
            Experiment250Item.createStack(TSDItems.EXPERIMENT_250, 1, 0.0D),
            100,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("quest_ram_cheese"),
            inputs(id("quest_ram_milk"), id("quest_ram_milk"), id("quest_ram_milk"), id("quest_ram_milk"), id("quest_ram_milk"), id("quest_ram_milk")),
            new ItemStack(TSDItems.QUEST_RAM_CHEESE, 10),
            stack("twilightforest:transformation_powder"),
            200,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("labyrinth_sashimi_medley"),
            inputs(id("hermit_crab_leg"), id("pinch_beetle_leg"), id("slime_beetle_leg"), id("fire_beetle_leg")),
            new ItemStack(TSDItems.LABYRINTH_SASHIMI_MEDLEY),
            new ItemStack(Items.BOWL),
            150,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("torchberry_sauce"),
            inputs("twilightforest:torchberries", "twilightforest:torchberries", "ore:cropOnion"),
            new ItemStack(TSDItems.TORCHBERRY_SAUCE, 2),
            new ItemStack(Items.BOWL),
            100,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("labyrinth_a5_mushroom_flavor_double_cheeseburger"),
            inputs(
                id("torchberry_sauce"),
                "twilightforest:twilight_plant@0",
                "ore:" + TSDOreDictionary.COOKED_MEEF,
                id("labyrinth_mushroom"),
                id("quest_ram_cheese"),
                "ore:" + TSDOreDictionary.COOKED_MEEF
            ),
            new ItemStack(TSDItems.LABYRINTH_A5_MUSHROOM_FLAVOR_DOUBLE_CHEESEBURGER),
            new ItemStack(TSDItems.LIVEROOT_BREAD),
            240,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("twilight_cheese_fondue_companion"),
            inputs(
                "ore:" + TSDOreDictionary.COOKED_MEEF,
                id("cooked_bighorn_mutton"),
                id("cooked_wild_boar_meat"),
                id("slime_beetle_honey_gland"),
                "twilightforest:torchberries",
                id("liveroot_bread")
            ),
            chefTaggedCompanion(),
            new ItemStack(Items.BOWL),
            240,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("double_crown_ice_cream"),
            inputs(
                "farmersdelight:tomato_sauce",
                "farmersdelight:tomato_sauce",
                "farmersdelight:tomato_sauce",
                "minecraft:snowball",
                id("gelid_crystal"),
                id("liveroot_dough")
            ),
            new ItemStack(TSDItems.DOUBLE_CROWN_ICE_CREAM),
            300,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("salt_helmet_crab"),
            inputs(
                id("hermit_crab"),
                id("torchberry_sauce"),
                id("torchberry_sauce"),
                "farmersdelight:canvas",
                "farmersdelight:canvas",
                "minecraft:slime_ball"
            ),
            new ItemStack(TSDBlocks.SALT_HELMET_CRAB),
            stack("farmersdelight:canvas"),
            600,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("scourge_steak"),
            inputs("twilightforest:hydra_chop", "minecraft:emerald", "minecraft:ender_pearl"),
            new ItemStack(TSDItems.SCOURGE_STEAK),
            new ItemStack(Items.BOWL),
            400,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("redcap_spice"),
            inputs(id("redcap_spice"), "twilightforest:twilight_plant@0", "minecraft:red_mushroom", "minecraft:gunpowder", "minecraft:sugar", "minecraft:redstone"),
            new ItemStack(TSDItems.REDCAP_SPICE, 3),
            stack("farmersdelight:canvas"),
            60,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("drink_me"),
            inputs(id("torchberry_sauce"), "minecraft:cooked_chicken", id("slime_beetle_honey_gland"), id("quest_ram_milk"), id("redcap_spice")),
            new ItemStack(TSDItems.DRINK_ME),
            new ItemStack(Items.GLASS_BOTTLE),
            140,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("unripe_pickled_bracken_jar"),
            inputs(id("bracken"), id("bracken"), id("bracken"), id("bracken"), "ore:listAllwater"),
            new ItemStack(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR),
            new ItemStack(TSDBlocks.GLASS_JAR),
            600,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("pickled_bracken_jar_watch"),
            inputs(id("bracken"), id("bracken"), id("bracken"), id("bracken"), "ore:listAllwater", id("rabbit_pocket_watch")),
            new ItemStack(TSDBlocks.PICKLED_BRACKEN_JAR),
            new ItemStack(TSDBlocks.GLASS_JAR),
            60,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("twilight_boar_knuckle"),
            inputs(
                "farmersdelight:ham",
                id("pickled_bracken"),
                "ore:cropOnion",
                "twilightforest:twilight_plant@0",
                id("labyrinth_mushroom"),
                id("torchberry_sauce")
            ),
            new ItemStack(TSDBlocks.TWILIGHT_BOAR_KNUCKLE),
            new ItemStack(Items.BOWL),
            1200,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("cream_of_labyrinth_mushroom_soup"),
            inputs(
                id("quest_ram_cheese"),
                id("labyrinth_mushroom"),
                id("labyrinth_mushroom"),
                "minecraft:red_mushroom",
                "minecraft:brown_mushroom"
            ),
            new ItemStack(TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP),
            new ItemStack(Items.BOWL),
            200,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("bowl_of_chicken_and_hydra_soup"),
            inputs(
                "ore:" + TSDOreDictionary.CHICKEN_AND_HYDRA_SOUP_CHICKEN,
                "twilightforest:hydra_chop",
                "twilightforest:naga_scale",
                id("redcap_spice"),
                "ore:cropOnion"
            ),
            new ItemStack(TSDItems.BOWL_OF_CHICKEN_AND_HYDRA_SOUP),
            new ItemStack(Items.BOWL),
            900,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("stir_fried_bracken"),
            inputs(
                "ore:" + TSDOreDictionary.RAW_WILD_BOAR_MEAT,
                "ore:" + TSDOreDictionary.RAW_BIGHORN_MUTTON,
                "ore:" + TSDOreDictionary.BRACKEN_INGREDIENT,
                "ore:" + TSDOreDictionary.BRACKEN_INGREDIENT,
                id("fire_beetle_flame_sac")
            ),
            new ItemStack(TSDItems.STIR_FRIED_BRACKEN),
            new ItemStack(Items.BOWL),
            200,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("twilight_borscht"),
            inputs(
                "minecraft:beetroot",
                "ore:cropTomato",
                "ore:" + TSDOreDictionary.BORSCHT_MEEF,
                id("torchberry_sauce"),
                "twilightforest:twilight_plant@0",
                id("liveroot_cone")
            ),
            new ItemStack(TSDBlocks.TWILIGHT_BORSCHT),
            new ItemStack(TSDBlocks.GLORY_CRUCIBLE),
            1800,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("tentacle_chow_mein"),
            inputs(
                id("experiment_000"),
                id("torchberry_sauce"),
                "ore:cropOnion",
                id("redcap_spice"),
                "ore:foodPasta"
            ),
            new ItemStack(TSDItems.TENTACLE_CHOW_MEIN),
            new ItemStack(Items.BOWL),
            200,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("million_pound_meal"),
            inputs(
                "ore:" + TSDOreDictionary.MILLION_POUND_WILD_BOAR,
                "farmersdelight:fried_egg",
                "ore:" + TSDOreDictionary.RAW_MEEF,
                "twilightforest:twilight_plant@0",
                "ore:" + TSDOreDictionary.BRACKEN_INGREDIENT,
                id("labyrinth_mushroom")
            ),
            millionPoundMealOutput(),
            new ItemStack(TSDItems.TORCHBERRY_SAUCE),
            200,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("naga_mixed_rice"),
            inputs(
                "twilightforest:trophy@0",
                "ore:" + TSDOreDictionary.NAGA_MIXED_RICE_PROTEIN,
                "twilightforest:meef_stroganoff",
                id("labyrinth_mushroom_crate"),
                id("bracken_crate"),
                "farmersdelight:rice_bag"
            ),
            nagaMixedRiceOutput(null),
            new ItemStack(Items.SHIELD),
            3600,
            1.0F
        );
        CookingPotRecipeApi.registerRecipe(
            id("abyss_pie"),
            inputs(
                id("experiment_000"),
                id("slime_beetle_honey_gland"),
                id("experiment_234"),
                id("redcap_spice"),
                id("liveroot_pie_crust"),
                "twilightforest:transformation_powder"
            ),
            new ItemStack(TSDBlocks.ABYSS_PIE),
            ItemStack.EMPTY,
            300,
            1.0F
        );
    }

    private static void registerDryingRackRecipes()
    {
        if (!Loader.isModLoaded("tconstruct")) {
            return;
        }
        try {
            Class<?> registry = Class.forName("slimeknights.tconstruct.library.TinkerRegistry");
            Method method = registry.getMethod("registerDryingRecipe", ItemStack.class, ItemStack.class, int.class);
            method.invoke(null, new ItemStack(TSDItems.QUEST_RAM_MILK), new ItemStack(TSDItems.QUEST_RAM_CHEESE, 2), 600);
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }
    }

    private static void registerHuntingDropRecipes()
    {
        registerAnyKillJei(id("wild_boar_meat_hunting"), "wild_boar", TSDItems.RAW_WILD_BOAR_MEAT, false);
        registerBurningKillJei(id("cooked_wild_boar_meat_hunting"), "wild_boar", TSDItems.COOKED_WILD_BOAR_MEAT, false);
        registerAnyKillJei(id("bighorn_mutton_hunting"), "bighorn_sheep", TSDItems.RAW_BIGHORN_MUTTON, false);
        registerBurningKillJei(id("cooked_bighorn_mutton_hunting"), "bighorn_sheep", TSDItems.COOKED_BIGHORN_MUTTON, false);
        registerAnyKillJei(id("gelid_crystal_hunting"), "snow_queen", TSDItems.GELID_CRYSTAL, false);
        registerAnyKillJei(id("labyrinth_mushroom_hunting"), "minoshroom", TSDItems.LABYRINTH_MUSHROOM, false);
        registerKnightPhantomJei(id("experiment_prototype_hunting"), new ItemStack(TSDItems.EXPERIMENT_PROTOTYPE));

        registerAnyKillJei(id("hermit_crab_leg_hunting"), "helmet_crab", TSDItems.HERMIT_CRAB_LEG, true);
        registerBurningKillJei(id("cooked_hermit_crab_leg_hunting"), "helmet_crab", TSDItems.COOKED_HERMIT_CRAB_LEG, true);
        registerAnyKillJei(id("pinch_beetle_leg_hunting"), "pinch_beetle", TSDItems.PINCH_BEETLE_LEG, true);
        registerBurningKillJei(id("cooked_pinch_beetle_leg_hunting"), "pinch_beetle", TSDItems.COOKED_PINCH_BEETLE_LEG, true);
        registerAnyKillJei(id("slime_beetle_leg_hunting"), "slime_beetle", TSDItems.SLIME_BEETLE_LEG, true);
        registerBurningKillJei(id("cooked_slime_beetle_leg_hunting"), "slime_beetle", TSDItems.COOKED_SLIME_BEETLE_LEG, true);
        registerAnyKillJei(id("fire_beetle_leg_hunting"), "fire_beetle", TSDItems.FIRE_BEETLE_LEG, true);
        registerBurningKillJei(id("cooked_fire_beetle_leg_hunting"), "fire_beetle", TSDItems.COOKED_FIRE_BEETLE_LEG, true);

        registerHuntingToolDrop(id("fire_beetle_flame_sac_hunting"), "fire_beetle", TSDItems.FIRE_BEETLE_FLAME_SAC);
        registerHuntingToolDrop(id("slime_beetle_honey_gland_hunting"), "slime_beetle", TSDItems.SLIME_BEETLE_HONEY_GLAND);
        registerHuntingToolDrop(id("hermit_crab_hunting"), "helmet_crab", TSDItems.HERMIT_CRAB);
        registerRedcapSpiceDrop(id("redcap_spice_redcap_hunting"), "redcap");
        registerRedcapSpiceDrop(id("redcap_spice_redcap_sapper_hunting"), "redcap_sapper");
        registerHuntingToolDrop(id("transformation_powder_death_tome_hunting"), "death_tome", TFItems.transformation_powder);
        registerRabbitPocketWatchJei();
    }

    private static void registerRedcapSpiceDrop(String recipeId, String entityPath)
    {
        HuntingDropRecipeApi.registerRecipeAdvance(
            recipeId,
            entity("twilightforest", entityPath),
            Collections.singletonList(HuntingDropOutput.of(new ItemStack(TSDItems.REDCAP_SPICE), 1.0F, 0.0F)),
            false,
            new ResourceLocation("twilightforest", entityPath),
            null,
            true,
            Collections.emptyList()
        );
    }

    private static void registerHarvestDropRecipes()
    {
        HarvestDropRecipeApi.registerRecipeJei(
            id("bracken_harvest"),
            new com.wdcftgg.farmersdelightlegacy.common.recipe.manager.HarvestDropRecipeManager.HarvestTargetMatcher()
            {
                @Override
                public boolean matches(IBlockState state)
                {
                    ResourceLocation blockId = state.getBlock().getRegistryName();
                    return blockId != null
                        && "twilightforest:twilight_plant".equals(blockId.toString())
                        && state.getBlock().getMetaFromState(state) == 3;
                }
            },
            Collections.singletonList(HuntingDropOutput.of(displayStack(TSDItems.BRACKEN, "twilight_spark_delight.jei.extra_by_looting"), 1.0F, 0.0F)),
            true,
            stackBlock("twilightforest:twilight_plant", 3),
            stackBlock("minecraft:grass", 0)
        );
    }

    private static void registerAnyKillJei(String recipeId, String entityPath, Item item, boolean huntingToolHint)
    {
        if (huntingToolHint) {
            HuntingDropRecipeApi.registerRecipeAdvanceJei(
                recipeId,
            entity("twilightforest", entityPath),
            doubleOutputs(item, true),
            false,
                new ResourceLocation("twilightforest", entityPath),
                null,
                false,
                Collections.emptyList()
            );
            return;
        }

        HuntingDropRecipeApi.registerRecipeJei(
            recipeId,
            entity("twilightforest", entityPath),
            new ItemStack(item),
            false,
            1.0F,
            0.0F,
            new ResourceLocation("twilightforest", entityPath),
            null,
            Collections.singletonList("twilight_spark_delight.jei.any_kill")
        );
    }

    private static void registerAnyKillJei(String recipeId, String entityPath, ItemStack outputStack)
    {
        HuntingDropRecipeApi.registerRecipeJei(
            recipeId,
            entity("twilightforest", entityPath),
            outputStack,
            false,
            1.0F,
            0.0F,
            new ResourceLocation("twilightforest", entityPath),
            null,
            Collections.singletonList("twilight_spark_delight.jei.any_kill")
        );
    }

    private static void registerKnightPhantomJei(String recipeId, ItemStack outputStack)
    {
        HuntingDropRecipeApi.registerRecipeJei(
            recipeId,
            entity("twilightforest", "knight_phantom"),
            outputStack,
            false,
            1.0F,
            0.0F,
            new ResourceLocation("twilightforest", "knight_phantom"),
            entity -> {
                if (entity instanceof EntityTFKnightPhantom) {
                    EntityTFKnightPhantom phantom = (EntityTFKnightPhantom) entity;
                    phantom.setNumber(0);
                    phantom.setItemStackToSlot(EntityEquipmentSlot.HEAD, stack("twilightforest:phantom_helmet"));
                    phantom.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack("twilightforest:phantom_chestplate"));
                    phantom.switchToFormation(EntityTFKnightPhantom.Formation.ATTACK_PLAYER_START);
                }
            },
            Collections.singletonList("twilight_spark_delight.jei.any_kill")
        );
    }

    private static void registerBurningKillJei(String recipeId, String entityPath, Item item, boolean huntingToolHint)
    {
        if (huntingToolHint) {
            HuntingDropRecipeApi.registerRecipeJei(
                recipeId,
                entity("twilightforest", entityPath),
                doubleOutputs(item, false),
                true,
                true,
                new ResourceLocation("twilightforest", entityPath)
            );
            return;
        }

        HuntingDropRecipeApi.registerRecipeJei(
            recipeId,
            entity("twilightforest", entityPath),
            new ItemStack(item),
            true,
            1.0F,
            0.0F,
            true,
            new ResourceLocation("twilightforest", entityPath)
        );
    }

    private static void registerHuntingToolDrop(String recipeId, String entityPath, Item item)
    {
        HuntingDropRecipeApi.registerRecipeAdvance(
            recipeId,
            entity("twilightforest", entityPath),
            Collections.singletonList(HuntingDropOutput.of(displayStack(item, "twilight_spark_delight.jei.hunting_tool_extra"), 1.0F, 0.0F)),
            false,
            new ResourceLocation("twilightforest", entityPath),
            null,
            false,
            Collections.emptyList()
        );
    }

    private static void registerRabbitPocketWatchJei()
    {
        HuntingDropRecipeApi.registerRecipeAdvanceJei(
            id("rabbit_pocket_watch_hunting"),
            entity("minecraft", "rabbit"),
            Collections.singletonList(HuntingDropOutput.of(new ItemStack(TSDItems.RABBIT_POCKET_WATCH), 1.0F, 0.0F)),
            false,
            new ResourceLocation("minecraft", "rabbit"),
            entity -> {
                if (entity instanceof EntityRabbit) {
                    ((EntityRabbit) entity).setRabbitType(99);
                    entity.setCustomNameTag(entity.getDisplayName().getUnformattedText());
                    entity.setAlwaysRenderNameTag(false);
                }
            },
            Collections.emptyList()
        );
    }

    private static void registerLiverootDoughRecipe(IForgeRegistry<IRecipe> registry)
    {
        ReusableWaterShapelessOreRecipe recipe = new ReusableWaterShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "liveroot_dough"),
            new ItemStack(TSDItems.LIVEROOT_DOUGH),
            new ItemStack(TSDItems.LIVEROOT_CONE),
            "listAllwater"
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "liveroot_dough");
        registry.register(recipe);
    }

    private static void registerTwilightSupremeSandwichRecipe(IForgeRegistry<IRecipe> registry)
    {
        ShapelessOreRecipe recipe = new ShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "twilight_supreme_sandwich"),
            new ItemStack(TSDItems.TWILIGHT_SUPREME_SANDWICH),
            TSDOreDictionary.LIVEROOT_BREAD,
            TSDOreDictionary.COOKED_WILD_BOAR_MEAT,
            item("twilightforest:cooked_venison"),
            TSDOreDictionary.COOKED_BIGHORN_MUTTON
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "twilight_supreme_sandwich");
        registry.register(recipe);
    }

    private static void registerTwilightSkewerRecipe(IForgeRegistry<IRecipe> registry)
    {
        ShapelessOreRecipe recipe = new ShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "twilight_skewer"),
            new ItemStack(TSDItems.TWILIGHT_SKEWER, 2),
            TSDOreDictionary.COOKED_BIGHORN_MUTTON,
            TSDOreDictionary.COOKED_WILD_BOAR_MEAT,
            item("twilightforest:torchberries"),
            new ItemStack(item("twilightforest:twilight_plant"), 1, 0),
            Items.STICK,
            Items.STICK
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "twilight_skewer");
        registry.register(recipe);
    }

    private static void registerLabyrinthFlavorSkewerRecipe(IForgeRegistry<IRecipe> registry)
    {
        ShapelessOreRecipe recipe = new ShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "labyrinth_flavor_skewer"),
            new ItemStack(TSDItems.LABYRINTH_FLAVOR_SKEWER, 2),
            new ItemStack(TSDItems.SLIME_BEETLE_HONEY_GLAND),
            new ItemStack(TSDItems.FIRE_BEETLE_FLAME_SAC),
            TSDOreDictionary.COOKED_MEEF,
            Items.STICK,
            Items.STICK
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "labyrinth_flavor_skewer");
        registry.register(recipe);
    }

    private static void registerLabyrinthTacoRecipe(IForgeRegistry<IRecipe> registry)
    {
        ShapelessOreRecipe recipe = new ShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "labyrinth_taco"),
            new ItemStack(TSDItems.LABYRINTH_TACO),
            item("twilightforest:maze_wafer"),
            new ItemStack(TSDItems.TORCHBERRY_SAUCE),
            TSDOreDictionary.COOKED_MEEF,
            new ItemStack(TSDItems.LABYRINTH_MUSHROOM),
            new ItemStack(item("twilightforest:twilight_plant"), 1, 0)
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "labyrinth_taco");
        registry.register(recipe);
    }

    private static void registerTwilightCheeseFondueRecipe(IForgeRegistry<IRecipe> registry)
    {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
        ingredients.set(0, Ingredient.fromStacks(stack("twilightforest:carminite")));
        ingredients.set(1, Ingredient.fromStacks(new ItemStack(TSDItems.QUEST_RAM_CHEESE)));
        ingredients.set(2, Ingredient.fromStacks(stack("twilightforest:carminite")));
        ingredients.set(3, Ingredient.fromStacks(stack("twilightforest:carminite")));
        ingredients.set(4, Ingredient.fromStacks(new ItemStack(TSDItems.TORCHBERRY_SAUCE)));
        ingredients.set(5, Ingredient.fromStacks(stack("twilightforest:carminite")));
        ingredients.set(6, Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT)));
        ingredients.set(7, Ingredient.fromStacks(stack("twilightforest:carminite")));
        ingredients.set(8, Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT)));

        ShapedRecipes recipe = new ShapedRecipes(
            new ResourceLocation(TwilightSparksDelight.MODID, "twilight_cheese_fondue").toString(),
            3,
            3,
            ingredients,
            new ItemStack(TSDBlocks.TWILIGHT_CHEESE_FONDUE)
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "twilight_cheese_fondue");
        registry.register(recipe);
    }

    private static void registerTwinRadianceIcePopRecipe(IForgeRegistry<IRecipe> registry)
    {
        ShapedOreRecipe recipe = new ShapedOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "twin_radiance_ice_pop"),
            new ItemStack(TSDItems.TWIN_RADIANCE_ICE_POP),
            " MT",
            "ACM",
            "SA ",
            'M', new ItemStack(TSDItems.QUEST_RAM_MILK),
            'T', new ItemStack(TSDItems.TORCHBERRY_SAUCE),
            'A', stack("twilightforest:aurora_block"),
            'C', new ItemStack(TSDItems.GELID_CRYSTAL),
            'S', new ItemStack(Items.STICK)
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "twin_radiance_ice_pop");
        registry.register(recipe);
    }

    private static void registerTrophyPedestalRecipe(IForgeRegistry<IRecipe> registry)
    {
        ShapedOreRecipe recipe = new ShapedOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "trophy_pedestal"),
            new ItemStack(TFBlocks.trophy_pedestal),
            " S ",
            "SKS",
            " S ",
            'S', new ItemStack(Blocks.STONEBRICK),
            'K', new ItemStack(TFItems.knightmetal_ingot)
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "trophy_pedestal");
        registry.register(recipe);
    }

    private static void registerGloryCrucibleRecipe(IForgeRegistry<IRecipe> registry)
    {
        ShapedOreRecipe recipe = new ShapedOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "glory_crucible"),
            new ItemStack(TSDBlocks.GLORY_CRUCIBLE),
            "A A",
            "A A",
            "AAA",
            'A', new ItemStack(TFItems.knightmetal_ingot)
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "glory_crucible");
        registry.register(recipe);
    }

    private static void registerTrailRationsRecipes(IForgeRegistry<IRecipe> registry)
    {
        ShapelessOreRecipe basic = new ShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "trail_rations"),
            new ItemStack(TSDItems.TRAIL_RATIONS),
            TSDOreDictionary.RAW_MEEF,
            TSDOreDictionary.TRAIL_RATIONS_SEASONING,
            TSDOreDictionary.TRAIL_RATIONS_MUSHROOM
        );
        basic.setRegistryName(TwilightSparksDelight.MODID, "trail_rations");
        registry.register(basic);

        KeepingItemShapelessOreRecipe watch = new KeepingItemShapelessOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "trail_rations_watch"),
            new ItemStack(TSDItems.TRAIL_RATIONS, 3),
            TSDItems.RABBIT_POCKET_WATCH,
            TSDOreDictionary.RAW_MEEF,
            TSDOreDictionary.TRAIL_RATIONS_SEASONING,
            TSDOreDictionary.TRAIL_RATIONS_MUSHROOM,
            TSDItems.RABBIT_POCKET_WATCH
        );
        watch.setRegistryName(TwilightSparksDelight.MODID, "trail_rations_watch");
        registry.register(watch);
    }

    private static void registerHelmetCrabLegSushiRollRecipe(IForgeRegistry<IRecipe> registry)
    {
        ShapedOreRecipe recipe = new ShapedOreRecipe(
            new ResourceLocation(TwilightSparksDelight.MODID, "helmet_crab_leg_sushi_roll"),
            new ItemStack(TSDItems.HELMET_CRAB_LEG_SUSHI_ROLL),
            "RCB",
            "MMM",
            'R', stack("farmersdelight:cooked_rice"),
            'C', new ItemStack(TSDItems.HERMIT_CRAB_LEG),
            'B', TSDOreDictionary.BRACKEN_INGREDIENT,
            'M', new ItemStack(item("twilightforest:twilight_plant"), 1, 0)
        );
        recipe.setRegistryName(TwilightSparksDelight.MODID, "helmet_crab_leg_sushi_roll");
        registry.register(recipe);
    }

    private static String id(String path)
    {
        return TwilightSparksDelight.MODID + ":" + path;
    }

    private static String[] inputs(String... values)
    {
        return values;
    }

    private static ItemStack stack(String id)
    {
        Item item = item(id);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static IBlockState stackBlock(String id, int meta)
    {
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
        return block == null ? net.minecraft.init.Blocks.AIR.getDefaultState() : block.getStateFromMeta(meta);
    }

    private static Item item(String id)
    {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
    }

    private static ItemStack displayStack(Item item, String tooltipKey)
    {
        ItemStack stack = new ItemStack(item);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("TsdJeiHint", tooltipKey);
        stack.setTagCompound(tag);
        return stack;
    }

    private static ItemStack chefTaggedCompanion()
    {
        ItemStack stack = new ItemStack(TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION);
        TwilightCheeseFondueCompanionItem.withFullDurability(stack);
        NBTTagCompound tag = new NBTTagCompound();
        if (stack.hasTagCompound()) {
            tag = stack.getTagCompound();
        }
        tag.setBoolean("RecordChefOnCraft", true);
        stack.setTagCompound(tag);
        return stack;
    }

    private static ItemStack millionPoundMealOutput()
    {
        ItemStack stack = new ItemStack(TSDItems.MILLION_POUND_MEAL);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(TSDItems.MILLION_POUND_MEAL_CRAFTED_TAG, true);
        stack.setTagCompound(tag);
        return stack;
    }

    private static ItemStack nagaMixedRiceOutput(String ingredient)
    {
        ItemStack stack = new ItemStack(TSDBlocks.NAGA_MIXED_RICE);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(TSDItems.NAGA_MIXED_RICE_CRAFTED_TAG, true);
        if (ingredient != null) {
            tag.setString(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG, ingredient);
        }
        stack.setTagCompound(tag);
        return stack;
    }

    private static ItemStack copyWithCount(ItemStack stack, int count)
    {
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    private static ItemStack displayStack(Item item, boolean huntingToolHint)
    {
        return huntingToolHint ? displayStack(item, "twilight_spark_delight.jei.hunting_tool_extra") : new ItemStack(item);
    }

    private static List<HuntingDropOutput> doubleOutputs(Item item, boolean anyKillHint)
    {
        ItemStack anyKillStack = anyKillHint ? displayStack(item, "twilight_spark_delight.jei.any_kill") : new ItemStack(item);
        return Arrays.asList(
            HuntingDropOutput.of(anyKillStack, 1.0F, 0.0F),
            HuntingDropOutput.of(displayStack(item, "twilight_spark_delight.jei.hunting_tool_extra"), 1.0F, 0.0F)
        );
    }

    private static HuntingTargetMatcher entity(String domain, String path)
    {
        return new HuntingTargetMatcher()
        {
            @Override
            public boolean matches(EntityLivingBase entity)
            {
                ResourceLocation id = EntityList.getKey(entity);
                return id != null
                    && domain.equals(id.getResourceDomain())
                    && path.equals(id.getResourcePath());
            }
        };
    }
}
