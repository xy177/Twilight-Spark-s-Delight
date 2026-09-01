package xy177.twilightsparksdelight.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.event.TSDAdvancements;

public class CrabItem extends Item
{
    private static final String NBT_BITE_COUNT = "BiteCount";
    private static final String PLAYER_TAG_FALSE_TEETH = "TsdFalseTeethUnlocked";

    public CrabItem()
    {
        setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            player.getCooldownTracker().setCooldown(this, 300);
            player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.item.hermit_crab.hint"), true);
            player.attackEntityFrom(new CrabBiteDamageSource(player), 2.0F);
            if (!player.isEntityAlive() && player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                net.minecraft.entity.player.EntityPlayerMP mp = (net.minecraft.entity.player.EntityPlayerMP) player;
                NBTTagCompound persisted = mp.getEntityData();
                if (!persisted.getBoolean(PLAYER_TAG_FALSE_TEETH)) {
                    persisted.setBoolean(PLAYER_TAG_FALSE_TEETH, true);
                    TSDAdvancements.MY_FALSE_TEETH.trigger(mp);
                }
            }
            int bites = getBites(stack) + 1;
            if (bites >= 8) {
                bites = 0;
                stack.shrink(1);
                give(player, new ItemStack(TSDItems.HERMIT_CRAB_LEG, 6));
                Item shard = ForgeRegistries.ITEMS.getValue(new ResourceLocation("twilightforest", "armor_shard"));
                if (shard != null) {
                    give(player, new ItemStack(shard, 45));
                }
                if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                    TSDAdvancements.RUTHLESS_IRON_MOUTH.trigger((net.minecraft.entity.player.EntityPlayerMP) player);
                }
            }
            setBites(stack, bites);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private static int getBites(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey(NBT_BITE_COUNT) ? tag.getInteger(NBT_BITE_COUNT) : 0;
    }

    private static void setBites(ItemStack stack, int bites)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger(NBT_BITE_COUNT, bites);
    }

    private static void give(EntityPlayer player, ItemStack stack)
    {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, java.util.List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("TsdJeiHint")) {
            tooltip.add(I18n.translateToLocal(stack.getTagCompound().getString("TsdJeiHint")));
        }
    }
}
