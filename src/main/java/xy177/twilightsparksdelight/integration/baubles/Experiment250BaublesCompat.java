package xy177.twilightsparksdelight.integration.baubles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandlerModifiable;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.lang.reflect.Method;

public final class Experiment250BaublesCompat
{
    private static boolean failed;

    private Experiment250BaublesCompat()
    {
    }

    public static FoundStack find(EntityPlayer player, double requiredActivity)
    {
        if (failed || !Loader.isModLoaded("baubles")) {
            return null;
        }
        try {
            Class<?> apiClass = Class.forName("baubles.api.BaublesApi");
            Method getHandler = apiClass.getMethod("getBaublesHandler", EntityPlayer.class);
            Object handler = getHandler.invoke(null, player);
            if (handler == null) {
                return null;
            }
            if (!(handler instanceof IItemHandlerModifiable)) {
                return null;
            }
            IItemHandlerModifiable inventory = (IItemHandlerModifiable) handler;
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (isUsable(stack, requiredActivity)) {
                    return new FoundStack(inventory, slot, stack);
                }
            }
        } catch (ReflectiveOperationException | LinkageError exception) {
            failed = true;
            if (TwilightSparksDelight.logger != null) {
                TwilightSparksDelight.logger.warn("Unable to access Baubles/BaublesEX for Experiment 250", exception);
            }
        }
        return null;
    }

    private static boolean isUsable(ItemStack stack, double requiredActivity)
    {
        return stack != null && !stack.isEmpty() && stack.getItem() == TSDItems.EXPERIMENT_250
            && Experiment250Item.getActivity(stack) > requiredActivity;
    }

    public static final class FoundStack
    {
        private final IItemHandlerModifiable handler;
        private final int slot;
        private final ItemStack stack;

        private FoundStack(IItemHandlerModifiable handler, int slot, ItemStack stack)
        {
            this.handler = handler;
            this.slot = slot;
            this.stack = stack;
        }

        public ItemStack getStack()
        {
            return stack;
        }

        public void sync()
        {
            handler.setStackInSlot(slot, stack);
        }
    }
}
