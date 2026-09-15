package xy177.twilightsparksdelight.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import xy177.twilightsparksdelight.registry.TSDItems;

/**
 * Records the player who first obtains a fondue companion.
 *
 * This is intentionally independent of sharing: obtaining the tool establishes
 * the chef, while the fondue block later records diners and awards the sharing
 * advancements when the feast is finished.
 */
public final class TSDFondueEvents {
    private TSDFondueEvents() {
    }

    @SubscribeEvent
    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        markChef(event.getEntity(), event.getCrafting());
    }

    @SubscribeEvent
    public static void onSmelted(PlayerEvent.ItemSmeltedEvent event) {
        markChef(event.getEntity(), event.getSmelting());
    }

    private static void markChef(Player player, ItemStack stack) {
        if (!(player instanceof ServerPlayer)
                || !stack.is(TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION.get())) {
            return;
        }
        if (TwilightCheeseFondueCompanionItem.getChef(stack) == null) {
            TwilightCheeseFondueCompanionItem.setChef(stack, player.getUUID());
        }
    }
}
