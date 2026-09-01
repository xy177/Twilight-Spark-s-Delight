package xy177.twilightsparksdelight.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xy177.twilightsparksdelight.common.entity.EntityThrownPickledBracken;

import java.util.List;

public class PickledBrackenItem extends TSDFoodItem
{
    public PickledBrackenItem()
    {
        super(7, 0.0F, false, null, EnumAction.EAT);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            return super.onItemRightClick(world, player, hand);
        }
        if (!world.isRemote) {
            EntityThrownPickledBracken thrown = new EntityThrownPickledBracken(world, player);
            thrown.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.0F, 1.0F);
            world.spawnEntity(thrown);
        }
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("twilight_spark_delight.tooltip.pickled_bracken_throw"));
    }
}
