package xy177.twilightsparksdelight.common.item;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.registry.TSDComponents;

public class Experiment250Item extends Item {
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 6;
    private static final double BASE_CAPACITY = 250.0D;
    private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

    public Experiment250Item(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static ItemStack createStack(Item item, int level, double activity) {
        ItemStack stack = new ItemStack(item);
        setLevel(stack, level);
        setActivity(stack, activity);
        return stack;
    }

    public static int getLevel(ItemStack stack) {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL,
                stack.getOrDefault(TSDComponents.EXPERIMENT_LEVEL, MIN_LEVEL)));
    }

    public static void setLevel(ItemStack stack, int level) {
        int clamped = Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
        stack.set(TSDComponents.EXPERIMENT_LEVEL, clamped);
        setActivity(stack, getActivity(stack));
    }

    public static double getActivity(ItemStack stack) {
        double activity = stack.getOrDefault(TSDComponents.EXPERIMENT_ACTIVITY, 0.0D);
        return clampActivity(activity, getLevel(stack));
    }

    public static void setActivity(ItemStack stack, double activity) {
        stack.set(TSDComponents.EXPERIMENT_ACTIVITY, clampActivity(activity, getLevel(stack)));
    }

    public static double addActivity(ItemStack stack, double amount) {
        double old = getActivity(stack);
        setActivity(stack, old + Math.max(0.0D, amount));
        return getActivity(stack) - old;
    }

    public static double getCapacity(ItemStack stack) {
        return getCapacity(getLevel(stack));
    }

    public static double getCapacity(int level) {
        double capacity = BASE_CAPACITY;
        for (int i = MIN_LEVEL; i < Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level)); i++) {
            capacity *= 3.0D;
        }
        return capacity;
    }

    public static float getActivityStage(ItemStack stack) {
        double ratio = getActivity(stack) / getCapacity(stack);
        return ratio >= 2D / 3D ? 2F : ratio >= 1D / 3D ? 1F : 0F;
    }

    public static ResourceLocation getBoundMeat(ItemStack stack) {
        String id = stack.getOrDefault(TSDComponents.EXPERIMENT_BOUND_MEAT, "");
        if (id.isEmpty()) {
            return null;
        }
        try {
            return ResourceLocation.parse(id);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void setBoundMeat(ItemStack stack, ResourceLocation meat) {
        if (meat == null) {
            stack.remove(TSDComponents.EXPERIMENT_BOUND_MEAT);
        } else {
            stack.set(TSDComponents.EXPERIMENT_BOUND_MEAT, meat.toString());
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
                                                   InteractionHand usedHand) {
        if (!TSDConfig.EXPERIMENT_BINDING_MODE_ENABLED.get() || !player.isCrouching()
                || player.level().isClientSide) {
            return InteractionResult.PASS;
        }
        ResourceLocation entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                .getKey(target.getType());
        java.util.List<ResourceLocation> candidates =
                TSDConfig.getExperimentBindings().get(entityId);
        if (candidates == null || candidates.isEmpty()) {
            return InteractionResult.PASS;
        }

        ResourceLocation current = getBoundMeat(stack);
        ResourceLocation next = candidates.get(0);
        if (current != null) {
            for (int index = 0; index < candidates.size(); index++) {
                if (current.equals(candidates.get(index))) {
                    next = candidates.get((index + 1) % candidates.size());
                    break;
                }
            }
        }
        setBoundMeat(stack, next);
        player.displayClientMessage(Component.translatable(
                "twilight_spark_delight.message.experiment_250.bound",
                new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(next)).getHoverName()), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        setLevel(stack, getLevel(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("twilight_spark_delight.tooltip.experiment_250.level",
                getLevel(stack)));
        tooltip.add(Component.translatable("twilight_spark_delight.tooltip.experiment_250.activity",
                FORMAT.format(getActivity(stack)), FORMAT.format(getCapacity(stack))));
        if (getActivity(stack) > TSDConfig.EXPERIMENT_FATAL_ACTIVITY_COST.get()) {
            tooltip.add(Component.translatable("twilight_spark_delight.tooltip.experiment_250.remaining_revives",
                    getRemainingRevives(stack)));
        }
        ResourceLocation bound = getBoundMeat(stack);
        if (bound != null && TSDConfig.EXPERIMENT_BINDING_MODE_ENABLED.get()) {
            tooltip.add(Component.translatable("twilight_spark_delight.tooltip.experiment_250.bound_meat",
                    new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(bound)).getHoverName()));
        }
    }

    public static int getRemainingRevives(ItemStack stack) {
        double activity = getActivity(stack);
        double cost = TSDConfig.EXPERIMENT_FATAL_ACTIVITY_COST.get();
        if (activity <= cost) return 0;
        int limit = Math.max(0, TSDConfig.EXPERIMENT_FATAL_TRIGGERS_PER_SLEEP.get()
                - stack.getOrDefault(TSDComponents.EXPERIMENT_REVIVES, 0));
        return cost <= 0 ? limit : Math.min(limit, Math.max(0, (int) Math.ceil(activity / cost) - 1));
    }

    private static double clampActivity(double activity, int level) {
        if (!Double.isFinite(activity) || activity <= 0.0D) {
            return 0.0D;
        }
        return Math.min(getCapacity(level), activity);
    }
}
