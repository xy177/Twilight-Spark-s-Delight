package xy177.twilightsparksdelight.mixin;

import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public interface CookingPotAccess {
    @Accessor("mealContainerStack")
    void tsd$setMealContainer(ItemStack stack);

    @Accessor("cookingPotData")
    ContainerData tsd$getCookingData();
}
