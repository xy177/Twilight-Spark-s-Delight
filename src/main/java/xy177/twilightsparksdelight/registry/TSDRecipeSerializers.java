package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.recipe.Experiment250ReplicationRecipe;
import xy177.twilightsparksdelight.common.recipe.ReusableWaterShapelessRecipe;
import xy177.twilightsparksdelight.common.recipe.KeepingItemShapelessRecipe;
import xy177.twilightsparksdelight.common.recipe.HelmetCrabCuttingRecipe;

public final class TSDRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TwilightSparksDelight.MOD_ID);
    public static final DeferredRegister<net.minecraft.world.item.crafting.RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, TwilightSparksDelight.MOD_ID);
    public static final RegistryObject<net.minecraft.world.item.crafting.RecipeType<xy177.twilightsparksdelight.common.recipe.TSDDryingRecipe>>
            DRYING_TYPE = TYPES.register("drying", () -> new net.minecraft.world.item.crafting.RecipeType<>() {
                @Override public String toString() { return "twilight_spark_delight:drying"; }
            });
    public static final RegistryObject<RecipeSerializer<xy177.twilightsparksdelight.common.recipe.TSDDryingRecipe>>
            DRYING = SERIALIZERS.register("drying", xy177.twilightsparksdelight.common.recipe.TSDDryingRecipe.Serializer::new);

    public static final RegistryObject<RecipeSerializer<Experiment250ReplicationRecipe>> EXPERIMENT_250_REPLICATION =
            SERIALIZERS.register("experiment_250_replication",
                    () -> new SimpleCraftingRecipeSerializer<>(Experiment250ReplicationRecipe::new));
    public static final RegistryObject<RecipeSerializer<ReusableWaterShapelessRecipe>> REUSABLE_WATER_SHAPELESS =
            SERIALIZERS.register("reusable_water_shapeless",
                    () -> new SimpleCraftingRecipeSerializer<>(ReusableWaterShapelessRecipe::new));
    public static final RegistryObject<RecipeSerializer<KeepingItemShapelessRecipe>> KEEPING_ITEM_SHAPELESS =
            SERIALIZERS.register("keeping_item_shapeless", KeepingItemShapelessRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<HelmetCrabCuttingRecipe>> HELMET_CRAB_CUTTING =
            SERIALIZERS.register("helmet_crab_cutting", HelmetCrabCuttingRecipe.Serializer::new);

    private TSDRecipeSerializers() {
    }

}
