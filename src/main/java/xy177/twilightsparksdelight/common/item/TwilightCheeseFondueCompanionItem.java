package xy177.twilightsparksdelight.common.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;

import java.util.UUID;

public class TwilightCheeseFondueCompanionItem extends Item
{
    private static final String DURABILITY_KEY = "CompanionDurability";
    private static final int MAX_USES = 6;

    public TwilightCheeseFondueCompanionItem()
    {
        setMaxStackSize(1);
        setMaxDamage(5);
        setNoRepair();
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        sanitizeStack(stack);
    }

    public static ItemStack withFullDurability(ItemStack stack)
    {
        setRemainingUses(stack, MAX_USES);
        return stack;
    }

    public static void sanitizeStack(ItemStack stack)
    {
        if (stack.isEmpty()) {
            return;
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ench")) {
            stack.getTagCompound().removeTag("ench");
        }
        ensureDurabilityTag(stack);
    }

    public static void damageCompanion(ItemStack stack, EntityPlayer player)
    {
        ensureDurabilityTag(stack);
        if (player.capabilities.isCreativeMode) {
            return;
        }

        int remaining = getRemainingUses(stack);
        if (remaining <= 1) {
            stack.shrink(1);
            return;
        }
        setRemainingUses(stack, remaining - 1);
    }

    private static int getRemainingUses(ItemStack stack)
    {
        ensureDurabilityTag(stack);
        return stack.getTagCompound().getInteger(DURABILITY_KEY);
    }

    private static void ensureDurabilityTag(ItemStack stack)
    {
        NBTTagCompound tag = getOrCreateTag(stack);
        int remaining = tag.hasKey(DURABILITY_KEY, 3) ? tag.getInteger(DURABILITY_KEY) : 1;
        setRemainingUses(stack, remaining);
    }

    private static void setRemainingUses(ItemStack stack, int remaining)
    {
        int clamped = Math.max(1, Math.min(MAX_USES, remaining));
        getOrCreateTag(stack).setInteger(DURABILITY_KEY, clamped);
        stack.setItemDamage(MAX_USES - clamped);
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public int getItemEnchantability()
    {
        return 0;
    }

    @Override
    public int getItemEnchantability(ItemStack stack)
    {
        return 0;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }

    @Override
    public boolean isRepairable()
    {
        return false;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack)
    {
        return 0.0F;
    }

    public static void setChef(ItemStack stack, UUID chef)
    {
        if (chef == null) {
            return;
        }
        getOrCreateTag(stack).setString("Chef", chef.toString());
    }

    public static UUID getChef(ItemStack stack)
    {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Chef", 8)) {
            return null;
        }
        try {
            return UUID.fromString(stack.getTagCompound().getString("Chef"));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void addDiner(ItemStack stack, UUID diner)
    {
        if (diner == null) {
            return;
        }
        NBTTagCompound tag = getOrCreateTag(stack);
        String value = diner.toString();
        int count = tag.getInteger("DinerCount");
        for (int i = 0; i < count; i++) {
            if (value.equals(tag.getString("Diner" + i))) {
                return;
            }
        }
        tag.setString("Diner" + count, value);
        tag.setInteger("DinerCount", count + 1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (world.getBlockState(pos).getBlock() == TSDBlocks.TWILIGHT_CHEESE_FONDUE) {
            return EnumActionResult.PASS;
        }
        if (!world.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.item.twilight_cheese_fondue_companion.need_fondue"), true);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        if (!world.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.item.twilight_cheese_fondue_companion.need_fondue"), true);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
