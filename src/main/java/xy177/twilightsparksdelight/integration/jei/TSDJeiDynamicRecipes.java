package xy177.twilightsparksdelight.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;
import xy177.twilightsparksdelight.common.tile.TSDGloryCrucibleBlockEntity;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDFluids;
import xy177.twilightsparksdelight.registry.TSDItems;

final class TSDJeiDynamicRecipes {
    record Pickling(ItemStack unripe, ItemStack finished, List<ItemStack> catalysts) {}
    record GloryCrucible(List<ItemStack> inputs, List<ItemStack> reagents,
                         List<ItemStack> outputs, List<Fluid> fluids, boolean brewing) {}

    private TSDJeiDynamicRecipes() {}

    static Pickling pickling() {
        List<ItemStack> catalysts = new ArrayList<>();
        catalysts.add(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR.get().asItem().getDefaultInstance());
        catalysts.add(TSDBlocks.PICKLED_BRACKEN_JAR.get().asItem().getDefaultInstance());
        catalysts.add(new ItemStack(TFBlocks.TIME_LOG_CORE.get()));
        catalysts.add(TSDBlocks.LABYRINTH_MUSHROOM_COLONY.get().asItem().getDefaultInstance());
        catalysts.add(new ItemStack(TFItems.PEACOCK_FEATHER_FAN.get()));
        return new Pickling(
                TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR.get().asItem().getDefaultInstance(),
                TSDBlocks.PICKLED_BRACKEN_JAR.get().asItem().getDefaultInstance(),
                List.copyOf(catalysts));
    }

    static List<GloryCrucible> crucibleHeating() {
        List<GloryCrucible> result = new ArrayList<>();
        var level = Minecraft.getInstance().level;
        if (level == null) return result;
        List<ItemStack> candidates = new ArrayList<>();
        for (var recipe : level.getRecipeManager().getRecipes()) {
            if (recipe.getType() != RecipeType.SMELTING && recipe.getType() != RecipeType.CAMPFIRE_COOKING) continue;
            for (var ingredient : recipe.getIngredients()) {
                for (ItemStack input : ingredient.getItems()) {
                    if (candidates.stream().noneMatch(stack -> ItemStack.isSameItemSameTags(stack, input))) {
                        candidates.add(input.copyWithCount(1));
                    }
                }
            }
        }
        for (ItemStack input : candidates) {
            // Ask the same resolver as the real block, including smelting-before-campfire priority.
            ItemStack output = TSDGloryCrucibleBlockEntity.findHeatingResult(level, input);
            if (!output.isEmpty()) {
                result.add(new GloryCrucible(List.of(input), List.of(), List.of(output),
                        List.of(TSDFluids.FIERY_BLOOD.get(), TSDFluids.FIERY_TEARS.get()), false));
            }
        }
        return result;
    }

    static List<GloryCrucible> crucibleBrewing(Collection<ItemStack> ingredients) {
        List<GloryCrucible> result = new ArrayList<>();
        var level = Minecraft.getInstance().level;
        if (level == null) return result;
        var reagents = ingredients.stream().filter(BrewingRecipeRegistry::isValidIngredient).toList();
        List<ItemStack> potions = new ArrayList<>();
        for (ItemStack stack : ingredients) {
            if (stack.getItem() instanceof PotionItem) addUnique(potions, stack);
        }
        // JEI registration may expose only each bottle's default (water) stack.
        // Enumerate registered contents explicitly, retaining custom supplied stacks too.
        var containers = potions.stream().map(ItemStack::getItem).distinct().toList();
        for (var container : containers) {
            for (var potion : BuiltInRegistries.POTION) {
                addUnique(potions, PotionUtils.setPotion(new ItemStack(container), potion));
            }
        }
        for (ItemStack input : potions) {
            for (ItemStack reagent : reagents) {
                ItemStack output = BrewingRecipeRegistry.getOutput(input.copyWithCount(1), reagent);
                if (!(output.getItem() instanceof PotionItem)
                        || ItemStack.isSameItemSameTags(input, output)) continue;
                result.add(new GloryCrucible(List.of(input.copyWithCount(1)),
                        List.of(reagent.copyWithCount(1)), List.of(output.copyWithCount(1)),
                        List.of(Fluids.WATER), true));
            }
        }
        return result;
    }

    private static void addUnique(List<ItemStack> stacks, ItemStack stack) {
        if (stacks.stream().noneMatch(existing -> ItemStack.isSameItemSameTags(existing, stack))) {
            stacks.add(stack.copyWithCount(1));
        }
    }

    private static void add(List<ItemStack> stacks, String id) {
        ResourceLocation key = new ResourceLocation(id);
        Optional.ofNullable(BuiltInRegistries.ITEM.getOptional(key).orElse(null))
                .ifPresent(item -> stacks.add(item.getDefaultInstance()));
    }
}
