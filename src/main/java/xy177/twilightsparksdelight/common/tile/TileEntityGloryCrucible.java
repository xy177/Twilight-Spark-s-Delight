package xy177.twilightsparksdelight.common.tile;

import com.wdcftgg.farmersdelightlegacy.common.recipe.CampfireCookingRecipe;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.CampfireCookingRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.util.CookingPotParticleDispatcher;
import com.wdcftgg.farmersdelightlegacy.common.util.HeatSourceHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import xy177.twilightsparksdelight.common.block.BlockGloryCrucible;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.integration.fruitsdelight.FruitsDelightCauldronCompat;
import xy177.twilightsparksdelight.common.registry.TSDFluids;
import xy177.twilightsparksdelight.common.util.GloryCrucibleCapacity;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityGloryCrucible extends TileEntity implements ITickable, IFluidHandler
{
    public static final int MAX_LEVEL = GloryCrucibleCapacity.MAX_LEVEL;
    public static final int LEVELS_PER_BUCKET = GloryCrucibleCapacity.LEVELS_PER_BUCKET;

    private static final ResourceLocation WATER_TEXTURE = new ResourceLocation("minecraft", "blocks/water_still");
    private static final String TAG_LEVEL = "LiquidLevel";
    private static final String TAG_VOLUME = "LiquidVolumeUnits";
    private static final String TAG_FLUID_STACK = "FluidStack";
    private static final String TAG_LEGACY_FLUID = "Fluid";
    private static final String TAG_LEGACY_AMOUNT = "Amount";
    private static final String TAG_POTION = "PotionLiquid";
    private static final String TAG_BREWING_TARGET = "BrewingTarget";
    private static final String TAG_BREWING_PROGRESS = "BrewingProgress";
    private static final String TAG_FRUITS_MODE = "FruitsCauldronMode";
    private static final String TAG_FRUITS_FRUIT = "FruitsCauldronFruit";
    private static final String TAG_FRUITS_LEVEL = "FruitsCauldronLevel";
    private static final String TAG_FRUITS_CAPACITY_SCALED = "FruitsCauldronCapacityScaled";
    private static final String TAG_FRUITS_VOLUME_LINKED = "FruitsCauldronVolumeLinked";

    private FluidStack fluidContents;
    private int liquidVolumeUnits;
    private ItemStack potionLiquid = ItemStack.EMPTY;
    private ItemStack brewingTarget = ItemStack.EMPTY;
    private int brewingProgress;
    private String fruitsCauldronMode = "";
    private String fruitsCauldronFruit = "";
    private int fruitsCauldronLevel;

    @Override
    public void update()
    {
        if (world == null || liquidVolumeUnits <= 0) {
            return;
        }
        boolean heated = HeatSourceHelper.isCookwareHeated(world, pos);
        if (world.isRemote) {
            updateClient(heated);
            return;
        }
        if (isBrewing()) {
            if (heated) {
                brewingProgress++;
                if (brewingProgress >= getBrewingDuration()) {
                    finishBrewing();
                } else if (brewingProgress % 20 == 0) {
                    markDirty();
                }
            }
            return;
        }
        processItemEntities(heated);
    }

    private void updateClient(boolean heated)
    {
        if (isBrewing() && heated && brewingProgress < getBrewingDuration()) {
            brewingProgress++;
        }
        if (canBubble() && heated && world.rand.nextInt(6) == 0) {
            double x = pos.getX() + 0.25D + world.rand.nextDouble() * 0.5D;
            double z = pos.getZ() + 0.25D + world.rand.nextDouble() * 0.5D;
            CookingPotParticleDispatcher.spawnCookingPotBubble(
                world,
                x,
                pos.getY() + getSurfaceHeight() + 0.02D,
                z,
                0.0D,
                0.01D,
                0.0D
            );
        }
    }

    private void processItemEntities(boolean heated)
    {
        AxisAlignedBB bounds = new AxisAlignedBB(
            pos.getX() + 0.125D,
            pos.getY() + 0.25D,
            pos.getZ() + 0.125D,
            pos.getX() + 0.875D,
            pos.getY() + getSurfaceHeight() + 0.05D,
            pos.getZ() + 0.875D
        );
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, bounds);
        for (EntityItem entityItem : items) {
            if (entityItem.isDead || entityItem.getItem().isEmpty()) {
                continue;
            }
            if (isHeatingFluid()) {
                ItemStack result = findHeatingResult(entityItem.getItem());
                if (!result.isEmpty()) {
                    consumeOne(entityItem);
                    eject(result);
                    return;
                }
            } else {
                FruitsDelightCauldronCompat.Action fruitsAction =
                    FruitsDelightCauldronCompat.findAction(this, entityItem.getItem(), heated);
                if (fruitsAction != null) {
                    consumeOne(entityItem);
                    applyFruitsCauldronState(
                        fruitsAction.nextMode,
                        fruitsAction.nextFruit,
                        fruitsAction.nextLevel
                    );
                    if (!fruitsAction.output.isEmpty()) {
                        eject(fruitsAction.output);
                    }
                    return;
                }
                if (heated && canBubble() && tryStartBrewing(entityItem)) {
                    return;
                }
            }
        }
    }

    public static ItemStack findHeatingResult(ItemStack input)
    {
        ItemStack furnaceResult = FurnaceRecipes.instance().getSmeltingResult(input);
        if (!furnaceResult.isEmpty()) {
            return furnaceResult.copy();
        }
        CampfireCookingRecipe campfireRecipe = CampfireCookingRecipeManager.findRecipe(input);
        return campfireRecipe == null ? ItemStack.EMPTY : campfireRecipe.getResultStack().copy();
    }

    private boolean tryStartBrewing(EntityItem entityItem)
    {
        if (potionLiquid.isEmpty()) {
            return false;
        }
        ItemStack reagent = entityItem.getItem();
        if (!PotionHelper.hasConversions(potionLiquid, reagent)) {
            return false;
        }
        ItemStack target = PotionHelper.doReaction(reagent, potionLiquid.copy());
        target.setCount(1);
        if (target.isEmpty() || ItemStack.areItemStacksEqual(target, potionLiquid)) {
            return false;
        }
        consumeOne(entityItem);
        brewingTarget = target;
        brewingProgress = 0;
        markAndSync();
        return true;
    }

    private static void consumeOne(EntityItem entityItem)
    {
        ItemStack stack = entityItem.getItem();
        stack.shrink(1);
        if (stack.isEmpty()) {
            entityItem.setDead();
        } else {
            entityItem.setItem(stack);
        }
    }

    private void eject(ItemStack stack)
    {
        EntityItem output = new EntityItem(
            world,
            pos.getX() + 0.5D,
            pos.getY() + 1.05D,
            pos.getZ() + 0.5D,
            stack.copy()
        );
        output.motionX = (world.rand.nextDouble() - 0.5D) * 0.12D;
        output.motionY = 0.18D;
        output.motionZ = (world.rand.nextDouble() - 0.5D) * 0.12D;
        output.setPickupDelay(10);
        world.spawnEntity(output);
    }

    private void finishBrewing()
    {
        potionLiquid = brewingTarget.copy();
        brewingTarget = ItemStack.EMPTY;
        brewingProgress = 0;
        markAndSync();
    }

    public boolean hasWholeBucketsOfPlainWaterForFruits()
    {
        int bucketVolume = GloryCrucibleCapacity.BUCKET_MB * GloryCrucibleCapacity.VOLUME_UNITS_PER_MB;
        return !isFruitsCauldronState()
            && isPlainWaterPotion()
            && liquidVolumeUnits >= bucketVolume
            && liquidVolumeUnits % bucketVolume == 0;
    }

    public String getFruitsCauldronMode()
    {
        return fruitsCauldronMode == null ? "" : fruitsCauldronMode;
    }

    public String getFruitsCauldronFruit()
    {
        return fruitsCauldronFruit == null ? "" : fruitsCauldronFruit;
    }

    public int getFruitsCauldronLevel()
    {
        return Math.max(0, fruitsCauldronLevel);
    }

    public int getFruitsOutputPortions()
    {
        return Math.max(0, liquidVolumeUnits / GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS);
    }

    public boolean isFruitsCauldronState()
    {
        return fruitsCauldronMode != null && !fruitsCauldronMode.isEmpty();
    }

    public void applyFruitsCauldronState(String mode, String fruit, int level)
    {
        if (mode == null || mode.isEmpty() || level <= 0) {
            fruitsCauldronMode = "";
            fruitsCauldronFruit = "";
            fruitsCauldronLevel = 0;
            liquidVolumeUnits = 0;
            fluidContents = null;
            potionLiquid = ItemStack.EMPTY;
            brewingTarget = ItemStack.EMPTY;
            brewingProgress = 0;
            markAndSync();
            return;
        }
        fruitsCauldronMode = mode;
        fruitsCauldronFruit = fruit == null ? "" : fruit;
        fruitsCauldronLevel = Math.max(1, Math.min(FruitsDelightCauldronCompat.getMaxLevel(mode), level));
        if (FruitsDelightCauldronCompat.isOutputMode(mode)) {
            liquidVolumeUnits = fruitsOutputVolumeUnits(fruitsCauldronLevel);
        }
        fluidContents = FruitsDelightCauldronCompat.getStateFluid(fruitsCauldronMode, fruitsCauldronFruit);
        potionLiquid = ItemStack.EMPTY;
        brewingTarget = ItemStack.EMPTY;
        brewingProgress = 0;
        markAndSync();
    }

    private static int fruitsOutputVolumeUnits(int level)
    {
        return Math.max(1, Math.min(
            GloryCrucibleCapacity.getCapacityVolumeUnits(),
            level * GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS
        ));
    }
    public boolean canPourPotion(ItemStack stack)
    {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemPotion) || isBrewing()
            || getAvailableVolumeUnits() < GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS || fluidContents != null) {
            return false;
        }
        if (potionLiquid.isEmpty()) {
            return true;
        }
        ItemStack one = stack.copy();
        one.setCount(1);
        return ItemStack.areItemsEqual(potionLiquid, one)
            && ItemStack.areItemStackTagsEqual(potionLiquid, one);
    }

    public boolean pourPotion(ItemStack stack)
    {
        if (!canPourPotion(stack)) {
            return false;
        }
        if (potionLiquid.isEmpty()) {
            potionLiquid = stack.copy();
            potionLiquid.setCount(1);
        }
        liquidVolumeUnits += GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS;
        markAndSync();
        return true;
    }

    public boolean canAddFluidPortions(FluidStack resource, int portions)
    {
        return resource != null
            && resource.getFluid() != null
            && portions > 0
            && getAvailableVolumeUnits() >= portions * GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS
            && canAcceptFluid(resource);
    }

    public boolean addFluidPortions(FluidStack resource, int portions)
    {
        return addFluidVolumeUnits(resource, portions * GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS);
    }

    private boolean addFluidVolumeUnits(FluidStack resource, int volumeUnits)
    {
        if (resource == null
            || resource.getFluid() == null
            || volumeUnits <= 0
            || getAvailableVolumeUnits() < volumeUnits
            || !canAcceptFluid(resource)) {
            return false;
        }
        if (liquidVolumeUnits == 0) {
            setFluidIdentity(resource);
        }
        liquidVolumeUnits += volumeUnits;
        markAndSync();
        return true;
    }

    private void setFluidIdentity(FluidStack resource)
    {
        if (resource.getFluid() == FluidRegistry.WATER) {
            potionLiquid = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
            fluidContents = null;
            return;
        }
        fluidContents = resource.copy();
        fluidContents.amount = 1;
        potionLiquid = ItemStack.EMPTY;
    }

    public boolean canBottlePotion()
    {
        return !isBrewing()
            && !potionLiquid.isEmpty()
            && liquidVolumeUnits >= GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS;
    }

    public ItemStack bottlePotion()
    {
        if (!canBottlePotion()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = potionLiquid.copy();
        result.setCount(1);
        liquidVolumeUnits -= GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS;
        clearIfEmpty();
        markAndSync();
        return result;
    }

    public boolean canUseAsWaterCauldron()
    {
        return !isBrewing()
            && isPlainWaterPotion()
            && liquidVolumeUnits >= GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS;
    }

    public boolean drainWaterBucket()
    {
        if (isBrewing() || !isPlainWaterPotion()
            || liquidVolumeUnits < GloryCrucibleCapacity.BUCKET_MB * GloryCrucibleCapacity.VOLUME_UNITS_PER_MB) {
            return false;
        }
        liquidVolumeUnits -= GloryCrucibleCapacity.BUCKET_MB * GloryCrucibleCapacity.VOLUME_UNITS_PER_MB;
        clearIfEmpty();
        markAndSync();
        return true;
    }

    public boolean consumeBottleUnit()
    {
        if (liquidVolumeUnits < GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS || isBrewing()) {
            return false;
        }
        liquidVolumeUnits -= GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS;
        clearIfEmpty();
        markAndSync();
        return true;
    }

    public boolean addRainWater()
    {
        if (isBrewing() || getAvailableVolumeUnits() < GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS) {
            return false;
        }
        if (liquidVolumeUnits == 0) {
            potionLiquid = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
            fluidContents = null;
        } else if (!isPlainWaterPotion()) {
            return false;
        }
        liquidVolumeUnits += GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS;
        markAndSync();
        return true;
    }

    public int getLiquidLevel()
    {
        return GloryCrucibleCapacity.getRenderLevel(liquidVolumeUnits);
    }

    public double getSurfaceHeight()
    {
        return (6.0D + 9.0D * GloryCrucibleCapacity.getFillRatio(liquidVolumeUnits)) / 16.0D;
    }

    private int getAvailableVolumeUnits()
    {
        return Math.max(0, GloryCrucibleCapacity.getCapacityVolumeUnits() - liquidVolumeUnits);
    }

    public ResourceLocation getLiquidTexture()
    {
        if (!potionLiquid.isEmpty()) {
            return WATER_TEXTURE;
        }
        if (fluidContents == null) {
            return WATER_TEXTURE;
        }
        ResourceLocation texture = fluidContents.getFluid().getStill(getCapabilityFluid());
        return texture == null ? WATER_TEXTURE : texture;
    }

    public int getRenderedColor(float partialTicks)
    {
        int start = getCurrentColor();
        if (!isBrewing()) {
            return start;
        }
        int end = PotionUtils.getColor(brewingTarget);
        float progress = Math.min(1.0F, (brewingProgress + partialTicks) / (float) getBrewingDuration());
        return interpolateColor(start, end, progress);
    }

    private int getCurrentColor()
    {
        if (!potionLiquid.isEmpty()) {
            return PotionUtils.getColor(potionLiquid) & 0xFFFFFF;
        }
        return fluidContents == null
            ? (isFruitsCauldronState()
                ? FruitsDelightCauldronCompat.getStateColor(fruitsCauldronMode, fruitsCauldronFruit)
                : 0xFFFFFF)
            : fluidContents.getFluid().getColor(getCapabilityFluid()) & 0xFFFFFF;
    }

    private static int interpolateColor(int start, int end, float progress)
    {
        int red = (int) (((start >> 16) & 255) + (((end >> 16) & 255) - ((start >> 16) & 255)) * progress);
        int green = (int) (((start >> 8) & 255) + (((end >> 8) & 255) - ((start >> 8) & 255)) * progress);
        int blue = (int) ((start & 255) + ((end & 255) - (start & 255)) * progress);
        return red << 16 | green << 8 | blue;
    }

    private boolean isHeatingFluid()
    {
        return potionLiquid.isEmpty()
            && fluidContents != null
            && TSDFluids.isGloryCrucibleHeatingFluid(fluidContents.getFluid());
    }

    private boolean canBubble()
    {
        return liquidVolumeUnits > 0
            && !isHeatingFluid()
            && (fluidContents == null || fluidContents.getFluid() != FluidRegistry.LAVA);
    }

    private boolean isPlainWaterPotion()
    {
        return !potionLiquid.isEmpty()
            && potionLiquid.getItem() == Items.POTIONITEM
            && PotionUtils.getPotionFromItem(potionLiquid) == PotionTypes.WATER;
    }

    private boolean isBrewing()
    {
        return !brewingTarget.isEmpty();
    }

    private int getBrewingDuration()
    {
        return Math.max(1, TSDConfig.gloryCrucibleBrewingTimeTicks);
    }

    private void clearIfEmpty()
    {
        if (liquidVolumeUnits <= 0) {
            liquidVolumeUnits = 0;
            fluidContents = null;
            potionLiquid = ItemStack.EMPTY;
            brewingTarget = ItemStack.EMPTY;
            brewingProgress = 0;
            fruitsCauldronMode = "";
            fruitsCauldronFruit = "";
            fruitsCauldronLevel = 0;
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new IFluidTankProperties[] {new IFluidTankProperties()
        {
            @Nullable
            @Override
            public FluidStack getContents()
            {
                return getCapabilityFluid();
            }

            @Override
            public int getCapacity()
            {
                return GloryCrucibleCapacity.getCapacityMb();
            }

            @Override
            public boolean canFill()
            {
                return !isFruitsCauldronState() && !isBrewing() && getAvailableVolumeUnits() > 0;
            }

            @Override
            public boolean canDrain()
            {
                return !isFruitsCauldronState() && !isBrewing() && getCapabilityFluid() != null;
            }

            @Override
            public boolean canFillFluidType(FluidStack stack)
            {
                return canAcceptFluid(stack);
            }

            @Override
            public boolean canDrainFluidType(FluidStack stack)
            {
                FluidStack contents = getCapabilityFluid();
                return contents != null && stack != null && contents.isFluidEqual(stack);
            }
        }};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (!canAcceptFluid(resource) || resource.amount <= 0) {
            return 0;
        }
        int acceptedMb = Math.min(resource.amount, getAvailableVolumeUnits() / GloryCrucibleCapacity.VOLUME_UNITS_PER_MB);
        if (acceptedMb <= 0) {
            return 0;
        }
        if (doFill) {
            addFluidVolumeUnits(resource, acceptedMb * GloryCrucibleCapacity.VOLUME_UNITS_PER_MB);
        }
        return acceptedMb;
    }

    private boolean canAcceptFluid(FluidStack resource)
    {
        if (resource == null || resource.getFluid() == null || isFruitsCauldronState() || isBrewing() || getAvailableVolumeUnits() <= 0) {
            return false;
        }
        if (liquidVolumeUnits == 0) {
            return true;
        }
        if (resource.getFluid() == FluidRegistry.WATER) {
            return isPlainWaterPotion();
        }
        return potionLiquid.isEmpty()
            && fluidContents != null
            && fluidContents.isFluidEqual(resource);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        FluidStack contents = getCapabilityFluid();
        if (resource == null || contents == null || !contents.isFluidEqual(resource)) {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        FluidStack contents = getCapabilityFluid();
        if (contents == null || maxDrain <= 0 || isFruitsCauldronState() || isBrewing()) {
            return null;
        }
        int drainedMb = Math.min(maxDrain, liquidVolumeUnits / GloryCrucibleCapacity.VOLUME_UNITS_PER_MB);
        if (drainedMb <= 0) {
            return null;
        }
        FluidStack result = contents.copy();
        result.amount = drainedMb;
        if (doDrain) {
            liquidVolumeUnits -= drainedMb * GloryCrucibleCapacity.VOLUME_UNITS_PER_MB;
            clearIfEmpty();
            markAndSync();
        }
        return result;
    }

    @Nullable
    private FluidStack getCapabilityFluid()
    {
        int amountMb = liquidVolumeUnits / GloryCrucibleCapacity.VOLUME_UNITS_PER_MB;
        if (amountMb <= 0) {
            return null;
        }
        if (isPlainWaterPotion()) {
            return new FluidStack(FluidRegistry.WATER, amountMb);
        }
        if (potionLiquid.isEmpty() && fluidContents != null) {
            FluidStack result = fluidContents.copy();
            result.amount = amountMb;
            return result;
        }
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this;
        }
        return super.getCapability(capability, facing);
    }

    private void markAndSync()
    {
        markDirty();
        if (world == null || world.isRemote) {
            return;
        }
        net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockGloryCrucible) {
            int level = getLiquidLevel();
            if (state.getValue(BlockGloryCrucible.LEVEL) != level) {
                world.setBlockState(pos, state.withProperty(BlockGloryCrucible.LEVEL, level), 2);
            }
            net.minecraft.block.state.IBlockState updated = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, updated, updated, 3);
            world.updateComparatorOutputLevel(pos, updated.getBlock());
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger(TAG_VOLUME, liquidVolumeUnits);
        compound.setInteger(TAG_LEVEL, getLiquidLevel());
        if (fluidContents != null) {
            compound.setTag(TAG_FLUID_STACK, fluidContents.writeToNBT(new NBTTagCompound()));
        }
        if (!potionLiquid.isEmpty()) {
            compound.setTag(TAG_POTION, potionLiquid.writeToNBT(new NBTTagCompound()));
        }
        if (!brewingTarget.isEmpty()) {
            compound.setTag(TAG_BREWING_TARGET, brewingTarget.writeToNBT(new NBTTagCompound()));
            compound.setInteger(TAG_BREWING_PROGRESS, brewingProgress);
        }
        if (isFruitsCauldronState()) {
            compound.setString(TAG_FRUITS_MODE, fruitsCauldronMode);
            compound.setString(TAG_FRUITS_FRUIT, fruitsCauldronFruit);
            compound.setInteger(TAG_FRUITS_LEVEL, fruitsCauldronLevel);
            compound.setBoolean(TAG_FRUITS_CAPACITY_SCALED, true);
            compound.setBoolean(TAG_FRUITS_VOLUME_LINKED, true);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_VOLUME, 3)) {
            liquidVolumeUnits = GloryCrucibleCapacity.clampVolumeUnits(compound.getInteger(TAG_VOLUME));
        } else if (compound.hasKey(TAG_LEVEL, 3)) {
            liquidVolumeUnits = GloryCrucibleCapacity.clampVolumeUnits(
                compound.getInteger(TAG_LEVEL) * GloryCrucibleCapacity.BOTTLE_VOLUME_UNITS
            );
        } else {
            int legacyAmount = Math.max(0, compound.getInteger(TAG_LEGACY_AMOUNT));
            liquidVolumeUnits = GloryCrucibleCapacity.clampVolumeUnits(
                legacyAmount * GloryCrucibleCapacity.VOLUME_UNITS_PER_MB
            );
        }
        fluidContents = compound.hasKey(TAG_FLUID_STACK, 10)
            ? FluidStack.loadFluidStackFromNBT(compound.getCompoundTag(TAG_FLUID_STACK))
            : null;
        if (fluidContents == null && compound.hasKey(TAG_LEGACY_FLUID, 8)) {
            Fluid legacyFluid = FluidRegistry.getFluid(compound.getString(TAG_LEGACY_FLUID));
            if (legacyFluid != null) {
                fluidContents = new FluidStack(legacyFluid, 1);
            }
        }
        potionLiquid = compound.hasKey(TAG_POTION, 10)
            ? new ItemStack(compound.getCompoundTag(TAG_POTION))
            : ItemStack.EMPTY;
        brewingTarget = compound.hasKey(TAG_BREWING_TARGET, 10)
            ? new ItemStack(compound.getCompoundTag(TAG_BREWING_TARGET))
            : ItemStack.EMPTY;
        brewingProgress = compound.getInteger(TAG_BREWING_PROGRESS);
        fruitsCauldronMode = compound.getString(TAG_FRUITS_MODE);
        fruitsCauldronFruit = compound.getString(TAG_FRUITS_FRUIT);
        fruitsCauldronLevel = compound.getInteger(TAG_FRUITS_LEVEL);
        if (isFruitsCauldronState() && !compound.getBoolean(TAG_FRUITS_VOLUME_LINKED)) {
            if (FruitsDelightCauldronCompat.isOutputMode(fruitsCauldronMode)) {
                if (!compound.getBoolean(TAG_FRUITS_CAPACITY_SCALED)) {
                    fruitsCauldronLevel = FruitsDelightCauldronCompat.scaleLegacyLevel(
                        fruitsCauldronMode,
                        fruitsCauldronLevel
                    );
                }
                liquidVolumeUnits = fruitsOutputVolumeUnits(fruitsCauldronLevel);
            } else {
                fruitsCauldronLevel = Math.min(
                    FruitsDelightCauldronCompat.getMaxLevel(fruitsCauldronMode),
                    fruitsCauldronLevel
                );
                liquidVolumeUnits = GloryCrucibleCapacity.getCapacityVolumeUnits();
            }
        }
        normalizeLoadedContents();
    }

    private void normalizeLoadedContents()
    {
        if (isFruitsCauldronState()) {
            if (liquidVolumeUnits <= 0 || fruitsCauldronLevel <= 0) {
                liquidVolumeUnits = 0;
                clearIfEmpty();
                return;
            }
            fruitsCauldronLevel = Math.min(
                FruitsDelightCauldronCompat.getMaxLevel(fruitsCauldronMode),
                fruitsCauldronLevel
            );
            if (FruitsDelightCauldronCompat.isOutputMode(fruitsCauldronMode)) {
                liquidVolumeUnits = fruitsOutputVolumeUnits(fruitsCauldronLevel);
            }
            potionLiquid = ItemStack.EMPTY;
            brewingTarget = ItemStack.EMPTY;
            brewingProgress = 0;
            fluidContents = FruitsDelightCauldronCompat.getStateFluid(fruitsCauldronMode, fruitsCauldronFruit);
            return;
        }
        if (liquidVolumeUnits <= 0 || (fluidContents == null && potionLiquid.isEmpty())) {
            liquidVolumeUnits = 0;
            clearIfEmpty();
            return;
        }
        if (!potionLiquid.isEmpty()) {
            potionLiquid.setCount(1);
            fluidContents = null;
        } else {
            fluidContents.amount = 1;
            if (fluidContents.getFluid() == FluidRegistry.WATER) {
                potionLiquid = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
                fluidContents = null;
            }
        }
        if (!brewingTarget.isEmpty()) {
            brewingTarget.setCount(1);
            if (potionLiquid.isEmpty()) {
                brewingTarget = ItemStack.EMPTY;
                brewingProgress = 0;
            }
        }
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet)
    {
        readFromNBT(packet.getNbtCompound());
        if (world != null) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }
}
