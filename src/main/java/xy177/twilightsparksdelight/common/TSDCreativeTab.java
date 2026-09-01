package xy177.twilightsparksdelight.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import xy177.twilightsparksdelight.common.item.Experiment250Item;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class TSDCreativeTab extends CreativeTabs
{
    public static final TSDCreativeTab INSTANCE = new TSDCreativeTab();
    private static final List<Supplier<ItemStack>> ORDERED_ITEMS = Arrays.asList(
        () -> new ItemStack(TSDItems.LIVEROOT_CONE),
        () -> new ItemStack(TSDItems.LIVEROOT_PIE_CRUST),
        () -> new ItemStack(TSDItems.LIVEROOT_DOUGH),
        () -> new ItemStack(TSDItems.LIVEROOT_BREAD),

        () -> new ItemStack(TSDItems.RAW_WILD_BOAR_MEAT),
        () -> new ItemStack(TSDItems.RAW_WILD_BOAR_MEAT_CUBES),
        () -> new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT),
        () -> new ItemStack(TSDItems.COOKED_WILD_BOAR_MEAT_CUBES),
        () -> new ItemStack(TSDItems.RAW_BIGHORN_MUTTON),
        () -> new ItemStack(TSDItems.RAW_BIGHORN_MUTTON_CHOP),
        () -> new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON),
        () -> new ItemStack(TSDItems.COOKED_BIGHORN_MUTTON_CHOP),

        () -> new ItemStack(TSDItems.HERMIT_CRAB),
        () -> new ItemStack(TSDItems.HERMIT_CRAB_LEG),
        () -> new ItemStack(TSDItems.COOKED_HERMIT_CRAB_LEG),

        () -> new ItemStack(TSDItems.PINCH_BEETLE_LEG),
        () -> new ItemStack(TSDItems.COOKED_PINCH_BEETLE_LEG),
        () -> new ItemStack(TSDItems.SLIME_BEETLE_LEG),
        () -> new ItemStack(TSDItems.COOKED_SLIME_BEETLE_LEG),
        () -> new ItemStack(TSDItems.SLIME_BEETLE_HONEY_GLAND),
        () -> new ItemStack(TSDItems.FIRE_BEETLE_LEG),
        () -> new ItemStack(TSDItems.COOKED_FIRE_BEETLE_LEG),
        () -> new ItemStack(TSDItems.FIRE_BEETLE_FLAME_SAC),

        () -> new ItemStack(TSDItems.GELID_CRYSTAL),
        () -> new ItemStack(TSDItems.LABYRINTH_MUSHROOM),
        () -> new ItemStack(TSDBlocks.LABYRINTH_MUSHROOM_COLONY),
        () -> new ItemStack(TSDBlocks.LABYRINTH_MUSHROOM_CRATE),
        () -> new ItemStack(TSDItems.EXPERIMENT_PROTOTYPE),
        () -> new ItemStack(TSDItems.EXPERIMENT_000),
        () -> new ItemStack(TSDItems.EXPERIMENT_234),
        () -> Experiment250Item.createStack(TSDItems.EXPERIMENT_250, 1, 0.0D),
        () -> new ItemStack(TSDItems.GRIDDLE_TENTACLE),
        () -> new ItemStack(TSDItems.FIERY_SLAG),
        () -> new ItemStack(TSDItems.RABBIT_POCKET_WATCH),
        () -> new ItemStack(TSDItems.REDCAP_SPICE),
        () -> new ItemStack(TSDItems.MINO_MINCE),
        () -> new ItemStack(TSDItems.MINO_PATTY),
        () -> new ItemStack(TSDItems.BRACKEN),
        () -> new ItemStack(TSDBlocks.TWILIGHT_BRACKEN_COLONY),
        () -> new ItemStack(TSDItems.PICKLED_BRACKEN),
        () -> new ItemStack(TSDBlocks.BRACKEN_CRATE),

        () -> new ItemStack(TSDItems.QUEST_RAM_MILK),
        () -> new ItemStack(TSDItems.QUEST_RAM_CHEESE),
        () -> new ItemStack(TSDItems.TORCHBERRY_SAUCE),
        () -> new ItemStack(TSDItems.DRINK_ME),
        () -> new ItemStack(TSDItems.EAT_ME),

        () -> new ItemStack(TSDItems.TWILIGHT_SKEWER),
        () -> new ItemStack(TSDItems.LABYRINTH_FLAVOR_SKEWER),
        () -> new ItemStack(TSDItems.LABYRINTH_SASHIMI_MEDLEY),
        () -> new ItemStack(TSDItems.LABYRINTH_A5_MUSHROOM_FLAVOR_DOUBLE_CHEESEBURGER),
        () -> new ItemStack(TSDItems.LABYRINTH_TACO),
        () -> new ItemStack(TSDItems.TWILIGHT_SUPREME_SANDWICH),
        () -> new ItemStack(TSDItems.SCOURGE_STEAK),
        () -> new ItemStack(TSDItems.TRAIL_RATIONS),
        () -> new ItemStack(TSDItems.DOUBLE_CROWN_ICE_CREAM),
        () -> new ItemStack(TSDItems.TWIN_RADIANCE_ICE_POP),
        () -> new ItemStack(TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP),
        () -> optionalCup(TSDItems.CREAM_OF_LABYRINTH_MUSHROOM_SOUP_CUP),
        () -> new ItemStack(TSDItems.BOWL_OF_CHICKEN_AND_HYDRA_SOUP),
        () -> optionalCup(TSDItems.CHICKEN_AND_HYDRA_SOUP_CUP),
        () -> new ItemStack(TSDItems.STIR_FRIED_BRACKEN),
        () -> new ItemStack(TSDItems.HELMET_CRAB_LEG_SUSHI_ROLL),
        () -> new ItemStack(TSDItems.HELMET_CRAB_LEG_SUSHI),
        () -> new ItemStack(TSDItems.TENTACLE_CHOW_MEIN),
        () -> new ItemStack(TSDItems.MILLION_POUND_MEAL),

        () -> new ItemStack(TSDBlocks.TWILIGHT_CHEESE_FONDUE),
        () -> TwilightCheeseFondueCompanionItem.withFullDurability(new ItemStack(TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION)),
        () -> new ItemStack(TSDItems.TWILIGHT_CHEESE_FONDUE_WITH_BREAD),
        () -> new ItemStack(TSDBlocks.SALT_HELMET_CRAB),
        () -> new ItemStack(TSDItems.BOWL_OF_SALTED_CRAB_MEAT),
        () -> new ItemStack(TSDItems.SALT_ROASTED_HELMET_CRAB_CLAW),
        () -> new ItemStack(TSDBlocks.TWILIGHT_BORSCHT),
        () -> new ItemStack(TSDItems.BOWL_OF_TWILIGHT_BORSCHT),
        () -> optionalCup(TSDItems.TWILIGHT_BORSCHT_CUP),
        () -> new ItemStack(TSDBlocks.TWILIGHT_BOAR_KNUCKLE),
        () -> new ItemStack(TSDItems.PLATE_OF_TWILIGHT_BOAR_KNUCKLE),
        () -> new ItemStack(TSDBlocks.NAGA_MIXED_RICE),
        () -> new ItemStack(TSDItems.BOWL_OF_NAGA_MIXED_RICE),
        () -> optionalCup(TSDItems.NAGA_MIXED_RICE_CUP),
        () -> new ItemStack(TSDBlocks.ABYSS_PIE),
        () -> new ItemStack(TSDItems.ABYSS_PIE_SLICE),
        () -> new ItemStack(TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR),
        () -> new ItemStack(TSDBlocks.PICKLED_BRACKEN_JAR),
        () -> net.minecraft.item.Item.getItemFromBlock(TSDBlocks.GLASS_JAR).getDefaultInstance(),
        () -> new ItemStack(TSDBlocks.GLORY_CRUCIBLE),
        () -> new ItemStack(TSDBlocks.GIANT_STOVE),
        () -> new ItemStack(TSDBlocks.GIANT_COOKING_POT),
        () -> new ItemStack(TSDBlocks.GIANTS_STOVE),
        () -> new ItemStack(TSDBlocks.GIANTS_COOKING_POT)
    );

    private TSDCreativeTab()
    {
        super("twilight_spark_delight");
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack(TSDItems.HERMIT_CRAB);
    }

    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> items)
    {
        for (Supplier<ItemStack> supplier : ORDERED_ITEMS) {
            ItemStack stack = supplier.get();
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
    }

    private static ItemStack optionalCup(net.minecraft.item.Item item)
    {
        return Loader.isModLoaded("miners_delight_bridge") ? new ItemStack(item) : ItemStack.EMPTY;
    }
}
