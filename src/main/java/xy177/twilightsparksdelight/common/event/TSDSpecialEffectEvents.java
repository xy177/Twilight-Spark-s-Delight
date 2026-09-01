package xy177.twilightsparksdelight.common.event;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.registry.TSDPotions;

import java.util.UUID;
import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDSpecialEffectEvents
{
    private static final UUID CHARGE_SPEED_UUID = UUID.fromString("742D83EC-715D-4E39-896C-C949E2AB6D9B");
    private static final AttributeModifier CHARGE_SPEED_MODIFIER =
        new AttributeModifier(CHARGE_SPEED_UUID, "TSD charge speed", 0.0D, 2).setSaved(false);
    private static final String TAG_CHARGE_STEP = "TsdChargeStepModified";
    private static final String TAG_SYMBIOSIS_ACC = "TsdSymbiosisAccumulator";
    private static final String TAG_SYMBIOSIS_HEAL = "TsdSymbiosisHealTick";
    private static final String TAG_QUIETLY_WRIGGLING = "TsdQuietlyWrigglingUnlocked";
    private static final String TAG_ENLARGE_THREE = "TsdEnlargeLevelThreeUnlocked";
    private static final String TAG_SORROW_DAMAGE = "TsdSorrowRawDamage";
    private static boolean hydraChopAdjusted;
    private static final ResourceLocation FROSTED = new ResourceLocation("twilightforest", "frosted");
    private static final ResourceLocation FIRE_RESISTANCE = new ResourceLocation("minecraft", "fire_resistance");

    private TSDSpecialEffectEvents()
    {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        adjustHydraChopFoodValues();
        EntityPlayer player = event.player;
        updateChargeEffect(player);
        if (!player.world.isRemote) {
            updateSymbiosis(player);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase living = event.getEntityLiving();
        if (living.world.isRemote || living instanceof EntityPlayer) {
            return;
        }
        updateSymbiosis(living);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        if (!event.getEntity().world.isRemote && event.getAmount() > 0.0F && event.getEntityLiving() instanceof EntityPlayerMP) {
            maybeProliferate((EntityPlayerMP) event.getEntityLiving(), ItemStack.EMPTY, false);
        }

        if (!event.getEntity().world.isRemote) {
            applySorrowDamageReduction(event.getEntityLiving(), event);
            applySymbiosisDamageReduction(event.getEntityLiving(), event);
        }

        if (event.getSource().getImmediateSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getImmediateSource();
            if (!player.world.isRemote) {
                applyChargeAttackBonus(player, event);
                applyGriefAttackBonus(player, event);
                applyAbyssCall(player, event.getEntityLiving());
            }
        }
    }

    @SubscribeEvent
    public static void onItemFinish(LivingEntityUseItemEvent.Finish event)
    {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP) || event.getEntityLiving().world.isRemote) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        applyDrinkMeOrEatMe(player, event.getItem());
        applyNagaMixedRiceEffects(player, event.getItem());
        applyTwilightDelightCompatibility(player, event.getItem());
        maybeProliferate(player, event.getItem(), true);
    }

    private static void applyNagaMixedRiceEffects(EntityPlayerMP player, ItemStack stack)
    {
        if (stack.isEmpty() || (stack.getItem() != TSDItems.BOWL_OF_NAGA_MIXED_RICE
            && stack.getItem() != TSDItems.NAGA_MIXED_RICE_CUP)
            || !stack.hasTagCompound()
            || !stack.getTagCompound().hasKey(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG, 8)) {
            return;
        }
        String ingredient = stack.getTagCompound().getString(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG);
        int duration = stack.getItem() == TSDItems.NAGA_MIXED_RICE_CUP ? 3600 : 6000;
        if (TSDItems.NAGA_MIXED_RICE_HYDRA.equals(ingredient)) {
            addEffect(player, FIRE_RESISTANCE, duration, 0);
        } else if (TSDItems.NAGA_MIXED_RICE_EXPERIMENT.equals(ingredient)) {
            addEffect(player, new ResourceLocation(TwilightSparksDelight.MODID, "sorrow"), duration, 0);
        }
    }

    private static void applyTwilightDelightCompatibility(EntityPlayerMP player, ItemStack stack)
    {
        if (!Loader.isModLoaded("twilightdelight") || stack.isEmpty()) {
            return;
        }

        if (stack.getItem() == TSDItems.GELID_CRYSTAL
            || stack.getItem() == TSDItems.DOUBLE_CROWN_ICE_CREAM
            || stack.getItem() == TSDItems.TWIN_RADIANCE_ICE_POP) {
            replaceEffect(player, FROSTED, new ResourceLocation("twilightdelight", "frozen_range"));
        }
        if (stack.getItem() == TSDItems.TWIN_RADIANCE_ICE_POP) {
            addEffect(player, new ResourceLocation("twilightdelight", "aurora_glowing"), 1800, 0);
        }

        if (stack.getItem() == TSDItems.FIRE_BEETLE_FLAME_SAC
            || stack.getItem() == TSDItems.LABYRINTH_FLAVOR_SKEWER
            || stack.getItem() == TSDItems.STIR_FRIED_BRACKEN
            || isHydraNagaMixedRice(stack)) {
            replaceEffect(player, FIRE_RESISTANCE, new ResourceLocation("twilightdelight", "fire_range"));
        }
    }

    private static boolean isHydraNagaMixedRice(ItemStack stack)
    {
        return (stack.getItem() == TSDItems.BOWL_OF_NAGA_MIXED_RICE
            || stack.getItem() == TSDItems.NAGA_MIXED_RICE_CUP)
            && stack.hasTagCompound()
            && TSDItems.NAGA_MIXED_RICE_HYDRA.equals(
                stack.getTagCompound().getString(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG)
            );
    }

    private static void replaceEffect(EntityPlayerMP player, ResourceLocation oldId, ResourceLocation newId)
    {
        Potion oldPotion = ForgeRegistries.POTIONS.getValue(oldId);
        Potion newPotion = ForgeRegistries.POTIONS.getValue(newId);
        if (oldPotion == null || newPotion == null) {
            return;
        }
        PotionEffect oldEffect = player.getActivePotionEffect(oldPotion);
        if (oldEffect == null) {
            return;
        }
        player.removePotionEffect(oldPotion);
        player.addPotionEffect(new PotionEffect(newPotion, oldEffect.getDuration(), oldEffect.getAmplifier(), false, false));
    }

    private static void addEffect(EntityPlayer player, ResourceLocation id, int duration, int amplifier)
    {
        Potion potion = ForgeRegistries.POTIONS.getValue(id);
        if (potion != null) {
            player.addPotionEffect(new PotionEffect(potion, duration, amplifier, false, false));
        }
    }

    private static void applyDrinkMeOrEatMe(EntityPlayerMP player, ItemStack stack)
    {
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getItem() == TSDItems.DRINK_ME) {
            changeScaleEffect(player, TSDPotions.SHRINK, TSDPotions.ENLARGE, 5);
        } else if (stack.getItem() == TSDItems.EAT_ME) {
            int level = changeScaleEffect(player, TSDPotions.ENLARGE, TSDPotions.SHRINK, 3);
            if (level >= 3 && !player.getEntityData().getBoolean(TAG_ENLARGE_THREE)) {
                player.getEntityData().setBoolean(TAG_ENLARGE_THREE, true);
                TSDAdvancements.DONT_EAT_ME.trigger(player);
            }
        }
    }

    private static int changeScaleEffect(EntityPlayerMP player, Potion increase, Potion decrease, int maxLevel)
    {
        PotionEffect decreaseEffect = player.getActivePotionEffect(decrease);
        if (decreaseEffect != null) {
            int decreaseAmplifier = decreaseEffect.getAmplifier();
            player.removePotionEffect(decrease);
            if (decreaseAmplifier > 0) {
                player.addPotionEffect(new PotionEffect(decrease, 3600, decreaseAmplifier - 1, false, false));
            }
            return 0;
        }

        PotionEffect increaseEffect = player.getActivePotionEffect(increase);
        int nextLevel = increaseEffect == null ? 1 : Math.min(maxLevel, increaseEffect.getAmplifier() + 2);
        player.addPotionEffect(new PotionEffect(increase, 3600, nextLevel - 1, false, false));
        return nextLevel;
    }

    private static void adjustHydraChopFoodValues()
    {
        if (hydraChopAdjusted) {
            return;
        }
        hydraChopAdjusted = true;
        Item hydraChop = item("twilightforest:hydra_chop");
        if (!(hydraChop instanceof ItemFood)) {
            return;
        }
        setItemFoodField((ItemFood) hydraChop, "healAmount", "field_77853_b", 16);
        setItemFoodField((ItemFood) hydraChop, "saturationModifier", "field_77854_c", 16.0F / (16.0F * 2.0F));
    }

    private static void setItemFoodField(ItemFood food, String deobfName, String srgName, Object value)
    {
        try {
            Field field;
            try {
                field = ItemFood.class.getDeclaredField(deobfName);
            } catch (NoSuchFieldException e) {
                field = ItemFood.class.getDeclaredField(srgName);
            }
            field.setAccessible(true);
            field.set(food, value);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static void updateChargeEffect(EntityPlayer player)
    {
        boolean active = player.isPotionActive(TSDPotions.CHARGE);
        if (player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(CHARGE_SPEED_UUID) != null) {
            player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(CHARGE_SPEED_MODIFIER);
        }

        if (active) {
            player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(
                new AttributeModifier(CHARGE_SPEED_UUID, "TSD charge speed", TSDConfig.chargeSpeedBonus, 2).setSaved(false)
            );
            player.stepHeight = (float) (0.6D + TSDConfig.chargeStepHeightBonus);
            player.getEntityData().setBoolean(TAG_CHARGE_STEP, true);
        } else if (player.getEntityData().getBoolean(TAG_CHARGE_STEP)) {
            player.stepHeight = 0.6F;
            player.getEntityData().removeTag(TAG_CHARGE_STEP);
        }
    }

    private static void applyChargeAttackBonus(EntityPlayer player, LivingHurtEvent event)
    {
        if (!player.isPotionActive(TSDPotions.CHARGE)) {
            return;
        }
        if (!player.isSprinting() && !isCriticalHit(player)) {
            return;
        }
        double base = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
        double current = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
        if (base <= 0.0D || current <= base) {
            return;
        }
        double accelerationBonus = Math.min(TSDConfig.chargeMaxAttackBonus, (current / base) - 1.0D);
        if (accelerationBonus > 0.0D) {
            event.setAmount((float) (event.getAmount() * (1.0D + accelerationBonus)));
        }
    }

    private static void applyGriefAttackBonus(EntityPlayer player, LivingHurtEvent event)
    {
        PotionEffect effect = player.getActivePotionEffect(TSDPotions.GRIEF);
        if (effect == null || event.getAmount() <= 0.0F) {
            return;
        }
        double bonus = (effect.getAmplifier() + 1) * 0.10D;
        event.setAmount((float) (event.getAmount() * (1.0D + bonus)));
    }

    private static void applyAbyssCall(EntityPlayer player, EntityLivingBase target)
    {
        if (!player.isPotionActive(TSDPotions.ABYSS_CALL) || !(target instanceof IMob)) {
            return;
        }
        target.addPotionEffect(new PotionEffect(TSDPotions.SYMBIOSIS, 300, 0, false, false));
    }

    private static boolean isCriticalHit(EntityPlayer player)
    {
        return player.fallDistance > 0.0F
            && !player.onGround
            && !player.isOnLadder()
            && !player.isInWater()
            && !player.isPotionActive(MobEffects.BLINDNESS)
            && player.getRidingEntity() == null;
    }

    private static void updateSymbiosis(EntityLivingBase living)
    {
        if (!living.isPotionActive(TSDPotions.SYMBIOSIS)) {
            living.getEntityData().removeTag(TAG_SYMBIOSIS_ACC);
            living.getEntityData().removeTag(TAG_SYMBIOSIS_HEAL);
            return;
        }

        int amplifier = living.getActivePotionEffect(TSDPotions.SYMBIOSIS).getAmplifier();
        int level = amplifier + 1;
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;
            double drain = TSDConfig.symbiosisHungerDrainPerTick / Math.max(1, level);
            Potion nourishment = ModEffects.NOURISHMENT;
            if (nourishment != null && player.isPotionActive(nourishment)) {
                drain *= 0.5D;
            }
            double accumulator = player.getEntityData().getDouble(TAG_SYMBIOSIS_ACC) + drain;
            while (accumulator >= 1.0D) {
                accumulator -= 1.0D;
                int currentFood = player.getFoodStats().getFoodLevel();
                if (currentFood > 0) {
                    player.getFoodStats().setFoodLevel(currentFood - 1);
                }
            }
            player.getEntityData().setDouble(TAG_SYMBIOSIS_ACC, accumulator);
            return;
        }

        int tickCounter = living.getEntityData().getInteger(TAG_SYMBIOSIS_HEAL) + 1;
        if (tickCounter >= TSDConfig.symbiosisHealIntervalTicks) {
            tickCounter = 0;
            if (living.getHealth() < living.getMaxHealth()) {
                living.heal(TSDConfig.symbiosisEntityHealAmount);
            }
        }
        living.getEntityData().setInteger(TAG_SYMBIOSIS_HEAL, tickCounter);
    }

    private static void applySymbiosisDamageReduction(EntityLivingBase living, LivingHurtEvent event)
    {
        if (!living.isPotionActive(TSDPotions.SYMBIOSIS)) {
            return;
        }
        int amplifier = living.getActivePotionEffect(TSDPotions.SYMBIOSIS).getAmplifier();
        double reduction = TSDConfig.symbiosisPlayerDamageReductionBase
            + (amplifier * TSDConfig.symbiosisPlayerDamageReductionPerAmplifier);
        reduction = Math.max(0.0D, Math.min(0.95D, reduction));
        if (reduction > 0.0D) {
            event.setAmount((float) (event.getAmount() * (1.0D - reduction)));
        }
    }

    private static void applySorrowDamageReduction(EntityLivingBase living, LivingHurtEvent event)
    {
        PotionEffect sorrow = living.getActivePotionEffect(TSDPotions.SORROW);
        if (sorrow == null || event.getAmount() <= 0.0F) {
            if (sorrow == null) {
                living.getEntityData().removeTag(TAG_SORROW_DAMAGE);
            }
            return;
        }

        float rawAmount = event.getAmount();
        double reduction = (sorrow.getAmplifier() + 1) * 0.10D;
        event.setAmount((float) (rawAmount * Math.max(0.0D, 1.0D - Math.min(0.95D, reduction))));

        double accumulated = living.getEntityData().getDouble(TAG_SORROW_DAMAGE) + rawAmount;
        if (accumulated < 18.0D) {
            living.getEntityData().setDouble(TAG_SORROW_DAMAGE, accumulated);
            return;
        }

        living.getEntityData().removeTag(TAG_SORROW_DAMAGE);
        living.removePotionEffect(TSDPotions.SORROW);
        living.addPotionEffect(new PotionEffect(
            TSDPotions.GRIEF,
            sorrow.getDuration(),
            sorrow.getAmplifier() + 1,
            false,
            false
        ));
    }

    private static void maybeProliferate(EntityPlayerMP player, ItemStack consumedStack, boolean fromEating)
    {
        if (player.world.rand.nextFloat() > TSDConfig.experimentProliferationChance) {
            return;
        }
        if (!hasExperimentCatalyst(player)) {
            return;
        }

        int breadSlot = -1;
        boolean useConsumedBread = fromEating && isBreadLike(consumedStack);
        if (!useConsumedBread) {
            breadSlot = findBreadSlot(player);
            if (breadSlot < 0) {
                return;
            }
        }

        MetalChoice metalChoice = findMetalChoice(player);
        if (!useConsumedBread) {
            consumeBread(player, breadSlot);
        }
        if (metalChoice != null) {
            consumeMetal(player, metalChoice);
            give(player, new ItemStack(TSDItems.EXPERIMENT_PROTOTYPE));
        } else {
            give(player, new ItemStack(TSDItems.EXPERIMENT_000));
        }

        player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.message.experiment_proliferation"), true);
        if (!player.getEntityData().getBoolean(TAG_QUIETLY_WRIGGLING)) {
            player.getEntityData().setBoolean(TAG_QUIETLY_WRIGGLING, true);
            TSDAdvancements.QUIETLY_WRIGGLING.trigger(player);
        }
    }

    private static boolean hasExperimentCatalyst(EntityPlayer player)
    {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() == TSDItems.EXPERIMENT_PROTOTYPE) {
                return true;
            }
            if (stack.getItem().getRegistryName() != null
                && "twilightforest".equals(stack.getItem().getRegistryName().getResourceDomain())
                && "trophy".equals(stack.getItem().getRegistryName().getResourcePath())
                && stack.getMetadata() == 4) {
                return true;
            }
        }
        return false;
    }

    private static MetalChoice findMetalChoice(EntityPlayer player)
    {
        int ingot = findItem(player, item("twilightforest:knightmetal_ingot"), 1, -1);
        if (ingot >= 0) {
            return new MetalChoice(ingot, 1);
        }
        int cluster = findItem(player, item("twilightforest:armor_shard_cluster"), 1, -1);
        if (cluster >= 0) {
            return new MetalChoice(cluster, 1);
        }
        int shards = findItem(player, item("twilightforest:armor_shard"), 9, -1);
        if (shards >= 0) {
            return new MetalChoice(shards, 9);
        }
        return null;
    }

    private static int findBreadSlot(EntityPlayer player)
    {
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            if (isBreadLike(player.inventory.mainInventory.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isBreadLike(ItemStack stack)
    {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        if (item == TSDItems.LIVEROOT_BREAD
            || item == TSDItems.LIVEROOT_DOUGH
            || item == TSDItems.LIVEROOT_CONE
            || item == net.minecraft.init.Items.BREAD) {
            return true;
        }
        for (int oreId : OreDictionary.getOreIDs(stack)) {
            String oreName = OreDictionary.getOreName(oreId);
            if (oreName == null) {
                continue;
            }
            String lower = oreName.toLowerCase();
            if (lower.contains("bread") || "fooddough".equals(lower)) {
                return true;
            }
        }
        return false;
    }

    private static void consumeBread(EntityPlayer player, int slot)
    {
        player.inventory.decrStackSize(slot, 1);
    }

    private static void consumeMetal(EntityPlayer player, MetalChoice choice)
    {
        player.inventory.decrStackSize(choice.slot, choice.count);
    }

    private static int findItem(EntityPlayer player, Item item, int requiredCount, int metadata)
    {
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (!stack.isEmpty() && stack.getItem() == item && (metadata < 0 || stack.getMetadata() == metadata) && stack.getCount() >= requiredCount) {
                return i;
            }
        }
        return -1;
    }

    private static Item item(String id)
    {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
    }

    private static void give(EntityPlayer player, ItemStack stack)
    {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    private static final class MetalChoice
    {
        private final int slot;
        private final int count;

        private MetalChoice(int slot, int count)
        {
            this.slot = slot;
            this.count = count;
        }
    }
}
