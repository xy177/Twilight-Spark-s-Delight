package xy177.twilightsparksdelight.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.common.block.TSDGloryCrucibleBlock;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDFluids;

public final class TSDGloryCrucibleBlockEntity extends BlockEntity implements IFluidHandler, HeatableBlockEntity {
    // Thirds of a millibucket preserve the exact three-bottles-per-bucket ratio.
    public static final int UNITS_PER_MB = 3;
    public static final int BOTTLE_UNITS = 1000;
    // One TF blood/tears item supplies 1000 mB, unlike a vanilla water bottle.
    public static final int FIERY_ITEM_MB = 1000;
    private FluidStack fluid = FluidStack.EMPTY;
    private int volume;
    private ItemStack potion = ItemStack.EMPTY;
    private ItemStack brewingTarget = ItemStack.EMPTY;
    private int brewingProgress;
    private int brewingDuration;
    private BlockState fruitState;

    public TSDGloryCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(TSDBlockEntities.GLORY_CRUCIBLE.get(), pos, state);
    }

    public FluidStack getFluid() {
        if (!potion.isEmpty()) {
            return new FluidStack(Fluids.WATER, Math.max(1, volume / UNITS_PER_MB));
        }
        return volume <= 0 ? FluidStack.EMPTY : fluid.copyWithAmount(Math.max(1, volume / UNITS_PER_MB));
    }

    public int getFluidAmount() {
        return volume / UNITS_PER_MB;
    }

    public int getVolumeUnits() {
        return volume;
    }

    public int getLiquidLevel() {
        return volume == 0 ? 0 : Math.min(6, (volume * 6 + capacityUnits() - 1) / capacityUnits());
    }

    public double getSurfaceHeight() {
        return (6.0D + 9.0D * volume / capacityUnits()) / 16.0D;
    }

    public boolean isBrewing() {
        return !brewingTarget.isEmpty();
    }

    public boolean hasPotion() {
        return !potion.isEmpty();
    }

    public BlockState getFruitState() {
        return fruitState;
    }

    public void setFruitState(BlockState state) {
        fruitState = state;
        markAndSync();
    }

    public int getPotionColor(float partialTick) {
        int start = potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
        if (!isBrewing()) {
            return start | 0xFF000000;
        }
        int end = brewingTarget.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
        float fraction = Mth.clamp((brewingProgress + partialTick) / Math.max(1, brewingDuration), 0, 1);
        int red = (int) Mth.lerp(fraction, (start >> 16) & 255, (end >> 16) & 255);
        int green = (int) Mth.lerp(fraction, (start >> 8) & 255, (end >> 8) & 255);
        int blue = (int) Mth.lerp(fraction, start & 255, end & 255);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    public boolean isPlainWater() {
        return fruitState == null && potion.isEmpty() && fluid.is(Fluids.WATER) && volume > 0;
    }

    public boolean canUseAsWaterCauldron() {
        return isPlainWater() && !isBrewing() && volume >= BOTTLE_UNITS;
    }

    public boolean addFluidBottle(FluidStack input) {
        if (!canAccept(input) || capacityUnits() - volume < BOTTLE_UNITS) {
            return false;
        }
        if (volume == 0) {
            fluid = input.copyWithAmount(1);
        }
        volume += BOTTLE_UNITS;
        markAndSync();
        return true;
    }

    public boolean pourPotion(ItemStack input) {
        if (!(input.getItem() instanceof PotionItem) || isBrewing() || fruitState != null
                || capacityUnits() - volume < BOTTLE_UNITS) {
            return false;
        }
        if (ItemStack.isSameItemSameComponents(input, waterBottle())) {
            return addFluidBottle(new FluidStack(Fluids.WATER, 1));
        }
        if (volume > 0 && (potion.isEmpty() || !ItemStack.isSameItemSameComponents(potion, input))) {
            return false;
        }
        potion = input.copyWithCount(1);
        fluid = FluidStack.EMPTY;
        volume += BOTTLE_UNITS;
        markAndSync();
        return true;
    }

    public ItemStack bottlePotion() {
        if (isBrewing() || volume < BOTTLE_UNITS || (potion.isEmpty() && !isPlainWater())) {
            return ItemStack.EMPTY;
        }
        ItemStack output = potion.isEmpty() ? waterBottle() : potion.copyWithCount(1);
        consumeBottle();
        return output;
    }

    public ItemStack bottleFieryFluid() {
        int units = FIERY_ITEM_MB * UNITS_PER_MB;
        if (isBrewing() || hasPotion() || fruitState != null || volume < units) {
            return ItemStack.EMPTY;
        }
        ItemStack output;
        if (fluid.is(TSDFluids.FIERY_BLOOD.get())) {
            output = new ItemStack(twilightforest.init.TFItems.FIERY_BLOOD.get());
        } else if (fluid.is(TSDFluids.FIERY_TEARS.get())) {
            output = new ItemStack(twilightforest.init.TFItems.FIERY_TEARS.get());
        } else {
            return ItemStack.EMPTY;
        }
        volume -= units;
        clearIfEmpty();
        markAndSync();
        return output;
    }

    public boolean consumeBottle() {
        if (isBrewing() || volume < BOTTLE_UNITS) {
            return false;
        }
        volume -= BOTTLE_UNITS;
        clearIfEmpty();
        markAndSync();
        return true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TSDGloryCrucibleBlockEntity crucible) {
        if (crucible.volume <= 0) {
            return;
        }
        if (crucible.isBrewing()) {
            if (crucible.isHeated(level, pos)) {
                crucible.brewingProgress++;
                if (!level.isClientSide) {
                    if (crucible.brewingProgress >= crucible.brewingDuration) {
                        crucible.potion = crucible.brewingTarget;
                        crucible.fluid = FluidStack.EMPTY;
                        crucible.brewingTarget = ItemStack.EMPTY;
                        crucible.brewingProgress = 0;
                        level.levelEvent(1035, pos, 0);
                        crucible.markAndSync();
                    } else if (crucible.brewingProgress % 20 == 0) {
                        crucible.markAndSync();
                    }
                }
            }
            return;
        }
        if (level.isClientSide) {
            return;
        }
        AABB bounds = new AABB(pos.getX() + 0.125D, pos.getY() + 0.25D, pos.getZ() + 0.125D,
                pos.getX() + 0.875D, pos.getY() + crucible.getSurfaceHeight() + 0.05D, pos.getZ() + 0.875D);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, bounds,
                entity -> entity.isAlive() && !entity.getItem().isEmpty())) {
            if (crucible.tryProcessIngredient(item.getItem())) {
                if (item.getItem().isEmpty()) {
                    item.discard();
                } else {
                    item.setItem(item.getItem().copy());
                }
                break;
            }
        }
    }

    public boolean tryProcessIngredient(ItemStack ingredient) {
        if (level == null || level.isClientSide || volume == 0 || isBrewing() || ingredient.isEmpty()) {
            return false;
        }
        var fruit = xy177.twilightsparksdelight.integration.FruitsDelightCompat.process(this, ingredient, false);
        if (fruit.matched()) {
            eject(fruit.output());
            eject(fruit.remainder());
            return true;
        }
        if (potion.isEmpty() && TSDFluids.isHeatingFluid(fluid.getFluid())) {
            ItemStack output = findHeatingResult(level, ingredient);
            if (output.isEmpty()) {
                return false;
            }
            consumeIngredient(ingredient);
            eject(output);
            return true;
        }
        if (!isHeated(level, worldPosition) || (potion.isEmpty() && !isPlainWater())) {
            return false;
        }
        ItemStack input = potion.isEmpty() ? waterBottle() : potion;
        if (!level.potionBrewing().hasMix(input, ingredient)) {
            return false;
        }
        ItemStack target = level.potionBrewing().mix(ingredient, input.copyWithCount(1));
        if (!(target.getItem() instanceof PotionItem) || ItemStack.isSameItemSameComponents(input, target)) {
            return false;
        }
        consumeIngredient(ingredient);
        potion = input.copyWithCount(1);
        fluid = FluidStack.EMPTY;
        brewingTarget = target.copyWithCount(1);
        brewingDuration = TSDConfig.GLORY_CRUCIBLE_BREWING_TICKS.get();
        brewingProgress = 0;
        markAndSync();
        return true;
    }

    public static ItemStack findHeatingResult(Level level, ItemStack stack) {
        SingleRecipeInput input = new SingleRecipeInput(stack.copyWithCount(1));
        ItemStack smelting = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level)
                .map(recipe -> recipe.value().assemble(input, level.registryAccess())).orElse(ItemStack.EMPTY);
        return smelting.isEmpty()
                ? level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, input, level)
                        .map(recipe -> recipe.value().assemble(input, level.registryAccess())).orElse(ItemStack.EMPTY)
                : smelting;
    }

    private void consumeIngredient(ItemStack input) {
        ItemStack remainder = input.copyWithCount(1).getCraftingRemainingItem();
        input.shrink(1);
        eject(remainder);
    }

    private void eject(ItemStack output) {
        if (level == null || output.isEmpty()) {
            return;
        }
        ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.05D,
                worldPosition.getZ() + 0.5D, output.copy());
        entity.setDeltaMovement((level.random.nextDouble() - 0.5D) * 0.12D, 0.18D,
                (level.random.nextDouble() - 0.5D) * 0.12D);
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
    }

    public boolean canBubble() {
        return volume > 0 && !fluid.is(Fluids.LAVA) && !TSDFluids.isHeatingFluid(fluid.getFluid());
    }

    private int capacityUnits() {
        return getTankCapacity(0) * UNITS_PER_MB;
    }

    private static ItemStack waterBottle() {
        return PotionContents.createItemStack(Items.POTION, Potions.WATER);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank != 0 || fruitState != null || !potion.isEmpty() || getFluidAmount() == 0
                ? FluidStack.EMPTY : fluid.copyWithAmount(getFluidAmount());
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? Math.max(1000, TSDConfig.GLORY_CRUCIBLE_CAPACITY_MB.get() / 1000 * 1000) : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack input) {
        return tank == 0 && !input.isEmpty();
    }

    private boolean canAccept(FluidStack input) {
        return !input.isEmpty() && !isBrewing() && potion.isEmpty() && fruitState == null
                && (volume == 0 || FluidStack.isSameFluidSameComponents(fluid, input));
    }

    @Override
    public int fill(FluidStack input, FluidAction action) {
        if (!canAccept(input)) {
            return 0;
        }
        int accepted = Math.min(input.getAmount(), Math.max(0, capacityUnits() - volume) / UNITS_PER_MB);
        if (accepted > 0 && action.execute()) {
            if (volume == 0) {
                fluid = input.copyWithAmount(1);
            }
            volume += accepted * UNITS_PER_MB;
            markAndSync();
        }
        return accepted;
    }

    @Override
    public FluidStack drain(FluidStack input, FluidAction action) {
        return input.isEmpty() || !FluidStack.isSameFluidSameComponents(fluid, input)
                ? FluidStack.EMPTY : drain(input.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maximum, FluidAction action) {
        if (maximum <= 0 || isBrewing() || !potion.isEmpty() || fruitState != null || fluid.isEmpty()) {
            return FluidStack.EMPTY;
        }
        int drained = Math.min(maximum, getFluidAmount());
        if (drained <= 0) {
            return FluidStack.EMPTY;
        }
        FluidStack output = fluid.copyWithAmount(drained);
        if (action.execute()) {
            volume -= drained * UNITS_PER_MB;
            clearIfEmpty();
            markAndSync();
        }
        return output;
    }

    private void clearIfEmpty() {
        if (volume <= 0) {
            volume = 0;
            fluid = FluidStack.EMPTY;
            potion = ItemStack.EMPTY;
            brewingTarget = ItemStack.EMPTY;
            brewingProgress = 0;
            fruitState = null;
        }
    }

    private void markAndSync() {
        setChanged();
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState current = level.getBlockState(worldPosition);
        if (current.getBlock() instanceof TSDGloryCrucibleBlock) {
            BlockState updated = current.setValue(TSDGloryCrucibleBlock.LEVEL, getLiquidLevel());
            if (updated != current) {
                level.setBlock(worldPosition, updated, 2);
            }
            // Send the payload even for changes within one quantized level.
            level.sendBlockUpdated(worldPosition, updated, updated, 3);
            level.updateNeighbourForOutputSignal(worldPosition, updated.getBlock());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("VolumeUnits", volume);
        if (fruitState != null) tag.put("FruitState", net.minecraft.nbt.NbtUtils.writeBlockState(fruitState));
        if (!fluid.isEmpty()) {
            tag.put("Fluid", fluid.save(registries));
        }
        if (!potion.isEmpty()) {
            tag.put("Potion", potion.save(registries));
        }
        if (isBrewing()) {
            tag.put("BrewingTarget", brewingTarget.save(registries));
            tag.putInt("BrewingProgress", brewingProgress);
            tag.putInt("BrewingDuration", brewingDuration);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fruitState = tag.contains("FruitState") ? net.minecraft.nbt.NbtUtils.readBlockState(
                registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),
                tag.getCompound("FruitState")) : null;
        if (fruitState != null && fruitState.isAir()) fruitState = null;
        fluid = tag.contains("Fluid") ? FluidStack.parse(registries, tag.get("Fluid")).orElse(FluidStack.EMPTY)
                : FluidStack.EMPTY;
        volume = Mth.clamp(tag.contains("VolumeUnits") ? tag.getInt("VolumeUnits")
                : fluid.getAmount() * UNITS_PER_MB, 0, capacityUnits());
        potion = ItemStack.parseOptional(registries, tag.getCompound("Potion"));
        brewingTarget = ItemStack.parseOptional(registries, tag.getCompound("BrewingTarget"));
        brewingProgress = Math.max(0, tag.getInt("BrewingProgress"));
        brewingDuration = Math.max(1, tag.getInt("BrewingDuration"));
        if (!potion.isEmpty()) {
            potion.setCount(1);
            fluid = FluidStack.EMPTY;
        } else if (!fluid.isEmpty()) {
            fluid.setAmount(1);
        } else {
            volume = 0;
        }
        clearIfEmpty();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
