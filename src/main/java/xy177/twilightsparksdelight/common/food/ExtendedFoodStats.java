package xy177.twilightsparksdelight.common.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xy177.twilightsparksdelight.common.config.TSDConfig;

public class ExtendedFoodStats extends FoodStats
{
    public static final int MAX_FOOD = ExtendedFoodProgression.FULL_CAP;
    public static final float MAX_SATURATION = ExtendedFoodProgression.FULL_CAP;
    private EntityPlayer currentPlayer;

    public ExtendedFoodStats()
    {
        super();
        clamp();
    }

    public ExtendedFoodStats(FoodStats original)
    {
        this();
        setFoodLevel(original.getFoodLevel());
        setFoodSaturationLevel(original.getSaturationLevel());
        setExhaustion(getExhaustion(original));
        setTimer(getTimer(original));
        setPrevFoodLevel(getPrevFoodLevel(original));
    }

    @Override
    public void addStats(int foodLevelIn, float foodSaturationModifier)
    {
        int food = clampFoodForPlayer(getFoodLevel() + foodLevelIn);
        setFoodLevel(food);
        setFoodSaturationLevel(getSaturationLevel() + foodLevelIn * foodSaturationModifier * 2.0F);
    }

    @Override
    public void addStats(ItemFood foodItem, ItemStack stack)
    {
        addStats(foodItem.getHealAmount(stack), foodItem.getSaturationModifier(stack));
    }

    @Override
    public boolean needFood()
    {
        return getFoodLevel() < getMaxFood();
    }

    @Override
    public void setFoodLevel(int foodLevelIn)
    {
        super.setFoodLevel(clampFoodForPlayer(foodLevelIn));
        setFoodSaturationLevel(getSaturationLevel());
    }

    @Override
    public void setFoodSaturationLevel(float saturationLevelIn)
    {
        super.setFoodSaturationLevel(clampSaturationForPlayer(saturationLevelIn, getFoodLevel()));
    }

    @Override
    public void addExhaustion(float exhaustion)
    {
        float multiplier = ExtendedFoodProgression.shouldUseExtraDrain(currentPlayer)
            ? (float) (1.0D + TSDConfig.extendedFoodExtraConsumptionMultiplier)
            : 1.0F;
        setExhaustion(Math.min(getExhaustion(this) + exhaustion * multiplier, 40.0F));
    }

    @Override
    public void onUpdate(EntityPlayer player)
    {
        currentPlayer = player;
        clamp(player);
        EnumDifficulty difficulty = player.world.getDifficulty();
        setPrevFoodLevel(getFoodLevel());

        float exhaustion = getExhaustion(this);
        if (exhaustion > 4.0F) {
            setExhaustion(exhaustion - 4.0F);
            float drain = ExtendedFoodProgression.shouldUseExtraDrain(player)
                ? (float) (1.0D + TSDConfig.extendedFoodExtraConsumptionMultiplier)
                : 1.0F;
            if (getSaturationLevel() > 0.0F) {
                setFoodSaturationLevel(Math.max(getSaturationLevel() - drain, 0.0F));
            } else if (difficulty != EnumDifficulty.PEACEFUL) {
                setFoodLevel(Math.max(getFoodLevel() - Math.max(1, Math.round(drain)), 0));
            }
        }

        boolean naturalRegeneration = player.world.getGameRules().getBoolean("naturalRegeneration");
        int timer = getTimer(this);
        if (naturalRegeneration && getSaturationLevel() > 0.0F && player.shouldHeal() && getFoodLevel() >= 20) {
            timer++;
            if (timer >= 10) {
                float saturation = Math.min(getSaturationLevel(), 6.0F);
                player.heal((float) ((saturation / 6.0F) * getExtraHealingMultiplier(player)));
                addExhaustion(saturation);
                timer = 0;
            }
        } else if (naturalRegeneration && getFoodLevel() >= 18 && player.shouldHeal()) {
            timer++;
            if (timer >= 80) {
                player.heal((float) getExtraHealingMultiplier(player));
                addExhaustion(6.0F);
                timer = 0;
            }
        } else if (getFoodLevel() <= 0) {
            timer++;
            if (timer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && difficulty == EnumDifficulty.NORMAL) {
                    player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                }
                timer = 0;
            }
        } else {
            timer = 0;
        }
        setTimer(timer);
        clamp(player);
        currentPlayer = null;
    }

    @Override
    public void readNBT(NBTTagCompound compound)
    {
        super.readNBT(compound);
        clamp();
    }

    private void clamp()
    {
        setFoodLevel(getFoodLevel());
        setFoodSaturationLevel(getSaturationLevel());
    }

    public void clamp(EntityPlayer player)
    {
        currentPlayer = player;
        setFoodLevel(getFoodLevel());
        setFoodSaturationLevel(getSaturationLevel());
        currentPlayer = null;
    }

    public void resetForRespawn(EntityPlayer player, boolean useExtendedCap, float saturation)
    {
        currentPlayer = player;
        setFoodLevel(useExtendedCap ? getMaxFood() : ExtendedFoodProgression.BASE_CAP);
        setFoodSaturationLevel(saturation);
        setExhaustion(0.0F);
        setTimer(0);
        setPrevFoodLevel(getFoodLevel());
        currentPlayer = null;
    }

    private int clampFoodForPlayer(int food)
    {
        return Math.max(0, Math.min(getMaxFood(), food));
    }

    private float clampSaturationForPlayer(float saturation, int food)
    {
        return Math.max(0.0F, Math.min(Math.min(getMaxSaturation(), food), saturation));
    }

    private int getMaxFood()
    {
        return currentPlayer == null ? MAX_FOOD : ExtendedFoodProgression.getMaxFood(currentPlayer);
    }

    private float getMaxSaturation()
    {
        return currentPlayer == null ? MAX_SATURATION : ExtendedFoodProgression.getMaxSaturation(currentPlayer);
    }

    private static double getExtraHealingMultiplier(EntityPlayer player)
    {
        return 1.0D + ExtendedFoodProgression.getExtraBenefit(player);
    }

    private static float getExhaustion(FoodStats stats)
    {
        return ObfuscationReflectionHelper.getPrivateValue(FoodStats.class, stats, "foodExhaustionLevel", "field_75126_c");
    }

    private static int getTimer(FoodStats stats)
    {
        return ObfuscationReflectionHelper.getPrivateValue(FoodStats.class, stats, "foodTimer", "field_75123_d");
    }

    private static int getPrevFoodLevel(FoodStats stats)
    {
        return ObfuscationReflectionHelper.getPrivateValue(FoodStats.class, stats, "prevFoodLevel", "field_75124_e");
    }

    private void setExhaustion(float exhaustion)
    {
        ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, this, exhaustion, "foodExhaustionLevel", "field_75126_c");
    }

    private void setTimer(int timer)
    {
        ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, this, timer, "foodTimer", "field_75123_d");
    }

    private void setPrevFoodLevel(int prevFoodLevel)
    {
        ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, this, prevFoodLevel, "prevFoodLevel", "field_75124_e");
    }
}
