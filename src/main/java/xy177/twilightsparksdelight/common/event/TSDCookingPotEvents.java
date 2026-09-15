package xy177.twilightsparksdelight.common.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import twilightforest.init.TFItems;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.common.item.Experiment250Item;

import xy177.twilightsparksdelight.mixin.CookingPotAccess;

/**
 * Runtime cooking-pot support for the two dynamic Experiment 250 operations.
 *
 * These operations are intentionally kept outside Farmer's Delight's data
 * recipe manager because their inputs and outputs depend on stack components,
 * activity, binding, and the loaded configuration.
 */
public final class TSDCookingPotEvents {
    private static final int INPUT_SLOTS = 6;
    private static final int MEAL_SLOT = 6;
    private static final int CONTAINER_SLOT = 7;
    private static final int OUTPUT_SLOT = 8;
    private static final int PROCESS_TIME = 100;
    private static final String MODE = "twilight_spark_delight.cooking_pot_mode";
    private static final String PROGRESS = "twilight_spark_delight.cooking_pot_progress";
    private static final String KEY = "twilight_spark_delight.cooking_pot_key";
    private static final String LAST_TICK = "twilight_spark_delight.cooking_pot_last_tick";

    private TSDCookingPotEvents() {
    }

    public static void tickPot(CookingPotBlockEntity pot) {
        if (pot.getLevel() == null || pot.getLevel().isClientSide) return;
        List<ItemStack> inputs = inputs(pot);
        Operation operation = findOperation(pot, inputs);
        if (operation == null || !pot.isHeated() || !canStore(pot, operation)) {
            reset(pot);
            return;
        }
        long time = pot.getLevel().getGameTime();
        if (pot.getPersistentData().contains(LAST_TICK)
                && pot.getPersistentData().getLong(LAST_TICK) == time) return;
        pot.getPersistentData().putLong(LAST_TICK, time);

        String key = operation.key(pot);
        if (!key.equals(pot.getPersistentData().getString(KEY))
                || !operation.mode.equals(pot.getPersistentData().getString(MODE))) {
            pot.getPersistentData().putString(KEY, key);
            pot.getPersistentData().putString(MODE, operation.mode);
            pot.getPersistentData().putInt(PROGRESS, 0);
        }

        int progress = pot.getPersistentData().getInt(PROGRESS) + 1;
        pot.getPersistentData().putInt(PROGRESS, progress);
        var data = ((CookingPotAccess) pot).tsd$getCookingData();
        data.set(0, progress);
        data.set(1, PROCESS_TIME);
        if (progress < PROCESS_TIME) {
            pot.setChanged();
            return;
        }

        if (operation.mode.equals("upgrade")) {
            finishUpgrade(pot, operation.upgrade);
        } else {
            finishReplication(pot, operation.replication);
        }
        reset(pot);
    }

    private static Operation findOperation(CookingPotBlockEntity pot, List<ItemStack> inputs) {
        if (inputs.size() == 2 || inputs.size() == 4) {
            Experiment250Logic.UpgradeResult upgrade = Experiment250Logic.upgrade(inputs);
            if (upgrade.valid()) {
                return new Operation("upgrade", upgrade, null);
            }
        }
        if (inputs.size() == 2
                && TSDConfig.EXPERIMENT_WORKSTATION.get() == TSDConfig.ExperimentWorkstation.COOKING_POT) {
            int experimentSlot = -1;
            int sourceSlot = -1;
            for (int slot = 0; slot < INPUT_SLOTS; slot++) {
                ItemStack stack = pot.getInventory().getStackInSlot(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                if (stack.getItem() instanceof Experiment250Item && experimentSlot < 0) {
                    experimentSlot = slot;
                } else if (sourceSlot < 0) {
                    sourceSlot = slot;
                }
            }
            if (experimentSlot >= 0 && sourceSlot >= 0) {
                ItemStack experiment = pot.getInventory().getStackInSlot(experimentSlot);
                ResourceLocation target = Experiment250Logic.resolveTarget(
                        experiment, pot.getInventory().getStackInSlot(sourceSlot));
                Experiment250Logic.ReplicationResult replication =
                        Experiment250Logic.calculate(experiment, target);
                if (replication.valid()) {
                    return new Operation("replication", null, new Replication(
                            experimentSlot, sourceSlot, target, replication));
                }
            }
        }
        return null;
    }

    private static boolean canStore(CookingPotBlockEntity pot, Operation operation) {
        if (!pot.getInventory().getStackInSlot(MEAL_SLOT).isEmpty()
                || !pot.getInventory().getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            return false;
        }
        return true;
    }

    private static void finishUpgrade(CookingPotBlockEntity pot,
                                      Experiment250Logic.UpgradeResult upgrade) {
        ((CookingPotAccess) pot).tsd$setMealContainer(new ItemStack(TFItems.TRANSFORMATION_POWDER.get()));
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            pot.getInventory().setStackInSlot(slot, ItemStack.EMPTY);
        }

        List<ItemStack> outputs = upgrade.outputs();
        // The meal slot stores servings, just as FD stores unstackable soup.
        // Each finished item still requires one powder and leaves singly.
        pot.getInventory().setStackInSlot(MEAL_SLOT, outputs.getFirst().copyWithCount(outputs.size()));
        pot.setChanged();
    }

    private static void finishReplication(CookingPotBlockEntity pot, Replication replication) {
        Item item = BuiltInRegistries.ITEM.get(replication.outputId);
        if (item == net.minecraft.world.item.Items.AIR) {
            return;
        }
        ((CookingPotAccess) pot).tsd$setMealContainer(ItemStack.EMPTY);

        ItemStack experiment = pot.getInventory()
                .getStackInSlot(replication.experimentSlot).copyWithCount(1);
        if (!Experiment250Logic.consume(experiment, replication.result)) {
            return;
        }

        ItemStack source = pot.getInventory().getStackInSlot(replication.sourceSlot).copy();
        source.shrink(1);
        pot.getInventory().setStackInSlot(replication.sourceSlot,
                source.isEmpty() ? ItemStack.EMPTY : source);
        pot.getInventory().setStackInSlot(replication.experimentSlot, experiment);
        // This path has no serving container, even for configured items that
        // normally have a crafting remainder.
        pot.getInventory().setStackInSlot(OUTPUT_SLOT,
                new ItemStack(item, replication.result.outputCount()));
        pot.setChanged();
    }

    private static List<ItemStack> inputs(CookingPotBlockEntity pot) {
        List<ItemStack> inputs = new ArrayList<>(INPUT_SLOTS);
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = pot.getInventory().getStackInSlot(slot);
            if (!stack.isEmpty()) {
                inputs.add(stack);
            }
        }
        return inputs;
    }

    private static void reset(CookingPotBlockEntity pot) {
        if (pot.getPersistentData().contains(PROGRESS)
                || pot.getPersistentData().contains(KEY)
                || pot.getPersistentData().contains(MODE)) {
            pot.getPersistentData().remove(PROGRESS);
            pot.getPersistentData().remove(KEY);
            pot.getPersistentData().remove(MODE);
            ((CookingPotAccess) pot).tsd$getCookingData().set(0, 0);
            pot.setChanged();
        }
    }

    private record Operation(String mode, Experiment250Logic.UpgradeResult upgrade,
                             Replication replication) {
        private String key(CookingPotBlockEntity pot) {
            StringBuilder result = new StringBuilder(mode).append('|');
            for (int slot = 0; slot < INPUT_SLOTS; slot++) {
                result.append(pot.getInventory().getStackInSlot(slot).saveOptional(
                        pot.getLevel().registryAccess()).toString()).append('|');
            }
            return result.toString();
        }
    }

    private record Replication(int experimentSlot, int sourceSlot, ResourceLocation outputId,
                               Experiment250Logic.ReplicationResult result) {
    }
}
