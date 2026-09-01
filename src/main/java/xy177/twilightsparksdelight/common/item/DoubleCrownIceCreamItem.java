package xy177.twilightsparksdelight.common.item;

import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xy177.twilightsparksdelight.common.config.TSDConfig;

import java.util.List;

public class DoubleCrownIceCreamItem extends TSDFoodItem
{
    private static final ResourceLocation FROSTED = new ResourceLocation("twilightforest", "frosted");

    public DoubleCrownIceCreamItem()
    {
        super(7, 8.4F / (7.0F * 2.0F), false);
        addEffect(new ResourceLocation("minecraft", "resistance"), 1800, 1, 1.0F);
    }

    @Override
    protected void afterEaten(ItemStack stack, World world, EntityPlayer player)
    {
        Potion frosted = ForgeRegistries.POTIONS.getValue(FROSTED);
        if (frosted == null) {
            return;
        }

        float noFrostedChance = clamp(TSDConfig.doubleCrownIceCreamNoFrostedChance);
        float longFrostedChance = Math.min(clamp(TSDConfig.doubleCrownIceCreamLongFrostedChance), 1.0F - noFrostedChance);
        float roll = world.rand.nextFloat();
        if (roll < noFrostedChance) {
            player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.message.double_crown_ice_cream.no_frosted"), true);
        } else if (roll < noFrostedChance + longFrostedChance) {
            player.addPotionEffect(new PotionEffect(frosted, 12000, 0, false, false));
            player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.message.double_crown_ice_cream.long_frosted"), true);
        } else {
            player.addPotionEffect(new PotionEffect(frosted, 3600, 0, false, false));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        if (Configuration.foodEffectTooltip) {
            addFrostedTooltip(tooltip);
        }
    }

    @SideOnly(Side.CLIENT)
    private void addFrostedTooltip(List<String> tooltip)
    {
        Potion frosted = ForgeRegistries.POTIONS.getValue(FROSTED);
        if (frosted == null) {
            return;
        }
        PotionEffect effect = new PotionEffect(frosted, 3600, 0);
        String duration = Potion.getPotionDurationString(effect, 1.0F);
        String effectName = new TextComponentTranslation(effect.getEffectName()).getFormattedText();
        TextComponentTranslation line = new TextComponentTranslation("farmersdelight.tooltip.food.effect", effectName, duration);
        line.getStyle().setColor(TextFormatting.BLUE);
        tooltip.add(line.getFormattedText());
    }

    private static float clamp(float value)
    {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
