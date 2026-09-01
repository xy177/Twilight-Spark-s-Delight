package xy177.twilightsparksdelight.integration.jei;

import com.wdcftgg.farmersdelightlegacy.common.recipe.CampfireCookingRecipe;
import com.wdcftgg.farmersdelightlegacy.common.recipe.CampfireCookingRecipe.IngredientEntry;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.CampfireCookingRecipeManager;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.common.registry.TSDFluids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GloryCrucibleJeiData
{
    private GloryCrucibleJeiData()
    {
    }

    public static List<GloryCrucibleRecipeWrapper> createHeatingRecipes()
    {
        List<GloryCrucibleRecipeWrapper> recipes = new ArrayList<>();
        Set<String> heatingKeys = new LinkedHashSet<>();
        for (Map.Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
            addHeatingRecipe(recipes, heatingKeys, entry.getKey(), entry.getValue());
        }
        for (CampfireCookingRecipe recipe : CampfireCookingRecipeManager.getRecipes()) {
            addCampfireRecipes(recipes, heatingKeys, recipe);
        }
        return recipes;
    }

    public static List<FluidStack> getHeatingFluids()
    {
        return Arrays.asList(
            new FluidStack(TSDFluids.FIERY_BLOOD, 1000),
            new FluidStack(TSDFluids.FIERY_TEARS, 1000)
        );
    }

    private static void addHeatingRecipe(List<GloryCrucibleRecipeWrapper> recipes, Set<String> keys,
        ItemStack input, ItemStack output)
    {
        if (input == null || input.isEmpty() || output == null || output.isEmpty()) {
            return;
        }
        ItemStack one = input.copy();
        one.setCount(1);
        String key = stackKey(one) + "=" + stackKey(output);
        if (keys.add(key)) {
            recipes.add(GloryCrucibleRecipeWrapper.heating(one, output));
        }
    }

    private static void addCampfireRecipes(List<GloryCrucibleRecipeWrapper> recipes, Set<String> keys,
        CampfireCookingRecipe recipe)
    {
        for (IngredientEntry ingredient : recipe.getIngredients()) {
            List<ItemStack> candidates = ingredientStacks(ingredient);
            for (ItemStack candidate : candidates) {
                if (candidate.isEmpty() || !FurnaceRecipes.instance().getSmeltingResult(candidate).isEmpty()) {
                    continue;
                }
                ItemStack output = recipe.getResultStack().copy();
                String key = stackKey(candidate) + "=" + stackKey(output);
                if (keys.add(key)) {
                    recipes.add(GloryCrucibleRecipeWrapper.heating(candidate, output));
                }
            }
        }
    }

    private static List<ItemStack> ingredientStacks(IngredientEntry ingredient)
    {
        if (ingredient.getOreDictName() != null) {
            List<ItemStack> ores = OreDictionary.getOres(ingredient.getOreDictName());
            List<ItemStack> copies = new ArrayList<>();
            for (ItemStack stack : ores) {
                copies.add(stack.copy());
            }
            return copies;
        }
        Item item = ingredient.getItem();
        if (item == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new ItemStack(item, 1, ingredient.getMetadata()));
    }

    public static List<GloryCrucibleRecipeWrapper> createBrewingRecipes(IIngredientRegistry ingredientRegistry)
    {
        Map<String, BrewingEntry> grouped = new LinkedHashMap<>();
        List<ItemStack> reagents = ingredientRegistry.getPotionIngredients();
        List<Item> potionContainers = Arrays.asList(Items.POTIONITEM, Items.SPLASH_POTION, Items.LINGERING_POTION);
        Collection<PotionType> potionTypes = ForgeRegistries.POTION_TYPES.getValuesCollection();
        for (Item container : potionContainers) {
            for (PotionType potionType : potionTypes) {
                ItemStack inputPotion = PotionUtils.addPotionToItemStack(new ItemStack(container), potionType);
                for (ItemStack reagent : reagents) {
                    if (!PotionHelper.hasConversions(inputPotion, reagent)) {
                        continue;
                    }
                    ItemStack outputPotion = PotionHelper.doReaction(reagent, inputPotion.copy());
                    if (outputPotion.isEmpty() || ItemStack.areItemStacksEqual(inputPotion, outputPotion)) {
                        continue;
                    }
                    String key = stackKey(inputPotion) + "=" + stackKey(outputPotion);
                    BrewingEntry entry = grouped.get(key);
                    if (entry == null) {
                        entry = new BrewingEntry(
                            PotionPreviewFluid.fromPotion(inputPotion),
                            PotionPreviewFluid.fromPotion(outputPotion)
                        );
                        grouped.put(key, entry);
                    }
                    String reagentKey = stackKey(reagent);
                    if (!entry.reagentKeys.contains(reagentKey)) {
                        entry.reagentKeys.add(reagentKey);
                        entry.reagents.add(reagent.copy());
                    }
                }
            }
        }
        List<GloryCrucibleRecipeWrapper> result = new ArrayList<>();
        for (BrewingEntry entry : grouped.values()) {
            result.add(GloryCrucibleRecipeWrapper.brewing(entry.reagents, entry.inputFluid, entry.outputFluid));
        }
        return result;
    }

    private static String stackKey(ItemStack stack)
    {
        if (stack == null || stack.isEmpty()) {
            return "empty";
        }
        ResourceLocation id = stack.getItem().getRegistryName();
        return (id == null ? "unregistered" : id.toString()) + ":" + stack.getMetadata()
            + ":" + (stack.getTagCompound() == null ? "" : stack.getTagCompound().toString());
    }

    private static final class BrewingEntry
    {
        private final FluidStack inputFluid;
        private final FluidStack outputFluid;
        private final List<ItemStack> reagents = new ArrayList<>();
        private final Set<String> reagentKeys = new LinkedHashSet<>();

        private BrewingEntry(FluidStack inputFluid, FluidStack outputFluid)
        {
            this.inputFluid = inputFluid;
            this.outputFluid = outputFluid;
        }
    }
}
