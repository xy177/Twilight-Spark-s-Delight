package xy177.twilightsparksdelight.common.food;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import xy177.twilightsparksdelight.common.config.TSDConfig;

public final class ExtendedFoodProgression
{
    public static final int BASE_CAP = 20;
    public static final int FULL_CAP = 40;
    private static final int CAP_PER_ADVANCEMENT = 2;
    private static final ResourceLocation[] PROGRESSION_ADVANCEMENTS = {
        id("progress_naga"),
        id("progress_lich"),
        id("progress_knights"),
        id("progress_labyrinth"),
        id("progress_yeti"),
        id("progress_ur_ghast"),
        id("progress_hydra"),
        id("progress_glacier"),
        id("quest_ram"),
        id("progress_merge")
    };

    private ExtendedFoodProgression()
    {
    }

    public static int getMaxFood(EntityPlayer player)
    {
        if (!TSDConfig.extendedFoodProgressiveCapsEnabled || !(player instanceof EntityPlayerMP)) {
            return FULL_CAP;
        }
        int completed = 0;
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        MinecraftServer server = playerMP.getServer();
        if (server == null) {
            return FULL_CAP;
        }
        for (ResourceLocation id : PROGRESSION_ADVANCEMENTS) {
            Advancement advancement = server.getAdvancementManager().getAdvancement(id);
            if (advancement != null && playerMP.getAdvancements().getProgress(advancement).isDone()) {
                completed++;
            }
        }
        return Math.min(FULL_CAP, BASE_CAP + completed * CAP_PER_ADVANCEMENT);
    }

    public static float getMaxSaturation(EntityPlayer player)
    {
        return getMaxFood(player);
    }

    public static boolean isInExtraRange(EntityPlayer player)
    {
        if (player == null) {
            return false;
        }
        FoodStats stats = player.getFoodStats();
        return stats.getFoodLevel() > BASE_CAP || stats.getSaturationLevel() > BASE_CAP;
    }

    public static boolean shouldUseExtraDrain(EntityPlayer player)
    {
        return TSDConfig.extendedFoodExtraConsumptionAndBenefitsEnabled
            && player != null
            && isInExtraRange(player)
            && !player.isPotionActive(MobEffects.HUNGER);
    }

    public static double getExtraBenefit(EntityPlayer player)
    {
        return TSDConfig.extendedFoodExtraConsumptionAndBenefitsEnabled && isInExtraRange(player)
            ? TSDConfig.extendedFoodExtraBenefitMultiplier
            : 0.0D;
    }

    private static ResourceLocation id(String path)
    {
        return new ResourceLocation("twilightforest", path);
    }
}
