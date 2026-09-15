package xy177.twilightsparksdelight.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xy177.twilightsparksdelight.common.event.TSDCookingPotEvents;
import twilightforest.init.TFItems;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import xy177.twilightsparksdelight.registry.TSDBlocks;
import xy177.twilightsparksdelight.registry.TSDComponents;
import xy177.twilightsparksdelight.registry.TSDItems;

/**
 * Hooks the actual completion, not a later inventory scan: retained ingredients
 * stay in their slot and crafted-only markers cannot leak to creative stacks.
 */
@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntityMixin {
    @Redirect(method = {"getMealFromItem", "getContainerFromItem", "takeServingFromItem"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z",
                    remap = true))
    private static boolean tsd$recognizeLargePotItem(ItemStack stack, net.minecraft.world.item.Item expected) {
        return stack.is(expected) || expected == vectorwing.farmersdelight.common.registry.ModItems.COOKING_POT.get()
                && stack.getItem() instanceof net.minecraft.world.item.BlockItem block
                && block.getBlock() instanceof xy177.twilightsparksdelight.common.block.TSDGiantCookingPotBlock;
    }

    @Inject(method = "isContainerValid", at = @At("HEAD"), cancellable = true)
    private void tsd$requireEmptyPicklingJar(ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        if (!container.is(TSDItems.MASON_JAR.get())) return;
        var meal = ((CookingPotBlockEntity) (Object) this).getMeal();
        var data = container.getTagElement("BlockEntityTag");
        if ((meal.is(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR.get().asItem())
                || meal.is(TSDBlocks.PICKLED_BRACKEN_JAR.get().asItem()))
                && data != null && !ItemStack.of(data.getCompound("Item")).isEmpty()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "cookingTick", at = @At("TAIL"))
    private static void tsd$dynamicRecipes(Level level, BlockPos pos, BlockState state,
                                           CookingPotBlockEntity pot, CallbackInfo ci) {
        if (!level.isClientSide) TSDCookingPotEvents.tickPot(pot);
    }

    @Redirect(method = "processCooking", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", remap = true), require = 1)
    private void tsd$retainWatch(ItemStack stack, int count,
                                 CookingPotRecipe recipe, CookingPotBlockEntity pot) {
        if (recipe.getId().getNamespace().equals(TwilightSparksDelight.MOD_ID)
                && stack.is(xy177.twilightsparksdelight.registry.TSDItems.RABBIT_POCKET_WATCH.get())) {
            return;
        }
        stack.shrink(count);
    }

    @Redirect(method = "processCooking", at = @At(value = "INVOKE",
            target = "Lvectorwing/farmersdelight/common/crafting/CookingPotRecipe;assemble(Lnet/minecraftforge/items/wrapper/RecipeWrapper;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;"), require = 1)
    private ItemStack tsd$prepareMeal(CookingPotRecipe recipe, RecipeWrapper input,
                                      RegistryAccess registries) {
        ItemStack output = recipe.assemble(input, registries);
        if (output.is(TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION.get())) {
            TwilightCheeseFondueCompanionItem.withFullDurability(output);
        }
        if (output.is(TSDItems.MILLION_POUND_MEAL.get())
                || output.is(TSDBlocks.NAGA_MIXED_RICE.get().asItem())) {
            TSDComponents.COOKED_ADVANCEMENT.set(output, true);
        }
        if (output.is(TSDBlocks.NAGA_MIXED_RICE.get().asItem())) {
            var tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                    TwilightSparksDelight.id("naga_mixed_rice_experiment"));
            String ingredient = "hydra";
            for (int slot = 0; slot < 6; slot++) {
                if (input.getItem(slot).is(tag)) {
                    ingredient = "experiment";
                    break;
                }
            }
            TSDComponents.NAGA_INGREDIENT.set(output, ingredient);
        }
        return output;
    }
}
