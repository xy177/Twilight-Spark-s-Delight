package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.recipe.Experiment250ReplicationRecipe;
import xy177.twilightsparksdelight.common.recipe.ReusableWaterShapelessRecipe;
import xy177.twilightsparksdelight.common.recipe.KeepingItemShapelessRecipe;
import xy177.twilightsparksdelight.common.recipe.HelmetCrabCuttingRecipe;

public final class TSDRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TwilightSparksDelight.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<Experiment250ReplicationRecipe>> EXPERIMENT_250_REPLICATION =
            SERIALIZERS.register("experiment_250_replication", Experiment250ReplicationRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ReusableWaterShapelessRecipe>> REUSABLE_WATER_SHAPELESS =
            SERIALIZERS.register("reusable_water_shapeless", ReusableWaterShapelessRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<KeepingItemShapelessRecipe>> KEEPING_ITEM_SHAPELESS =
            SERIALIZERS.register("keeping_item_shapeless", KeepingItemShapelessRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HelmetCrabCuttingRecipe>> HELMET_CRAB_CUTTING =
            SERIALIZERS.register("helmet_crab_cutting", HelmetCrabCuttingRecipe.Serializer::new);

    private TSDRecipeSerializers() {
    }

}
