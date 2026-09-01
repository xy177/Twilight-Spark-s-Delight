package xy177.twilightsparksdelight.common.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.Optional;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.event.Experiment250FatalProtectionEvents;

import java.text.DecimalFormat;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class Experiment250Item extends Item implements IBauble
{
    public static final String TAG_LEVEL = "Level";
    public static final String TAG_ACTIVITY = "Activity";
    public static final String TAG_BOUND_MEAT = "BoundMeat";
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 6;
    private static final double BASE_CAPACITY = 250.0D;

    public Experiment250Item()
    {
        setMaxStackSize(1);
        addPropertyOverride(
            new ResourceLocation(TwilightSparksDelight.MODID, "activity_stage"),
            (stack, world, entity) -> getActivityStage(stack)
        );
    }

    public static ItemStack createStack(Item item, int level, double activity)
    {
        ItemStack stack = new ItemStack(item);
        setLevel(stack, level);
        setActivity(stack, activity);
        return stack;
    }

    public static int getLevel(ItemStack stack)
    {
        if (stack.isEmpty() || !stack.hasTagCompound()) {
            return MIN_LEVEL;
        }
        return clampLevel(stack.getTagCompound().getInteger(TAG_LEVEL));
    }

    public static void setLevel(ItemStack stack, int level)
    {
        if (stack.isEmpty()) {
            return;
        }
        int clampedLevel = clampLevel(level);
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setInteger(TAG_LEVEL, clampedLevel);
        tag.setDouble(TAG_ACTIVITY, clampActivity(tag.getDouble(TAG_ACTIVITY), clampedLevel));
    }

    public static double getActivity(ItemStack stack)
    {
        if (stack.isEmpty() || !stack.hasTagCompound()) {
            return 0.0D;
        }
        int level = getLevel(stack);
        return clampActivity(stack.getTagCompound().getDouble(TAG_ACTIVITY), level);
    }

    public static void setActivity(ItemStack stack, double activity)
    {
        if (stack.isEmpty()) {
            return;
        }
        int level = getLevel(stack);
        getOrCreateTag(stack).setDouble(TAG_ACTIVITY, clampActivity(activity, level));
    }

    public static double addActivity(ItemStack stack, double amount)
    {
        double before = getActivity(stack);
        setActivity(stack, before + Math.max(0.0D, amount));
        return getActivity(stack) - before;
    }

    public static double getCapacity(ItemStack stack)
    {
        return getCapacity(getLevel(stack));
    }

    public static double getCapacity(int level)
    {
        double capacity = BASE_CAPACITY;
        for (int current = MIN_LEVEL; current < clampLevel(level); current++) {
            capacity *= 3.0D;
        }
        return capacity;
    }

    public static ResourceLocation getBoundMeat(ItemStack stack)
    {
        if (stack.isEmpty() || !stack.hasTagCompound()) {
            return null;
        }
        String id = stack.getTagCompound().getString(TAG_BOUND_MEAT);
        if (id.isEmpty()) {
            return null;
        }
        try {
            return new ResourceLocation(id);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static void setBoundMeat(ItemStack stack, ResourceLocation meatId)
    {
        if (stack.isEmpty()) {
            return;
        }
        NBTTagCompound tag = getOrCreateTag(stack);
        if (meatId == null) {
            tag.removeTag(TAG_BOUND_MEAT);
        } else {
            tag.setString(TAG_BOUND_MEAT, meatId.toString());
        }
    }

    public static void normalize(ItemStack stack)
    {
        if (stack.isEmpty()) {
            return;
        }
        int level = getLevel(stack);
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setInteger(TAG_LEVEL, level);
        tag.setDouble(TAG_ACTIVITY, clampActivity(tag.getDouble(TAG_ACTIVITY), level));
    }

    private static float getActivityStage(ItemStack stack)
    {
        double ratio = getActivity(stack) / getCapacity(stack);
        if (ratio >= 2.0D / 3.0D) {
            return 2.0F;
        }
        if (ratio >= 1.0D / 3.0D) {
            return 1.0F;
        }
        return 0.0F;
    }

    private static int clampLevel(int level)
    {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
    }

    private static double clampActivity(double activity, int level)
    {
        if (Double.isNaN(activity) || activity <= 0.0D) {
            return 0.0D;
        }
        return Math.min(getCapacity(level), activity);
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
    {
        normalize(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab)) {
            items.add(createStack(this, MIN_LEVEL, 0.0D));
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return getLevel(stack) >= MAX_LEVEL;
    }

    @Override
    @Optional.Method(modid = "baubles")
    public BaubleType getBaubleType(ItemStack stack)
    {
        return BaubleType.TRINKET;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        DecimalFormat format = new DecimalFormat("0.##");
        tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.translateToLocalFormatted(
            "twilight_spark_delight.tooltip.experiment_250.level",
            getLevel(stack)
        ));
        tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.translateToLocalFormatted(
            "twilight_spark_delight.tooltip.experiment_250.activity",
            format.format(getActivity(stack)),
            format.format(getCapacity(stack))
        ));
        if (getActivity(stack) > TSDConfig.experiment250FatalProtectionActivityCost
            && Minecraft.getMinecraft().player != null) {
            tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.translateToLocalFormatted(
                "twilight_spark_delight.tooltip.experiment_250.remaining_revives",
                Experiment250FatalProtectionEvents.getRemainingTriggerCount(Minecraft.getMinecraft().player, stack)
            ));
        }
        ResourceLocation boundMeat = getBoundMeat(stack);
        if (boundMeat != null) {
            tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.translateToLocalFormatted(
                "twilight_spark_delight.tooltip.experiment_250.bound_meat",
                boundMeat.toString()
            ));
        }
    }
}
