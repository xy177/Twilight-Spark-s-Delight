package xy177.twilightsparksdelight.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xy177.twilightsparksdelight.common.config.TSDConfig;

import java.util.ArrayList;
import java.util.List;

public class RandomCureFoodItem extends TSDFoodItem
{
    public RandomCureFoodItem(int amount, float saturation, boolean wolfFood, Item containerItem, EnumAction action)
    {
        super(amount, saturation, wolfFood, containerItem, action);
    }

    @Override
    protected void afterEaten(ItemStack stack, World world, EntityPlayer player)
    {
        List<PotionEffect> candidates = new ArrayList<>();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            Potion potion = effect.getPotion();
            if (potion != null && potion.isBadEffect() && !TSDConfig.isRandomCureBlacklisted(potion)) {
                candidates.add(effect);
            }
        }
        if (!candidates.isEmpty()) {
            PotionEffect chosen = candidates.get(world.rand.nextInt(candidates.size()));
            player.removePotionEffect(chosen.getPotion());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(TextFormatting.DARK_PURPLE + new TextComponentTranslation("twilight_spark_delight.tooltip.random_cure").getFormattedText());
    }
}
