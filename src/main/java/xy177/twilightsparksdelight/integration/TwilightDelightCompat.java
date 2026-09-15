package xy177.twilightsparksdelight.integration;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import twilightforest.init.TFMobEffects;
import xy177.twilightsparksdelight.registry.TSDEffects;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class TwilightDelightCompat {
    private static Holder<MobEffect> effect(String name) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.fromNamespaceAndPath(
                "twilightdelight", name)).orElse(null);
    }

    public static List<MobEffectInstance> foodEffects(ItemStack stack, List<MobEffectInstance> original) {
        var result = new ArrayList<MobEffectInstance>();
        boolean frozen = stack.is(TSDItems.GELID_CRYSTAL.get()) || stack.is(TSDItems.DOUBLE_CROWN_ICE_CREAM.get())
                || stack.is(TSDItems.TWIN_RADIANCE_ICE_POP.get());
        boolean fire = stack.is(TSDItems.FIRE_BEETLE_FLAME_SAC.get())
                || stack.is(TSDItems.LABYRINTH_FLAVOR_SKEWER.get()) || stack.is(TSDItems.STIR_FRIED_BRACKEN.get())
                || "hydra".equals(stack.get(xy177.twilightsparksdelight.registry.TSDComponents.NAGA_INGREDIENT));
        for (var instance : original) {
            Holder<MobEffect> replacement = frozen && instance.is(TFMobEffects.FROSTY) ? effect("frozen_range")
                    : fire && instance.is(MobEffects.FIRE_RESISTANCE) ? effect("fire_range") : null;
            result.add(replacement == null ? instance : new MobEffectInstance(replacement,
                    instance.getDuration(), instance.getAmplifier(), false, false, true));
        }
        Holder<MobEffect> aurora = effect("aurora_glowing");
        if (aurora != null && stack.is(TSDItems.TWIN_RADIANCE_ICE_POP.get())) {
            result.add(new MobEffectInstance(aurora, 1800, 0, false, false, true));
        }
        return result;
    }

    public static void afterEating(LivingEntity entity, ItemStack stack) {
        var current = new ArrayList<>(entity.getActiveEffects());
        var replacements = foodEffects(stack, current);
        for (int index = 0; index < replacements.size(); index++) {
            var replacement = replacements.get(index);
            if (index < current.size()) {
                if (replacement == current.get(index)) continue;
                entity.removeEffect(current.get(index).getEffect());
            }
            entity.addEffect(replacement);
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        Holder<MobEffect> sadness = effect("temporal_sadness");
        if (sadness == null) return;
        var added = event.getEffectInstance();
        var sorrow = entity.getEffect(TSDEffects.SORROW);
        if (added.is(sadness) && (sorrow != null || entity.hasEffect(TSDEffects.GRIEF))) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            entity.removeEffect(sadness);
            if (sorrow != null) turnToGrief(entity, sorrow);
        } else if (added.is(TSDEffects.SORROW) && entity.hasEffect(sadness)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            entity.removeEffect(sadness);
            turnToGrief(entity, added);
        } else if (added.is(TSDEffects.GRIEF)) {
            entity.removeEffect(sadness);
        }
    }

    private static void turnToGrief(LivingEntity entity, MobEffectInstance sorrow) {
        entity.removeEffect(TSDEffects.SORROW);
        entity.getPersistentData().remove("tsd_sorrow_damage");
        entity.addEffect(new MobEffectInstance(TSDEffects.GRIEF, sorrow.getDuration(),
                sorrow.getAmplifier() + 1, false, false, true));
    }

    private TwilightDelightCompat() {}
}
