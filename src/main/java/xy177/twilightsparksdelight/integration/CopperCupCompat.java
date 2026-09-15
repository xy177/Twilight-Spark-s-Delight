package xy177.twilightsparksdelight.integration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenStructure;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDItems;

public final class CopperCupCompat {
    private static final ResourceLocation CUP =
            new ResourceLocation("miners_delight", "copper_cup");

    private CopperCupCompat() {}

    public static ItemStack container() {
        var item = BuiltInRegistries.ITEM.get(CUP);
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    public static boolean isCup(ItemStack stack) {
        ItemStack cup = container();
        return !cup.isEmpty() && stack.is(cup.getItem());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServing(PlayerInteractEvent.RightClickBlock event) {
        if (!isCup(event.getItemStack())) return;
        var level = event.getLevel();
        var pot = resolvePot(level, event.getPos());
        if (pot == null || servingFor(pot.getMeal()).isEmpty()) return;
        if (!level.isClientSide) {
            var player = event.getEntity();
            var result = takePortions(pot, event.getItemStack(), !player.getAbilities().instabuild);
            if (!result.isEmpty()) {
                pot.awardUsedRecipes(player, java.util.List.of(result));
                if (player.getItemInHand(event.getHand()).isEmpty()) {
                    player.setItemInHand(event.getHand(), result);
                } else if (!player.getInventory().add(result)) {
                    player.drop(result, false);
                }
                level.playSound(null, pot.getBlockPos(),
                        vectorwing.farmersdelight.common.registry.ModSounds.BLOCK_FOOD_TAKE_PORTION.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
            }
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }

    private static CookingPotBlockEntity resolvePot(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.is(TSDBlocks.GIANT_COOKING_POT_PART.get())) {
            pos = TSDGiantKitchenStructure.findController(level, pos, TSDBlocks.GIANT_COOKING_POT.get());
        } else if (state.is(TSDBlocks.GIANTS_COOKING_POT_PART.get())) {
            pos = TSDGiantKitchenStructure.findController(level, pos, TSDBlocks.GIANTS_COOKING_POT.get());
        }
        return pos != null && level.getBlockEntity(pos) instanceof CookingPotBlockEntity pot ? pot : null;
    }

    public static ItemStack servingFor(ItemStack meal) {
        if (meal.is(TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP.get())) {
            return new ItemStack(TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP.get(), 2);
        }
        if (meal.is(TSDItems.BOWL_OF_CHICKEN_AND_HYDRA_SOUP.get())) {
            return new ItemStack(TSDItems.CHICKEN_AND_HYDRA_SOUP_CUP.get(), 2);
        }
        return ItemStack.EMPTY;
    }

    /** Called after the interaction has validated the actual optional cup item. */
    public static ItemStack takePortions(CookingPotBlockEntity pot, ItemStack containers, boolean consumeContainers) {
        var result = servingFor(pot.getMeal());
        var level = pot.getLevel();
        if (level == null || level.isClientSide || result.isEmpty() || containers.getCount() < 2) return ItemStack.EMPTY;
        pot.getInventory().extractItem(CookingPotBlockEntity.MEAL_DISPLAY_SLOT, 1, false);
        if (consumeContainers) containers.shrink(2);
        pot.setChanged();
        level.sendBlockUpdated(pot.getBlockPos(), pot.getBlockState(), pot.getBlockState(), 3);
        return result;
    }

    public static java.util.List<CookingPotRecipe> recipeViews(Level level, ItemStack cup) {
        if (cup.isEmpty()) return java.util.List.of();
        var result = new java.util.ArrayList<CookingPotRecipe>();
        for (var original : level.getRecipeManager().getAllRecipesFor(
                vectorwing.farmersdelight.common.registry.ModRecipeTypes.COOKING.get())) {
            var serving = servingFor(original.getResultItem(level.registryAccess()));
            if (serving.isEmpty()) continue;
            var id = TwilightSparksDelight.id(original.getId().getNamespace() + "/"
                    + original.getId().getPath() + "_copper_cup_jei");
            var view = new CookingPotRecipe(id, original.getGroup(), original.getRecipeBookTab(),
                    original.getIngredients(), serving, cup.copyWithCount(2),
                    original.getExperience(), original.getCookTime());
            result.add(view);
        }
        return java.util.List.copyOf(result);
    }

    public static boolean isCupFood(ItemStack stack) {
        return stack.is(TSDItems.NAGA_MIXED_RICE_CUP.get())
                || stack.is(TSDItems.TWILIGHT_BORSCHT_CUP.get())
                || stack.is(TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP.get())
                || stack.is(TSDItems.CHICKEN_AND_HYDRA_SOUP_CUP.get());
    }
}
