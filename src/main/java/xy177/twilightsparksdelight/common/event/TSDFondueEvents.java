package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.ChefTaggedFeastItemBlock;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;

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
        if (crafted.getItem() == TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION) {
            markChef(crafted, event.player);
        } else if (crafted.getItem() == Item.getItemFromBlock(TSDBlocks.TWILIGHT_CHEESE_FONDUE)) {
            triggerFondueCraft(event.player);
        } else if (crafted.getItem() == Item.getItemFromBlock(TSDBlocks.SALT_HELMET_CRAB)) {
            markFeastChef(crafted, event.player);
        }
    }

    @SubscribeEvent
    public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event)
    {
        ItemStack smelted = event.smelting;
        if (!smelted.isEmpty() && smelted.getItem() == TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION) {
            markChef(smelted, event.player);
        } else if (!smelted.isEmpty() && smelted.getItem() == Item.getItemFromBlock(TSDBlocks.SALT_HELMET_CRAB)) {
            markFeastChef(smelted, event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }
        for (ItemStack stack : event.player.inventory.mainInventory) {
            markCompanionChef(stack, event.player);
            markFeastChefIfNeeded(stack, event.player);
        }
        for (ItemStack stack : event.player.inventory.armorInventory) {
            markCompanionChef(stack, event.player);
            markFeastChefIfNeeded(stack, event.player);
        }
        for (ItemStack stack : event.player.inventory.offHandInventory) {
            markCompanionChef(stack, event.player);
            markFeastChefIfNeeded(stack, event.player);
        }
    }

    private static void markCompanionChef(ItemStack stack, EntityPlayer player)
    {
        if (stack.isEmpty() || stack.getItem() != TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION) {
            return;
        }
        if (TwilightCheeseFondueCompanionItem.getChef(stack) != null) {
            TwilightCheeseFondueCompanionItem.sanitizeStack(stack);
            return;
        }
        TwilightCheeseFondueCompanionItem.sanitizeStack(stack);
        TwilightCheeseFondueCompanionItem.setChef(stack, player.getUniqueID());
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            tag.removeTag("RecordChefOnCraft");
        }
    }

    private static void markChef(ItemStack stack, EntityPlayer player)
    {
        if (stack.isEmpty() || stack.getItem() != TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION) {
            return;
        }
        if (TwilightCheeseFondueCompanionItem.getChef(stack) != null) {
            TwilightCheeseFondueCompanionItem.sanitizeStack(stack);
            return;
        }
        TwilightCheeseFondueCompanionItem.sanitizeStack(stack);
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.getBoolean("RecordChefOnCraft")) {
            return;
        }
        TwilightCheeseFondueCompanionItem.setChef(stack, player.getUniqueID());
        tag = stack.getTagCompound();
        if (tag != null) {
            tag.removeTag("RecordChefOnCraft");
        }
    }

    private static void markFeastChef(ItemStack stack, EntityPlayer player)
    {
        if (stack.isEmpty() || stack.getItem() != Item.getItemFromBlock(TSDBlocks.SALT_HELMET_CRAB)) {
            return;
        }
        ChefTaggedFeastItemBlock.setChef(stack, player.getUniqueID());
        triggerSelfContainedCookware(player);
    }

    private static void markFeastChefIfNeeded(ItemStack stack, EntityPlayer player)
    {
        if (stack.isEmpty() || stack.getItem() != Item.getItemFromBlock(TSDBlocks.SALT_HELMET_CRAB)) {
            return;
        }
        if (ChefTaggedFeastItemBlock.getChef(stack) == null) {
            ChefTaggedFeastItemBlock.setChef(stack, player.getUniqueID());
        }
        triggerSelfContainedCookware(player);
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
