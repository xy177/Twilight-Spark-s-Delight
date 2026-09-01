package xy177.twilightsparksdelight.common.event;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.effect.ShrinkEffectHelper;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDShrinkEvents
{
    private static final String TAG_SCALE = "TsdShrinkScale";
    private static final String TAG_SHRINK_STEP = "TsdShrinkStepModified";
    private static final float MIN_SHRINK_WIDTH = 0.3F;
    private static final UUID REACH_UUID = UUID.fromString("9B08B1C7-8898-4DE4-82B2-3D80D5A2CB25");
    private static final AttributeModifier REACH_PLACEHOLDER =
        new AttributeModifier(REACH_UUID, "TSD scale reach", 0.0D, 0).setSaved(false);

    private TSDShrinkEvents()
    {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        updatePlayerSize(event.player);
        updateReach(event.player);
        updateStepHeight(event.player);
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event)
    {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        float scale = ShrinkEffectHelper.getScale((EntityPlayer) event.getEntityLiving());
        if (scale > 1.0F) {
            event.getEntityLiving().motionY *= Math.sqrt(scale);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        double multiplier = ShrinkEffectHelper.getReachMultiplier(player);
        if (multiplier >= 1.0D) {
            return;
        }
        double allowedReach = getBaseReach(player) * multiplier;
        Entity target = event.getTarget();
        double extraTargetWidth = Math.max(target.width, target.height) * 0.5D;
        double allowedDistance = allowedReach + extraTargetWidth;
        if (player.getDistanceSq(target) > allowedDistance * allowedDistance) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event)
    {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        double dodgeChance = ShrinkEffectHelper.getShrinkDodgeChance(player);
        if (dodgeChance > 0.0D && player.getRNG().nextDouble() < dodgeChance) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getEntityLiving() instanceof EntityPlayer) {
            float victimScale = ShrinkEffectHelper.getScale((EntityPlayer) event.getEntityLiving());
            if (victimScale > 1.0F) {
                event.setAmount(event.getAmount() * victimScale);
            }
        }

        if (event.getSource().getImmediateSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) event.getSource().getImmediateSource();
            float scale = ShrinkEffectHelper.getScale(attacker);
            if (scale > 1.0F) {
                double baseReach = getBaseReach(attacker);
                double scaledReach = baseReach * scale;
                if (scaledReach > 0.0D) {
                    event.setAmount((float) (event.getAmount() * (baseReach / scaledReach)));
                }
            }
        }
    }

    private static void updatePlayerSize(EntityPlayer player)
    {
        float scale = ShrinkEffectHelper.getScale(player);
        float previous = player.getEntityData().hasKey(TAG_SCALE)
            ? player.getEntityData().getFloat(TAG_SCALE)
            : 1.0F;
        float width = ShrinkEffectHelper.PLAYER_WIDTH * scale;
        if (scale < 1.0F) {
            width = Math.max(MIN_SHRINK_WIDTH, width);
        }
        if (Math.abs(scale - previous) < 0.001F
            && Math.abs(player.width - width) < 0.001F
            && Math.abs(player.height - ShrinkEffectHelper.PLAYER_HEIGHT * scale) < 0.001F) {
            return;
        }

        setSize(player, width, ShrinkEffectHelper.PLAYER_HEIGHT * scale);
        player.eyeHeight = ShrinkEffectHelper.PLAYER_EYE_HEIGHT * scale;
        if (Math.abs(scale - 1.0F) > 0.001F) {
            player.getEntityData().setFloat(TAG_SCALE, scale);
        } else {
            player.getEntityData().removeTag(TAG_SCALE);
            player.eyeHeight = player.getDefaultEyeHeight();
        }
    }

    private static void updateReach(EntityPlayer player)
    {
        IAttributeInstance reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        if (reach == null) {
            return;
        }
        AttributeModifier oldModifier = reach.getModifier(REACH_UUID);
        if (oldModifier != null) {
            reach.removeModifier(oldModifier);
        }

        double multiplier = ShrinkEffectHelper.getReachMultiplier(player);
        if (Math.abs(multiplier - 1.0D) <= 0.001D) {
            return;
        }

        double baseReach = getBaseReach(player);
        double addition = baseReach * (multiplier - 1.0D);
        if (Math.abs(addition) > 0.001D) {
            reach.applyModifier(new AttributeModifier(REACH_UUID, "TSD scale reach", addition, 0).setSaved(false));
        }
    }

    private static double getBaseReach(EntityPlayer player)
    {
        IAttributeInstance reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        if (reach == null) {
            return player instanceof EntityPlayerMP && ((EntityPlayerMP) player).interactionManager.isCreative()
                ? 5.0D
                : 4.5D;
        }
        return reach.getBaseValue();
    }

    private static void updateStepHeight(EntityPlayer player)
    {
        float scale = ShrinkEffectHelper.getScale(player);
        if (scale > 1.0F) {
            player.stepHeight = 0.6F * scale;
            player.getEntityData().setBoolean(TAG_SHRINK_STEP, true);
        } else if (player.getEntityData().getBoolean(TAG_SHRINK_STEP)) {
            player.stepHeight = 0.6F;
            player.getEntityData().removeTag(TAG_SHRINK_STEP);
        }
    }

    private static void setSize(EntityPlayer player, float width, float height)
    {
        player.width = width;
        player.height = height;
        double half = width / 2.0D;
        player.setEntityBoundingBox(new AxisAlignedBB(
            player.posX - half,
            player.posY,
            player.posZ - half,
            player.posX + half,
            player.posY + height,
            player.posZ + half
        ));
    }
}
