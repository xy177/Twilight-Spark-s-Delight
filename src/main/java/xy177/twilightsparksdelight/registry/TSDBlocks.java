package xy177.twilightsparksdelight.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.block.TSDFeastBlock;
import xy177.twilightsparksdelight.common.block.TSDColonyBlock;
import xy177.twilightsparksdelight.common.block.TSDStageFeastBlock;
import xy177.twilightsparksdelight.common.block.TSDLargeStageFeastBlock;
import xy177.twilightsparksdelight.common.block.TSDStructurePartBlock;
import xy177.twilightsparksdelight.common.block.TSDBitesFeastBlock;
import xy177.twilightsparksdelight.common.block.TSDNagaMixedRiceBlock;
import xy177.twilightsparksdelight.common.block.TSDAbyssPieBlock;
import xy177.twilightsparksdelight.common.block.TSDSaltHelmetCrabBlock;
import xy177.twilightsparksdelight.common.block.TSDTwilightBorschtBlock;
import xy177.twilightsparksdelight.common.block.TSDTwilightCheeseFondueBlock;
import xy177.twilightsparksdelight.common.block.TSDGloryCrucibleBlock;
import xy177.twilightsparksdelight.common.block.TSDUnripePickledBrackenJarBlock;
import xy177.twilightsparksdelight.common.block.TSDPickledBrackenJarBlock;
import xy177.twilightsparksdelight.common.item.TSDColonyItem;
import xy177.twilightsparksdelight.common.item.TSDGiantsCookingPotItem;
import xy177.twilightsparksdelight.common.block.TSDGiantStoveBlock;
import xy177.twilightsparksdelight.common.block.TSDGiantCookingPotBlock;
import xy177.twilightsparksdelight.common.block.TSDGiantKitchenPartBlock;

public final class TSDBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TwilightSparksDelight.MOD_ID);

    public static final DeferredBlock<TSDTwilightCheeseFondueBlock> TWILIGHT_CHEESE_FONDUE =
            BLOCKS.register("twilight_cheese_fondue", () -> new TSDTwilightCheeseFondueBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.5F)));
    static {
        TSDItems.ITEMS.register("twilight_cheese_fondue", () -> new BlockItem(TWILIGHT_CHEESE_FONDUE.get(),
                new Item.Properties().stacksTo(1)));
    }
    public static final DeferredBlock<TSDSaltHelmetCrabBlock> SALT_HELMET_CRAB =
            BLOCKS.register("salt_helmet_crab", () -> new TSDSaltHelmetCrabBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.5F)));
    static {
        TSDItems.ITEMS.register("salt_helmet_crab", () -> new BlockItem(SALT_HELMET_CRAB.get(),
                new Item.Properties().stacksTo(1)));
    }
    public static final DeferredBlock<Block> LABYRINTH_MUSHROOM_COLONY = registerColony("labyrinth_mushroom_colony",
            MapColor.COLOR_RED, TSDItems.LABYRINTH_MUSHROOM);
    public static final DeferredBlock<Block> TWILIGHT_BRACKEN_COLONY = registerColony("twilight_bracken_colony",
            MapColor.COLOR_GREEN, TSDItems.BRACKEN);
    public static final DeferredBlock<TSDUnripePickledBrackenJarBlock> UNRIPE_PICKLED_BRACKEN_JAR =
            BLOCKS.register("unripe_pickled_bracken_jar", () -> new TSDUnripePickledBrackenJarBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F)));
    static {
        TSDItems.ITEMS.register("unripe_pickled_bracken_jar", () -> new BlockItem(
                UNRIPE_PICKLED_BRACKEN_JAR.get(), new Item.Properties().stacksTo(1)));
    }
    public static final DeferredBlock<TSDPickledBrackenJarBlock> PICKLED_BRACKEN_JAR =
            BLOCKS.register("pickled_bracken_jar", () -> new TSDPickledBrackenJarBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5F)));
    static {
        TSDItems.ITEMS.register("pickled_bracken_jar", () -> new BlockItem(
                PICKLED_BRACKEN_JAR.get(), new Item.Properties().stacksTo(1)));
    }
    public static final DeferredBlock<TSDGloryCrucibleBlock> GLORY_CRUCIBLE =
            BLOCKS.register("glory_crucible", () -> new TSDGloryCrucibleBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F)));
    static {
        TSDItems.ITEMS.register("glory_crucible", () -> new BlockItem(GLORY_CRUCIBLE.get(),
                new Item.Properties()));
    }
    public static final DeferredBlock<TSDTwilightBorschtBlock> TWILIGHT_BORSCHT =
            BLOCKS.register("twilight_borscht", () -> new TSDTwilightBorschtBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(1.5F)));
    static {
        TSDItems.ITEMS.register("twilight_borscht", () -> new BlockItem(TWILIGHT_BORSCHT.get(),
                new Item.Properties().stacksTo(1)));
    }
    public static final DeferredBlock<Block> LABYRINTH_MUSHROOM_CRATE = register("labyrinth_mushroom_crate", MapColor.WOOD);
    public static final DeferredBlock<Block> BRACKEN_CRATE = register("bracken_crate", MapColor.WOOD);
    public static final DeferredBlock<TSDStructurePartBlock> NAGA_MIXED_RICE_PART =
            BLOCKS.register("naga_mixed_rice_part", () -> new TSDStructurePartBlock(
                    BlockBehaviour.Properties.of().strength(1.0F), TSDBlocks::nagaController));
    public static final DeferredBlock<TSDNagaMixedRiceBlock> NAGA_MIXED_RICE =
            BLOCKS.register("naga_mixed_rice", () -> new TSDNagaMixedRiceBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.5F),
                    new TSDNagaMixedRiceBlock.SupplierArgs(
                            TSDItems.BOWL_OF_NAGA_MIXED_RICE,
                            NAGA_MIXED_RICE_PART)));
    static {
        TSDItems.ITEMS.register("naga_mixed_rice", () -> new BlockItem(NAGA_MIXED_RICE.get(),
                new Item.Properties().stacksTo(1)));
    }
    public static final DeferredBlock<TSDAbyssPieBlock> ABYSS_PIE = BLOCKS.register("abyss_pie", () -> new TSDAbyssPieBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(1.5F)));
    static {
        TSDItems.ITEMS.register("abyss_pie", () -> new BlockItem(ABYSS_PIE.get(), new Item.Properties().stacksTo(1)));
    }
    public static final DeferredBlock<TSDStructurePartBlock> TWILIGHT_BOAR_KNUCKLE_PART =
            BLOCKS.register("twilight_boar_knuckle_part", () -> new TSDStructurePartBlock(
                    BlockBehaviour.Properties.of().strength(1.0F), TSDBlocks::boarController));
    public static final DeferredBlock<TSDLargeStageFeastBlock> TWILIGHT_BOAR_KNUCKLE =
            BLOCKS.register("twilight_boar_knuckle",
                    () -> new xy177.twilightsparksdelight.common.block.TSDTwilightBoarKnuckleBlock(
                            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(1.5F),
                            TWILIGHT_BOAR_KNUCKLE_PART));
    static {
        TSDItems.ITEMS.register("twilight_boar_knuckle", () -> new BlockItem(
                TWILIGHT_BOAR_KNUCKLE.get(), new Item.Properties().stacksTo(1)));
    }
    public static final DeferredBlock<TSDGiantStoveBlock> GIANT_STOVE =
            BLOCKS.register("giant_stove", () -> new TSDGiantStoveBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F),
                    TSDBlocks::giantStovePart, 2));
    public static final DeferredBlock<TSDGiantKitchenPartBlock> GIANT_STOVE_PART =
            BLOCKS.register("giant_stove_part", () -> new TSDGiantKitchenPartBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F),
                    TSDBlocks::giantStove, TSDGiantKitchenPartBlock.Kind.STOVE));
    public static final DeferredBlock<TSDGiantCookingPotBlock> GIANT_COOKING_POT =
            BLOCKS.register("giant_cooking_pot", () -> new TSDGiantCookingPotBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F),
                    TSDBlocks::giantCookingPotPart, 2));
    public static final DeferredBlock<TSDGiantKitchenPartBlock> GIANT_COOKING_POT_PART =
            BLOCKS.register("giant_cooking_pot_part", () -> new TSDGiantKitchenPartBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F),
                    TSDBlocks::giantCookingPot, TSDGiantKitchenPartBlock.Kind.COOKING_POT));
    public static final DeferredBlock<TSDGiantStoveBlock> GIANTS_STOVE =
            BLOCKS.register("giants_stove", () -> new TSDGiantStoveBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F),
                    TSDBlocks::giantsStovePart, 4));
    public static final DeferredBlock<TSDGiantKitchenPartBlock> GIANTS_STOVE_PART =
            BLOCKS.register("giants_stove_part", () -> new TSDGiantKitchenPartBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F),
                    TSDBlocks::giantsStove, TSDGiantKitchenPartBlock.Kind.STOVE));
    public static final DeferredBlock<TSDGiantCookingPotBlock> GIANTS_COOKING_POT =
            BLOCKS.register("giants_cooking_pot", () -> new TSDGiantCookingPotBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F),
                    TSDBlocks::giantsCookingPotPart, 4));
    public static final DeferredBlock<TSDGiantKitchenPartBlock> GIANTS_COOKING_POT_PART =
            BLOCKS.register("giants_cooking_pot_part", () -> new TSDGiantKitchenPartBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F),
                    TSDBlocks::giantsCookingPot, TSDGiantKitchenPartBlock.Kind.COOKING_POT));
    static {
        TSDItems.ITEMS.register("giant_stove", () -> new BlockItem(
                GIANT_STOVE.get(), new Item.Properties().stacksTo(1)));
        TSDItems.ITEMS.register("giant_cooking_pot", () -> new BlockItem(
                GIANT_COOKING_POT.get(), new Item.Properties().stacksTo(1)));
        TSDItems.ITEMS.register("giants_stove", () -> new BlockItem(
                GIANTS_STOVE.get(), new Item.Properties().stacksTo(1)));
        TSDItems.ITEMS.register("giants_cooking_pot", () -> new TSDGiantsCookingPotItem(
                new Item.Properties().stacksTo(1)));
    }

    private static Block giantStove() {
        return GIANT_STOVE.get();
    }

    private static Block giantStovePart() {
        return GIANT_STOVE_PART.get();
    }

    private static Block giantCookingPot() {
        return GIANT_COOKING_POT.get();
    }

    private static Block giantCookingPotPart() {
        return GIANT_COOKING_POT_PART.get();
    }

    private static Block giantsStove() {
        return GIANTS_STOVE.get();
    }

    private static Block giantsStovePart() {
        return GIANTS_STOVE_PART.get();
    }

    private static Block giantsCookingPot() {
        return GIANTS_COOKING_POT.get();
    }

    private static Block giantsCookingPotPart() {
        return GIANTS_COOKING_POT_PART.get();
    }

    private TSDBlocks() {
    }

    private static DeferredBlock<Block> register(String id, MapColor color) {
        DeferredBlock<Block> block = BLOCKS.register(id, () -> new Block(BlockBehaviour.Properties.of().mapColor(color)
                .strength(1.5F).sound(net.minecraft.world.level.block.SoundType.WOOD)));
        TSDItems.ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredBlock<Block> registerFeast(String id, MapColor color, DeferredItem<Item> servingItem,
                                                       int servings, boolean hasLeftovers) {
        DeferredBlock<Block> block = BLOCKS.register(id, () -> new TSDFeastBlock(
                BlockBehaviour.Properties.of().mapColor(color).strength(1.5F),
                servingItem,
                servings,
                hasLeftovers));
        TSDItems.ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredBlock<Block> registerColony(String id, MapColor color, DeferredItem<Item> harvestItem) {
        DeferredBlock<Block> block = BLOCKS.register(id, () -> new TSDColonyBlock(
                BlockBehaviour.Properties.of().mapColor(color),
                harvestItem,
                () -> harvestItem.get()));
        TSDItems.ITEMS.register(id, () -> new TSDColonyItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredBlock<TSDStageFeastBlock> registerStage(String id, MapColor color, String stateName,
                                                                    int maxStage, DeferredItem<Item> servingItem,
                                                                    boolean requiresBowl, int footprint) {
        DeferredBlock<TSDStageFeastBlock> block = BLOCKS.register(id, () -> new TSDStageFeastBlock(
                BlockBehaviour.Properties.of().mapColor(color).strength(1.5F)
                        .sound(net.minecraft.world.level.block.SoundType.WOOL),
                maxStage, servingItem, requiresBowl, footprint));
        TSDItems.ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1)));
        return block;
    }

    private static DeferredBlock<TSDLargeStageFeastBlock> registerLargeStage(
            String id, MapColor color, String stateName, int maxStage,
            DeferredItem<Item> servingItem, boolean requiresBowl, int width, int depth,
            DeferredBlock<TSDStructurePartBlock> part) {
        DeferredBlock<TSDLargeStageFeastBlock> block = BLOCKS.register(id, () -> new TSDLargeStageFeastBlock(
                BlockBehaviour.Properties.of().mapColor(color).strength(1.5F),
                stateName, maxStage, servingItem, requiresBowl, width, depth, part));
        TSDItems.ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1)));
        return block;
    }

    private static TSDLargeStageFeastBlock nagaController() {
        return NAGA_MIXED_RICE.get();
    }

    private static TSDLargeStageFeastBlock boarController() {
        return TWILIGHT_BOAR_KNUCKLE.get();
    }
}
