package xy177.twilightsparksdelight.common.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.registry.TSDEffects;

public final class SizeEffectHelper {
    private SizeEffectHelper() {
    }

    public static float getScale(Player player) {
        if (!TSDEffects.ENLARGE.isPresent() || !TSDEffects.SHRINK.isPresent()) return 1.0F;
        MobEffectInstance enlarge = getEffectDuringConstruction(player, TSDEffects.ENLARGE.get());
        MobEffectInstance shrink = getEffectDuringConstruction(player, TSDEffects.SHRINK.get());
        int enlargeLevel = enlarge == null ? 0 : enlarge.getAmplifier() + 1;
        int shrinkLevel = shrink == null ? 0 : shrink.getAmplifier() + 1;
        int netLevel = enlargeLevel - shrinkLevel;

        if (netLevel > 0) {
            double increase = TSDConfig.ENLARGE_BASE_SCALE.get()
                    + TSDConfig.ENLARGE_SCALE_PER_LEVEL.get() * netLevel;
            return (float) Math.max(0.1D, 1.0D + increase);
        }
        if (netLevel < 0) {
            double reduction = TSDConfig.SHRINK_BASE_REDUCTION.get()
                    + TSDConfig.SHRINK_REDUCTION_PER_LEVEL.get() * -netLevel;
            return (float) Math.max(0.1D, 1.0D - Math.min(0.9D, reduction));
        }
        return 1.0F;
    }

    private static MobEffectInstance getEffectDuringConstruction(Player player,
                                                                  net.minecraft.world.effect.MobEffect effect) {
        /*
         * Forge queries dimensions from Entity's constructor. A
         * LivingEntity's active-effect map is not initialized at that point,
         * so an effect lookup must be treated as empty until construction is
         * complete.
         */
        try {
            return player.getEffect(effect);
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public static double getReachMultiplier(Player player) {
        float scale = getScale(player);
        return scale > 1.0F ? scale : scale < 1.0F
                ? 1.0D - (1.0D - scale) * 0.5D
                : 1.0D;
    }

    public static double getDodgeChance(Player player) {
        float scale = getScale(player);
        return scale < 1.0F ? Math.max(0.0D, Math.min(0.95D, 1.0D - scale)) : 0.0D;
    }

    public static double attributeValueWithout(AttributeInstance attribute, UUID excluded,
                                                boolean sanitize) {
        double base = attribute.getBaseValue();
        double baseMultiplier = 1;
        double totalMultiplier = 1;
        for (var modifier : attribute.getModifiers()) {
            if (modifier.getId().equals(excluded)) continue;
            switch (modifier.getOperation()) {
                case ADDITION -> base += modifier.getAmount();
                case MULTIPLY_BASE -> baseMultiplier += modifier.getAmount();
                case MULTIPLY_TOTAL -> totalMultiplier *= 1 + modifier.getAmount();
            }
        }
        double value = base * baseMultiplier * totalMultiplier;
        return sanitize ? attribute.getAttribute().sanitizeValue(value) : value;
    }

    public static double additiveModifierFactor(AttributeInstance attribute) {
        double base = 1;
        double total = 1;
        for (var modifier : attribute.getModifiers()) {
            if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) base += modifier.getAmount();
            else if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) total *= 1 + modifier.getAmount();
        }
        return base * total;
    }

    public static double jumpVelocityForHeight(double height, double gravity) {
        if (gravity <= 0) return 0.42D;
        double low = 0;
        double high = Math.max(1, Math.sqrt(2 * gravity * height) + gravity + 1);
        for (int attempt = 0; attempt < 40; attempt++) {
            double velocity = (low + high) * 0.5D;
            double rise = 0;
            double motion = velocity;
            for (int tick = 0; motion > 0 && tick < 1000; tick++) {
                rise += motion;
                motion = (motion - gravity) * 0.98D;
            }
            if (rise < height) low = velocity; else high = velocity;
        }
        return (low + high) * 0.5D;
    }
}
