package xy177.twilightsparksdelight.common.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.effect.SizeEffectHelper;

public final class TSDSizeEffectEvents {
    private static final ResourceLocation SCALE_ID =
            SizeEffectHelper.SCALE_MODIFIER_ID;
    private static final ResourceLocation BLOCK_REACH_ID =
            ResourceLocation.fromNamespaceAndPath("twilight_spark_delight", "size_block_reach");
    private static final ResourceLocation ENTITY_REACH_ID =
            ResourceLocation.fromNamespaceAndPath("twilight_spark_delight", "size_entity_reach");
    private static final ResourceLocation STEP_HEIGHT_ID =
            ResourceLocation.fromNamespaceAndPath("twilight_spark_delight", "size_step_height");

    private TSDSizeEffectEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        float scale = SizeEffectHelper.getScale(player);
        AttributeInstance size = player.getAttribute(Attributes.SCALE);
        if (size != null && updateModifierAmount(size, SCALE_ID, scale - 1.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) {
            player.refreshDimensions();
        }
        updateReach(player, scale);
        updateStepHeight(player, scale);
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            float scale = SizeEffectHelper.getScale(player);
            if (scale > 1.0F) {
                var movement = player.getDeltaMovement();
                double velocity = SizeEffectHelper.jumpVelocityForHeight(scale, player.getGravity());
                // Preserve jump bonuses supplied by potions or other mods.
                velocity += Math.max(0, movement.y() - 0.42D);
                player.setDeltaMovement(movement.x(), velocity, movement.z());
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
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
        var reach = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reach == null || reach.getValue() <= 0) return 1;
        double original = SizeEffectHelper.attributeValueWithout(reach, ENTITY_REACH_ID, true);
        return Math.min(1, Math.max(0, original / reach.getValue()));
    }

    private static void updateReach(Player player, float scale) {
        AttributeInstance block = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entity = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        double multiplier = scale > 1.0F ? scale : scale < 1.0F
                ? 1.0D - (1.0D - scale) * 0.5D : 1.0D;
        updateModifier(block, BLOCK_REACH_ID, multiplier);
        updateModifier(entity, ENTITY_REACH_ID, multiplier);
    }

    private static void updateModifier(AttributeInstance attribute, ResourceLocation id, double multiplier) {
        if (attribute == null) {
            return;
        }
        double factor = SizeEffectHelper.additiveModifierFactor(attribute);
        double delta = attribute.getAttribute().value().getDefaultValue() * (multiplier - 1.0D);
        // Leave external flat and multiplicative reach bonuses unchanged.
        updateModifierAmount(attribute, id, factor > 0 ? delta / factor : 0,
                AttributeModifier.Operation.ADD_VALUE);
    }

    private static void updateStepHeight(Player player, float scale) {
        AttributeInstance step = player.getAttribute(Attributes.STEP_HEIGHT);
        if (step == null) {
            return;
        }
        updateModifierAmount(step, STEP_HEIGHT_ID, scale > 1 ? 0.6D * (scale - 1) : 0,
                AttributeModifier.Operation.ADD_VALUE);
    }

    private static boolean updateModifierAmount(AttributeInstance attribute, ResourceLocation id,
            double amount, AttributeModifier.Operation operation) {
        var existing = attribute.getModifier(id);
        if (existing != null && Math.abs(existing.amount() - amount) < 0.00001D) return false;
        if (existing == null && Math.abs(amount) < 0.00001D) return false;
        attribute.removeModifier(id);
        if (Math.abs(amount) > 0.00001D) {
            attribute.addTransientModifier(new AttributeModifier(id, amount, operation));
        }
        return true;
    }
}
