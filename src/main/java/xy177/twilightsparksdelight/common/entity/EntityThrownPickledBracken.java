package xy177.twilightsparksdelight.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.event.TSDBrackenPacificationEvents;

import java.util.List;

public class EntityThrownPickledBracken extends EntityThrowable
{
    private static final double RANGE = 6.0D;

    public EntityThrownPickledBracken(World world)
    {
        super(world);
    }

    public EntityThrownPickledBracken(World world, EntityLivingBase thrower)
    {
        super(world, thrower);
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (!world.isRemote) {
            pacifyNearby();
            setDead();
        }
    }

    private void pacifyNearby()
    {
        EntityLivingBase thrower = getThrower();
        if (!(thrower instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) thrower;
        AxisAlignedBB area = getEntityBoundingBox().grow(RANGE);
        List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, area);
        for (EntityLiving entity : entities) {
            if (!isHostileToPlayer(entity, player)) {
                continue;
            }
            entity.setAttackTarget(null);
            entity.setRevengeTarget(null);
            TSDBrackenPacificationEvents.pacify(entity, player, 600);
        }
    }

    private boolean isHostileToPlayer(EntityLiving entity, EntityPlayer player)
    {
        if (entity instanceof IMob && entity.isNonBoss()) {
            Entity target = entity.getAttackTarget();
            EntityLivingBase revenge = entity.getRevengeTarget();
            return target == player || revenge == player;
        }
        return false;
    }
}
