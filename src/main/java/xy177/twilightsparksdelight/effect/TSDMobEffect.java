package xy177.twilightsparksdelight.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import xy177.twilightsparksdelight.TSDConfig;

public class TSDMobEffect extends MobEffect {
    public enum Behavior {
        NONE,
        SYMBIOSIS
    }

    private final Behavior behavior;

    public TSDMobEffect(MobEffectCategory category, int color) {
        this(category, color, Behavior.NONE);
    }

    public TSDMobEffect(MobEffectCategory category, int color, Behavior behavior) {
        super(category, color);
        this.behavior = behavior;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return behavior == Behavior.SYMBIOSIS;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (behavior == Behavior.SYMBIOSIS && !entity.level().isClientSide) {
            if (entity instanceof Player player) {
                double value = player.getPersistentData().getDouble("tsd_symbiosis_hunger");
                double drain = TSDConfig.SYMBIOSIS_HUNGER_DRAIN_PER_TICK.get() / Math.max(1, amplifier + 1);
                if (player.hasEffect(vectorwing.farmersdelight.common.registry.ModEffects.NOURISHMENT)) {
                    drain *= 0.5D;
                }
                value += drain;
                while (value >= 1.0D) {
                    value -= 1.0D;
                    player.getFoodData().setFoodLevel(Math.max(0, player.getFoodData().getFoodLevel() - 1));
                }
                player.getPersistentData().putDouble("tsd_symbiosis_hunger", value);
            } else {
                int ticks = entity.getPersistentData().getInt("tsd_symbiosis_heal_ticks") + 1;
                if (ticks >= TSDConfig.SYMBIOSIS_HEAL_INTERVAL.get()) {
                    ticks = 0;
                    entity.heal(TSDConfig.SYMBIOSIS_ENTITY_HEAL_AMOUNT.get().floatValue());
                }
                entity.getPersistentData().putInt("tsd_symbiosis_heal_ticks", ticks);
            }
        }
        return true;
    }
}
