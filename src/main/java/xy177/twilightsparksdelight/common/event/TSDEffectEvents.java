package xy177.twilightsparksdelight.common.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import xy177.twilightsparksdelight.registry.TSDEffects;
import xy177.twilightsparksdelight.TSDConfig;

/**
 * Runtime combat behavior for effects whose result depends on the damage event.
 */
public final class TSDEffectEvents {
    private static final ResourceLocation CHARGE_SPEED =
            ResourceLocation.fromNamespaceAndPath("twilight_spark_delight", "charge_speed");
    private static final ResourceLocation CHARGE_STEP =
            ResourceLocation.fromNamespaceAndPath("twilight_spark_delight", "charge_step");
    private TSDEffectEvents() {
    }

    @SubscribeEvent
    public static void onEffectAdded(net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added event) {
        if (event.getOldEffectInstance() != null || event.getEntity().level().isClientSide) return;
        var effect = event.getEffectInstance().getEffect();
        var data = event.getEntity().getPersistentData();
        if (effect.is(TSDEffects.SYMBIOSIS)) {
            data.remove("tsd_symbiosis_hunger");
            data.remove("tsd_symbiosis_heal_ticks");
        } else if (effect.is(TSDEffects.SORROW)) {
            data.remove("tsd_sorrow_damage");
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance sorrow = entity.getEffect(TSDEffects.SORROW);
        if (sorrow != null && event.getAmount() > 0.0F) {
            float raw = event.getAmount();
            float reduction = Math.min(0.95F, 0.1F * (sorrow.getAmplifier() + 1));
            event.setAmount(raw * (1.0F - reduction));
            double total = entity.getPersistentData().getDouble("tsd_sorrow_damage") + raw;
            if (total >= 18.0D) {
                entity.getPersistentData().remove("tsd_sorrow_damage");
                entity.removeEffect(TSDEffects.SORROW);
                entity.addEffect(new MobEffectInstance(TSDEffects.GRIEF, sorrow.getDuration(),
                        sorrow.getAmplifier() + 1, false, false, true));
            } else {
                entity.getPersistentData().putDouble("tsd_sorrow_damage", total);
            }
        } else if (sorrow == null) {
            entity.getPersistentData().remove("tsd_sorrow_damage");
        }
        MobEffectInstance symbiosis = entity.getEffect(TSDEffects.SYMBIOSIS);
        if (symbiosis != null) {
            float reduction = Math.min(0.95F,
                    TSDConfig.SYMBIOSIS_DAMAGE_REDUCTION.get().floatValue()
                            + TSDConfig.SYMBIOSIS_DAMAGE_REDUCTION_PER_LEVEL.get().floatValue()
                            * symbiosis.getAmplifier());
            event.setAmount(event.getAmount() * (1.0F - reduction));
        }
        if (event.getSource().getDirectEntity() instanceof Player attacker) {
            if (attacker.hasEffect(TSDEffects.GRIEF)) {
                float increase = TSDConfig.GRIEF_DAMAGE_BONUS_PER_LEVEL.get().floatValue()
                        * (attacker.getEffect(TSDEffects.GRIEF).getAmplifier() + 1);
                event.setAmount(event.getAmount() * (1.0F + increase));
            }
            if (attacker.hasEffect(TSDEffects.ABYSS_CALL)
                    && entity instanceof net.minecraft.world.entity.monster.Enemy) {
                entity.addEffect(new MobEffectInstance(TSDEffects.SYMBIOSIS, 300, 0, false, false, true));
            }
            boolean critical = attacker.fallDistance > 0.0F && !attacker.onGround()
                    && !attacker.onClimbable() && !attacker.isInWater()
                    && !attacker.hasEffect(MobEffects.BLINDNESS) && !attacker.isPassenger();
            AttributeInstance speed = attacker.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attacker.hasEffect(TSDEffects.CHARGE) && (attacker.isSprinting() || critical)
                    && speed != null && speed.getBaseValue() > 0.0D) {
                double bonus = Math.max(0.0D, Math.min(TSDConfig.CHARGE_MAX_ATTACK_BONUS.get(),
                        speed.getValue() / speed.getBaseValue() - 1.0D));
                event.setAmount((float) (event.getAmount() * (1.0D + bonus)));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance step = player.getAttribute(Attributes.STEP_HEIGHT);
        if (speed != null) {
            speed.removeModifier(CHARGE_SPEED);
            MobEffectInstance charge = player.getEffect(TSDEffects.CHARGE);
            if (charge != null) {
                speed.addTransientModifier(new AttributeModifier(CHARGE_SPEED,
                        TSDConfig.CHARGE_SPEED_BONUS.get(),
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
        if (step != null) {
            step.removeModifier(CHARGE_STEP);
            MobEffectInstance charge = player.getEffect(TSDEffects.CHARGE);
            if (charge != null) {
                step.addTransientModifier(new AttributeModifier(CHARGE_STEP,
                        TSDConfig.CHARGE_STEP_HEIGHT_BONUS.get(),
                        AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }
}
