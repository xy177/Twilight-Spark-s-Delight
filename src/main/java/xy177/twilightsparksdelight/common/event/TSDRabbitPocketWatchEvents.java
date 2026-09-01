package xy177.twilightsparksdelight.common.event;

import com.wdcftgg.farmersdelightlegacy.common.item.ItemKnife;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import twilightforest.TFFeature;
import twilightforest.entity.passive.EntityTFBunny;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.integration.baubles.RabbitPocketWatchBaublesCompat;
import xy177.twilightsparksdelight.common.registry.TSDItems;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDRabbitPocketWatchEvents
{
    private static final String TAG_SPECIAL_RABBIT = "TsdPocketWatchRabbit";
    private static final ResourceLocation TRANSFORMATION_POWDER = new ResourceLocation("twilightforest", "transformation_powder");
    private static final int KILLER_RABBIT_TYPE = 99;

    private TSDRabbitPocketWatchEvents()
    {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote || !hasWatchInHotbar(event.player)) {
            return;
        }

        giveQuietEffect(event.player, MobEffects.SPEED);
        giveQuietEffect(event.player, MobEffects.JUMP_BOOST);
        if (hasWatchInHand(event.player)) {
            giveQuietEffect(event.player, MobEffects.HASTE);
        }
    }

    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer
            && event.getPotionEffect().getPotion() == MobEffects.MINING_FATIGUE
            && hasWatchInHotbar((EntityPlayer) event.getEntityLiving())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItem(event.getHand());
        if (!isTransformationPowder(held) || !isRabbitTarget(event.getTarget())) {
            return;
        }

        World world = player.world;
        if (world.isRemote) {
            return;
        }

        float chance = isInAnyHollowHill(world, event.getTarget().getPosition())
            ? 1.0F
            : TSDConfig.rabbitPocketWatchKillerRabbitChance;
        if (player.getRNG().nextFloat() >= chance) {
            return;
        }

        makeSpecialKillerRabbit(world, event.getTarget());
        if (!player.capabilities.isCreativeMode) {
            held.shrink(1);
        }
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event)
    {
        Entity immediate = event.getSource().getImmediateSource();
        Entity trueSource = event.getSource().getTrueSource();
        if (event.getEntityLiving() instanceof EntityPlayer
            && (isSpecialKillerRabbit(immediate) || isSpecialKillerRabbit(trueSource))) {
            event.setCanceled(true);
            return;
        }

        if (!event.getSource().isProjectile()
            || !(event.getEntityLiving() instanceof EntityPlayer)
            || !(trueSource instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP attacker = (EntityPlayerMP) trueSource;
        EntityPlayer defender = (EntityPlayer) event.getEntityLiving();
        if (hasWatchInHand(attacker) && defender.isHandActive() && defender.getActiveItemStack().getItem() == Items.SHIELD) {
            defender.disableShield(true);
            TSDAdvancements.GUNFIRE_BREAKS_RABBIT_WATCH.trigger(attacker);
        }
    }

    public static boolean isSpecialKillerRabbit(Entity entity)
    {
        return entity instanceof EntityRabbit && entity.getEntityData().getBoolean(TAG_SPECIAL_RABBIT);
    }

    public static boolean canDropPocketWatch(EntityLivingBase entity, EntityPlayer player)
    {
        return isSpecialKillerRabbit(entity) && ItemKnife.isKnife(player.getHeldItemMainhand());
    }

    private static void giveQuietEffect(EntityPlayer player, net.minecraft.potion.Potion potion)
    {
        player.addPotionEffect(new PotionEffect(potion, 40, 0, true, false));
    }

    private static boolean hasWatchInHotbar(EntityPlayer player)
    {
        for (int i = 0; i < 9; i++) {
            if (isWatch(player.inventory.getStackInSlot(i))) {
                return true;
            }
        }
        return isWatch(player.getHeldItemOffhand()) || RabbitPocketWatchBaublesCompat.isEquipped(player);
    }

    private static boolean hasWatchInHand(EntityPlayer player)
    {
        return isWatch(player.getHeldItemMainhand())
            || isWatch(player.getHeldItemOffhand())
            || RabbitPocketWatchBaublesCompat.isEquipped(player);
    }

    private static boolean isWatch(ItemStack stack)
    {
        return !stack.isEmpty() && stack.getItem() == TSDItems.RABBIT_POCKET_WATCH;
    }

    private static boolean isTransformationPowder(ItemStack stack)
    {
        if (stack.isEmpty()) {
            return false;
        }
        Item powder = ForgeRegistries.ITEMS.getValue(TRANSFORMATION_POWDER);
        return powder != null && stack.getItem() == powder;
    }

    private static boolean isRabbitTarget(Entity entity)
    {
        return entity instanceof EntityRabbit || entity instanceof EntityTFBunny;
    }

    private static void makeSpecialKillerRabbit(World world, Entity target)
    {
        EntityRabbit rabbit;
        if (target instanceof EntityRabbit) {
            rabbit = (EntityRabbit) target;
            rabbit.setRabbitType(KILLER_RABBIT_TYPE);
        } else {
            rabbit = new EntityRabbit(world);
            rabbit.copyLocationAndAnglesFrom(target);
            rabbit.setRabbitType(KILLER_RABBIT_TYPE);
            world.spawnEntity(rabbit);
            target.setDead();
        }
        rabbit.setCustomNameTag("");
        rabbit.setAlwaysRenderNameTag(false);
        rabbit.getEntityData().setBoolean(TAG_SPECIAL_RABBIT, true);
    }

    private static boolean isInAnyHollowHill(World world, BlockPos pos)
    {
        BlockPos center = TFFeature.getNearestCenterXYZ(pos.getX(), pos.getZ(), world);
        TFFeature feature = TFFeature.getFeatureAt(center.getX(), center.getZ(), world);
        if (feature != TFFeature.SMALL_HILL && feature != TFFeature.MEDIUM_HILL && feature != TFFeature.LARGE_HILL) {
            return false;
        }

        int radius = (feature.size * 2 + 1) * 8 - 6;
        int dx = center.getX() - pos.getX();
        int dy = (center.getY() - pos.getY()) * 2;
        int dz = center.getZ() - pos.getZ();
        return dx * dx + dy * dy + dz * dz < radius * radius;
    }
}
