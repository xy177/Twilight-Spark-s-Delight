package xy177.twilightsparksdelight.common.registry;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class TSDOreDictionary
{
    public static final String LIVEROOT_DOUGH = "foodLiverootDough";
    public static final String LIVEROOT_BREAD = "foodLiverootBread";
    public static final String RAW_WILD_BOAR_MEAT = "foodRawWildBoarMeat";
    public static final String RAW_BIGHORN_MUTTON = "foodRawBighornMutton";
    public static final String COOKED_WILD_BOAR_MEAT = "foodCookedWildBoarMeat";
    public static final String COOKED_BIGHORN_MUTTON = "foodCookedBighornMutton";
    public static final String BORSCHT_MEEF = "foodTwilightBorschtMeef";
    public static final String FOOD_TORCHBERRY_SAUCE = "foodTorchberrySauce";
    public static final String TRAIL_RATIONS_SEASONING = "foodTrailRationsSeasoning";
    public static final String TRAIL_RATIONS_MUSHROOM = "foodTrailRationsMushroom";
    public static final String RAW_MEEF = "foodRawMeef";
    public static final String COOKED_MEEF = "foodCookedMeef";
    public static final String INGOT_FIERY = "ingotFiery";
    public static final String CHICKEN_AND_HYDRA_SOUP_CHICKEN = "foodChickenAndHydraSoupChicken";
    public static final String BRACKEN_INGREDIENT = "foodTwilightBrackenIngredient";
    public static final String MILLION_POUND_WILD_BOAR = "foodMillionPoundWildBoar";
    public static final String NAGA_MIXED_RICE_PROTEIN = "foodNagaMixedRiceProtein";
    public static final String NAGA_MIXED_RICE_HYDRA = "foodNagaMixedRiceHydra";
    public static final String NAGA_MIXED_RICE_EXPERIMENT = "foodNagaMixedRiceExperiment";

    private TSDOreDictionary()
    {
    }

    public static void register()
    {
        OreDictionary.registerOre("foodDough", new ItemStack(TSDItems.LIVEROOT_DOUGH));
        OreDictionary.registerOre("foodBread", new ItemStack(TSDItems.LIVEROOT_BREAD));
        OreDictionary.registerOre(LIVEROOT_BREAD, new ItemStack(TSDItems.LIVEROOT_BREAD));
        OreDictionary.registerOre(LIVEROOT_DOUGH, new ItemStack(TSDItems.LIVEROOT_DOUGH));
        OreDictionary.registerOre(RAW_WILD_BOAR_MEAT, new ItemStack(TSDItems.RAW_WILD_BOAR_MEAT));
        OreDictionary.registerOre(RAW_BIGHORN_MUTTON, new ItemStack(TSDItems.RAW_BIGHORN_MUTTON));
        OreDictionary.registerOre(COOKED_WILD_BOAR_MEAT, new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT));
        OreDictionary.registerOre(COOKED_BIGHORN_MUTTON, new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON));
        OreDictionary.registerOre(RAW_WILD_BOAR_MEAT, new ItemStack(TSDItems.RAW_WILD_BOAR_MEAT_CUBES));
        OreDictionary.registerOre(RAW_BIGHORN_MUTTON, new ItemStack(TSDItems.RAW_BIGHORN_MUTTON_CHOP));
        OreDictionary.registerOre(COOKED_WILD_BOAR_MEAT, new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT_CUBES));
        OreDictionary.registerOre(COOKED_BIGHORN_MUTTON, new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON_CHOP));
        OreDictionary.registerOre(BORSCHT_MEEF, new ItemStack(TSDItems.MINO_MINCE));
        OreDictionary.registerOre(BORSCHT_MEEF, new ItemStack(TSDItems.MINO_PATTY));
        OreDictionary.registerOre(BORSCHT_MEEF, new ItemStack(net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("twilightforest", "raw_meef"))));
        OreDictionary.registerOre(BORSCHT_MEEF, new ItemStack(net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("twilightforest", "cooked_meef"))));
        OreDictionary.registerOre(FOOD_TORCHBERRY_SAUCE, new ItemStack(TSDItems.TORCHBERRY_SAUCE));
        OreDictionary.registerOre(TRAIL_RATIONS_SEASONING, new ItemStack(TSDItems.TORCHBERRY_SAUCE));
        OreDictionary.registerOre(TRAIL_RATIONS_SEASONING, new ItemStack(Items.MUSHROOM_STEW));
        OreDictionary.registerOre(TRAIL_RATIONS_MUSHROOM, new ItemStack(net.minecraft.init.Blocks.RED_MUSHROOM));
        OreDictionary.registerOre(TRAIL_RATIONS_MUSHROOM, new ItemStack(net.minecraft.init.Blocks.BROWN_MUSHROOM));
        OreDictionary.registerOre(RAW_MEEF, new ItemStack(TSDItems.MINO_MINCE));
        OreDictionary.registerOre(COOKED_MEEF, new ItemStack(TSDItems.MINO_PATTY));
        OreDictionary.registerOre(RAW_MEEF, new ItemStack(net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("twilightforest", "raw_meef"))));
        OreDictionary.registerOre(COOKED_MEEF, new ItemStack(net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("twilightforest", "cooked_meef"))));
        OreDictionary.registerOre(INGOT_FIERY, new ItemStack(TSDItems.FIERY_SLAG));
        OreDictionary.registerOre(CHICKEN_AND_HYDRA_SOUP_CHICKEN, new ItemStack(Items.CHICKEN));
        OreDictionary.registerOre(CHICKEN_AND_HYDRA_SOUP_CHICKEN, new ItemStack(Items.COOKED_CHICKEN));
        registerOreIfPresent(CHICKEN_AND_HYDRA_SOUP_CHICKEN, "farmersdelight:chicken_cuts");
        registerOreIfPresent(CHICKEN_AND_HYDRA_SOUP_CHICKEN, "farmersdelight:cooked_chicken_cuts");
        OreDictionary.registerOre(BRACKEN_INGREDIENT, new ItemStack(TSDItems.BRACKEN));
        OreDictionary.registerOre(BRACKEN_INGREDIENT, new ItemStack(TSDItems.PICKLED_BRACKEN));
        OreDictionary.registerOre(MILLION_POUND_WILD_BOAR, new ItemStack(TSDItems.RAW_WILD_BOAR_MEAT));
        OreDictionary.registerOre(MILLION_POUND_WILD_BOAR, new ItemStack(TSDItems.RAW_WILD_BOAR_MEAT_CUBES));
        OreDictionary.registerOre(MILLION_POUND_WILD_BOAR, new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT));
        OreDictionary.registerOre(MILLION_POUND_WILD_BOAR, new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT_CUBES));
        registerOreIfPresent(NAGA_MIXED_RICE_PROTEIN, "twilightforest:hydra_chop");
        OreDictionary.registerOre(NAGA_MIXED_RICE_PROTEIN, new ItemStack(TSDItems.EXPERIMENT_234));
        registerOreIfPresent(NAGA_MIXED_RICE_PROTEIN, "twilightdelight:hydra_piece");
        registerOreIfPresent(NAGA_MIXED_RICE_PROTEIN, "twilightdelight:experiment_113");
        registerOreIfPresent(NAGA_MIXED_RICE_HYDRA, "twilightforest:hydra_chop");
        registerOreIfPresent(NAGA_MIXED_RICE_HYDRA, "twilightdelight:hydra_piece");
        OreDictionary.registerOre(NAGA_MIXED_RICE_EXPERIMENT, new ItemStack(TSDItems.EXPERIMENT_234));
        registerOreIfPresent(NAGA_MIXED_RICE_EXPERIMENT, "twilightdelight:experiment_113");
        OreDictionary.registerOre("listAllwater", new ItemStack(Items.WATER_BUCKET));
        OreDictionary.registerOre("listAllwater", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER));
    }

    private static void registerOreIfPresent(String oreName, String itemId)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item != null) {
            OreDictionary.registerOre(oreName, new ItemStack(item));
        }
    }
}
