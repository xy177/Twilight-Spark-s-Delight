package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import twilightforest.item.TFItems;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class Experiment250ScepterEvents
{
    private static final String TAG_REDIRECT_TICK = "TsdExperiment250ScepterRedirectTick";
    private static final String TAG_COUNTED_ATTACKER = "TsdExperiment250CountedAttacker";
    private static final String TAG_COUNTED_TICK = "TsdExperiment250CountedTick";
    private static final Map<UUID, FoodSnapshot> FOOD_SNAPSHOTS = new HashMap<>();

    private Experiment250ScepterEvents()
    {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onScepterAttack(LivingAttackEvent event)
    {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        if (player.world.isRemote || getRedirectExperiment250(player).isEmpty()) {
            return;
        }
        rememberFoodBeforeRecovery(player);
        player.getEntityData().setLong(TAG_REDIRECT_TICK, player.world.getTotalWorldTime());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScepterDamage(LivingDamageEvent event)
    {
        if (event.getEntityLiving().world.isRemote || event.getAmount() <= 0.0F
            || !(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack experiment250 = getRedirectExperiment250(player);
        if (experiment250.isEmpty()) {
            return;
        }

        rememberFoodBeforeRecovery(player);
        player.getEntityData().setLong(TAG_REDIRECT_TICK, player.world.getTotalWorldTime());
        EntityLivingBase target = event.getEntityLiving();
        Experiment250Item.addActivity(experiment250, target.isPotionActive(MobEffects.SLOWNESS) ? 3.0D : 1.0D);
        target.getEntityData().setString(TAG_COUNTED_ATTACKER, player.getUniqueID().toString());
        target.getEntityData().setLong(TAG_COUNTED_TICK, player.world.getTotalWorldTime());
        player.inventoryContainer.detectAndSendChanges();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScepterDeath(LivingDeathEvent event)
    {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack experiment250 = getRedirectExperiment250(player);
        if (player.world.isRemote || experiment250.isEmpty()) {
            return;
        }
        String countedAttacker = event.getEntityLiving().getEntityData().getString(TAG_COUNTED_ATTACKER);
        long countedTick = event.getEntityLiving().getEntityData().getLong(TAG_COUNTED_TICK);
        if (player.getUniqueID().toString().equals(countedAttacker)
            && countedTick == player.world.getTotalWorldTime()) {
            return;
        }
        Experiment250Item.addActivity(
            experiment250,
            event.getEntityLiving().isPotionActive(MobEffects.SLOWNESS) ? 3.0D : 1.0D
        );
        player.inventoryContainer.detectAndSendChanges();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onScepterHeal(LivingHealEvent event)
    {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getAmount() > 1.0001F) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (!player.world.isRemote && isRedirectingThisTick(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        if (player.world.isRemote) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            if (!getRedirectExperiment250(player).isEmpty() && player.getItemInUseCount() % 10 == 0) {
                rememberFoodBeforeRecovery(player);
            }
            return;
        }

        FoodSnapshot snapshot = FOOD_SNAPSHOTS.remove(player.getUniqueID());
        if (snapshot == null) {
            return;
        }
        FoodStats foodStats = player.getFoodStats();
        foodStats.setFoodLevel(Math.min(foodStats.getFoodLevel(), snapshot.food));
        foodStats.setFoodSaturationLevel(Math.min(foodStats.getSaturationLevel(), snapshot.saturation));
    }

    private static void rememberFoodBeforeRecovery(EntityPlayer player)
    {
        if (player.getItemInUseCount() % 10 != 0) {
            return;
        }
        FoodStats foodStats = player.getFoodStats();
        FOOD_SNAPSHOTS.putIfAbsent(
            player.getUniqueID(),
            new FoodSnapshot(foodStats.getFoodLevel(), foodStats.getSaturationLevel())
        );
    }

    private static boolean isRedirectingThisTick(EntityPlayer player)
    {
        return player.getEntityData().getLong(TAG_REDIRECT_TICK) == player.world.getTotalWorldTime()
            || (!getRedirectExperiment250(player).isEmpty() && player.getItemInUseCount() % 10 == 0);
    }

    private static ItemStack getRedirectExperiment250(EntityPlayer player)
    {
        if (!player.isHandActive() || player.getActiveItemStack().isEmpty()
            || player.getActiveItemStack().getItem() != TFItems.lifedrain_scepter) {
            return ItemStack.EMPTY;
        }
        EnumHand otherHand = player.getActiveHand() == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        ItemStack experiment250 = player.getHeldItem(otherHand);
        if (experiment250.isEmpty() || experiment250.getItem() != TSDItems.EXPERIMENT_250) {
            return ItemStack.EMPTY;
        }
        return Experiment250Item.getActivity(experiment250) + 1.0E-9D < Experiment250Item.getCapacity(experiment250)
            ? experiment250
            : ItemStack.EMPTY;
    }

    private static final class FoodSnapshot
    {
        private final int food;
        private final float saturation;

        private FoodSnapshot(int food, float saturation)
        {
            this.food = food;
            this.saturation = saturation;
        }
    }
}
