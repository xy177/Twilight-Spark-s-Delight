package xy177.twilightsparksdelight.integration.baubles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandlerModifiable;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.lang.reflect.Method;

public final class RabbitPocketWatchBaublesCompat
{
    private static boolean initialized;
    private static boolean failed;
    private static Method getBaublesHandler;

    private RabbitPocketWatchBaublesCompat()
    {
    }

    public static boolean isEquipped(EntityPlayer player)
    {
        if (player == null || failed || !Loader.isModLoaded("baubles")) {
            return false;
        }

        try {
            initialize();
            Object handler = getBaublesHandler.invoke(null, player);
            if (!(handler instanceof IItemHandlerModifiable)) {
                return false;
            }
            IItemHandlerModifiable inventory = (IItemHandlerModifiable) handler;
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() == TSDItems.RABBIT_POCKET_WATCH) {
                    return true;
                }
            }
        } catch (ReflectiveOperationException | LinkageError exception) {
            failed = true;
            if (TwilightSparksDelight.logger != null) {
                TwilightSparksDelight.logger.warn("Unable to access Baubles/BaublesEX for the Rabbit's Pocket Watch", exception);
            }
        }
        return false;
    }

    private static void initialize() throws ReflectiveOperationException
    {
        if (initialized) {
            return;
        }
        Class<?> apiClass = Class.forName("baubles.api.BaublesApi");
        getBaublesHandler = apiClass.getMethod("getBaublesHandler", EntityPlayer.class);
        initialized = true;
    }
}
