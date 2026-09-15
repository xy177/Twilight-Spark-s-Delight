package xy177.twilightsparksdelight.common.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.registry.TSDItems;

/**
 * Uses Twilight Forest trophy pedestals as a small, item-entity based 250 network.
 */
public final class TSDExperiment250PedestalEvents {
    private static final String PEDESTAL_POS = "twilight_spark_delight.pedestal_pos";
    private static final String CREATIVE_DISPLAY = "twilight_spark_delight.creative_display";
    private static final float NORMAL_DAMAGE = 1.0F;
    private static final float SLOWED_DAMAGE = 3.0F;

    private TSDExperiment250PedestalEvents() {
    }

    @SubscribeEvent
    public static void onRightClickPedestal(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(TFBlocks.TROPHY_PEDESTAL.get())) {
            return;
        }
        ItemEntity display = findDisplay(level, pos);
        Player player = event.getEntity();
        if (display != null) {
            if (!level.isClientSide) {
                ItemStack stack = display.getItem().copyWithCount(1);
                display.discard();
                boolean createdInCreative = display.getPersistentData().getBoolean(CREATIVE_DISPLAY);
                if (!createdInCreative && !player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            return;
        }

        ItemStack held = event.getItemStack();
        if (!isDisplayItem(held)) {
            return;
        }
        if (!level.isClientSide) {
            ItemStack displayed = held.copyWithCount(1);
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.35D,
                    pos.getZ() + 0.5D, displayed);
            entity.getPersistentData().putLong(PEDESTAL_POS, pos.asLong());
            entity.getPersistentData().putBoolean(CREATIVE_DISPLAY, player.getAbilities().instabuild);
            configureDisplay(entity, pos);
            level.addFreshEntity(entity);
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Level level = event.level;
        if (level.isClientSide || !event.haveTime()) {
            return;
        }
        if (level.getGameTime() % 20L != 0L) return;
        List<ItemEntity> displays = getDisplays(level);
        for (ItemEntity display : displays) {
            BlockPos pos = getPedestalPos(display);
            if (pos == null || !level.getBlockState(pos).is(TFBlocks.TROPHY_PEDESTAL.get())
                    || !isDisplayItem(display.getItem())) {
                releaseDisplay(display);
                continue;
            }
            configureDisplay(display, pos);
        }
        displays.removeIf(Entity::isRemoved);
        if (level.getGameTime() % 60L == 0L) {
            runNetwork(level, displays);
        }
    }

    private static void runNetwork(Level level, List<ItemEntity> displays) {
        List<ItemEntity> scepters = new ArrayList<>();
        List<ItemEntity> experiments = new ArrayList<>();
        for (ItemEntity display : displays) {
            ItemStack stack = display.getItem();
            if (stack.is(TFItems.LIFEDRAIN_SCEPTER.get())) {
                scepters.add(display);
            } else if (stack.getItem() instanceof Experiment250Item
                    && Experiment250Item.getActivity(stack) + 1.0E-9D
                    < Experiment250Item.getCapacity(stack)) {
                experiments.add(display);
            }
        }
        for (ItemEntity scepter : scepters) {
            ItemEntity experiment = selectExperiment(scepter, experiments);
            if (experiment != null) {
                drainCreature(level, scepter, experiment);
            }
        }
    }

    private static ItemEntity selectExperiment(ItemEntity scepter, List<ItemEntity> experiments) {
        BlockPos source = getPedestalPos(scepter);
        if (source == null) {
            return null;
        }
        return experiments.stream()
                .filter(candidate -> isInNetwork(source, getPedestalPos(candidate)))
                .filter(candidate -> Experiment250Item.getActivity(candidate.getItem())
                        < Experiment250Item.getCapacity(candidate.getItem()))
                .min(Comparator.comparingDouble((ItemEntity candidate) ->
                        horizontalDistanceSq(source, getPedestalPos(candidate)))
                        .thenComparingInt(candidate -> directionPriority(source, getPedestalPos(candidate)))
                        .thenComparingInt(candidate -> getPedestalPos(candidate).getX())
                        .thenComparingInt(candidate -> getPedestalPos(candidate).getZ()))
                .orElse(null);
    }

    private static void drainCreature(Level level, ItemEntity scepter, ItemEntity experiment) {
        BlockPos pedestal = getPedestalPos(scepter);
        if (pedestal == null) {
            return;
        }
        AABB search = new AABB(pedestal).inflate(3.0D);
        List<LivingEntity> normal = new ArrayList<>();
        List<LivingEntity> slowed = new ArrayList<>();
        for (LivingEntity target : level.getEntities(EntityTypeTest.forClass(LivingEntity.class), search,
                TSDExperiment250PedestalEvents::isValidTarget)) {
            if (target.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN)) {
                slowed.add(target);
            } else {
                normal.add(target);
            }
        }
        List<LivingEntity> candidates = slowed.isEmpty() ? normal : slowed;
        if (candidates.isEmpty()) {
            return;
        }
        LivingEntity target = candidates.get(level.random.nextInt(candidates.size()));
        boolean wasSlowed = target.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
        var damage = new net.minecraft.world.damagesource.DamageSource(level.registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                .getHolderOrThrow(xy177.twilightsparksdelight.registry.TSDDamageTypes.LIFEDRAIN_PEDESTAL));
        boolean damaged = target.hurt(damage, wasSlowed ? SLOWED_DAMAGE : NORMAL_DAMAGE);
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, true, true));
        if (damaged) {
            ItemStack charged = experiment.getItem().copy();
            Experiment250Item.addActivity(charged, wasSlowed ? 1.5D : 0.5D);
            experiment.setItem(charged);
        }
        if (level instanceof net.minecraft.server.level.ServerLevel server) {
            trail(server, scepter.position().add(0, 0.25, 0), target.getEyePosition());
            trail(server, scepter.position().add(0, 0.25, 0), experiment.position().add(0, 0.25, 0));
        }
    }

    private static boolean isValidTarget(LivingEntity target) {
        return target.isAlive() && !(target instanceof Player) && !target.hasCustomName()
                && !(target instanceof net.minecraft.world.entity.npc.AbstractVillager)
                && !(target instanceof net.minecraft.world.entity.monster.Witch)
                && !(target instanceof net.minecraft.world.entity.decoration.ArmorStand)
                && !(target instanceof net.minecraft.world.entity.OwnableEntity ownable && ownable.getOwnerUUID() != null)
                && !(target instanceof net.minecraft.world.entity.animal.horse.AbstractHorse horse && horse.isTamed())
                && !target.getType().is(net.minecraftforge.common.Tags.EntityTypes.BOSSES)
                && !target.getType().equals(twilightforest.init.TFEntities.QUEST_RAM.get())
                && !isExcludedBoss(target);
    }

    private static boolean isExcludedBoss(LivingEntity target) {
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        return id != null && (id.equals(new net.minecraft.resources.ResourceLocation("ender_dragon"))
                || id.equals(new net.minecraft.resources.ResourceLocation("wither"))
                || id.getNamespace().equals("twilightforest")
                && List.of("naga", "lich", "minoshroom", "hydra", "knight_phantom", "snow_queen",
                        "ur_ghast", "alpha_yeti").contains(id.getPath()));
    }

    private static List<ItemEntity> getDisplays(Level level) {
        List<ItemEntity> result = new ArrayList<>();
        if (level instanceof net.minecraft.server.level.ServerLevel server) {
            for (Entity entity : server.getAllEntities()) {
                if (entity instanceof ItemEntity item && item.getPersistentData().contains(PEDESTAL_POS)
                        && !item.isRemoved()) result.add(item);
            }
        }
        return result;
    }

    private static ItemEntity findDisplay(Level level, BlockPos pos) {
        AABB bounds = new AABB(pos).inflate(0.75D).expandTowards(0.0D, 1.5D, 0.0D);
        return level.getEntities(EntityTypeTest.forClass(ItemEntity.class), bounds,
                entity -> pos.equals(getPedestalPos(entity))).stream().findFirst().orElse(null);
    }

    private static void configureDisplay(ItemEntity entity, BlockPos pos) {
        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.35D, pos.getZ() + 0.5D);
        entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
        entity.setNoGravity(true);
        entity.setNeverPickUp();
        entity.setUnlimitedLifetime();
        entity.setInvulnerable(true);
    }

    private static void releaseDisplay(ItemEntity display) {
        if (!display.getPersistentData().getBoolean(CREATIVE_DISPLAY)) {
            ItemStack stack = display.getItem().copyWithCount(1);
            display.level().addFreshEntity(new ItemEntity(display.level(), display.getX(), display.getY(),
                    display.getZ(), stack));
        }
        display.discard();
    }

    private static BlockPos getPedestalPos(ItemEntity entity) {
        CompoundTag data = entity.getPersistentData();
        return data.contains(PEDESTAL_POS) ? BlockPos.of(data.getLong(PEDESTAL_POS)) : null;
    }

    private static boolean isDisplayItem(ItemStack stack) {
        return stack.is(TFItems.LIFEDRAIN_SCEPTER.get()) || stack.getItem() instanceof Experiment250Item;
    }

    private static boolean isInNetwork(BlockPos origin, BlockPos target) {
        return target != null && origin.getY() == target.getY() && !origin.equals(target)
                && Math.abs(origin.getX() - target.getX()) <= 2
                && Math.abs(origin.getZ() - target.getZ()) <= 2;
    }

    private static double horizontalDistanceSq(BlockPos origin, BlockPos target) {
        int dx = target.getX() - origin.getX();
        int dz = target.getZ() - origin.getZ();
        return dx * dx + dz * dz;
    }

    private static int directionPriority(BlockPos origin, BlockPos target) {
        int dx = target.getX() - origin.getX();
        int dz = target.getZ() - origin.getZ();
        return Math.abs(dz) >= Math.abs(dx) ? (dz < 0 ? 0 : 2) : (dx > 0 ? 1 : 3);
    }

    private static void trail(net.minecraft.server.level.ServerLevel level,
                               net.minecraft.world.phys.Vec3 from, net.minecraft.world.phys.Vec3 to) {
        for (int step = 0; step <= 20; step++) {
            var point = from.lerp(to, step / 20.0D);
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.ENTITY_EFFECT,
                    point.x, point.y, point.z, 0, 0.75, 0.05, 0.05, 1);
        }
    }
}
