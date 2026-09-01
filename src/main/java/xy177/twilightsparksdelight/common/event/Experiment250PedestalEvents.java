package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class Experiment250PedestalEvents
{
    private static final String TAG_PEDESTAL_POS = "TsdExperiment250PedestalPos";
    private static final String TAG_CREATIVE_DISPLAY = "TsdExperiment250CreativeDisplay";
    private static final DamageSource PEDESTAL_LIFEDRAIN = new DamageSource("twilight_spark_delight.lifedrain_pedestal")
        .setMagicDamage();

    private Experiment250PedestalEvents()
    {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPedestalInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getWorld().getBlockState(event.getPos()).getBlock() != TFBlocks.trophy_pedestal) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItem(event.getHand());
        EntityItem display = findDisplay(event.getWorld(), event.getPos());
        if (display == null && !isDisplayItem(held)) {
            return;
        }

        if (!event.getWorld().isRemote) {
            if (display != null) {
                retrieveDisplay(player, display);
            } else {
                placeDisplay(player, held, event.getPos());
            }
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPedestalBroken(BlockEvent.BreakEvent event)
    {
        if (!event.getWorld().isRemote
            && event.getState().getBlock() == TFBlocks.trophy_pedestal) {
            EntityItem display = findDisplay(event.getWorld(), event.getPos());
            if (display != null) {
                releaseDisplay(display);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }
        List<EntityItem> displays = maintainDisplays(event.world);
        if (event.world.getTotalWorldTime() % 60L == 0L && event.world instanceof WorldServer) {
            runPedestalNetwork((WorldServer) event.world, displays);
        }
    }

    private static void placeDisplay(EntityPlayer player, ItemStack held, BlockPos pedestalPos)
    {
        ItemStack displayedStack = held.copy();
        displayedStack.setCount(1);
        EntityItem display = new EntityItem(
            player.world,
            pedestalPos.getX() + 0.5D,
            pedestalPos.getY() + 1.35D,
            pedestalPos.getZ() + 0.5D,
            displayedStack
        );
        display.getEntityData().setLong(TAG_PEDESTAL_POS, pedestalPos.toLong());
        display.getEntityData().setBoolean(TAG_CREATIVE_DISPLAY, player.capabilities.isCreativeMode);
        configureDisplay(display, pedestalPos);
        player.world.spawnEntity(display);
        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    private static void retrieveDisplay(EntityPlayer player, EntityItem display)
    {
        ItemStack stack = display.getItem().copy();
        boolean creativeDisplay = display.getEntityData().getBoolean(TAG_CREATIVE_DISPLAY);
        display.setDead();
        if (!creativeDisplay && !player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    private static List<EntityItem> maintainDisplays(World world)
    {
        List<EntityItem> displays = new ArrayList<>();
        List<Entity> loadedEntities = new ArrayList<>(world.loadedEntityList);
        for (Entity entity : loadedEntities) {
            if (!(entity instanceof EntityItem) || entity.isDead || !isPedestalDisplay((EntityItem) entity)) {
                continue;
            }
            EntityItem display = (EntityItem) entity;
            BlockPos pedestalPos = getPedestalPos(display);
            if (pedestalPos == null || world.getBlockState(pedestalPos).getBlock() != TFBlocks.trophy_pedestal
                || !isDisplayItem(display.getItem())) {
                releaseDisplay(display);
                continue;
            }
            configureDisplay(display, pedestalPos);
            displays.add(display);
        }
        return displays;
    }

    private static void configureDisplay(EntityItem display, BlockPos pedestalPos)
    {
        display.setPosition(
            pedestalPos.getX() + 0.5D,
            pedestalPos.getY() + 1.35D,
            pedestalPos.getZ() + 0.5D
        );
        display.motionX = 0.0D;
        display.motionY = 0.0D;
        display.motionZ = 0.0D;
        display.setNoGravity(true);
        display.setInfinitePickupDelay();
        display.setEntityInvulnerable(true);
        display.lifespan = Integer.MAX_VALUE;
    }

    private static void releaseDisplay(EntityItem display)
    {
        if (!display.world.isRemote && !display.getEntityData().getBoolean(TAG_CREATIVE_DISPLAY)) {
            EntityItem dropped = new EntityItem(
                display.world,
                display.posX,
                display.posY,
                display.posZ,
                display.getItem().copy()
            );
            dropped.setDefaultPickupDelay();
            display.world.spawnEntity(dropped);
        }
        display.setDead();
    }

    private static EntityItem findDisplay(World world, BlockPos pedestalPos)
    {
        AxisAlignedBB bounds = new AxisAlignedBB(pedestalPos).grow(0.75D).expand(0.0D, 1.5D, 0.0D);
        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, bounds)) {
            if (entityItem.isDead) {
                continue;
            }
            BlockPos storedPos = getPedestalPos(entityItem);
            if (pedestalPos.equals(storedPos)) {
                return entityItem;
            }
        }
        return null;
    }

    private static boolean isPedestalDisplay(EntityItem display)
    {
        return display.getEntityData().hasKey(TAG_PEDESTAL_POS);
    }

    private static BlockPos getPedestalPos(EntityItem display)
    {
        return isPedestalDisplay(display)
            ? BlockPos.fromLong(display.getEntityData().getLong(TAG_PEDESTAL_POS))
            : null;
    }

    private static boolean isDisplayItem(ItemStack stack)
    {
        return !stack.isEmpty()
            && (stack.getItem() == TSDItems.EXPERIMENT_250 || stack.getItem() == TFItems.lifedrain_scepter);
    }

    private static void runPedestalNetwork(WorldServer world, List<EntityItem> displays)
    {
        List<EntityItem> experiments = new ArrayList<>();
        List<EntityItem> scepters = new ArrayList<>();
        for (EntityItem display : displays) {
            ItemStack stack = display.getItem();
            if (stack.getItem() == TSDItems.EXPERIMENT_250
                && Experiment250Item.getActivity(stack) + 1.0E-9D < Experiment250Item.getCapacity(stack)) {
                experiments.add(display);
            } else if (stack.getItem() == TFItems.lifedrain_scepter) {
                scepters.add(display);
            }
        }

        for (EntityItem scepter : scepters) {
            EntityItem experiment = selectExperiment(scepter, experiments);
            if (experiment != null) {
                drainCreature(world, scepter, experiment);
            }
        }
    }

    private static EntityItem selectExperiment(EntityItem scepter, List<EntityItem> experiments)
    {
        BlockPos scepterPos = getPedestalPos(scepter);
        if (scepterPos == null) {
            return null;
        }
        return experiments.stream()
            .filter(experiment -> isWithinPedestalNetwork(scepterPos, getPedestalPos(experiment)))
            .min(Comparator
                .comparingDouble((EntityItem experiment) -> horizontalDistanceSq(scepterPos, getPedestalPos(experiment)))
                .thenComparingInt(experiment -> directionPriority(scepterPos, getPedestalPos(experiment)))
                .thenComparingInt(experiment -> getPedestalPos(experiment).getX())
                .thenComparingInt(experiment -> getPedestalPos(experiment).getZ()))
            .orElse(null);
    }

    private static boolean isWithinPedestalNetwork(BlockPos origin, BlockPos target)
    {
        return target != null && origin.getY() == target.getY()
            && Math.abs(origin.getX() - target.getX()) <= 2
            && Math.abs(origin.getZ() - target.getZ()) <= 2
            && !origin.equals(target);
    }

    private static double horizontalDistanceSq(BlockPos origin, BlockPos target)
    {
        int dx = target.getX() - origin.getX();
        int dz = target.getZ() - origin.getZ();
        return dx * dx + dz * dz;
    }

    private static int directionPriority(BlockPos origin, BlockPos target)
    {
        int dx = target.getX() - origin.getX();
        int dz = target.getZ() - origin.getZ();
        if (Math.abs(dz) >= Math.abs(dx)) {
            return dz < 0 ? 0 : 2;
        }
        return dx > 0 ? 1 : 3;
    }

    private static void drainCreature(WorldServer world, EntityItem scepter, EntityItem experiment)
    {
        BlockPos scepterPos = getPedestalPos(scepter);
        if (scepterPos == null) {
            return;
        }
        AxisAlignedBB range = new AxisAlignedBB(scepterPos).grow(3.0D);
        List<EntityLivingBase> allTargets = new ArrayList<>();
        List<EntityLivingBase> slowedTargets = new ArrayList<>();
        for (EntityLivingBase target : world.getEntitiesWithinAABB(EntityLivingBase.class, range)) {
            if (!isValidTarget(target)) {
                continue;
            }
            allTargets.add(target);
            if (target.isPotionActive(MobEffects.SLOWNESS)) {
                slowedTargets.add(target);
            }
        }
        List<EntityLivingBase> candidates = slowedTargets.isEmpty() ? allTargets : slowedTargets;
        if (candidates.isEmpty()) {
            return;
        }

        EntityLivingBase target = candidates.get(world.rand.nextInt(candidates.size()));
        boolean slowed = target.isPotionActive(MobEffects.SLOWNESS);
        float damage = slowed ? 3.0F : 1.0F;
        boolean damaged = target.attackEntityFrom(PEDESTAL_LIFEDRAIN, damage);
        target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 0));
        if (damaged) {
            ItemStack experimentStack = experiment.getItem().copy();
            Experiment250Item.addActivity(experimentStack, slowed ? 1.5D : 0.5D);
            experiment.setItem(experimentStack);
        }

        Vec3d scepterPoint = new Vec3d(scepter.posX, scepter.posY + 0.25D, scepter.posZ);
        spawnTrail(world, scepterPoint, new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ));
        spawnTrail(world, scepterPoint, new Vec3d(experiment.posX, experiment.posY + 0.25D, experiment.posZ));
    }

    private static boolean isValidTarget(EntityLivingBase target)
    {
        if (!target.isEntityAlive() || target instanceof EntityPlayer || target instanceof EntityVillager
            || target instanceof EntityWitch || target instanceof EntityDragon || target instanceof EntityWither
            || !target.isNonBoss() || target.hasCustomName()) {
            return false;
        }
        ResourceLocation id = net.minecraft.entity.EntityList.getKey(target);
        if (id != null && "twilightforest:quest_ram".equals(id.toString())) {
            return false;
        }
        if (target instanceof EntityTameable && ((EntityTameable) target).isTamed()) {
            return false;
        }
        if (target instanceof AbstractHorse && ((AbstractHorse) target).isTame()) {
            return false;
        }
        return !(target instanceof IEntityOwnable) || ((IEntityOwnable) target).getOwnerId() == null;
    }

    private static void spawnTrail(WorldServer world, Vec3d from, Vec3d to)
    {
        for (int step = 0; step <= 20; step++) {
            double ratio = step / 20.0D;
            world.spawnParticle(
                EnumParticleTypes.SPELL_MOB,
                true,
                from.x + (to.x - from.x) * ratio,
                from.y + (to.y - from.y) * ratio,
                from.z + (to.z - from.z) * ratio,
                0,
                0.75D,
                0.05D,
                0.05D,
                1.0D
            );
        }
    }
}
