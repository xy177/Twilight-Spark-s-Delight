package xy177.twilightsparksdelight.common.event;

import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.recipe.CookingPotRecipe;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.CookingPotRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import twilightforest.item.TFItems;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.experiment.Experiment250Logic;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.registry.TSDOreDictionary;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDCookingPotEvents
{
    private static final String TAG_WATCH_SLOT = "TsdEatMeWatchSlot";
    private static final String TAG_WATCH_READY = "TsdEatMeWatchReady";
    private static final String TAG_EXPERIMENT_250_UPGRADE = "TsdExperiment250Upgrade";
    private static final String TAG_EXPERIMENT_250_UPGRADE_KEY = "TsdExperiment250UpgradeKey";
    private static final String TAG_EXPERIMENT_250_REPLICATION = "TsdExperiment250Replication";
    private static final String TAG_EXPERIMENT_250_REPLICATION_KEY = "TsdExperiment250ReplicationKey";
    private static final String TAG_NAGA_MIXED_RICE_INGREDIENT = "TsdPendingNagaMixedRiceIngredient";
    private static final int EXPERIMENT_250_PROCESS_TIME = 100;
    private static final int INPUT_SLOTS = 6;
    private static final int MEAL_SLOT = 6;

    private TSDCookingPotEvents()
    {
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }
        for (TileEntity tile : event.world.loadedTileEntityList) {
            if (tile instanceof TileEntityCookingPot) {
                TileEntityCookingPot pot = (TileEntityCookingPot) tile;
                updateNagaMixedRiceIngredient(pot);
                updateExperiment250Upgrade(pot);
                updateExperiment250Replication(pot);
                updatePocketWatchReturn(pot);
            }
        }
    }

    private static void updateNagaMixedRiceIngredient(TileEntityCookingPot pot)
    {
        NBTTagCompound data = pot.getTileData();
        String currentIngredient = findNagaMixedRiceIngredient(pot);
        if (currentIngredient != null) {
            data.setString(TAG_NAGA_MIXED_RICE_INGREDIENT, currentIngredient);
        }

        String pendingIngredient = data.hasKey(TAG_NAGA_MIXED_RICE_INGREDIENT, 8)
            ? data.getString(TAG_NAGA_MIXED_RICE_INGREDIENT)
            : null;
        boolean updated = false;
        if (pendingIngredient != null) {
            updated |= applyNagaMixedRiceIngredient(pot, MEAL_SLOT, pendingIngredient);
            updated |= applyNagaMixedRiceIngredient(pot, 8, pendingIngredient);
        }
        if (updated) {
            data.removeTag(TAG_NAGA_MIXED_RICE_INGREDIENT);
            pot.markDirty();
            pot.getWorld().notifyBlockUpdate(
                pot.getPos(),
                pot.getWorld().getBlockState(pot.getPos()),
                pot.getWorld().getBlockState(pot.getPos()),
                3
            );
        } else if (currentIngredient == null && !hasNagaMixedRiceResult(pot)) {
            data.removeTag(TAG_NAGA_MIXED_RICE_INGREDIENT);
        }
    }

    private static String findNagaMixedRiceIngredient(TileEntityCookingPot pot)
    {
        List<ItemStack> inputs = new ArrayList<>(INPUT_SLOTS);
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            inputs.add(pot.getStackInSlot(slot));
        }
        CookingPotRecipe recipe = CookingPotRecipeManager.findRecipe(inputs);
        if (recipe == null
            || !(TwilightSparksDelight.MODID + ":naga_mixed_rice").equals(recipe.getRecipeId())) {
            return null;
        }
        for (ItemStack input : inputs) {
            if (matchesOre(input, TSDOreDictionary.NAGA_MIXED_RICE_HYDRA)) {
                return TSDItems.NAGA_MIXED_RICE_HYDRA;
            }
            if (matchesOre(input, TSDOreDictionary.NAGA_MIXED_RICE_EXPERIMENT)) {
                return TSDItems.NAGA_MIXED_RICE_EXPERIMENT;
            }
        }
        return null;
    }

    private static boolean matchesOre(ItemStack stack, String oreName)
    {
        if (stack.isEmpty()) {
            return false;
        }
        for (int oreId : OreDictionary.getOreIDs(stack)) {
            if (oreName.equals(OreDictionary.getOreName(oreId))) {
                return true;
            }
        }
        return false;
    }

    private static boolean applyNagaMixedRiceIngredient(TileEntityCookingPot pot, int slot, String ingredient)
    {
        ItemStack stack = pot.getStackInSlot(slot);
        if (stack.isEmpty() || stack.getItem() != net.minecraft.item.Item.getItemFromBlock(TSDBlocks.NAGA_MIXED_RICE)) {
            return false;
        }
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound().copy() : new NBTTagCompound();
        if (tag.hasKey(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG, 8)) {
            return false;
        }
        tag.setString(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG, ingredient);
        ItemStack taggedStack = stack.copy();
        taggedStack.setTagCompound(tag);
        pot.setInventorySlotContents(slot, taggedStack);
        return true;
    }

    private static boolean hasNagaMixedRiceResult(TileEntityCookingPot pot)
    {
        for (int slot : new int[] {MEAL_SLOT, 8}) {
            ItemStack stack = pot.getStackInSlot(slot);
            if (!stack.isEmpty()
                && stack.getItem() == net.minecraft.item.Item.getItemFromBlock(TSDBlocks.NAGA_MIXED_RICE)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote || !(event.player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        for (ItemStack stack : player.inventory.mainInventory) {
            grantMillionPoundMealAdvancement(player, stack);
            grantNagaMixedRiceAdvancement(player, stack);
        }
        grantMillionPoundMealAdvancement(player, player.inventory.getItemStack());
        grantNagaMixedRiceAdvancement(player, player.inventory.getItemStack());
    }

    private static boolean grantMillionPoundMealAdvancement(EntityPlayerMP player, ItemStack stack)
    {
        if (stack.isEmpty() || stack.getItem() != TSDItems.MILLION_POUND_MEAL || !stack.hasTagCompound()
            || !stack.getTagCompound().getBoolean(TSDItems.MILLION_POUND_MEAL_CRAFTED_TAG)) {
            return false;
        }
        stack.getTagCompound().removeTag(TSDItems.MILLION_POUND_MEAL_CRAFTED_TAG);
        TSDAdvancements.MILLION_POUND_MEAL.trigger(player);
        return true;
    }

    private static boolean grantNagaMixedRiceAdvancement(EntityPlayerMP player, ItemStack stack)
    {
        if (stack.isEmpty() || stack.getItem() != net.minecraft.item.Item.getItemFromBlock(TSDBlocks.NAGA_MIXED_RICE)
            || !stack.hasTagCompound()
            || !stack.getTagCompound().getBoolean(TSDItems.NAGA_MIXED_RICE_CRAFTED_TAG)) {
            return false;
        }
        stack.getTagCompound().removeTag(TSDItems.NAGA_MIXED_RICE_CRAFTED_TAG);
        TSDAdvancements.TIME_TO_EVEN_THE_SCALES.trigger(player);
        return true;
    }

    private static void updateExperiment250Upgrade(TileEntityCookingPot pot)
    {
        List<ItemStack> inputs = new ArrayList<>();
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = pot.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                inputs.add(stack);
            }
        }

        Experiment250Logic.UpgradeResult result = Experiment250Logic.calculateUpgrade(inputs);
        NBTTagCompound data = pot.getTileData();
        if (!result.isValid() || !pot.isHeated() || !canStoreUpgradeResult(pot, result)) {
            resetExperiment250Upgrade(pot, data);
            return;
        }

        String recipeKey = buildUpgradeKey(inputs);
        if (!recipeKey.equals(data.getString(TAG_EXPERIMENT_250_UPGRADE_KEY))) {
            data.setString(TAG_EXPERIMENT_250_UPGRADE_KEY, recipeKey);
            data.setInteger(TAG_EXPERIMENT_250_UPGRADE, 0);
        }

        int progress = data.getInteger(TAG_EXPERIMENT_250_UPGRADE) + 1;
        data.setInteger(TAG_EXPERIMENT_250_UPGRADE, progress);
        pot.setField(0, Math.min(progress, EXPERIMENT_250_PROCESS_TIME));
        pot.setField(1, EXPERIMENT_250_PROCESS_TIME);
        if (progress < EXPERIMENT_250_PROCESS_TIME) {
            return;
        }

        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            pot.setInventorySlotContents(slot, ItemStack.EMPTY);
        }
        List<ItemStack> outputs = result.getOutputs();
        ItemStack storedOutputs = outputs.get(0).copy();
        storedOutputs.setCount(outputs.size());
        pot.applyStoredMealFromStack(storedOutputs, new ItemStack(TFItems.transformation_powder), false);
        resetExperiment250Upgrade(pot, data);
        pot.markDirty();
        pot.getWorld().notifyBlockUpdate(pot.getPos(), pot.getWorld().getBlockState(pot.getPos()), pot.getWorld().getBlockState(pot.getPos()), 3);
    }

    private static boolean canStoreUpgradeResult(TileEntityCookingPot pot, Experiment250Logic.UpgradeResult result)
    {
        return pot.getStackInSlot(MEAL_SLOT).isEmpty();
    }

    private static String buildUpgradeKey(List<ItemStack> inputs)
    {
        StringBuilder key = new StringBuilder();
        for (ItemStack stack : inputs) {
            key.append(stack.serializeNBT().toString()).append('|');
        }
        return key.toString();
    }

    private static void resetExperiment250Upgrade(TileEntityCookingPot pot, NBTTagCompound data)
    {
        if (data.hasKey(TAG_EXPERIMENT_250_UPGRADE) || data.hasKey(TAG_EXPERIMENT_250_UPGRADE_KEY)) {
            data.removeTag(TAG_EXPERIMENT_250_UPGRADE);
            data.removeTag(TAG_EXPERIMENT_250_UPGRADE_KEY);
            pot.setField(0, 0);
        }
    }

    private static void updateExperiment250Replication(TileEntityCookingPot pot)
    {
        NBTTagCompound data = pot.getTileData();
        if (!"cooking_pot".equals(TSDConfig.experiment250WorkstationMode)) {
            resetExperiment250Replication(pot, data);
            return;
        }

        ReplicationMatch match = findReplicationMatch(pot);
        if (!match.isValid() || !pot.isHeated() || !pot.getStackInSlot(MEAL_SLOT).isEmpty()) {
            resetExperiment250Replication(pot, data);
            return;
        }

        String recipeKey = match.buildKey(pot);
        if (!recipeKey.equals(data.getString(TAG_EXPERIMENT_250_REPLICATION_KEY))) {
            data.setString(TAG_EXPERIMENT_250_REPLICATION_KEY, recipeKey);
            data.setInteger(TAG_EXPERIMENT_250_REPLICATION, 0);
        }

        int progress = data.getInteger(TAG_EXPERIMENT_250_REPLICATION) + 1;
        data.setInteger(TAG_EXPERIMENT_250_REPLICATION, progress);
        pot.setField(0, Math.min(progress, EXPERIMENT_250_PROCESS_TIME));
        pot.setField(1, EXPERIMENT_250_PROCESS_TIME);
        if (progress < EXPERIMENT_250_PROCESS_TIME) {
            return;
        }

        ItemStack experiment250 = pot.getStackInSlot(match.experimentSlot);
        ItemStack source = pot.getStackInSlot(match.sourceSlot);
        net.minecraft.item.Item outputItem = ForgeRegistries.ITEMS.getValue(match.outputId);
        if (outputItem == null || source.isEmpty()
            || !Experiment250Logic.consumeReplicationActivity(experiment250, match.result)) {
            resetExperiment250Replication(pot, data);
            return;
        }

        source.shrink(1);
        pot.setInventorySlotContents(match.sourceSlot, source.isEmpty() ? ItemStack.EMPTY : source);
        pot.setInventorySlotContents(match.experimentSlot, experiment250);
        pot.applyStoredMealFromStack(
            new ItemStack(outputItem, match.result.getOutputCount()),
            ItemStack.EMPTY,
            false
        );
        resetExperiment250Replication(pot, data);
        pot.markDirty();
        pot.getWorld().notifyBlockUpdate(
            pot.getPos(),
            pot.getWorld().getBlockState(pot.getPos()),
            pot.getWorld().getBlockState(pot.getPos()),
            3
        );
    }

    private static ReplicationMatch findReplicationMatch(TileEntityCookingPot pot)
    {
        int experimentSlot = -1;
        int sourceSlot = -1;
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = pot.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() == TSDItems.EXPERIMENT_250 && experimentSlot < 0) {
                experimentSlot = slot;
            } else if (sourceSlot < 0) {
                sourceSlot = slot;
            } else {
                return ReplicationMatch.NONE;
            }
        }
        if (experimentSlot < 0 || sourceSlot < 0) {
            return ReplicationMatch.NONE;
        }
        ItemStack experiment250 = pot.getStackInSlot(experimentSlot);
        ResourceLocation outputId = Experiment250Logic.resolveReplicationTarget(
            experiment250,
            pot.getStackInSlot(sourceSlot)
        );
        Experiment250Logic.ReplicationResult result = Experiment250Logic.calculateReplication(experiment250, outputId);
        return result.isValid()
            ? new ReplicationMatch(experimentSlot, sourceSlot, outputId, result)
            : ReplicationMatch.NONE;
    }

    private static void resetExperiment250Replication(TileEntityCookingPot pot, NBTTagCompound data)
    {
        if (data.hasKey(TAG_EXPERIMENT_250_REPLICATION) || data.hasKey(TAG_EXPERIMENT_250_REPLICATION_KEY)) {
            data.removeTag(TAG_EXPERIMENT_250_REPLICATION);
            data.removeTag(TAG_EXPERIMENT_250_REPLICATION_KEY);
            pot.setField(0, 0);
        }
    }

    private static void updatePocketWatchReturn(TileEntityCookingPot pot)
    {
        NBTTagCompound data = pot.getTileData();
        int watchSlot = findWatchSlot(pot);
        if (watchSlot >= 0 && hasWatchKeepingIngredients(pot)) {
            data.setInteger(TAG_WATCH_SLOT, watchSlot);
            data.setBoolean(TAG_WATCH_READY, true);
            return;
        }

        if (!data.getBoolean(TAG_WATCH_READY)) {
            return;
        }

        int storedSlot = data.getInteger(TAG_WATCH_SLOT);
        if (storedSlot >= 0 && storedSlot < INPUT_SLOTS
            && pot.getStackInSlot(storedSlot).isEmpty()
            && isWatchKeepingMeal(pot.getStackInSlot(MEAL_SLOT))) {
            pot.setInventorySlotContents(storedSlot, new ItemStack(TSDItems.RABBIT_POCKET_WATCH));
            pot.markDirty();
        }
        data.removeTag(TAG_WATCH_SLOT);
        data.removeTag(TAG_WATCH_READY);
    }

    private static int findWatchSlot(TileEntityCookingPot pot)
    {
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = pot.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == TSDItems.RABBIT_POCKET_WATCH) {
                return i;
            }
        }
        return -1;
    }

    private static boolean hasWatchKeepingIngredients(TileEntityCookingPot pot)
    {
        return hasEatMeBulkIngredients(pot) || hasPickledBrackenJarIngredients(pot);
    }

    private static boolean hasEatMeBulkIngredients(TileEntityCookingPot pot)
    {
        boolean hasExperiment115 = false;
        boolean hasRedstoneBlock = false;
        boolean hasRedcapSpice = false;
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = pot.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem().getRegistryName() == null) {
                continue;
            }
            String id = stack.getItem().getRegistryName().toString();
            if ("twilightforest:experiment_115".equals(id)) {
                hasExperiment115 = true;
            } else if ("minecraft:redstone_block".equals(id)) {
                hasRedstoneBlock = true;
            } else if (stack.getItem() == TSDItems.REDCAP_SPICE) {
                hasRedcapSpice = true;
            }
        }
        return hasExperiment115 && hasRedstoneBlock && hasRedcapSpice;
    }

    private static boolean hasPickledBrackenJarIngredients(TileEntityCookingPot pot)
    {
        int bracken = 0;
        boolean hasWater = false;
        boolean hasJar = !pot.getStackInSlot(7).isEmpty();
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = pot.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem().getRegistryName() == null) {
                continue;
            }
            if (stack.getItem() == TSDItems.BRACKEN) {
                bracken += stack.getCount();
            } else if ("minecraft:water_bucket".equals(stack.getItem().getRegistryName().toString())
                || "minecraft:potion".equals(stack.getItem().getRegistryName().toString())) {
                hasWater = true;
            }
        }
        return bracken >= 4 && hasWater && hasJar;
    }

    private static boolean isWatchKeepingMeal(ItemStack stack)
    {
        return !stack.isEmpty()
            && (stack.getItem() == TSDItems.EAT_ME
            || stack.getItem() == net.minecraft.item.Item.getItemFromBlock(TSDBlocks.PICKLED_BRACKEN_JAR));
    }

    private static final class ReplicationMatch
    {
        private static final ReplicationMatch NONE = new ReplicationMatch(-1, -1, null, null);
        private final int experimentSlot;
        private final int sourceSlot;
        private final ResourceLocation outputId;
        private final Experiment250Logic.ReplicationResult result;

        private ReplicationMatch(
            int experimentSlot,
            int sourceSlot,
            ResourceLocation outputId,
            Experiment250Logic.ReplicationResult result
        ) {
            this.experimentSlot = experimentSlot;
            this.sourceSlot = sourceSlot;
            this.outputId = outputId;
            this.result = result;
        }

        private boolean isValid()
        {
            return experimentSlot >= 0 && sourceSlot >= 0 && outputId != null && result != null && result.isValid();
        }

        private String buildKey(TileEntityCookingPot pot)
        {
            return experimentSlot + ":" + sourceSlot + ":"
                + pot.getStackInSlot(experimentSlot).serializeNBT() + ":"
                + pot.getStackInSlot(sourceSlot).serializeNBT();
        }
    }
}
