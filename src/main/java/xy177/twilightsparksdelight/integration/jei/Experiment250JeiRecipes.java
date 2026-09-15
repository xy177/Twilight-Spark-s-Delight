package xy177.twilightsparksdelight.integration.jei;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.registry.TSDItems;

final class Experiment250JeiRecipes {
    private Experiment250JeiRecipes() {}

    record Upgrade(int inputCount, ItemStack input, ItemStack output) {}
    record Row(int level, ItemStack experiment, ItemStack output, double cost, double batchCost) {}
    record Differentiation(ResourceLocation item, List<Row> rows) {}

    static List<Upgrade> upgrades() {
        List<Upgrade> recipes = new ArrayList<>();
        for (int level = 1; level < Experiment250Item.MAX_LEVEL; level++) {
            for (int count : new int[] {2, 4}) {
                ItemStack input = Experiment250Item.createStack(TSDItems.EXPERIMENT_250.get(), level, 0);
                var result = Experiment250Logic.upgrade(java.util.Collections.nCopies(count, input));
                if (result.valid()) {
                    recipes.add(new Upgrade(count, input,
                            result.outputs().get(0).copyWithCount(result.outputs().size())));
                }
            }
        }
        return recipes;
    }

    static List<Differentiation> differentiations() {
        List<Differentiation> recipes = new ArrayList<>();
        for (ResourceLocation id : TSDConfig.getExperimentMeatActivityCosts().keySet()) {
            var item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
            if (item == null || item.getDefaultInstance().isEmpty()) {
                continue;
            }
            List<Row> rows = new ArrayList<>();
            for (int level = 1; level <= Experiment250Item.MAX_LEVEL; level++) {
                ItemStack experiment = Experiment250Item.createStack(TSDItems.EXPERIMENT_250.get(),
                        level, Experiment250Item.getCapacity(level));
                if (TSDConfig.EXPERIMENT_BINDING_MODE_ENABLED.get()) {
                    Experiment250Item.setBoundMeat(experiment, id);
                }
                var result = Experiment250Logic.calculate(experiment, id);
                rows.add(new Row(level, experiment,
                        result.valid() ? new ItemStack(item, result.outputCount()) : ItemStack.EMPTY,
                        Experiment250Logic.getCost(id, level),
                        result.activityCost()));
            }
            if (rows.stream().anyMatch(row -> !row.output().isEmpty())) {
                recipes.add(new Differentiation(id, List.copyOf(rows)));
            }
        }
        return recipes;
    }
}
