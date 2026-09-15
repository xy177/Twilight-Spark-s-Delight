package xy177.twilightsparksdelight.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDItems;

/**
 * Optional client-side JEI integration.  The service entry is only discovered
 * by JEI, so the common mod never loads JEI classes when JEI is absent.
 */
@JeiPlugin
public final class TwilightSparksDelightJeiPlugin implements IModPlugin {
    public static final RecipeType<xy177.twilightsparksdelight.common.recipe.TSDDryingRecipe> DRYING =
            RecipeType.create(TwilightSparksDelight.MOD_ID, "drying", xy177.twilightsparksdelight.common.recipe.TSDDryingRecipe.class);
    public static final RecipeType<Experiment250JeiRecipes.Upgrade> CULTIVATION =
            RecipeType.create(TwilightSparksDelight.MOD_ID, "cultivation",
                    Experiment250JeiRecipes.Upgrade.class);
    public static final RecipeType<Experiment250JeiRecipes.Differentiation> DIFFERENTIATION =
            RecipeType.create(TwilightSparksDelight.MOD_ID, "differentiation",
                    Experiment250JeiRecipes.Differentiation.class);
    public static final RecipeType<TSDHuntingCategory.Drop> HUNTING =
            RecipeType.create(TwilightSparksDelight.MOD_ID, "hunting", TSDHuntingCategory.Drop.class);
    public static final RecipeType<TSDHarvestCategory.Harvest> HARVESTING =
            RecipeType.create(TwilightSparksDelight.MOD_ID, "harvesting", TSDHarvestCategory.Harvest.class);

    @Override
    public ResourceLocation getPluginUid() {
        return TwilightSparksDelight.id("jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime runtime) {
        if (!Boolean.getBoolean("tsd.clientValidation")) {
            return;
        }
        try {
            // The validation harness is excluded from the distributed JAR. Keep
            // this dev-only bridge reflective so production does not depend on it.
            Class<?> harness = Class.forName(
                    "xy177.twilightsparksdelight.test.ClientPortCapture");
            Object instance = harness.getDeclaredConstructor().newInstance();
            harness.getMethod("onRuntimeAvailable", mezz.jei.api.runtime.IJeiRuntime.class)
                    .invoke(instance, runtime);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Unable to start the client validation harness", error);
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new Experiment250UpgradeCategory(helper),
                new Experiment250DifferentiationCategory(helper),
                new TSDPicklingCategory(helper),
                new TSDHuntingCategory(helper),
                new TSDHarvestCategory(helper),
                new TSDDryingCategory(helper),
                new TSDGloryCrucibleCategory(helper, false),
                new TSDGloryCrucibleCategory(helper, true));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CULTIVATION, Experiment250JeiRecipes.upgrades());
        registration.addRecipes(DIFFERENTIATION, Experiment250JeiRecipes.differentiations());
        registration.addRecipes(PICKLING, java.util.List.of(TSDJeiDynamicRecipes.pickling()));
        registration.addRecipes(CRUCIBLE_HEATING, TSDJeiDynamicRecipes.crucibleHeating());
        registration.addRecipes(CRUCIBLE_BREWING, TSDJeiDynamicRecipes.crucibleBrewing(
                registration.getIngredientManager().getAllIngredients(mezz.jei.api.constants.VanillaTypes.ITEM_STACK)));
        registration.addRecipes(HUNTING, TSDHuntingCategory.recipes());
        registration.addRecipes(HARVESTING, TSDHarvestCategory.recipes());
        var level = net.minecraft.client.Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(DRYING, level.getRecipeManager().getAllRecipesFor(
                    xy177.twilightsparksdelight.registry.TSDRecipeSerializers.DRYING_TYPE.get()));
            registration.addRecipes(vectorwing.farmersdelight.integration.jei.FDRecipeTypes.COOKING,
                    xy177.twilightsparksdelight.integration.CopperCupCompat.recipeViews(level,
                            xy177.twilightsparksdelight.integration.CopperCupCompat.container()));
        }
        Experiment250JeiInfo.register(registration);
        if (net.minecraftforge.fml.ModList.get().isLoaded("fruitsdelight")) {
            TSDJeiFruitsCompat.registerInfo(registration);
        }
    }

    @Override
    public void registerAdvanced(mezz.jei.api.registration.IAdvancedRegistration registration) {
        registration.addRecipeCategoryDecorator(RecipeTypes.CRAFTING, new TSDWatchRecipeDecorator<>());
        registration.addRecipeCategoryDecorator(vectorwing.farmersdelight.integration.jei.FDRecipeTypes.COOKING,
                new TSDWatchRecipeDecorator<>());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TSDBlocks.DRYING_RACK.get()), DRYING);
        registration.addRecipeCatalyst(new ItemStack(TSDItems.EXPERIMENT_250.get()), CULTIVATION);
        registration.addRecipeCatalyst(new ItemStack(TSDItems.EXPERIMENT_250.get()), DIFFERENTIATION);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COOKING_POT.get()), CULTIVATION);
        registration.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANT_COOKING_POT.get()), CULTIVATION);
        registration.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANTS_COOKING_POT.get()), CULTIVATION);
        switch (xy177.twilightsparksdelight.TSDConfig.EXPERIMENT_WORKSTATION.get()) {
            case COOKING_POT -> {
                registration.addRecipeCatalyst(new ItemStack(ModBlocks.COOKING_POT.get()), DIFFERENTIATION);
                registration.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANT_COOKING_POT.get()), DIFFERENTIATION);
                registration.addRecipeCatalyst(new ItemStack(TSDBlocks.GIANTS_COOKING_POT.get()), DIFFERENTIATION);
            }
            case CUTTING_BOARD -> registration.addRecipeCatalyst(new ItemStack(ModBlocks.CUTTING_BOARD.get()), DIFFERENTIATION);
            case CRAFTING_TABLE -> registration.addRecipeCatalyst(new ItemStack(net.minecraft.world.item.Items.CRAFTING_TABLE), DIFFERENTIATION);
        }
        for (var stove : java.util.List.of(TSDBlocks.GIANT_STOVE.get(), TSDBlocks.GIANTS_STOVE.get())) {
            registration.addRecipeCatalyst(new ItemStack(stove), RecipeTypes.CAMPFIRE_COOKING);
        }
        for (var pot : java.util.List.of(TSDBlocks.GIANT_COOKING_POT.get(), TSDBlocks.GIANTS_COOKING_POT.get())) {
            registration.addRecipeCatalyst(new ItemStack(pot),
                    vectorwing.farmersdelight.integration.jei.FDRecipeTypes.COOKING);
        }
        registration.addRecipeCatalyst(new ItemStack(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR.get()), PICKLING);
        registration.addRecipeCatalyst(new ItemStack(TSDBlocks.PICKLED_BRACKEN_JAR.get()), PICKLING);
        registration.addRecipeCatalyst(new ItemStack(TSDBlocks.GLORY_CRUCIBLE.get()), CRUCIBLE_HEATING);
        registration.addRecipeCatalyst(new ItemStack(TSDBlocks.GLORY_CRUCIBLE.get()), CRUCIBLE_BREWING);
        if (net.minecraftforge.fml.ModList.get().isLoaded("fruitsdelight")) {
            TSDJeiFruitsCompat.registerCatalysts(registration);
        }
    }

    public static final RecipeType<TSDJeiDynamicRecipes.Pickling> PICKLING =
            RecipeType.create(TwilightSparksDelight.MOD_ID, "pickling",
                    TSDJeiDynamicRecipes.Pickling.class);
    public static final RecipeType<TSDJeiDynamicRecipes.GloryCrucible> CRUCIBLE_HEATING =
            RecipeType.create(TwilightSparksDelight.MOD_ID, "glory_crucible_heating",
                    TSDJeiDynamicRecipes.GloryCrucible.class);
    public static final RecipeType<TSDJeiDynamicRecipes.GloryCrucible> CRUCIBLE_BREWING =
            RecipeType.create(TwilightSparksDelight.MOD_ID, "glory_crucible_brewing",
                    TSDJeiDynamicRecipes.GloryCrucible.class);
}
