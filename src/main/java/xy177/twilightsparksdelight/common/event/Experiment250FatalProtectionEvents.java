package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.food.ExtendedFoodProgression;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.network.TSDNetwork;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.integration.baubles.Experiment250BaublesCompat;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class Experiment250FatalProtectionEvents
{
    private static final String TAG_TRIGGER_COUNT = "TsdExperiment250FatalProtectionCount";

    private Experiment250FatalProtectionEvents()
    {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFatalDamage(LivingDamageEvent event)
    {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)
            || event.getAmount() < event.getEntityLiving().getHealth()) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        Entity attacker = event.getSource().getTrueSource();
        boolean bossAttack = attacker instanceof EntityLivingBase && !((EntityLivingBase) attacker).isNonBoss();
        if (bossAttack && !TSDConfig.experiment250FatalProtectionAllowsBossAttacks) {
            return;
        }

        ResourceLocation attackerId = attacker == null ? null : EntityList.getKey(attacker);
        boolean consumesTrigger = !TSDConfig.isExperiment250NonConsumingAttacker(attackerId)
            && (!bossAttack || TSDConfig.experiment250FatalProtectionBossAttacksConsumeTrigger);
        int triggerCount = getTriggerCount(player);
        if (consumesTrigger && triggerCount >= TSDConfig.experiment250FatalProtectionTriggersPerSleep) {
            return;
        }

        LocatedExperiment experiment = findExperiment(player, TSDConfig.experiment250FatalProtectionActivityCost);
        if (experiment == null) {
            return;
        }

        event.setCanceled(true);
        Experiment250Item.setActivity(
            experiment.stack,
            Experiment250Item.getActivity(experiment.stack) - TSDConfig.experiment250FatalProtectionActivityCost
        );
        experiment.sync();
        if (consumesTrigger) {
            setTriggerCount(player, triggerCount + 1);
            syncTriggerCount(player);
        }

        player.setHealth(Math.max(player.getHealth(), player.getMaxHealth() * 0.5F));
        int maxFood = TSDConfig.extendedFoodStatsEnabled ? ExtendedFoodProgression.getMaxFood(player) : 20;
        float maxSaturation = TSDConfig.extendedFoodStatsEnabled
            ? ExtendedFoodProgression.getMaxSaturation(player)
            : 20.0F;
        FoodStats foodStats = player.getFoodStats();
        foodStats.setFoodLevel(maxFood);
        foodStats.setFoodSaturationLevel(maxSaturation);
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
        player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 800, 0));
        player.world.setEntityState(player, (byte) 35);
        TSDNetwork.displayExperiment250Activation(player, experiment.stack);
        player.inventoryContainer.detectAndSendChanges();
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event)
    {
        if (!event.getEntityPlayer().world.isRemote) {
            setTriggerCount(event.getEntityPlayer(), 0);
            syncTriggerCount((EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
            setTriggerCount(player, 0);
            syncTriggerCount(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP) {
            syncTriggerCount((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            setTriggerCount(player, 0);
            syncTriggerCount(player);
        }
    }

    private static LocatedExperiment findExperiment(EntityPlayer player, double requiredActivity)
    {
        String location = TSDConfig.experiment250FatalProtectionItemLocation;
        ItemStack offhand = player.getHeldItemOffhand();
        if (isUsable(offhand, requiredActivity)) {
            return new LocatedExperiment(offhand, () -> player.inventoryContainer.detectAndSendChanges());
        }
        if ("offhand".equals(location)) {
            return null;
        }
        if ("baubles_or_offhand".equals(location)) {
            Experiment250BaublesCompat.FoundStack bauble = Experiment250BaublesCompat.find(player, requiredActivity);
            return bauble == null ? null : new LocatedExperiment(bauble.getStack(), bauble::sync);
        }
        for (ItemStack stack : player.inventory.mainInventory) {
            if (isUsable(stack, requiredActivity)) {
                return new LocatedExperiment(stack, () -> player.inventoryContainer.detectAndSendChanges());
            }
        }
        return null;
    }

    private static boolean isUsable(ItemStack stack, double requiredActivity)
    {
        return !stack.isEmpty() && stack.getItem() == TSDItems.EXPERIMENT_250
            && Experiment250Item.getActivity(stack) > requiredActivity;
    }

    public static int getTriggerCount(EntityPlayer player)
    {
        return getPersistentData(player).getInteger(TAG_TRIGGER_COUNT);
    }

    public static void setTriggerCount(EntityPlayer player, int count)
    {
        getPersistentData(player).setInteger(TAG_TRIGGER_COUNT, Math.max(0, count));
    }

    public static int getRemainingTriggerCount(EntityPlayer player, ItemStack stack)
    {
        if (player == null || stack.isEmpty()) {
            return 0;
        }
        double activity = Experiment250Item.getActivity(stack);
        double cost = TSDConfig.experiment250FatalProtectionActivityCost;
        if (activity <= cost) {
            return 0;
        }
        int remainingLimit = Math.max(
            0,
            TSDConfig.experiment250FatalProtectionTriggersPerSleep - getTriggerCount(player)
        );
        if (cost <= 0.0D) {
            return remainingLimit;
        }
        int affordableTriggers = Math.max(0, (int) Math.ceil(activity / cost) - 1);
        return Math.min(remainingLimit, affordableTriggers);
    }

    private static void syncTriggerCount(EntityPlayerMP player)
    {
        TSDNetwork.syncExperiment250TriggerCount(player, getTriggerCount(player));
    }

    private static NBTTagCompound getPersistentData(EntityPlayer player)
    {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static final class LocatedExperiment
    {
        private final ItemStack stack;
        private final Runnable synchronizer;

        private LocatedExperiment(ItemStack stack, Runnable synchronizer)
        {
            this.stack = stack;
            this.synchronizer = synchronizer;
        }

        private void sync()
        {
            synchronizer.run();
        }
    }
}
