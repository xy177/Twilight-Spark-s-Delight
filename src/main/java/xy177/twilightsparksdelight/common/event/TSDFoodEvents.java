package xy177.twilightsparksdelight.common.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraft.server.level.ServerPlayer;
import xy177.twilightsparksdelight.registry.TSDEffects;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDTriggers;

/**
 * Food-specific state transitions that cannot be represented by FoodProperties.
 */
public final class TSDFoodEvents {
    private static final int EFFECT_DURATION = 3600;

    private TSDFoodEvents() {
    }

    public static void modifyDefaultComponents(net.neoforged.neoforge.event.ModifyDefaultComponentsEvent event) {
        var hydraChop = twilightforest.init.TFItems.HYDRA_CHOP.get();
        var original = hydraChop.components().get(net.minecraft.core.component.DataComponents.FOOD);
        if (original != null) {
            event.modify(hydraChop, components -> components.set(net.minecraft.core.component.DataComponents.FOOD,
                    new net.minecraft.world.food.FoodProperties(16, 16.0F, original.canAlwaysEat(),
                            original.eatSeconds(), original.usingConvertsTo(), original.effects())));
        }
    }

    @SubscribeEvent
    public static void onItemFinished(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        LivingEntity consumer = event.getEntity();
        if (consumer.level().isClientSide) {
            return;
        }
        for (var effect : xy177.twilightsparksdelight.common.food.NagaRiceVariant.effects(stack)) {
            consumer.addEffect(effect);
        }
        xy177.twilightsparksdelight.integration.TwilightDelightCompat.afterEating(consumer, stack);
        if (stack.is(TSDItems.DRINK_ME.get())) {
            changeSizeEffect(consumer, TSDEffects.SHRINK, TSDEffects.ENLARGE, 5);
        } else if (stack.is(TSDItems.EAT_ME.get())) {
            int level = changeSizeEffect(consumer, TSDEffects.ENLARGE, TSDEffects.SHRINK, 3);
            if (consumer instanceof ServerPlayer player && level >= 3
                    && !player.getPersistentData().getBoolean("twilight_spark_delight.enlarge_three_unlocked")) {
                player.getPersistentData().putBoolean(
                        "twilight_spark_delight.enlarge_three_unlocked", true);
                TSDTriggers.DONT_EAT_ME.get().trigger(player);
            }
        }
    }

    private static int changeSizeEffect(LivingEntity entity,
                                         net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> increase,
                                         net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> decrease,
                                         int maximumLevel) {
        MobEffectInstance opposite = entity.getEffect(decrease);
        if (opposite != null) {
            entity.removeEffect(decrease);
            if (opposite.getAmplifier() > 0) {
                entity.addEffect(new MobEffectInstance(decrease, EFFECT_DURATION,
                        opposite.getAmplifier() - 1, false, false, true));
            }
            return 0;
        }
        MobEffectInstance current = entity.getEffect(increase);
        int nextLevel = current == null ? 1 : Math.min(maximumLevel, current.getAmplifier() + 2);
        entity.addEffect(new MobEffectInstance(increase, EFFECT_DURATION, nextLevel - 1, false, false, true));
        return nextLevel;
    }
}
