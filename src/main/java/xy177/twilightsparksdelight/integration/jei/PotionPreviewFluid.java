package xy177.twilightsparksdelight.integration.jei;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public final class PotionPreviewFluid
{
    public static final String TAG_NAME = "PotionPreviewName";
    public static final String TAG_COLOR = "PotionPreviewColor";
    public static final String TAG_POTION_STACK = "PotionPreviewStack";

    private PotionPreviewFluid()
    {
    }

    public static int getColor(FluidStack stack)
    {
        return stack != null && stack.tag != null && stack.tag.hasKey(TAG_COLOR, 3)
            ? stack.tag.getInteger(TAG_COLOR)
            : 0xFFFFFF;
    }

    public static String getLocalizedName(FluidStack stack)
    {
        return stack != null && stack.tag != null && stack.tag.hasKey(TAG_NAME, 8)
            ? stack.tag.getString(TAG_NAME)
            : stack == null || stack.getFluid() == null ? "" : stack.getLocalizedName();
    }

    public static FluidStack fromPotion(ItemStack potion)
    {
        FluidStack fluid = new FluidStack(FluidRegistry.WATER, 1000);
        fluid.tag = new NBTTagCompound();
        fluid.tag.setString(TAG_NAME, potion.getDisplayName());
        fluid.tag.setInteger(TAG_COLOR, net.minecraft.potion.PotionUtils.getColor(potion));
        fluid.tag.setTag(TAG_POTION_STACK, potion.writeToNBT(new NBTTagCompound()));
        return fluid;
    }

    public static ItemStack getPotionStack(FluidStack stack)
    {
        return stack != null && stack.tag != null && stack.tag.hasKey(TAG_POTION_STACK, 10)
            ? new ItemStack(stack.tag.getCompoundTag(TAG_POTION_STACK))
            : ItemStack.EMPTY;
    }
}
