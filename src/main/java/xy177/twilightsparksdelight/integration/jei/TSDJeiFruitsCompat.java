package xy177.twilightsparksdelight.integration.jei;

import dev.xkmc.fruitsdelight.content.cauldrons.CauldronRecipe;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import xy177.twilightsparksdelight.registry.TSDBlocks;

/** Loaded only after both optional mods have been confirmed present. */
final class TSDJeiFruitsCompat {
    private TSDJeiFruitsCompat() {}

    static void registerCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TSDBlocks.GLORY_CRUCIBLE.get()),
                RecipeType.create("fruitsdelight", "cauldron", CauldronRecipe.class),
                RecipeType.create("fruitsdelight", "cauldron_heated", CauldronRecipe.class));
    }

    static void registerInfo(IRecipeRegistration registration) {
        registration.addItemStackInfo(new ItemStack(TSDBlocks.GLORY_CRUCIBLE.get()),
                Component.translatable("twilight_spark_delight.jei.glory_crucible.fruits_delight"));
    }
}
