package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import com.wdcftgg.farmersdelightlegacy.api.recipe.CookingPotRecipeApi;
import com.wdcftgg.farmersdelightlegacy.client.jei.CookingPotJeiRecipe;
import com.wdcftgg.farmersdelightlegacy.client.jei.JeiUids;
import com.wdcftgg.farmersdelightlegacy.common.recipe.CookingPotRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class TwilightSparksDelightJeiPlugin implements IModPlugin
{
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(
            new PicklingRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
            new Experiment250UpgradeRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
            new Experiment250ReplicationRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
            new GloryCrucibleRecipeCategory(
                registry.getJeiHelpers().getGuiHelper(),
                TSDJeiRecipeTypes.GLORY_CRUCIBLE_HEATING,
                "twilight_spark_delight.jei.glory_crucible.heating.title"
            ),
            new GloryCrucibleRecipeCategory(
                registry.getJeiHelpers().getGuiHelper(),
                TSDJeiRecipeTypes.GLORY_CRUCIBLE_BREWING,
                "twilight_spark_delight.jei.glory_crucible.brewing.title"
            )
        );
    }

    @Override
    public void register(IModRegistry registry)
    {
        registry.addRecipes(Collections.singletonList(new PicklingRecipeWrapper()), TSDJeiRecipeTypes.PICKLING);
        registry.addRecipes(GloryCrucibleJeiData.createHeatingRecipes(), TSDJeiRecipeTypes.GLORY_CRUCIBLE_HEATING);
        registry.addRecipes(
            GloryCrucibleJeiData.createBrewingRecipes(registry.getIngredientRegistry()),
            TSDJeiRecipeTypes.GLORY_CRUCIBLE_BREWING
        );
        registry.addRecipeCatalyst(
            new ItemStack(TSDBlocks.GLORY_CRUCIBLE),
            TSDJeiRecipeTypes.GLORY_CRUCIBLE_HEATING,
            TSDJeiRecipeTypes.GLORY_CRUCIBLE_BREWING
        );
        registry.addRecipeCatalyst(new ItemStack(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR), TSDJeiRecipeTypes.PICKLING);
        if (Loader.isModLoaded("fruits_delight_legacy")) {
            registry.addRecipeCatalyst(
                new ItemStack(TSDBlocks.GLORY_CRUCIBLE),
                "fruits_delight_legacy.cauldron",
                "fruits_delight_legacy.cauldron_heated"
            );
        }
        registry.addRecipeCatalyst(new ItemStack(TSDBlocks.PICKLED_BRACKEN_JAR), TSDJeiRecipeTypes.PICKLING);

        registry.addRecipes(createUpgradeRecipes(), TSDJeiRecipeTypes.EXPERIMENT_250_UPGRADE);
        registry.addRecipes(createReplicationRecipes(), TSDJeiRecipeTypes.EXPERIMENT_250_REPLICATION);

        ItemStack experiment250 = Experiment250Item.createStack(TSDItems.EXPERIMENT_250, 1, 0.0D);
        registry.addRecipeCatalyst(experiment250, TSDJeiRecipeTypes.EXPERIMENT_250_UPGRADE);
        registry.addRecipeCatalyst(experiment250, TSDJeiRecipeTypes.EXPERIMENT_250_REPLICATION);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.COOKING_POT), TSDJeiRecipeTypes.EXPERIMENT_250_UPGRADE);
        registry.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANT_COOKING_POT), TSDJeiRecipeTypes.EXPERIMENT_250_UPGRADE);
        if ("cutting_board".equals(TSDConfig.experiment250WorkstationMode)) {
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.CUTTING_BOARD), TSDJeiRecipeTypes.EXPERIMENT_250_REPLICATION);
        } else if ("cooking_pot".equals(TSDConfig.experiment250WorkstationMode)) {
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.COOKING_POT), TSDJeiRecipeTypes.EXPERIMENT_250_REPLICATION);
            registry.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANT_COOKING_POT), TSDJeiRecipeTypes.EXPERIMENT_250_REPLICATION);
        } else {
            registry.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), TSDJeiRecipeTypes.EXPERIMENT_250_REPLICATION);
        }
        registerExperiment250Information(registry, experiment250);
        registry.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANT_STOVE), JeiUids.CAMPFIRE);
        registry.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANT_COOKING_POT), JeiUids.COOKING_POT);
        registry.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANTS_STOVE), JeiUids.CAMPFIRE);
        registry.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANTS_COOKING_POT), JeiUids.COOKING_POT);
        registry.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANTS_COOKING_POT), TSDJeiRecipeTypes.EXPERIMENT_250_UPGRADE);
        if ("cooking_pot".equals(TSDConfig.experiment250WorkstationMode)) {
            registry.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANTS_COOKING_POT), TSDJeiRecipeTypes.EXPERIMENT_250_REPLICATION);
        }
        registerCopperCupRecipes(registry);
    }

    private static void registerCopperCupRecipes(IModRegistry registry)
    {
        if (!Loader.isModLoaded("miners_delight_bridge")) {
            return;
        }
        Item copperCup = ForgeRegistries.ITEMS.getValue(new ResourceLocation("miners_delight", "copper_cup"));
        if (copperCup == null) {
            return;
        }

        List<CookingPotJeiRecipe> recipes = new ArrayList<>();
        for (CookingPotRecipe recipe : CookingPotRecipeApi.getRecipes()) {
            Item cupFood = getCupFoodForRecipe(recipe.getResultStack());
            if (cupFood == null) {
                continue;
            }
            CookingPotRecipe cupRecipe = new CookingPotRecipe(
                recipe.getRecipeId() + "_copper_cup_jei",
                recipe.getIngredients(),
                new ItemStack(cupFood, 2),
                new ItemStack(copperCup, 2),
                recipe.getCookTime(),
                recipe.getExperience(),
                true
            );
            recipes.add(CookingPotJeiRecipe.of(cupRecipe));
        }
        if (!recipes.isEmpty()) {
            registry.addRecipes(recipes, JeiUids.COOKING_POT);
        }
    }

    private static Item getCupFoodForRecipe(ItemStack output)
    {
        if (output.isEmpty()) {
            return null;
        }
        if (output.getItem() == TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP) {
            return TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP;
        }
        if (output.getItem() == TSDItems.BOWL_OF_CHICKEN_AND_HYDRA_SOUP) {
            return TSDItems.CHICKEN_AND_HYDRA_SOUP_CUP;
        }
        return null;
    }

    private static List<Experiment250UpgradeRecipeWrapper> createUpgradeRecipes()
    {
        List<Experiment250UpgradeRecipeWrapper> recipes = new ArrayList<>();
        for (int level = 1; level < Experiment250Item.MAX_LEVEL; level++) {
            recipes.add(new Experiment250UpgradeRecipeWrapper(level, 2, level + 1, 1));
            if (level == Experiment250Item.MAX_LEVEL - 1) {
                recipes.add(new Experiment250UpgradeRecipeWrapper(level, 4, Experiment250Item.MAX_LEVEL, 2));
            } else {
                recipes.add(new Experiment250UpgradeRecipeWrapper(
                    level,
                    4,
                    Math.min(Experiment250Item.MAX_LEVEL, level + 2),
                    1
                ));
            }
        }
        return recipes;
    }

    private static List<Experiment250ReplicationRecipeWrapper> createReplicationRecipes()
    {
        List<Experiment250ReplicationRecipeWrapper> recipes = new ArrayList<>();
        for (ResourceLocation meatId : TSDConfig.getExperiment250ReplicationMeatIds()) {
            if (!ForgeRegistries.ITEMS.containsKey(meatId)) {
                continue;
            }
            recipes.add(new Experiment250ReplicationRecipeWrapper(meatId));
        }
        return recipes;
    }

    private static void registerExperiment250Information(IModRegistry registry, ItemStack experiment250)
    {
        addExperiment250InformationPage(registry, experiment250, "scepter");
        addExperiment250InformationPage(registry, experiment250, "pedestal");
        addExperiment250FatalInformationPage(registry, experiment250);
        if (TSDConfig.experiment250BindingModeEnabled) {
            addExperiment250InformationPage(registry, experiment250, "binding");
        }

        ItemStack scepter = new ItemStack(TFItems.lifedrain_scepter);
        addExperiment250InformationPage(registry, scepter, "scepter");
        addExperiment250InformationPage(registry, scepter, "pedestal");
        addExperiment250InformationPage(registry, new ItemStack(TFBlocks.trophy_pedestal), "pedestal");
    }

    private static void addExperiment250InformationPage(IModRegistry registry, ItemStack ingredient, String section)
    {
        String key = "twilight_spark_delight.jei.experiment_250.function." + section;
        registry.addIngredientInfo(ingredient, ItemStack.class, key, key + ".desc");
    }

    private static void addExperiment250FatalInformationPage(IModRegistry registry, ItemStack experiment250)
    {
        String locationKey = "twilight_spark_delight.jei.experiment_250.function.fatal.location."
            + TSDConfig.experiment250FatalProtectionItemLocation;
        DecimalFormat format = new DecimalFormat("0.##");
        List<String> lines = new ArrayList<>();
        lines.add("twilight_spark_delight.jei.experiment_250.function.fatal");
        lines.add(I18n.format(
            "twilight_spark_delight.jei.experiment_250.function.fatal.desc",
            I18n.format(locationKey),
            format.format(TSDConfig.experiment250FatalProtectionActivityCost),
            format.format(TSDConfig.experiment250FatalProtectionActivityCost),
            TSDConfig.experiment250FatalProtectionTriggersPerSleep
        ));
        if (!TSDConfig.experiment250FatalProtectionAllowsBossAttacks) {
            lines.add("twilight_spark_delight.jei.experiment_250.function.fatal.boss_blocked");
        } else if (!TSDConfig.experiment250FatalProtectionBossAttacksConsumeTrigger) {
            lines.add("twilight_spark_delight.jei.experiment_250.function.fatal.boss_free");
        }
        if (!TSDConfig.getExperiment250NonConsumingAttackers().isEmpty()) {
            String attackers = TSDConfig.getExperiment250NonConsumingAttackers().stream()
                .map(ResourceLocation::toString)
                .sorted()
                .collect(Collectors.joining(", "));
            lines.add(I18n.format(
                "twilight_spark_delight.jei.experiment_250.function.fatal.non_consuming",
                attackers
            ));
        }
        registry.addIngredientInfo(experiment250, ItemStack.class, lines.toArray(new String[0]));
    }
}
