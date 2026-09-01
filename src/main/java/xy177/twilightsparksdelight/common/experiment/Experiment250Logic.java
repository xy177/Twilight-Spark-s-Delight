package xy177.twilightsparksdelight.common.experiment;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Experiment250Logic
{
    private static final int[] MAX_OUTPUTS = {2, 4, 8, 16, 32, 64};

    private Experiment250Logic()
    {
    }

    public static ReplicationResult calculateReplication(ItemStack experiment250, ResourceLocation rawMeatId)
    {
        if (experiment250.isEmpty() || experiment250.getItem() != TSDItems.EXPERIMENT_250) {
            return ReplicationResult.NONE;
        }
        double baseCost = TSDConfig.getExperiment250MeatActivityCost(rawMeatId);
        if (baseCost <= 0.0D) {
            return ReplicationResult.NONE;
        }

        int level = Experiment250Item.getLevel(experiment250);
        double discount = Math.max(0.0D, Math.min(1.0D,
            TSDConfig.experiment250ActivityDiscountPerLevel * (level - 1)));
        double costPerCopy = baseCost * (1.0D - discount);
        if (costPerCopy <= 0.0D) {
            return ReplicationResult.NONE;
        }

        int maximumCopies = MAX_OUTPUTS[level - 1] - 1;
        int affordableCopies = (int) Math.floor((Experiment250Item.getActivity(experiment250) + 1.0E-9D) / costPerCopy);
        int replicatedCopies = Math.min(maximumCopies, Math.max(0, affordableCopies));
        if (replicatedCopies <= 0) {
            return ReplicationResult.NONE;
        }
        return new ReplicationResult(replicatedCopies + 1, replicatedCopies * costPerCopy, costPerCopy);
    }

    public static int getMaximumReplicationOutput(int level)
    {
        return MAX_OUTPUTS[Math.max(1, Math.min(Experiment250Item.MAX_LEVEL, level)) - 1];
    }

    public static double getReplicationCostPerCopy(int level, ResourceLocation rawMeatId)
    {
        double baseCost = TSDConfig.getExperiment250MeatActivityCost(rawMeatId);
        if (baseCost <= 0.0D) {
            return -1.0D;
        }
        int clampedLevel = Math.max(1, Math.min(Experiment250Item.MAX_LEVEL, level));
        double discount = Math.max(0.0D, Math.min(1.0D,
            TSDConfig.experiment250ActivityDiscountPerLevel * (clampedLevel - 1)));
        return baseCost * (1.0D - discount);
    }

    public static ResourceLocation resolveReplicationTarget(ItemStack experiment250, ItemStack input)
    {
        if (experiment250.isEmpty() || experiment250.getItem() != TSDItems.EXPERIMENT_250 || input.isEmpty()) {
            return null;
        }
        if (TSDConfig.experiment250BindingModeEnabled) {
            ResourceLocation boundMeat = Experiment250Item.getBoundMeat(experiment250);
            return boundMeat != null && isDough(input) && TSDConfig.isExperiment250RawMeat(boundMeat)
                ? boundMeat
                : null;
        }
        ResourceLocation inputId = input.getItem().getRegistryName();
        return TSDConfig.isExperiment250RawMeat(inputId) ? inputId : null;
    }

    private static boolean isDough(ItemStack stack)
    {
        for (ItemStack dough : OreDictionary.getOres("foodDough", false)) {
            if (OreDictionary.itemMatches(dough, stack, false)) {
                return true;
            }
        }
        return false;
    }

    public static boolean consumeReplicationActivity(ItemStack experiment250, ReplicationResult result)
    {
        if (result == null || !result.isValid()) {
            return false;
        }
        double activity = Experiment250Item.getActivity(experiment250);
        if (activity + 1.0E-9D < result.getActivityCost()) {
            return false;
        }
        Experiment250Item.setActivity(experiment250, activity - result.getActivityCost());
        return true;
    }

    public static UpgradeResult calculateUpgrade(List<ItemStack> inputs)
    {
        if (inputs == null || (inputs.size() != 2 && inputs.size() != 4)) {
            return UpgradeResult.NONE;
        }

        int level = -1;
        double totalActivity = 0.0D;
        ResourceLocation sharedBinding = null;
        boolean first = true;
        boolean sameBinding = true;
        for (ItemStack stack : inputs) {
            if (stack.isEmpty() || stack.getItem() != TSDItems.EXPERIMENT_250) {
                return UpgradeResult.NONE;
            }
            int stackLevel = Experiment250Item.getLevel(stack);
            if (level < 0) {
                level = stackLevel;
            } else if (level != stackLevel) {
                return UpgradeResult.NONE;
            }
            totalActivity += Experiment250Item.getActivity(stack);
            ResourceLocation binding = Experiment250Item.getBoundMeat(stack);
            if (first) {
                sharedBinding = binding;
                first = false;
            } else if (!Objects.equals(sharedBinding, binding)) {
                sameBinding = false;
            }
        }

        if (level >= Experiment250Item.MAX_LEVEL) {
            return UpgradeResult.NONE;
        }

        int outputCount = 1;
        int outputLevel;
        if (inputs.size() == 2) {
            outputLevel = level + 1;
        } else if (level == Experiment250Item.MAX_LEVEL - 1) {
            outputLevel = Experiment250Item.MAX_LEVEL;
            outputCount = 2;
        } else {
            outputLevel = Math.min(Experiment250Item.MAX_LEVEL, level + 2);
        }

        List<ItemStack> outputs = new ArrayList<>(outputCount);
        double activityPerOutput = totalActivity / outputCount;
        for (int index = 0; index < outputCount; index++) {
            ItemStack output = Experiment250Item.createStack(TSDItems.EXPERIMENT_250, outputLevel, activityPerOutput);
            if (sameBinding && sharedBinding != null) {
                Experiment250Item.setBoundMeat(output, sharedBinding);
            }
            outputs.add(output);
        }
        return new UpgradeResult(outputs);
    }

    public static final class ReplicationResult
    {
        private static final ReplicationResult NONE = new ReplicationResult(0, 0.0D, 0.0D);
        private final int outputCount;
        private final double activityCost;
        private final double activityCostPerCopy;

        private ReplicationResult(int outputCount, double activityCost, double activityCostPerCopy)
        {
            this.outputCount = outputCount;
            this.activityCost = activityCost;
            this.activityCostPerCopy = activityCostPerCopy;
        }

        public boolean isValid()
        {
            return outputCount >= 2 && activityCost > 0.0D;
        }

        public int getOutputCount()
        {
            return outputCount;
        }

        public double getActivityCost()
        {
            return activityCost;
        }

        public double getActivityCostPerCopy()
        {
            return activityCostPerCopy;
        }
    }

    public static final class UpgradeResult
    {
        private static final UpgradeResult NONE = new UpgradeResult(Collections.emptyList());
        private final List<ItemStack> outputs;

        private UpgradeResult(List<ItemStack> outputs)
        {
            this.outputs = outputs;
        }

        public boolean isValid()
        {
            return !outputs.isEmpty();
        }

        public List<ItemStack> getOutputs()
        {
            return outputs;
        }
    }
}
