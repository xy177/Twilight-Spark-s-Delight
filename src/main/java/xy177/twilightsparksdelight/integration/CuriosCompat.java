package xy177.twilightsparksdelight.integration;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.registry.TSDItems;

/** Calls into Curios only after the optional mod has been detected. */
public final class CuriosCompat {
    public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (!ModList.get().isLoaded("curios")) return;
        ItemStack stack = event.getObject();
        if ((TSDItems.RABBIT_POCKET_WATCH.isPresent() && stack.is(TSDItems.RABBIT_POCKET_WATCH.get()))
                || (TSDItems.EXPERIMENT_250.isPresent() && stack.is(TSDItems.EXPERIMENT_250.get()))) {
            Loaded.attachCapabilities(event);
        }
    }

    public static ItemStack find(Player player, Predicate<ItemStack> predicate) {
        if (!ModList.get().isLoaded("curios")) return ItemStack.EMPTY;
        return Loaded.find(player, predicate);
    }

    public static boolean hasWatch(Player player) {
        return !find(player, stack -> stack.is(TSDItems.RABBIT_POCKET_WATCH.get())).isEmpty();
    }

    public static void forEach(Player player, Consumer<ItemStack> action) {
        if (!ModList.get().isLoaded("curios")) return;
        Loaded.forEach(player, action);
    }

    // The facade must verify without resolving any classes from an absent mod.
    private static final class Loaded {
        private static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
            ItemStack stack = event.getObject();
            event.addCapability(TwilightSparksDelight.id("curio"), CuriosApi.createCurioProvider(new ICurio() {
                @Override
                public ItemStack getStack() {
                    return stack;
                }
            }));
        }

        private static ItemStack find(Player player, Predicate<ItemStack> predicate) {
            return CuriosApi.getCuriosInventory(player).resolve()
                    .flatMap(inventory -> inventory.findFirstCurio(predicate))
                    .map(result -> result.stack()).orElse(ItemStack.EMPTY);
        }

        private static void forEach(Player player, Consumer<ItemStack> action) {
            CuriosApi.getCuriosInventory(player).ifPresent(inventory ->
                    inventory.getCurios().values().forEach(handler -> {
                        var stacks = handler.getStacks();
                        for (int index = 0; index < stacks.getSlots(); index++) {
                            action.accept(stacks.getStackInSlot(index));
                        }
                    }));
        }
    }

    private CuriosCompat() {}
}
