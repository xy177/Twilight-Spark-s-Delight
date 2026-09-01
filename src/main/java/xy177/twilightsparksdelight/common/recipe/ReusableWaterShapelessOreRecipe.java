package xy177.twilightsparksdelight.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ReusableWaterShapelessOreRecipe extends ShapelessOreRecipe
{
    public ReusableWaterShapelessOreRecipe(ResourceLocation group, ItemStack result, Object... recipe)
    {
        super(group, result, recipe);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> remaining = super.getRemainingItems(inv);
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && isListAllWater(stack)) {
                remaining.set(i, stack.copy());
            }
        }
        return remaining;
    }

    private static boolean isListAllWater(ItemStack stack)
    {
        for (ItemStack water : OreDictionary.getOres("listAllwater", false)) {
            if (OreDictionary.itemMatches(water, stack, false)) {
                return true;
            }
        }
        return false;
    }
}
