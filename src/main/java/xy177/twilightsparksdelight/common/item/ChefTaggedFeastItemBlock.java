package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.tile.TileEntitySharedFeast;

import java.util.UUID;

public class ChefTaggedFeastItemBlock extends ItemBlock
{
    public ChefTaggedFeastItemBlock(Block block)
    {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        UUID chef = getChef(stack);
        BlockPos placedPos = world.getBlockState(pos).getBlock().isReplaceable(world, pos) ? pos : pos.offset(facing);
        EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        if (!world.isRemote && result == EnumActionResult.SUCCESS) {
            if (world.getTileEntity(placedPos) instanceof TileEntitySharedFeast) {
                TileEntitySharedFeast feast = (TileEntitySharedFeast) world.getTileEntity(placedPos);
                if (chef == null) {
                    chef = player.getUniqueID();
                }
                feast.setChef(chef);
            }
        }
        return result;
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
        if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("Chef", 8)) {
            return null;
        }
        try {
            return UUID.fromString(stack.getTagCompound().getString("Chef"));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
