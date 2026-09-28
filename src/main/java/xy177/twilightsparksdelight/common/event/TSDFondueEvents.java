package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDFondueEvents
{
    private TSDFondueEvents()
    {
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        ItemStack crafted = event.crafting;
        if (crafted.isEmpty()) {
            return;
        }
        if (crafted.getItem() == Item.getItemFromBlock(TSDBlocks.TWILIGHT_CHEESE_FONDUE)) {
            triggerFondueCraft(event.player);
        } else if (crafted.getItem() == Item.getItemFromBlock(TSDBlocks.SALT_HELMET_CRAB)) {
            triggerSelfContainedCookware(event.player);
        }
    }

    @SubscribeEvent
    public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event)
    {
        ItemStack smelted = event.smelting;
        if (!smelted.isEmpty() && smelted.getItem() == Item.getItemFromBlock(TSDBlocks.SALT_HELMET_CRAB)) {
            triggerSelfContainedCookware(event.player);
        }
    }

    private static void triggerFondueCraft(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP) {
            TSDAdvancements.FONDUE_FOREIGN_STYLE.trigger((EntityPlayerMP) player);
        }
    }

    private static void triggerSelfContainedCookware(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP) {
            TSDAdvancements.SELF_CONTAINED_COOKWARE.trigger((EntityPlayerMP) player);
        }
    }
}
