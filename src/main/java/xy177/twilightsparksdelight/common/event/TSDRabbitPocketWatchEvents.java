package xy177.twilightsparksdelight.common.event;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import twilightforest.entity.passive.DwarfRabbit;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.registry.TSDTriggers;

/** Pocket-watch behavior, including the optional Curios charm slot. */
public final class TSDRabbitPocketWatchEvents {
    private static final String SPECIAL_RABBIT_TAG = "TwilightSparkDelightSpecialKillerRabbit";

    private TSDRabbitPocketWatchEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide || !hasPocketWatchInHotbar(player)) {
            return;
        }

        giveQuietEffect(player, MobEffects.MOVEMENT_SPEED);
        giveQuietEffect(player, MobEffects.JUMP);
        if (hasPocketWatchInHand(player)) {
            giveQuietEffect(player, MobEffects.DIG_SPEED);
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player
                && event.getEffectInstance().getEffect() == MobEffects.DIG_SLOWDOWN
                && hasPocketWatchInHotbar(player)) {
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        Entity target = event.getTarget();
        if (player.level().isClientSide
                || !held.is(TFItems.TRANSFORMATION_POWDER.get())
                || !isRabbitTarget(target)
                || player.getRandom().nextFloat() >= TSDConfig.RABBIT_POCKET_WATCH_KILLER_RABBIT_CHANCE.get()) {
            return;
        }

        if (convertToKillerRabbit(target) == null) {
            return;
        }
        if (!player.isCreative()) {
            held.shrink(1);
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        Entity directAttacker = event.getSource().getDirectEntity();

        if (victim instanceof Player
                && (isSpecialKillerRabbit(attacker) || isSpecialKillerRabbit(directAttacker))) {
            event.setCanceled(true);
            return;
        }

        if (!(victim instanceof Player target)
                || !event.getSource().is(DamageTypeTags.IS_PROJECTILE)
                || !(attacker instanceof net.minecraft.server.level.ServerPlayer shooter)
                || !hasPocketWatchInHand(shooter)
                || !target.isBlocking()
                || !target.getUseItem().is(Items.SHIELD)) {
            return;
        }

        target.disableShield(true);
        target.getCooldowns().addCooldown(Items.SHIELD, 100);
        TSDTriggers.GUNFIRE_BREAKS_RABBIT_WATCH.trigger(shooter);
    }

    public static boolean isSpecialKillerRabbit(Entity entity) {
        return entity instanceof Rabbit
                && entity.getPersistentData().getBoolean(SPECIAL_RABBIT_TAG);
    }

    public static boolean canDropPocketWatch(LivingEntity entity, Player player) {
        return entity != null && player != null
                && isSpecialKillerRabbit(entity)
                && isKnife(player.getMainHandItem());
    }

    private static Rabbit convertToKillerRabbit(Entity target) {
        Level level = target.level();
        var originalName = target.getCustomName();
        Rabbit rabbit;
        if (target instanceof Rabbit existing) {
            rabbit = existing;
            rabbit.setVariant(Rabbit.Variant.EVIL);
        } else {
            rabbit = EntityType.RABBIT.create(level);
            if (rabbit == null) {
                return null;
            }
            rabbit.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
            if (target instanceof LivingEntity living && living.isBaby()) {
                rabbit.setBaby(true);
            }
            target.discard();
            level.addFreshEntity(rabbit);
            rabbit.setVariant(Rabbit.Variant.EVIL);
        }
        // Keep the converted rabbit unnamed. The marker is only for addon
        // behavior and is persisted with the entity.
        rabbit.setCustomName(originalName);
        rabbit.getPersistentData().putBoolean(SPECIAL_RABBIT_TAG, true);
        return rabbit;
    }

    private static boolean isRabbitTarget(Entity entity) {
        return entity instanceof Rabbit || entity instanceof DwarfRabbit;
    }

    private static boolean hasPocketWatchInHand(Player player) {
        return player.getMainHandItem().is(xy177.twilightsparksdelight.registry.TSDItems.RABBIT_POCKET_WATCH.get())
                || player.getOffhandItem().is(xy177.twilightsparksdelight.registry.TSDItems.RABBIT_POCKET_WATCH.get())
                || xy177.twilightsparksdelight.integration.CuriosCompat.hasWatch(player);
    }

    private static boolean hasPocketWatchInHotbar(Player player) {
        for (int slot = 0; slot < 9; slot++) {
            if (player.getInventory().getItem(slot).is(xy177.twilightsparksdelight.registry.TSDItems.RABBIT_POCKET_WATCH.get())) {
                return true;
            }
        }
        return player.getOffhandItem().is(xy177.twilightsparksdelight.registry.TSDItems.RABBIT_POCKET_WATCH.get())
                || xy177.twilightsparksdelight.integration.CuriosCompat.hasWatch(player);
    }

    private static void giveQuietEffect(Player player, net.minecraft.world.effect.MobEffect effect) {
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect, 40, 0, true, false, false));
    }

    private static boolean isKnife(ItemStack stack) {
        return TSDLootEvents.isKnife(stack);
    }
}
