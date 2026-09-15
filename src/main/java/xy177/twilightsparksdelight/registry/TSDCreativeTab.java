package xy177.twilightsparksdelight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;

public final class TSDCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TwilightSparksDelight.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TWILIGHT_SPARKS_DELIGHT =
            TABS.register("twilight_sparks_delight", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.twilight_spark_delight"))
                    .icon(() -> TSDItems.HERMIT_CRAB.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(TSDItems.LIVEROOT_FLOUR);
                        output.accept(TSDItems.LIVEROOT_PIE_CRUST);
                        output.accept(TSDItems.LIVEROOT_DOUGH);
                        output.accept(TSDItems.LIVEROOT_BREAD);
                        output.accept(TSDItems.RAW_WILD_BOAR_MEAT);
                        output.accept(TSDItems.RAW_WILD_BOAR_MEAT_CUBES);
                        output.accept(TSDItems.COOKED_WILD_BOAR_MEAT);
                        output.accept(TSDItems.COOKED_WILD_BOAR_MEAT_CUBES);
                        output.accept(TSDItems.RAW_BIGHORN_MUTTON);
                        output.accept(TSDItems.RAW_BIGHORN_MUTTON_CHOP);
                        output.accept(TSDItems.COOKED_BIGHORN_MUTTON);
                        output.accept(TSDItems.COOKED_BIGHORN_MUTTON_CHOP);
                        output.accept(TSDItems.HERMIT_CRAB);
                        output.accept(TSDItems.HERMIT_CRAB_LEG);
                        output.accept(TSDItems.COOKED_HERMIT_CRAB_LEG);
                        output.accept(TSDItems.PINCH_BEETLE_LEG);
                        output.accept(TSDItems.COOKED_PINCH_BEETLE_LEG);
                        output.accept(TSDItems.SLIME_BEETLE_LEG);
                        output.accept(TSDItems.COOKED_SLIME_BEETLE_LEG);
                        output.accept(TSDItems.SLIME_BEETLE_HONEY_GLAND);
                        output.accept(TSDItems.FIRE_BEETLE_LEG);
                        output.accept(TSDItems.COOKED_FIRE_BEETLE_LEG);
                        output.accept(TSDItems.FIRE_BEETLE_FLAME_SAC);
                        output.accept(TSDItems.GELID_CRYSTAL);
                        output.accept(TSDItems.LABYRINTH_MUSHROOM);
                        output.accept(TSDBlocks.LABYRINTH_MUSHROOM_COLONY);
                        output.accept(TSDBlocks.LABYRINTH_MUSHROOM_CRATE);
                        output.accept(TSDItems.EXPERIMENT_PROTOTYPE);
                        output.accept(TSDItems.EXPERIMENT_000);
                        output.accept(TSDItems.EXPERIMENT_234);
                        output.accept(xy177.twilightsparksdelight.common.item.Experiment250Item.createStack(
                                TSDItems.EXPERIMENT_250.get(), 1, 0));
                        output.accept(TSDItems.GRIDDLE_TENTACLE);
                        output.accept(TSDItems.FIERY_SLAG);
                        output.accept(twilightforest.init.TFItems.POCKET_WATCH.get());
                        output.accept(TSDItems.REDCAP_SPICE);
                        output.accept(TSDItems.MINO_MINCE);
                        output.accept(TSDItems.MINO_PATTY);
                        output.accept(TSDItems.BRACKEN);
                        output.accept(TSDBlocks.TWILIGHT_BRACKEN_COLONY);
                        output.accept(TSDItems.PICKLED_BRACKEN);
                        output.accept(TSDBlocks.BRACKEN_CRATE);
                        output.accept(TSDItems.QUEST_RAM_MILK);
                        output.accept(TSDItems.QUEST_RAM_CHEESE);
                        output.accept(TSDItems.TORCHBERRY_SAUCE);
                        output.accept(TSDItems.DRINK_ME);
                        output.accept(TSDItems.EAT_ME);
                        output.accept(TSDItems.TWILIGHT_SKEWER);
                        output.accept(TSDItems.LABYRINTH_FLAVOR_SKEWER);
                        output.accept(TSDItems.LABYRINTH_SASHIMI_MEDLEY);
                        output.accept(TSDItems.LABYRINTH_A5_DOUBLE_CHEESEBURGER);
                        output.accept(TSDItems.LABYRINTH_TACO);
                        output.accept(TSDItems.TWILIGHT_SUPREME_SANDWICH);
                        output.accept(TSDItems.SCOURGE_STEAK);
                        output.accept(TSDItems.TRAIL_RATIONS);
                        output.accept(TSDItems.DOUBLE_CROWN_ICE_CREAM);
                        output.accept(TSDItems.TWIN_RADIANCE_ICE_POP);
                        output.accept(TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP);
                        optionalCup(output, TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP.get());
                        output.accept(TSDItems.BOWL_OF_CHICKEN_AND_HYDRA_SOUP);
                        optionalCup(output, TSDItems.CHICKEN_AND_HYDRA_SOUP_CUP.get());
                        output.accept(TSDItems.STIR_FRIED_BRACKEN);
                        output.accept(TSDItems.HELMET_CRAB_LEG_SUSHI_ROLL);
                        output.accept(TSDItems.HELMET_CRAB_LEG_SUSHI);
                        output.accept(TSDItems.TENTACLE_CHOW_MEIN);
                        output.accept(TSDItems.MILLION_POUND_MEAL);
                        output.accept(TSDBlocks.TWILIGHT_CHEESE_FONDUE);
                        output.accept(TwilightCheeseFondueCompanionItem.withFullDurability(
                                new ItemStack(TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION.get())));
                        output.accept(TSDItems.TWILIGHT_CHEESE_FONDUE_WITH_BREAD);
                        output.accept(TSDBlocks.SALT_HELMET_CRAB);
                        output.accept(TSDItems.BOWL_OF_SALTED_CRAB_MEAT);
                        output.accept(TSDItems.SALT_ROASTED_HELMET_CRAB_CLAW);
                        output.accept(TSDBlocks.TWILIGHT_BORSCHT);
                        output.accept(TSDItems.BOWL_OF_TWILIGHT_BORSCHT);
                        optionalCup(output, TSDItems.TWILIGHT_BORSCHT_CUP.get());
                        output.accept(TSDBlocks.TWILIGHT_BOAR_KNUCKLE);
                        output.accept(TSDItems.PLATE_OF_TWILIGHT_BOAR_KNUCKLE);
                        output.accept(TSDBlocks.NAGA_MIXED_RICE);
                        output.accept(TSDItems.BOWL_OF_NAGA_MIXED_RICE);
                        optionalCup(output, TSDItems.NAGA_MIXED_RICE_CUP.get());
                        output.accept(TSDBlocks.ABYSS_PIE);
                        output.accept(TSDItems.ABYSS_PIE_SLICE);
                        output.accept(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR);
                        output.accept(TSDBlocks.PICKLED_BRACKEN_JAR);
                        output.accept(twilightforest.init.TFBlocks.MASON_JAR.get());
                        output.accept(TSDBlocks.GLORY_CRUCIBLE);
                        output.accept(TSDBlocks.GIANT_STOVE);
                        output.accept(TSDBlocks.GIANT_COOKING_POT);
                        output.accept(TSDBlocks.GIANTS_STOVE);
                        output.accept(TSDBlocks.GIANTS_COOKING_POT);
                    })
                    .build());

    private static void optionalCup(CreativeModeTab.Output output, net.minecraft.world.item.Item food) {
        if (!xy177.twilightsparksdelight.integration.CopperCupCompat.container().isEmpty()) output.accept(food);
    }

    private TSDCreativeTab() {
    }
}
