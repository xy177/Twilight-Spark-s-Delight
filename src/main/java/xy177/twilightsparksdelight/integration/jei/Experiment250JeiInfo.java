package xy177.twilightsparksdelight.integration.jei;

import java.util.ArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.registry.TSDItems;

final class Experiment250JeiInfo {
    private static final String PREFIX = "twilight_spark_delight.jei.experiment_250.function.";

    static void register(IRecipeRegistration registration) {
        ItemStack stack = new ItemStack(TSDItems.EXPERIMENT_250.get());
        for (String function : new String[] {"scepter", "pedestal", "binding"}) {
            if (function.equals("binding") && !TSDConfig.EXPERIMENT_BINDING_MODE_ENABLED.get()) continue;
            registration.addIngredientInfo(stack, VanillaTypes.ITEM_STACK,
                    Component.translatable(PREFIX + function), Component.translatable(PREFIX + function + ".desc"));
        }
        var lines = new ArrayList<Component>();
        lines.add(Component.translatable(PREFIX + "fatal"));
        String location = switch (TSDConfig.EXPERIMENT_FATAL_ITEM_LOCATION.get()) {
            case OFFHAND -> "offhand";
            case INVENTORY -> "inventory";
            default -> "baubles_or_offhand";
        };
        String activity = new java.text.DecimalFormat("0.##").format(TSDConfig.EXPERIMENT_FATAL_ACTIVITY_COST.get());
        lines.add(Component.translatable(PREFIX + "fatal.desc",
                Component.translatable(PREFIX + "fatal.location." + location),
                activity, activity, TSDConfig.EXPERIMENT_FATAL_TRIGGERS_PER_SLEEP.get()));
        if (!TSDConfig.EXPERIMENT_FATAL_ALLOWS_BOSS_ATTACKS.get()) {
            lines.add(Component.translatable(PREFIX + "fatal.boss_blocked"));
        } else if (!TSDConfig.EXPERIMENT_FATAL_BOSS_ATTACKS_CONSUME_TRIGGER.get()) {
            lines.add(Component.translatable(PREFIX + "fatal.boss_free"));
        }
        var attackers = TSDConfig.EXPERIMENT_FATAL_NON_CONSUMING_ATTACKERS.get();
        if (!attackers.isEmpty()) {
            lines.add(Component.translatable(PREFIX + "fatal.non_consuming", String.join(", ", attackers)));
        }
        registration.addIngredientInfo(stack, VanillaTypes.ITEM_STACK, lines.toArray(Component[]::new));
    }

    private Experiment250JeiInfo() {}
}
