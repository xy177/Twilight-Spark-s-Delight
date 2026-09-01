package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.food.ExtendedFoodStats;
import xy177.twilightsparksdelight.common.food.ExtendedFoodProgression;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDExtendedFoodEvents
{
    private static final java.util.UUID EXTENDED_FOOD_SPRINT_UUID =
        java.util.UUID.fromString("0F0A3011-6B3E-4603-8B80-08D7D908DD9A");

    private TSDExtendedFoodEvents()
    {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        if (TSDConfig.extendedFoodStatsEnabled) {
            install(event.player);
            updateSprintBonus(event.player);
        } else {
            removeSprintBonus(event.player);
            uninstall(event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        if (!TSDConfig.extendedFoodStatsEnabled) {
            return;
        }
        install(event.getEntityPlayer());
        FoodStats original = event.getOriginal().getFoodStats();
        FoodStats current = event.getEntityPlayer().getFoodStats();
        if (current instanceof ExtendedFoodStats) {
            if (event.isWasDeath()) {
                ((ExtendedFoodStats) current).resetForRespawn(
                    event.getEntityPlayer(),
                    !TSDConfig.extendedFoodKeepOnRespawn,
                    current.getSaturationLevel()
                );
            } else {
                ((ExtendedFoodStats) current).readNBT(write(original));
                ((ExtendedFoodStats) current).clamp(event.getEntityPlayer());
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(BreakSpeed event)
    {
        if (!TSDConfig.extendedFoodStatsEnabled) {
            return;
        }
        double bonus = ExtendedFoodProgression.getExtraBenefit(event.getEntityPlayer());
        if (bonus > 0.0D) {
            event.setNewSpeed((float) (event.getNewSpeed() * (1.0D + bonus)));
        }
    }

    private static void install(EntityPlayer player)
    {
        FoodStats current = player.getFoodStats();
        if (current instanceof ExtendedFoodStats) {
            ((ExtendedFoodStats) current).clamp(player);
            return;
        }
        ExtendedFoodStats extended = new ExtendedFoodStats(current);
        setFoodStats(player, extended);
        extended.clamp(player);
    }

    private static void updateSprintBonus(EntityPlayer player)
    {
        removeSprintBonus(player);
        double bonus = ExtendedFoodProgression.getExtraBenefit(player);
        if (bonus > 0.0D && player.isSprinting()) {
            player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(
                new AttributeModifier(EXTENDED_FOOD_SPRINT_UUID, "TSD extended food sprint", bonus, 2).setSaved(false)
            );
        }
    }

    private static void removeSprintBonus(EntityPlayer player)
    {
        if (player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(EXTENDED_FOOD_SPRINT_UUID) != null) {
            player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(EXTENDED_FOOD_SPRINT_UUID);
        }
    }

    private static void uninstall(EntityPlayer player)
    {
        FoodStats current = player.getFoodStats();
        if (!(current instanceof ExtendedFoodStats)) {
            return;
        }
        FoodStats vanilla = new FoodStats();
        vanilla.setFoodLevel(Math.min(20, current.getFoodLevel()));
        vanilla.setFoodSaturationLevel(Math.min(vanilla.getFoodLevel(), current.getSaturationLevel()));
        setFoodStats(player, vanilla);
    }

    private static net.minecraft.nbt.NBTTagCompound write(FoodStats stats)
    {
        net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
        stats.writeNBT(tag);
        return tag;
    }

    private static void setFoodStats(EntityPlayer player, FoodStats stats)
    {
        ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, player, stats, "foodStats", "field_71100_bB");
    }
}
