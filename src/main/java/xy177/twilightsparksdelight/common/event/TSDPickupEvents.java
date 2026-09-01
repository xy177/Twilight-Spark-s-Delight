package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.ChefTaggedFeastItemBlock;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDPickupEvents
{
    private TSDPickupEvents()
    {
    }

    @SubscribeEvent
    public static void onPickup(EntityItemPickupEvent event)
    {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) {
            return;
        }
        ItemStack stack = event.getItem().getItem();
        if (stack.isEmpty()) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if (stack.getItem() == TSDItems.FIRE_BEETLE_FLAME_SAC) {
            TSDAdvancements.FIRE_BEETLE_SAC.trigger(player);
        } else if (stack.getItem() == TSDItems.SLIME_BEETLE_HONEY_GLAND) {
            TSDAdvancements.SLIME_BEETLE_GLAND.trigger(player);
        } else if (stack.getItem() == TSDItems.REDCAP_SPICE) {
            TSDAdvancements.REDCAP_SPICE.trigger(player);
        } else if (stack.getItem() == Item.getItemFromBlock(TSDBlocks.TWILIGHT_CHEESE_FONDUE)) {
            TSDAdvancements.FONDUE_FOREIGN_STYLE.trigger(player);
        } else if (stack.getItem() == Item.getItemFromBlock(TSDBlocks.SALT_HELMET_CRAB)) {
            if (ChefTaggedFeastItemBlock.getChef(stack) == null) {
                ChefTaggedFeastItemBlock.setChef(stack, player.getUniqueID());
            }
            TSDAdvancements.SELF_CONTAINED_COOKWARE.trigger(player);
        }
    }
}
