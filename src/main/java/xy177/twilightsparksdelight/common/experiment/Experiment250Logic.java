package xy177.twilightsparksdelight.common.experiment;

import java.util.List;
import java.util.Objects;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class Experiment250Logic {
    private static final int[] MAX_OUTPUTS = {2, 4, 8, 16, 32, 64};
    private static final TagKey<net.minecraft.world.item.Item> FOOD_DOUGH_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods/dough"));
    private static final TagKey<net.minecraft.world.item.Item> LEGACY_DOUGH_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dough"));

    private Experiment250Logic() {
    }

    public static ResourceLocation resolveTarget(ItemStack experiment, ItemStack input) {
        if (!(experiment.getItem() instanceof Experiment250Item) || input.isEmpty()) {
            return null;
        }
        if (TSDConfig.EXPERIMENT_BINDING_MODE_ENABLED.get()) {
            ResourceLocation bound = Experiment250Item.getBoundMeat(experiment);
            return bound != null && (input.is(FOOD_DOUGH_TAG) || input.is(LEGACY_DOUGH_TAG))
                    ? (isConfiguredMeat(bound) ? bound : null) : null;
        }
        ResourceLocation inputId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(input.getItem());
        return isConfiguredMeat(inputId) ? inputId : null;
    }

    public static ReplicationResult calculate(ItemStack experiment, ResourceLocation rawMeat) {
        if (!(experiment.getItem() instanceof Experiment250Item) || !isConfiguredMeat(rawMeat)) {
            return ReplicationResult.NONE;
        }
        double cost = getCost(rawMeat, Experiment250Item.getLevel(experiment));
        if (cost <= 0.0D || !Double.isFinite(cost)) return ReplicationResult.NONE;
        int stackLimit = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rawMeat)
                .getDefaultInstance().getMaxStackSize();
        int maximumCopies = Math.min(stackLimit, MAX_OUTPUTS[Experiment250Item.getLevel(experiment) - 1]) - 1;
        int copies = Math.min(maximumCopies,
                (int) Math.floor((Experiment250Item.getActivity(experiment) + 1.0E-9D) / cost));
        return copies <= 0 ? ReplicationResult.NONE
                : new ReplicationResult(copies + 1, copies * cost, cost);
    }

    public static boolean consume(ItemStack experiment, ReplicationResult result) {
        if (result == null || !result.valid()
                || Experiment250Item.getActivity(experiment) + 1.0E-9D < result.activityCost()) {
            return false;
        }
        Experiment250Item.setActivity(experiment,
                Experiment250Item.getActivity(experiment) - result.activityCost());
        return true;
    }

    public static UpgradeResult upgrade(List<ItemStack> inputs) {
        if (inputs == null || (inputs.size() != 2 && inputs.size() != 4)) {
            return UpgradeResult.NONE;
        }
        int level = -1;
        double activity = 0.0D;
        ResourceLocation binding = null;
        boolean sameBinding = true;
        boolean first = true;
        for (ItemStack input : inputs) {
            if (!(input.getItem() instanceof Experiment250Item)) {
                return UpgradeResult.NONE;
            }
            int inputLevel = Experiment250Item.getLevel(input);
            if (level < 0) {
                level = inputLevel;
            } else if (level != inputLevel) {
                return UpgradeResult.NONE;
            }
            activity += Experiment250Item.getActivity(input);
            ResourceLocation currentBinding = Experiment250Item.getBoundMeat(input);
            if (first) {
                binding = currentBinding;
                first = false;
            } else if (!Objects.equals(binding, currentBinding)) {
                sameBinding = false;
            }
        }
        if (level >= Experiment250Item.MAX_LEVEL) {
            return UpgradeResult.NONE;
        }
        int outputLevel = inputs.size() == 2 ? level + 1
                : Math.min(Experiment250Item.MAX_LEVEL, level + 2);
        int outputCount = inputs.size() == 4 && level == Experiment250Item.MAX_LEVEL - 1 ? 2 : 1;
        java.util.ArrayList<ItemStack> outputs = new java.util.ArrayList<>();
        for (int index = 0; index < outputCount; index++) {
            ItemStack output = Experiment250Item.createStack(TSDItems.EXPERIMENT_250.get(),
                    outputLevel, activity / outputCount);
            if (sameBinding && binding != null) {
                Experiment250Item.setBoundMeat(output, binding);
            }
            outputs.add(output);
        }
        return new UpgradeResult(outputs);
    }

    private static boolean isConfiguredMeat(ResourceLocation id) {
        return id != null && TSDConfig.getExperimentMeatActivityCost(id) > 0.0D;
    }

    public static double getCost(ResourceLocation id, int level) {
        double base = TSDConfig.getExperimentMeatActivityCost(id);
        return base * Math.max(0.0D,
                1.0D - TSDConfig.EXPERIMENT_ACTIVITY_DISCOUNT_PER_LEVEL.get() * (level - 1));
    }

    public record ReplicationResult(int outputCount, double activityCost, double activityCostPerCopy) {
        private static final ReplicationResult NONE = new ReplicationResult(0, 0.0D, 0.0D);
        public boolean valid() {
            return outputCount >= 2 && activityCost > 0.0D;
        }
    }

    public record UpgradeResult(List<ItemStack> outputs) {
        private static final UpgradeResult NONE = new UpgradeResult(List.of());
        public boolean valid() {
            return !outputs.isEmpty();
        }
    }
}
