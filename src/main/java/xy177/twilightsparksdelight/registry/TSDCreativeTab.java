package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;

public final class TSDCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TwilightSparksDelight.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TWILIGHT_SPARKS_DELIGHT =
            TABS.register("twilight_sparks_delight", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.twilight_spark_delight"))
                    .icon(() -> TSDItems.HERMIT_CRAB.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(TSDItems.LIVEROOT_FLOUR.get());
                        output.accept(TSDItems.LIVEROOT_PIE_CRUST.get());
                        output.accept(TSDItems.LIVEROOT_DOUGH.get());
                        output.accept(TSDItems.LIVEROOT_BREAD.get());
                        output.accept(TSDItems.RAW_WILD_BOAR_MEAT.get());
                        output.accept(TSDItems.RAW_WILD_BOAR_MEAT_CUBES.get());
                        output.accept(TSDItems.COOKED_WILD_BOAR_MEAT.get());
                        output.accept(TSDItems.COOKED_WILD_BOAR_MEAT_CUBES.get());
                        output.accept(TSDItems.RAW_BIGHORN_MUTTON.get());
                        output.accept(TSDItems.RAW_BIGHORN_MUTTON_CHOP.get());
                        output.accept(TSDItems.COOKED_BIGHORN_MUTTON.get());
                        output.accept(TSDItems.COOKED_BIGHORN_MUTTON_CHOP.get());
                        output.accept(TSDItems.HERMIT_CRAB.get());
                        output.accept(TSDItems.HERMIT_CRAB_LEG.get());
                        output.accept(TSDItems.COOKED_HERMIT_CRAB_LEG.get());
                        output.accept(TSDItems.PINCH_BEETLE_LEG.get());
                        output.accept(TSDItems.COOKED_PINCH_BEETLE_LEG.get());
                        output.accept(TSDItems.SLIME_BEETLE_LEG.get());
                        output.accept(TSDItems.COOKED_SLIME_BEETLE_LEG.get());
                        output.accept(TSDItems.SLIME_BEETLE_HONEY_GLAND.get());
                        output.accept(TSDItems.FIRE_BEETLE_LEG.get());
                        output.accept(TSDItems.COOKED_FIRE_BEETLE_LEG.get());
                        output.accept(TSDItems.FIRE_BEETLE_FLAME_SAC.get());
                        output.accept(TSDItems.GELID_CRYSTAL.get());
                        output.accept(TSDItems.LABYRINTH_MUSHROOM.get());
                        output.accept(TSDBlocks.LABYRINTH_MUSHROOM_COLONY.get());
                        output.accept(TSDBlocks.LABYRINTH_MUSHROOM_CRATE.get());
                        output.accept(TSDItems.EXPERIMENT_PROTOTYPE.get());
                        output.accept(TSDItems.EXPERIMENT_000.get());
                        output.accept(TSDItems.EXPERIMENT_234.get());
                        output.accept(xy177.twilightsparksdelight.common.item.Experiment250Item.createStack(
                                TSDItems.EXPERIMENT_250.get(), 1, 0));
                        output.accept(TSDItems.GRIDDLE_TENTACLE.get());
                        output.accept(TSDItems.FIERY_SLAG.get());
                        output.accept(xy177.twilightsparksdelight.registry.TSDItems.RABBIT_POCKET_WATCH.get());
                        output.accept(TSDItems.REDCAP_SPICE.get());
                        output.accept(TSDItems.MINO_MINCE.get());
                        output.accept(TSDItems.MINO_PATTY.get());
                        output.accept(TSDItems.BRACKEN.get());
                        output.accept(TSDBlocks.TWILIGHT_BRACKEN_COLONY.get());
                        output.accept(TSDItems.PICKLED_BRACKEN.get());
                        output.accept(TSDBlocks.BRACKEN_CRATE.get());
                        output.accept(TSDItems.QUEST_RAM_MILK.get());
                        output.accept(TSDItems.QUEST_RAM_CHEESE.get());
                        output.accept(TSDItems.TORCHBERRY_SAUCE.get());
                        output.accept(TSDItems.DRINK_ME.get());
                        output.accept(TSDItems.EAT_ME.get());
                        output.accept(TSDItems.TWILIGHT_SKEWER.get());
                        output.accept(TSDItems.LABYRINTH_FLAVOR_SKEWER.get());
                        output.accept(TSDItems.LABYRINTH_SASHIMI_MEDLEY.get());
                        output.accept(TSDItems.LABYRINTH_A5_DOUBLE_CHEESEBURGER.get());
                        output.accept(TSDItems.LABYRINTH_TACO.get());
                        output.accept(TSDItems.TWILIGHT_SUPREME_SANDWICH.get());
                        output.accept(TSDItems.SCOURGE_STEAK.get());
                        output.accept(TSDItems.TRAIL_RATIONS.get());
                        output.accept(TSDItems.DOUBLE_CROWN_ICE_CREAM.get());
                        output.accept(TSDItems.TWIN_RADIANCE_ICE_POP.get());
                        output.accept(TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP.get());
                        optionalCup(output, TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP.get());
                        output.accept(TSDItems.BOWL_OF_CHICKEN_AND_HYDRA_SOUP.get());
                        optionalCup(output, TSDItems.CHICKEN_AND_HYDRA_SOUP_CUP.get());
                        output.accept(TSDItems.STIR_FRIED_BRACKEN.get());
                        output.accept(TSDItems.HELMET_CRAB_LEG_SUSHI_ROLL.get());
                        output.accept(TSDItems.HELMET_CRAB_LEG_SUSHI.get());
                        output.accept(TSDItems.TENTACLE_CHOW_MEIN.get());
                        output.accept(TSDItems.MILLION_POUND_MEAL.get());
                        output.accept(TSDBlocks.TWILIGHT_CHEESE_FONDUE.get());
                        output.accept(TwilightCheeseFondueCompanionItem.withFullDurability(
                                new ItemStack(TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION.get())));
                        output.accept(TSDItems.TWILIGHT_CHEESE_FONDUE_WITH_BREAD.get());
                        output.accept(TSDBlocks.SALT_HELMET_CRAB.get());
                        output.accept(TSDItems.BOWL_OF_SALTED_CRAB_MEAT.get());
                        output.accept(TSDItems.SALT_ROASTED_HELMET_CRAB_CLAW.get());
                        output.accept(TSDBlocks.TWILIGHT_BORSCHT.get());
                        output.accept(TSDItems.BOWL_OF_TWILIGHT_BORSCHT.get());
                        optionalCup(output, TSDItems.TWILIGHT_BORSCHT_CUP.get());
                        output.accept(TSDBlocks.TWILIGHT_BOAR_KNUCKLE.get());
                        output.accept(TSDItems.PLATE_OF_TWILIGHT_BOAR_KNUCKLE.get());
                        output.accept(TSDBlocks.NAGA_MIXED_RICE.get());
                        output.accept(TSDItems.BOWL_OF_NAGA_MIXED_RICE.get());
                        optionalCup(output, TSDItems.NAGA_MIXED_RICE_CUP.get());
                        output.accept(TSDBlocks.ABYSS_PIE.get());
                        output.accept(TSDItems.ABYSS_PIE_SLICE.get());
                        output.accept(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR.get());
                        output.accept(TSDBlocks.PICKLED_BRACKEN_JAR.get());
                        output.accept(TSDItems.MASON_JAR.get());
                        output.accept(TSDBlocks.DRYING_RACK.get());
                        output.accept(TSDBlocks.GLORY_CRUCIBLE.get());
                        output.accept(TSDBlocks.GIANT_STOVE.get());
                        output.accept(TSDBlocks.GIANT_COOKING_POT.get());
                        output.accept(TSDBlocks.GIANTS_STOVE.get());
                        output.accept(TSDBlocks.GIANTS_COOKING_POT.get());
                    })
                    .build());

    private static void optionalCup(CreativeModeTab.Output output, net.minecraft.world.item.Item food) {
        if (!xy177.twilightsparksdelight.integration.CopperCupCompat.container().isEmpty()) output.accept(food);
    }

    private TSDCreativeTab() {
    }
}
