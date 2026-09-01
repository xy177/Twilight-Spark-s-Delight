package xy177.twilightsparksdelight.common.tile;

import com.wdcftgg.farmersdelightlegacy.common.recipe.CookingPotRecipe;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.CookingPotRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.util.CookingPotParticleDispatcher;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockCookingPot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.block.GiantKitchenStructure;

import java.util.ArrayList;
import java.util.List;

public class TileEntityGiantCookingPot extends TileEntityCookingPot
{
    private static final int INPUT_SLOT_COUNT = 6;
    private static final int MEAL_DISPLAY_SLOT = 6;
    private static final int OUTPUT_SLOT = 8;
    private boolean structureChecked;

    @Override
    public void update()
    {
        if (this.world == null) {
            return;
        }
        if (this.world.isRemote) {
            animationTick();
            return;
        }
        if (!this.structureChecked) {
            GiantKitchenStructure.repairLegacyLayout(this.world, this.pos, this.world.getBlockState(this.pos));
            this.structureChecked = true;
        }

        CookingPotRecipe initialRecipe = findCurrentRecipe();
        if (initialRecipe != null && !canFitResult(initialRecipe.getResultStack())) {
            updateWithoutCooking();
            return;
        }
        int inputCountBefore = countInputs();
        updateBasePot(initialRecipe);

        if (initialRecipe == null || countInputs() >= inputCountBefore) {
            return;
        }

        int batchLimit = getBatchLimit();
        for (int batch = 1; batch < batchLimit; batch++) {
            CookingPotRecipe recipe = findCurrentRecipe();
            if (recipe == null
                || !initialRecipe.getRecipeId().equals(recipe.getRecipeId())
                || !isHeated()
                || !canFitResult(recipe.getResultStack())) {
                break;
            }

            int previousInputCount = countInputs();
            setField(0, Math.max(0, recipe.getCookTime() - 1));
            setField(1, Math.max(1, recipe.getCookTime()));
            updateBasePot(recipe);
            if (countInputs() >= previousInputCount) {
                break;
            }
        }
    }

    @Override
    public String getName()
    {
        return "twilight_spark_delight.container.giant_cooking_pot";
    }

    protected int getBatchLimit()
    {
        return Math.max(1, TSDConfig.giantCookingPotBatchCount);
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation(getName());
    }

    private CookingPotRecipe findCurrentRecipe()
    {
        List<ItemStack> inputs = new ArrayList<>(INPUT_SLOT_COUNT);
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            inputs.add(getStackInSlot(slot));
        }
        CookingPotRecipe recipe = CookingPotRecipeManager.findRecipe(inputs);
        if (recipe != null) {
            return recipe;
        }
        for (CookingPotRecipe candidate : CookingPotRecipeManager.getRecipes()) {
            if (createInputPlan(candidate) != null) {
                return candidate;
            }
        }
        return null;
    }

    private int countInputs()
    {
        int count = 0;
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            count += getStackInSlot(slot).getCount();
        }
        return count;
    }

    private void updateBasePot(CookingPotRecipe recipe)
    {
        if (recipe == null || !isHeated()) {
            super.update();
            return;
        }

        InputPlan plan = createInputPlan(recipe);
        if (plan == null) {
            super.update();
            return;
        }
        int storedResultBefore = countStoredResult(recipe.getResultStack());
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            setInventorySlotContents(slot, ItemStack.EMPTY);
        }
        for (int ingredient = 0; ingredient < plan.ingredientSources.length; ingredient++) {
            ItemStack normalized = plan.originalInputs[plan.ingredientSources[ingredient]].copy();
            normalized.setCount(1);
            setInventorySlotContents(ingredient, normalized);
        }

        super.update();

        TileEntityGiantCookingPot current = this;
        if (this.world.getTileEntity(this.pos) instanceof TileEntityGiantCookingPot) {
            current = (TileEntityGiantCookingPot) this.world.getTileEntity(this.pos);
        }
        boolean processed = current.countStoredResult(recipe.getResultStack())
            >= storedResultBefore + recipe.getResultStack().getCount();
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            ItemStack restored = plan.originalInputs[slot].copy();
            if (processed) {
                restored.shrink(plan.consumedCounts[slot]);
            }
            current.setInventorySlotContents(slot, restored);
        }
        current.markDirty();
    }

    private void updateWithoutCooking()
    {
        int cookTime = getField(0);
        int cookTimeTotal = getField(1);
        setField(0, -1);
        super.update();
        TileEntityGiantCookingPot current = this;
        if (this.world.getTileEntity(this.pos) instanceof TileEntityGiantCookingPot) {
            current = (TileEntityGiantCookingPot) this.world.getTileEntity(this.pos);
        }
        current.setField(0, cookTime);
        current.setField(1, cookTimeTotal);
    }

    private InputPlan createInputPlan(CookingPotRecipe recipe)
    {
        // Expand stacked duplicate ingredients into the six logical recipe slots used by the base pot.
        List<CookingPotRecipe.IngredientEntry> ingredients = recipe.getIngredients();
        if (ingredients.isEmpty() || ingredients.size() > INPUT_SLOT_COUNT) {
            return null;
        }

        ItemStack[] originals = new ItemStack[INPUT_SLOT_COUNT];
        int nonEmptySlots = 0;
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            originals[slot] = getStackInSlot(slot).copy();
            if (!originals[slot].isEmpty()) {
                nonEmptySlots++;
            }
        }
        if (nonEmptySlots > ingredients.size()) {
            return null;
        }

        int[] remainingCounts = new int[INPUT_SLOT_COUNT];
        int[] consumedCounts = new int[INPUT_SLOT_COUNT];
        int[] ingredientSources = new int[ingredients.size()];
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            remainingCounts[slot] = originals[slot].getCount();
        }
        if (!assignIngredients(
            ingredients,
            originals,
            remainingCounts,
            consumedCounts,
            ingredientSources,
            0
        )) {
            return null;
        }
        return new InputPlan(originals, consumedCounts, ingredientSources);
    }

    private boolean assignIngredients(
        List<CookingPotRecipe.IngredientEntry> ingredients,
        ItemStack[] inputs,
        int[] remainingCounts,
        int[] consumedCounts,
        int[] ingredientSources,
        int ingredientIndex
    ) {
        if (ingredientIndex >= ingredients.size()) {
            for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
                if (!inputs[slot].isEmpty() && consumedCounts[slot] <= 0) {
                    return false;
                }
            }
            return true;
        }

        CookingPotRecipe.IngredientEntry ingredient = ingredients.get(ingredientIndex);
        for (int pass = 0; pass < 2; pass++) {
            for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
                boolean alreadyUsed = consumedCounts[slot] > 0;
                if ((pass == 0 && alreadyUsed)
                    || (pass == 1 && !alreadyUsed)
                    || remainingCounts[slot] <= 0
                    || !ingredient.matches(inputs[slot])) {
                    continue;
                }
                remainingCounts[slot]--;
                consumedCounts[slot]++;
                ingredientSources[ingredientIndex] = slot;
                if (assignIngredients(
                    ingredients,
                    inputs,
                    remainingCounts,
                    consumedCounts,
                    ingredientSources,
                    ingredientIndex + 1
                )) {
                    return true;
                }
                remainingCounts[slot]++;
                consumedCounts[slot]--;
            }
        }
        return false;
    }

    private int countStoredResult(ItemStack result)
    {
        int count = 0;
        ItemStack meal = getStackInSlot(MEAL_DISPLAY_SLOT);
        ItemStack output = getStackInSlot(OUTPUT_SLOT);
        if (!meal.isEmpty() && stacksMatch(meal, result)) {
            count += meal.getCount();
        }
        if (!output.isEmpty() && stacksMatch(output, result)) {
            count += output.getCount();
        }
        return count;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    private boolean canFitResult(ItemStack result)
    {
        if (result.isEmpty()) {
            return false;
        }
        ItemStack meal = getStackInSlot(MEAL_DISPLAY_SLOT);
        ItemStack output = getStackInSlot(OUTPUT_SLOT);
        if ((!meal.isEmpty() && !stacksMatch(meal, result))
            || (!output.isEmpty() && !stacksMatch(output, result))) {
            return false;
        }
        int storedCount = (meal.isEmpty() ? 0 : meal.getCount()) + (output.isEmpty() ? 0 : output.getCount());
        return storedCount + result.getCount() <= Math.min(getInventoryStackLimit(), result.getMaxStackSize());
    }

    private static boolean stacksMatch(ItemStack first, ItemStack second)
    {
        return ItemStack.areItemsEqual(first, second) && ItemStack.areItemStackTagsEqual(first, second);
    }

    private static final class InputPlan
    {
        private final ItemStack[] originalInputs;
        private final int[] consumedCounts;
        private final int[] ingredientSources;

        private InputPlan(ItemStack[] originalInputs, int[] consumedCounts, int[] ingredientSources)
        {
            this.originalInputs = originalInputs;
            this.consumedCounts = consumedCounts;
            this.ingredientSources = ingredientSources;
        }
    }

    private void animationTick()
    {
        if (!isHeated()) {
            return;
        }
        IBlockState state = this.world.getBlockState(this.pos);
        EnumFacing facing = state.getBlock() instanceof BlockCookingPot
            ? state.getValue(BlockCookingPot.FACING)
            : EnumFacing.NORTH;
        int size = GiantKitchenStructure.getStructureSize(state.getBlock());
        double centerX = GiantKitchenStructure.getCenterX(this.pos, facing, size);
        double centerZ = GiantKitchenStructure.getCenterZ(this.pos, facing, size);
        double bubbleRadius = size * 0.3D;
        double steamRadius = size * 0.2D;
        if (this.world.rand.nextFloat() < 0.2F) {
            CookingPotParticleDispatcher.spawnCookingPotBubble(
                this.world,
                centerX + this.world.rand.nextDouble() * bubbleRadius * 2.0D - bubbleRadius,
                this.pos.getY() + size * 0.7D,
                centerZ + this.world.rand.nextDouble() * bubbleRadius * 2.0D - bubbleRadius,
                0.0D,
                0.0D,
                0.0D
            );
        }
        if (this.world.rand.nextFloat() < 0.05F) {
            CookingPotParticleDispatcher.spawnSteam(
                this.world,
                centerX + this.world.rand.nextDouble() * steamRadius * 2.0D - steamRadius,
                this.pos.getY() + size * 0.7D,
                centerZ + this.world.rand.nextDouble() * steamRadius * 2.0D - steamRadius,
                0.0D,
                this.world.rand.nextBoolean() ? 0.03D : 0.01D,
                0.0D
            );
        }
    }
}
