package xy177.twilightsparksdelight.common.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;

public final class TSDBrackenPacificationEvents {
    private static final String TARGET = "TsdPickledBrackenPacifiedTarget";
    private static final String TICKS = "TsdPickledBrackenPacifiedTicks";

    private TSDBrackenPacificationEvents() {
    }

    public static boolean isHostileTo(Mob mob, Player player) {
        return mob.isAlive() && mob instanceof Enemy && !mob.getType().is(Tags.EntityTypes.BOSSES)
                && (mob.getTarget() == player || mob.getLastHurtByMob() == player
                || memoryValue(mob, MemoryModuleType.ATTACK_TARGET) == player);
    }

    public static void pacify(Mob mob, Player player, int ticks) {
        if (mob.level().isClientSide || ticks <= 0) {
            return;
        }
        CompoundTag data = mob.getPersistentData();
        data.putUUID(TARGET, player.getUUID());
        data.putInt(TICKS, ticks);
        clearPlayerTargets(mob);
    }

    @SubscribeEvent
    public static void onEntityTick(LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide) {
            return;
        }
        CompoundTag data = mob.getPersistentData();
        int ticks = data.getInt(TICKS);
        if (ticks <= 0) {
            return;
        }
        clearPlayerTargets(mob);
        if (ticks == 1) {
            clearPacification(mob);
        } else {
            data.putInt(TICKS, ticks - 1);
        }
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob mob && !mob.level().isClientSide
                && isPacified(mob, event.getNewTarget())) {
            // Cancellation preserves a previous unrelated target, including for Brain-based AI.
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof Mob mob && !mob.level().isClientSide
                && event.getSource().getEntity() instanceof Player player && isPacified(mob, player)) {
            clearPacification(mob);
        }
    }

    private static void clearPlayerTargets(Mob mob) {
        if (isPacified(mob, mob.getTarget())) {
            mob.setTarget(null);
            mob.getNavigation().stop();
        }
        if (isPacified(mob, mob.getLastHurtByMob())) {
            mob.setLastHurtByMob(null);
        }
        if (isPacified(mob, memoryValue(mob, MemoryModuleType.ATTACK_TARGET))) {
            mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            mob.getNavigation().stop();
        }
        if (isPacified(mob, memoryValue(mob, MemoryModuleType.HURT_BY_ENTITY))) {
            mob.getBrain().eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
        }
    }

    private static LivingEntity memoryValue(Mob mob, MemoryModuleType<LivingEntity> type) {
        var memory = mob.getBrain().getMemoryInternal(type);
        return memory == null ? null : memory.orElse(null);
    }

    private static boolean isPacified(Mob mob, LivingEntity target) {
        CompoundTag data = mob.getPersistentData();
        return target instanceof Player && data.getInt(TICKS) > 0 && data.hasUUID(TARGET)
                && target.getUUID().equals(data.getUUID(TARGET));
    }

    private static void clearPacification(Mob mob) {
        mob.getPersistentData().remove(TARGET);
        mob.getPersistentData().remove(TICKS);
    }
}
