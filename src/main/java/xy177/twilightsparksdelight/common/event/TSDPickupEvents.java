package xy177.twilightsparksdelight.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDItems;
import xy177.twilightsparksdelight.registry.TSDTriggers;
import xy177.twilightsparksdelight.registry.TSDComponents;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Acquisition hooks cover pickups, crafting, containers and cursor-held stacks.
 */
public final class TSDPickupEvents {
    private TSDPickupEvents() {
    }

    @SubscribeEvent
    public static void onPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        checkAcquiredStack(player, event.getOriginalStack());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 10 != 0) return;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            checkAcquiredStack(player, player.getInventory().getItem(slot));
        }
        checkAcquiredStack(player, player.containerMenu.getCarried());
    }

    public static void checkAcquiredStack(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getOrDefault(TSDComponents.COOKED_ADVANCEMENT, false)) {
            if (stack.is(TSDItems.MILLION_POUND_MEAL.get())) {
                TSDTriggers.MILLION_POUND_MEAL.get().trigger(player);
            } else if (stack.is(TSDBlocks.NAGA_MIXED_RICE.get().asItem())) {
                TSDTriggers.TIME_TO_EVEN_THE_SCALES.get().trigger(player);
            }
        }
        if (stack.is(TSDItems.FIRE_BEETLE_FLAME_SAC.get())) {
            TSDTriggers.FIRE_BEETLE_SAC.get().trigger(player);
        } else if (stack.is(TSDItems.SLIME_BEETLE_HONEY_GLAND.get())) {
            TSDTriggers.SLIME_BEETLE_GLAND.get().trigger(player);
        } else if (stack.is(TSDItems.REDCAP_SPICE.get())) {
            TSDTriggers.REDCAP_SPICE.get().trigger(player);
        } else if (stack.is(TSDBlocks.TWILIGHT_CHEESE_FONDUE.get().asItem())) {
            TSDTriggers.FONDUE_FOREIGN_STYLE.get().trigger(player);
        } else if (stack.is(TSDBlocks.SALT_HELMET_CRAB.get().asItem())) {
            TSDTriggers.SELF_CONTAINED_COOKWARE.get().trigger(player);
        } else if (stack.is(TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION.get())) {
            if (TwilightCheeseFondueCompanionItem.getChef(stack) == null) {
                TwilightCheeseFondueCompanionItem.setChef(stack, player.getUUID());
            }
        }
    }
}
