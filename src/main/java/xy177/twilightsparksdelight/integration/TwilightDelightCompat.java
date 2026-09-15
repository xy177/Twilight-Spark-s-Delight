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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import twilightforest.init.TFMobEffects;
import xy177.twilightsparksdelight.registry.TSDEffects;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class TwilightDelightCompat {
    private static MobEffect effect(String name) {
        return BuiltInRegistries.MOB_EFFECT.getOptional(new ResourceLocation(
                "twilightdelight", name)).orElse(null);
    }

    public static List<MobEffectInstance> foodEffects(ItemStack stack, List<MobEffectInstance> original) {
        var result = new ArrayList<MobEffectInstance>();
        boolean frozen = stack.is(TSDItems.GELID_CRYSTAL.get()) || stack.is(TSDItems.DOUBLE_CROWN_ICE_CREAM.get())
                || stack.is(TSDItems.TWIN_RADIANCE_ICE_POP.get());
        boolean fire = stack.is(TSDItems.FIRE_BEETLE_FLAME_SAC.get())
                || stack.is(TSDItems.LABYRINTH_FLAVOR_SKEWER.get()) || stack.is(TSDItems.STIR_FRIED_BRACKEN.get())
                || "hydra".equals(xy177.twilightsparksdelight.registry.TSDComponents.NAGA_INGREDIENT.get(stack));
        for (var instance : original) {
            MobEffect replacement = frozen && instance.getEffect() == TFMobEffects.FROSTY.get() ? effect("frozen_range")
                    : fire && instance.getEffect() == MobEffects.FIRE_RESISTANCE ? effect("fire_range") : null;
            result.add(replacement == null ? instance : new MobEffectInstance(replacement,
                    instance.getDuration(), instance.getAmplifier(), false, false, true));
        }
        MobEffect aurora = effect("aurora_glowing");
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
        MobEffect sadness = effect("temporal_sadness");
        if (sadness == null) return;
        var added = event.getEffectInstance();
        var sorrow = entity.getEffect(TSDEffects.SORROW.get());
        if (added.getEffect() == sadness && (sorrow != null || entity.hasEffect(TSDEffects.GRIEF.get()))) {
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
            entity.removeEffect(sadness);
            if (sorrow != null) turnToGrief(entity, sorrow);
        } else if (added.getEffect() == TSDEffects.SORROW.get() && entity.hasEffect(sadness)) {
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
            entity.removeEffect(sadness);
            turnToGrief(entity, added);
        } else if (added.getEffect() == TSDEffects.GRIEF.get()) {
            entity.removeEffect(sadness);
        }
    }

    private static void turnToGrief(LivingEntity entity, MobEffectInstance sorrow) {
        entity.removeEffect(TSDEffects.SORROW.get());
        entity.getPersistentData().remove("tsd_sorrow_damage");
        entity.addEffect(new MobEffectInstance(TSDEffects.GRIEF.get(), sorrow.getDuration(),
                sorrow.getAmplifier() + 1, false, false, true));
    }

    private TwilightDelightCompat() {}
}
