package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDBrackenPacificationEvents
{
    private static final String TAG_TARGET = "TsdPickledBrackenPacifiedTarget";
    private static final String TAG_TICKS = "TsdPickledBrackenPacifiedTicks";

    private TSDBrackenPacificationEvents()
    {
    }

    public static void pacify(EntityLiving entity, EntityPlayer player, int ticks)
    {
        NBTTagCompound data = entity.getEntityData();
        data.setString(TAG_TARGET, player.getUniqueID().toString());
        data.setInteger(TAG_TICKS, Math.max(0, ticks));
    }

    @SubscribeEvent
    public static void onLivingUpdate(TickEvent.WorldTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }
        for (EntityLiving entity : event.world.getEntities(EntityLiving.class, e -> e.getEntityData().getInteger(TAG_TICKS) > 0)) {
            NBTTagCompound data = entity.getEntityData();
            data.setInteger(TAG_TICKS, data.getInteger(TAG_TICKS) - 1);
            if (isTargetPacified(entity, entity.getAttackTarget())) {
                entity.setAttackTarget(null);
            }
            if (isTargetPacified(entity, entity.getRevengeTarget())) {
                entity.setRevengeTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void onSetAttackTarget(LivingSetAttackTargetEvent event)
    {
        if (!(event.getEntityLiving() instanceof EntityLiving)) {
            return;
        }
        EntityLiving entity = (EntityLiving) event.getEntityLiving();
        if (isTargetPacified(entity, event.getTarget())) {
            entity.setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event)
    {
        if (!(event.getEntityLiving() instanceof EntityLiving) || !(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        EntityLiving entity = (EntityLiving) event.getEntityLiving();
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        if (isTargetPacified(entity, player)) {
            clearPacification(entity);
        }
    }

    private static boolean isTargetPacified(EntityLiving entity, EntityLivingBase target)
    {
        if (!(target instanceof EntityPlayer)) {
            return false;
        }
        NBTTagCompound data = entity.getEntityData();
        return data.getInteger(TAG_TICKS) > 0 && ((EntityPlayer) target).getUniqueID().toString().equals(data.getString(TAG_TARGET));
    }

    private static void clearPacification(EntityLiving entity)
    {
        NBTTagCompound data = entity.getEntityData();
        data.removeTag(TAG_TARGET);
        data.removeTag(TAG_TICKS);
    }
}
