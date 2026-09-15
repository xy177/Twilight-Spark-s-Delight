package xy177.twilightsparksdelight.common.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import xy177.twilightsparksdelight.common.event.TSDBrackenPacificationEvents;
import xy177.twilightsparksdelight.registry.TSDEntities;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class ThrownPickledBracken extends ThrowableItemProjectile {
    private static final double RANGE = 6.0D;
    private static final int PACIFICATION_TICKS = 600;

    public ThrownPickledBracken(EntityType<? extends ThrownPickledBracken> type, Level level) {
        super(type, level);
    }

    public ThrownPickledBracken(Level level, LivingEntity owner) {
        super(TSDEntities.PICKLED_BRACKEN.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return TSDItems.PICKLED_BRACKEN.get();
    }

    @Override
    protected void onHit(HitResult hit) {
        super.onHit(hit);
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 impact = hit.getLocation();
        if (getOwner() instanceof Player player) {
            AABB area = new AABB(impact, impact).inflate(RANGE);
            for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, area,
                    candidate -> TSDBrackenPacificationEvents.isHostileTo(candidate, player))) {
                TSDBrackenPacificationEvents.pacify(mob, player, PACIFICATION_TICKS);
            }
        }
        serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, getItem()),
                impact.x, impact.y, impact.z, 8, 0.15D, 0.15D, 0.15D, 0.05D);
        discard();
    }
}
