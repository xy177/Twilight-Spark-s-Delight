package xy177.twilightsparksdelight.common.tile;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import xy177.twilightsparksdelight.TSDConfig;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;
import xy177.twilightsparksdelight.registry.TSDBlocks;

/**
 * Farmer's Delight cooking-pot logic with a larger, configurable batch capacity.
 *
 * The inherited inventory and menu are intentionally retained so recipes and
 * external item-handler integrations continue to use the upstream contract.
 */
public class TSDGiantCookingPotBlockEntity extends CookingPotBlockEntity {
    private static final int INPUT_SLOT_COUNT = 6;
    private long completedBatches;

    public TSDGiantCookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return TSDBlockEntities.GIANT_COOKING_POT.get();
    }

    @Override
    public Component getName() {
        if (getBlockState().is(TSDBlocks.GIANTS_COOKING_POT.get())) {
            return Component.translatable("container.twilight_spark_delight.giants_cooking_pot");
        }
        return Component.translatable("container.twilight_spark_delight.giant_cooking_pot");
    }

    public static void cookingTick(Level level, BlockPos pos, BlockState state,
                                   TSDGiantCookingPotBlockEntity blockEntity) {
        if (level.isClientSide) {
            CookingPotBlockEntity.cookingTick(level, pos, state, blockEntity);
            return;
        }

        Optional<RecipePlan> initialPlan = findRecipePlan(level, blockEntity);
        if (initialPlan.isEmpty() || !blockEntity.isHeated()
                || !blockEntity.canProcessResult(initialPlan.get().recipe().value())) {
            CookingPotBlockEntity.cookingTick(level, pos, state, blockEntity);
            return;
        }

        RecipeHolder<CookingPotRecipe> recipe = initialPlan.get().recipe();
        int completed = blockEntity.processOneBatch(level, pos, state, initialPlan.get(), false);
        if (completed == 0) {
            return;
        }

        /*
         * A normal pot finishes one batch per cooking tick.  The enlarged pot
         * keeps the same cooking time, but chains additional complete batches
         * while there is enough input and output capacity.  Each batch still
         * enters the upstream implementation, so containers, remainders,
         * recipe statistics, and the meal slot retain Farmer's Delight's
         * behavior.
         */
        for (int batch = completed; batch < blockEntity.getBatchLimit(); batch++) {
            Optional<RecipePlan> nextPlan = findRecipePlan(level, blockEntity);
            if (nextPlan.isEmpty() || !nextPlan.get().recipe().id().equals(recipe.id())
                    || !blockEntity.canProcessResult(nextPlan.get().recipe().value())) {
                break;
            }
            blockEntity.cookingPotData.set(0, Math.max(0, recipe.value().getCookTime() - 1));
            blockEntity.cookingPotData.set(1, Math.max(1, recipe.value().getCookTime()));
            if (blockEntity.processOneBatch(level, pos, state, nextPlan.get(), true) == 0) {
                break;
            }
        }
    }

    public static void animationTick(Level level, BlockPos pos, BlockState state,
                                     TSDGiantCookingPotBlockEntity blockEntity) {
        if (!blockEntity.isHeated()) return;
        var bounds = xy177.twilightsparksdelight.common.block.TSDGiantKitchenStructure.bounds(pos, state);
        var center = bounds.getCenter();
        double size = bounds.getXsize();
        var random = level.random;
        if (random.nextFloat() < 0.2F) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE_POP,
                    center.x + (random.nextDouble() - 0.5) * 0.6 * size, pos.getY() + 0.7 * size,
                    center.z + (random.nextDouble() - 0.5) * 0.6 * size, 0, 0, 0);
        }
        if (random.nextFloat() < 0.05F) {
            level.addParticle(vectorwing.farmersdelight.common.registry.ModParticleTypes.STEAM.get(),
                    center.x + (random.nextDouble() - 0.5) * 0.4 * size, pos.getY() + 0.65 * size,
                    center.z + (random.nextDouble() - 0.5) * 0.4 * size, 0, 0.015, 0);
        }
    }

    public int getBatchLimit() {
        return Math.max(1, getBlockState().is(TSDBlocks.GIANTS_COOKING_POT.get())
                ? TSDConfig.GIANTS_COOKING_POT_MAX_BATCHES.get()
                : TSDConfig.GIANT_COOKING_POT_MAX_BATCHES.get());
    }

    @Override
    protected boolean canCook(CookingPotRecipe recipe) {
        return canProcessResult(recipe) && super.canCook(recipe);
    }

    @Override
    public void setRecipeUsed(RecipeHolder<?> recipe) {
        super.setRecipeUsed(recipe);
        if (recipe != null) completedBatches++;
    }

    private boolean canProcessResult(CookingPotRecipe recipe) {
        ItemStack result = recipe.assemble(
                new RecipeWrapper(getInventory()), level.registryAccess());
        if (result.isEmpty()) {
            return false;
        }
        ItemStack meal = getMeal();
        ItemStack output = getInventory().getStackInSlot(CookingPotBlockEntity.OUTPUT_SLOT);
        if ((!meal.isEmpty() && !ItemStack.isSameItem(meal, result))
                || (!output.isEmpty() && !ItemStack.isSameItem(output, result))) {
            return false;
        }
        int stored = (meal.isEmpty() ? 0 : meal.getCount())
                + (output.isEmpty() ? 0 : output.getCount());
        return stored + result.getCount() <= result.getMaxStackSize();
    }

    private static Optional<RecipePlan> findRecipePlan(
            Level level, TSDGiantCookingPotBlockEntity blockEntity) {
        ItemStackHandler inventory = blockEntity.getInventory();
        Optional<RecipeHolder<CookingPotRecipe>> direct = level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.COOKING.get(), new RecipeWrapper(inventory), level);
        if (direct.isPresent()) {
            Optional<RecipePlan> directPlan = createPlan(direct.get(), inventory);
            if (directPlan.isPresent()) {
                return directPlan;
            }
        }

        for (RecipeHolder<CookingPotRecipe> candidate :
                level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COOKING.get())) {
            Optional<RecipePlan> plan = createPlan(candidate, inventory);
            if (plan.isPresent()) {
                return plan;
            }
        }
        return Optional.empty();
    }

    private static Optional<RecipePlan> createPlan(
            RecipeHolder<CookingPotRecipe> recipe, ItemStackHandler inventory) {
        int[] sources = new int[recipe.value().getIngredients().size()];
        ItemStack[] original = new ItemStack[INPUT_SLOT_COUNT];
        int nonEmpty = 0;
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            original[slot] = inventory.getStackInSlot(slot).copy();
            if (!original[slot].isEmpty()) {
                nonEmpty++;
            }
        }
        if (sources.length == 0 || sources.length > INPUT_SLOT_COUNT
                || nonEmpty > sources.length) {
            return Optional.empty();
        }
        int[] remaining = new int[INPUT_SLOT_COUNT];
        int[] consumed = new int[INPUT_SLOT_COUNT];
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            remaining[slot] = original[slot].getCount();
        }
        if (!assignIngredients(recipe.value().getIngredients(), original, remaining,
                consumed, sources, 0)) {
            return Optional.empty();
        }
        return Optional.of(new RecipePlan(recipe, original, consumed, sources));
    }

    private static boolean assignIngredients(
            java.util.List<Ingredient> ingredients, ItemStack[] inputs, int[] remaining,
            int[] consumed, int[] sources, int ingredientIndex) {
        if (ingredientIndex >= ingredients.size()) {
            for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
                if (!inputs[slot].isEmpty() && consumed[slot] <= 0) {
                    return false;
                }
            }
            return true;
        }
        Ingredient ingredient = ingredients.get(ingredientIndex);
        for (int pass = 0; pass < 2; pass++) {
            for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
                boolean used = consumed[slot] > 0;
                if ((pass == 0 && used) || (pass == 1 && !used)
                        || remaining[slot] <= 0 || !ingredient.test(inputs[slot])) {
                    continue;
                }
                remaining[slot]--;
                consumed[slot]++;
                sources[ingredientIndex] = slot;
                if (assignIngredients(ingredients, inputs, remaining, consumed, sources,
                        ingredientIndex + 1)) {
                    return true;
                }
                remaining[slot]++;
                consumed[slot]--;
            }
        }
        return false;
    }

    private int processOneBatch(Level level, BlockPos pos, BlockState state,
                                RecipePlan plan, boolean forceCompletion) {
        ItemStackHandler inventory = getInventory();
        long batchesBefore = completedBatches;
        for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
        for (int ingredient = 0; ingredient < plan.sources().length; ingredient++) {
            ItemStack one = plan.originalInputs()[plan.sources()[ingredient]].copy();
            one.setCount(1);
            inventory.setStackInSlot(ingredient, one);
        }

        if (forceCompletion) {
            cookingPotData.set(0, Math.max(0, plan.recipe().value().getCookTime() - 1));
            cookingPotData.set(1, Math.max(1, plan.recipe().value().getCookTime()));
        }
        try {
            CookingPotBlockEntity.cookingTick(level, pos, state, this);
        } finally {
            // Read the real remaining logical ingredients: upstream hooks can retain
            // a catalyst or change how many items were consumed.
            int[] consumed = new int[INPUT_SLOT_COUNT];
            if (completedBatches > batchesBefore) {
                for (int ingredient = 0; ingredient < plan.sources().length; ingredient++) {
                    if (inventory.getStackInSlot(ingredient).isEmpty()) {
                        consumed[plan.sources()[ingredient]]++;
                    }
                }
            }
            for (int slot = 0; slot < INPUT_SLOT_COUNT; slot++) {
                ItemStack restored = plan.originalInputs()[slot].copy();
                restored.shrink(consumed[slot]);
                inventory.setStackInSlot(slot, restored);
            }
        }
        setChanged();
        return completedBatches > batchesBefore ? 1 : 0;
    }

    private record RecipePlan(RecipeHolder<CookingPotRecipe> recipe, ItemStack[] originalInputs,
                               int[] consumedCounts, int[] sources) {
    }

}
