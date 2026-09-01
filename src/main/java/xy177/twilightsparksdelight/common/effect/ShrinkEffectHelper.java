package xy177.twilightsparksdelight.common.effect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.registry.TSDPotions;

public final class ShrinkEffectHelper
{
    public static final float PLAYER_WIDTH = 0.6F;
    public static final float PLAYER_HEIGHT = 1.8F;
    public static final float PLAYER_EYE_HEIGHT = 1.62F;

    private ShrinkEffectHelper()
    {
    }

    public static float getScale(EntityPlayer player)
    {
        PotionEffect enlarge = player.getActivePotionEffect(TSDPotions.ENLARGE);
        PotionEffect shrink = player.getActivePotionEffect(TSDPotions.SHRINK);
        int enlargeLevel = enlarge == null ? 0 : enlarge.getAmplifier() + 1;
        int shrinkLevel = shrink == null ? 0 : shrink.getAmplifier() + 1;
        int netLevel = enlargeLevel - shrinkLevel;
        if (netLevel < 0) {
            double reduction = TSDConfig.shrinkBaseReduction + TSDConfig.shrinkReductionPerLevel * -netLevel;
            return (float) Math.max(0.1D, 1.0D - Math.min(0.9D, reduction));
        } else if (netLevel > 0) {
            double growth = TSDConfig.enlargeBaseScale + TSDConfig.enlargeScalePerLevel * netLevel;
            return (float) Math.max(0.1D, 1.0D + growth);
        }
        return 1.0F;
    }

    public static boolean hasScaleEffect(EntityPlayer player)
    {
        return Math.abs(getScale(player) - 1.0F) > 0.001F;
    }

    public static double getShrinkDodgeChance(EntityPlayer player)
    {
        float scale = getScale(player);
        return scale < 1.0F ? Math.max(0.0D, Math.min(0.95D, 1.0D - scale)) : 0.0D;
    }

    public static double getReachMultiplier(EntityPlayer player)
    {
        float scale = getScale(player);
        if (scale > 1.0F) {
            return scale;
        }
        if (scale < 1.0F) {
            return 1.0D - (1.0D - scale) * 0.5D;
        }
        return 1.0D;
    }
}
