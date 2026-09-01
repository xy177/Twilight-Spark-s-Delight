package xy177.twilightsparksdelight.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class KeepingItemShapelessOreRecipe extends ShapelessOreRecipe
{
    private final Item keptItem;

    public KeepingItemShapelessOreRecipe(ResourceLocation group, ItemStack result, Item keptItem, Object... recipe)
    {
        super(group, result, recipe);
        this.keptItem = keptItem;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inventory)
    {
        NonNullList<ItemStack> remaining = super.getRemainingItems(inventory);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == keptItem) {
                remaining.set(i, stack.copy());
            }
        }
        return remaining;
    }
}
