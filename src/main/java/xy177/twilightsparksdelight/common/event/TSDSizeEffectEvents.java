package xy177.twilightsparksdelight.common.event;

import java.util.UUID;
import java.util.Map;
import java.util.WeakHashMap;
import java.nio.charset.StandardCharsets;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.effect.SizeEffectHelper;

public final class TSDSizeEffectEvents {
    private static final UUID BLOCK_REACH_ID =
            UUID.nameUUIDFromBytes("twilight_spark_delight:size_block_reach".getBytes(StandardCharsets.UTF_8));
    private static final UUID ENTITY_REACH_ID =
            UUID.nameUUIDFromBytes("twilight_spark_delight:size_entity_reach".getBytes(StandardCharsets.UTF_8));
    private static final UUID STEP_HEIGHT_ID =
            UUID.nameUUIDFromBytes("twilight_spark_delight:size_step_height".getBytes(StandardCharsets.UTF_8));
    private static final Map<Player, Float> LAST_SCALE = new WeakHashMap<>();

    private TSDSizeEffectEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Player player = event.player;
        float scale = SizeEffectHelper.getScale(player);
        Float previous = LAST_SCALE.put(player, scale);
        if (previous == null || Math.abs(previous - scale) > 0.00001F) {
            player.refreshDimensions();
        }
        if (player.level().isClientSide) return;
        updateReach(player, scale);
        updateStepHeight(player, scale);
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            float scale = SizeEffectHelper.getScale(player);
            if (scale > 1.0F) {
                var movement = player.getDeltaMovement();
                double velocity = SizeEffectHelper.jumpVelocityForHeight(scale,
                        player.getAttributeValue(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get()));
                // Preserve jump bonuses supplied by potions or other mods.
                velocity += Math.max(0, movement.y() - 0.42D);
                player.setDeltaMovement(movement.x(), velocity, movement.z());
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim instanceof Player player) {
            double dodgeChance = SizeEffectHelper.getDodgeChance(player);
            if (dodgeChance > 0.0D && player.getRandom().nextDouble() < dodgeChance) {
                event.setCanceled(true);
                return;
            }
            float scale = SizeEffectHelper.getScale(player);
            if (scale > 1.0F) {
                event.setAmount(event.getAmount() * scale);
            }
        }

        if (event.getSource().getDirectEntity() instanceof Player attacker) {
            float scale = SizeEffectHelper.getScale(attacker);
            if (scale > 1.0F) {
                event.setAmount((float) (event.getAmount() * outgoingDamageMultiplier(attacker)));
            }
        }
    }

    public static double outgoingDamageMultiplier(Player player) {
        var reach = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get());
        if (reach == null || reach.getValue() <= 0) return 1;
        double original = SizeEffectHelper.attributeValueWithout(reach, ENTITY_REACH_ID, true);
        return Math.min(1, Math.max(0, original / reach.getValue()));
    }

    private static void updateReach(Player player, float scale) {
        AttributeInstance block = player.getAttribute(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get());
        AttributeInstance entity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get());
        double multiplier = scale > 1.0F ? scale : scale < 1.0F
                ? 1.0D - (1.0D - scale) * 0.5D : 1.0D;
        updateModifier(block, BLOCK_REACH_ID, multiplier);
        updateModifier(entity, ENTITY_REACH_ID, multiplier);
    }

    private static void updateModifier(AttributeInstance attribute, UUID id, double multiplier) {
        if (attribute == null) {
            return;
        }
        double factor = SizeEffectHelper.additiveModifierFactor(attribute);
        double delta = attribute.getAttribute().getDefaultValue() * (multiplier - 1.0D);
        // Leave external flat and multiplicative reach bonuses unchanged.
        updateModifierAmount(attribute, id, factor > 0 ? delta / factor : 0,
                AttributeModifier.Operation.ADDITION);
    }

    private static void updateStepHeight(Player player, float scale) {
        AttributeInstance step = player.getAttribute(net.minecraftforge.common.ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (step == null) {
            return;
        }
        updateModifierAmount(step, STEP_HEIGHT_ID, scale > 1 ? 0.6D * (scale - 1) : 0,
                AttributeModifier.Operation.ADDITION);
    }

    private static boolean updateModifierAmount(AttributeInstance attribute, UUID id,
            double amount, AttributeModifier.Operation operation) {
        var existing = attribute.getModifier(id);
        if (existing != null && Math.abs(existing.getAmount() - amount) < 0.00001D) return false;
        if (existing == null && Math.abs(amount) < 0.00001D) return false;
        attribute.removeModifier(id);
        if (Math.abs(amount) > 0.00001D) {
            attribute.addTransientModifier(new AttributeModifier(id, "Twilight Delight size", amount, operation));
        }
        return true;
    }
}
