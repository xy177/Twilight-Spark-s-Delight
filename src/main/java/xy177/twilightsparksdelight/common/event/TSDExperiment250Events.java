package xy177.twilightsparksdelight.common.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.food.ExtendedFoodProgression;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.registry.TSDComponents;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class TSDExperiment250Events {
    private static final String FATAL_TRIGGER_COUNT = "twilight_spark_delight.experiment_250_fatal_trigger_count";
    private static final TagKey<Item> BREAD_TAG = TagKey.create(
            Registries.ITEM, new ResourceLocation("forge", "bread"));
    private TSDExperiment250Events() {
    }

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onFinalDamage(LivingDamageEvent event) {
        tryFatalProtection(event);
        if (event.getEntity() instanceof ServerPlayer player && event.getAmount() > 0.0F) {
            maybeProliferate(player, ItemStack.EMPTY, false);
        }
    }

    private static void tryFatalProtection(LivingDamageEvent event) {
        // Forge fires this after armor, effects and absorption have been applied.
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getAmount() <= 0
                || event.getAmount() < player.getHealth()) {
            return;
        }
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        boolean bossAttack = isBoss(attacker);
        if (bossAttack && !TSDConfig.EXPERIMENT_FATAL_ALLOWS_BOSS_ATTACKS.get()) {
            return;
        }
        ResourceLocation attackerId = attacker == null
                ? null : BuiltInRegistries.ENTITY_TYPE.getKey(attacker.getType());
        boolean consumesTrigger = !TSDConfig.isExperimentFatalNonConsumingAttacker(attackerId)
                && (!bossAttack || TSDConfig.EXPERIMENT_FATAL_BOSS_ATTACKS_CONSUME_TRIGGER.get());
        int used = getFatalTriggerCount(player);
        if (consumesTrigger && used >= TSDConfig.EXPERIMENT_FATAL_TRIGGERS_PER_SLEEP.get()) {
            return;
        }

        ItemStack container = findExperiment(player);
        if (container.isEmpty()
                || Experiment250Item.getActivity(container) <= TSDConfig.EXPERIMENT_FATAL_ACTIVITY_COST.get()) {
            return;
        }

        Experiment250Item.setActivity(container,
                Experiment250Item.getActivity(container) - TSDConfig.EXPERIMENT_FATAL_ACTIVITY_COST.get());
        if (consumesTrigger) {
            setFatalTriggerCount(player, used + 1);
        }
        forEachExperiment(player, stack -> TSDComponents.EXPERIMENT_REVIVES.set(stack, consumesTrigger ? used + 1 : used));
        event.setAmount(0);
        player.setHealth(Math.max(player.getHealth(), player.getMaxHealth() * 0.5F));
        player.getFoodData().setFoodLevel(ExtendedFoodProgression.getMaxFood(player));
        player.getFoodData().setSaturation(ExtendedFoodProgression.getMaxSaturation(player));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        xy177.twilightsparksdelight.network.TSDNetwork.sendToPlayer(player,
                new xy177.twilightsparksdelight.network.ExperimentActivationPayload(container.copy()));
        player.inventoryMenu.broadcastChanges();
    }

    @SubscribeEvent
    public static void onItemFinished(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            maybeProliferate(player, event.getItem(), true);
        }
    }

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            setFatalTriggerCount(player, 0);
            clearReviveCounts(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            clearReviveCounts(event.getEntity());
            setFatalTriggerCount(event.getEntity(), 0);
        }
    }

    @SubscribeEvent
    public static void onWakeUp(PlayerWakeUpEvent event) {
        if (!event.getEntity().level().isClientSide) {
            setFatalTriggerCount(event.getEntity(), 0);
            forEachExperiment(event.getEntity(), stack -> TSDComponents.EXPERIMENT_REVIVES.remove(stack));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Player player = event.player;
        if (!player.level().isClientSide) {
            int used = getFatalTriggerCount(player);
            forEachExperiment(player, stack -> {
                if (TSDComponents.EXPERIMENT_REVIVES.getOrDefault(stack, -1) != used) {
                    TSDComponents.EXPERIMENT_REVIVES.set(stack, used);
                }
            });
        }
    }

    private static ItemStack findExperiment(Player player) {
        var location = TSDConfig.EXPERIMENT_FATAL_ITEM_LOCATION.get();
        for (ItemStack stack : player.getInventory().offhand) {
            if (isUsableExperiment(stack)) {
                return stack;
            }
        }
        if (location == TSDConfig.FatalProtectionItemLocation.OFFHAND) {
            return ItemStack.EMPTY;
        }
        if (location == TSDConfig.FatalProtectionItemLocation.BAUBLES_OR_OFFHAND) {
            return xy177.twilightsparksdelight.integration.CuriosCompat.find(
                    player, TSDExperiment250Events::isUsableExperiment);
        }
        for (ItemStack stack : player.getInventory().items) {
            if (isUsableExperiment(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean isUsableExperiment(ItemStack stack) {
        return stack.getItem() instanceof Experiment250Item
                && Experiment250Item.getActivity(stack) > TSDConfig.EXPERIMENT_FATAL_ACTIVITY_COST.get();
    }

    private static void clearReviveCounts(Player player) {
        forEachExperiment(player, stack -> TSDComponents.EXPERIMENT_REVIVES.remove(stack));
    }

    private static int getFatalTriggerCount(Player player) {
        return Math.max(0, player.getPersistentData().getInt(FATAL_TRIGGER_COUNT));
    }

    private static void setFatalTriggerCount(Player player, int count) {
        player.getPersistentData().putInt(FATAL_TRIGGER_COUNT, Math.max(0, count));
    }

    private static boolean isBoss(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return entity.getType().is(net.minecraftforge.common.Tags.EntityTypes.BOSSES)
                || id != null && (id.equals(new ResourceLocation("ender_dragon"))
                || id.equals(new ResourceLocation("wither"))
                || id.getNamespace().equals("twilightforest")
                && java.util.Set.of("naga", "lich", "minoshroom", "hydra", "knight_phantom",
                        "snow_queen", "ur_ghast", "alpha_yeti").contains(id.getPath()));
    }

    private static void forEachExperiment(Player player, java.util.function.Consumer<ItemStack> action) {
        player.getInventory().items.forEach(stack -> {
            if (stack.getItem() instanceof Experiment250Item) {
                action.accept(stack);
            }
        });
        player.getInventory().offhand.forEach(stack -> {
            if (stack.getItem() instanceof Experiment250Item) {
                action.accept(stack);
            }
        });
        xy177.twilightsparksdelight.integration.CuriosCompat.forEach(player, stack -> {
            if (stack.getItem() instanceof Experiment250Item) action.accept(stack);
        });
    }

    private static void maybeProliferate(ServerPlayer player, ItemStack consumedStack, boolean fromEating) {
        if (player.getRandom().nextDouble() > TSDConfig.EXPERIMENT_PROLIFERATION_CHANCE.get()
                || !hasCatalyst(player)) {
            return;
        }

        boolean ateBread = fromEating && isBreadLike(consumedStack);
        int breadSlot = ateBread ? -1 : findBreadSlot(player);
        if (!ateBread && breadSlot < 0) {
            return;
        }

        MetalChoice metal = findMetalChoice(player);
        if (breadSlot >= 0) {
            player.getInventory().removeItem(breadSlot, 1);
        }
        if (metal != null) {
            player.getInventory().removeItem(metal.slot(), metal.count());
        }

        ItemStack output = new ItemStack(metal == null
                ? TSDItems.EXPERIMENT_000.get() : TSDItems.EXPERIMENT_PROTOTYPE.get());
        player.getInventory().placeItemBackInInventory(output);
        player.displayClientMessage(Component.translatable(
                "twilight_spark_delight.message.experiment_proliferation"), true);
        if (!player.getPersistentData().getBoolean("twilight_spark_delight.quietly_wriggling_unlocked")) {
            player.getPersistentData().putBoolean(
                    "twilight_spark_delight.quietly_wriggling_unlocked", true);
            xy177.twilightsparksdelight.registry.TSDTriggers.QUIETLY_WRIGGLING.trigger(player);
        }
    }

    private static boolean hasCatalyst(Player player) {
        return player.getInventory().items.stream()
                .anyMatch(stack -> stack.is(TSDItems.EXPERIMENT_PROTOTYPE.get())
                        || stack.is(twilightforest.init.TFItems.KNIGHT_PHANTOM_TROPHY.get()));
    }

    private static int findBreadSlot(Player player) {
        for (int index = 0; index < player.getInventory().items.size(); index++) {
            if (isBreadLike(player.getInventory().items.get(index))) {
                return index;
            }
        }
        return -1;
    }

    private static boolean isBreadLike(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(BREAD_TAG) || stack.is(Experiment250Logic.DOUGH_TAG)
                || stack.is(net.minecraft.world.item.Items.BREAD)
                || stack.is(TSDItems.LIVEROOT_BREAD.get())
                || stack.is(TSDItems.LIVEROOT_DOUGH.get())
                || stack.is(TSDItems.LIVEROOT_FLOUR.get()));
    }

    private static MetalChoice findMetalChoice(Player player) {
        int slot = findItem(player, twilightforest.init.TFItems.KNIGHTMETAL_INGOT.get(), 1);
        if (slot >= 0) {
            return new MetalChoice(slot, 1);
        }
        slot = findItem(player, twilightforest.init.TFItems.ARMOR_SHARD_CLUSTER.get(), 1);
        if (slot >= 0) {
            return new MetalChoice(slot, 1);
        }
        slot = findItem(player, twilightforest.init.TFItems.ARMOR_SHARD.get(), 9);
        if (slot >= 0) {
            return new MetalChoice(slot, 9);
        }
        return null;
    }

    private static int findItem(Player player, Item item, int count) {
        for (int index = 0; index < player.getInventory().items.size(); index++) {
            ItemStack stack = player.getInventory().items.get(index);
            if (stack.is(item) && stack.getCount() >= count) {
                return index;
            }
        }
        return -1;
    }

    private record MetalChoice(int slot, int count) {
    }
}
