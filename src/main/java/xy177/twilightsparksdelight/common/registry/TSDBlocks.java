package xy177.twilightsparksdelight.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import xy177.twilightsparksdelight.TwilightSparksDelight;
import xy177.twilightsparksdelight.common.block.BlockLabyrinthMushroomColony;
import xy177.twilightsparksdelight.common.block.BlockAbyssPie;
import xy177.twilightsparksdelight.common.block.BlockGiantCookingPot;
import xy177.twilightsparksdelight.common.block.BlockGiantStove;
import xy177.twilightsparksdelight.common.block.BlockGiantsCookingPot;
import xy177.twilightsparksdelight.common.block.BlockGiantsStove;
import xy177.twilightsparksdelight.common.block.BlockGiantStructurePart;
import xy177.twilightsparksdelight.common.block.BlockGloryCrucible;
import xy177.twilightsparksdelight.common.block.BlockGlassJar;
import xy177.twilightsparksdelight.common.block.BlockPickledBrackenJar;
import xy177.twilightsparksdelight.common.block.BlockSaltHelmetCrab;
import xy177.twilightsparksdelight.common.block.BlockTwilightCheeseFondue;
import xy177.twilightsparksdelight.common.block.BlockTwilightBrackenColony;
import xy177.twilightsparksdelight.common.block.BlockTwilightBorscht;
import xy177.twilightsparksdelight.common.block.BlockTwilightBoarKnuckle;
import xy177.twilightsparksdelight.common.block.BlockTwilightBoarKnucklePart;
import xy177.twilightsparksdelight.common.block.BlockNagaMixedRice;
import xy177.twilightsparksdelight.common.block.BlockNagaMixedRicePart;
import xy177.twilightsparksdelight.common.block.BlockUnripePickledBrackenJar;
import xy177.twilightsparksdelight.common.item.ChefTaggedFeastItemBlock;
import xy177.twilightsparksdelight.common.item.GiantCookingPotItemBlock;
import xy177.twilightsparksdelight.common.item.GiantStoveItemBlock;
import xy177.twilightsparksdelight.common.item.GiantsCookingPotItemBlock;
import xy177.twilightsparksdelight.common.item.GlassJarItemBlock;
import xy177.twilightsparksdelight.common.item.LabyrinthMushroomColonyItemBlock;
import xy177.twilightsparksdelight.common.item.TwilightBrackenColonyItemBlock;
import xy177.twilightsparksdelight.client.render.GlassJarItemStackRenderer;
import xy177.twilightsparksdelight.client.render.TileEntityGlassJarRenderer;
import xy177.twilightsparksdelight.client.render.TileEntityGloryCrucibleRenderer;
import xy177.twilightsparksdelight.client.render.TileEntityGiantStoveRenderer;
import xy177.twilightsparksdelight.client.render.TileEntityGiantsKitchenRenderer;
import xy177.twilightsparksdelight.common.tile.TileEntityGloryCrucible;
import xy177.twilightsparksdelight.common.tile.TileEntityGlassJar;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantStove;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantsStove;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantsCookingPot;
import xy177.twilightsparksdelight.common.tile.TileEntityNagaMixedRice;
import xy177.twilightsparksdelight.common.tile.TileEntityTwilightBoarKnuckle;
import xy177.twilightsparksdelight.client.render.TileEntityTwilightBoarKnuckleRenderer;
import xy177.twilightsparksdelight.client.render.TileEntityNagaMixedRiceRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = TwilightSparksDelight.MODID)
public final class TSDBlocks
{
    private static final List<Block> BLOCKS = new ArrayList<>();
    private static final List<Item> BLOCK_ITEMS = new ArrayList<>();

    public static final Block TWILIGHT_CHEESE_FONDUE = register("twilight_cheese_fondue", new BlockTwilightCheeseFondue(), TSDBlocks::singleStackItemBlock);
    public static final Block SALT_HELMET_CRAB = register("salt_helmet_crab", new BlockSaltHelmetCrab(), ChefTaggedFeastItemBlock::new);
    public static final Block LABYRINTH_MUSHROOM_COLONY = register("labyrinth_mushroom_colony", new BlockLabyrinthMushroomColony(), LabyrinthMushroomColonyItemBlock::new);
    public static final Block TWILIGHT_BRACKEN_COLONY = register("twilight_bracken_colony", new BlockTwilightBrackenColony(), TwilightBrackenColonyItemBlock::new);
    public static final Block UNRIPE_PICKLED_BRACKEN_JAR = register("unripe_pickled_bracken_jar", new BlockUnripePickledBrackenJar(), TSDBlocks::singleStackItemBlock);
    public static final Block PICKLED_BRACKEN_JAR = register("pickled_bracken_jar", new BlockPickledBrackenJar(), TSDBlocks::singleStackItemBlock);
    public static final Block GLASS_JAR = register("glass_jar", new BlockGlassJar(), GlassJarItemBlock::new);
    public static final Block GLORY_CRUCIBLE = register("glory_crucible", new BlockGloryCrucible(), ItemBlock::new);
    public static final Block TWILIGHT_BORSCHT = register("twilight_borscht", new BlockTwilightBorscht(), TSDBlocks::singleStackItemBlock);
    public static final Block LABYRINTH_MUSHROOM_CRATE = register("labyrinth_mushroom_crate", createCrateBlock(), ItemBlock::new);
    public static final Block BRACKEN_CRATE = register("bracken_crate", createCrateBlock(), ItemBlock::new);
    public static final BlockNagaMixedRice NAGA_MIXED_RICE = register(
        "naga_mixed_rice",
        new BlockNagaMixedRice(),
        xy177.twilightsparksdelight.common.item.NagaMixedRiceItemBlock::new
    );
    public static final BlockNagaMixedRicePart NAGA_MIXED_RICE_PART = registerBlockOnly(
        "naga_mixed_rice_part",
        new BlockNagaMixedRicePart(NAGA_MIXED_RICE)
    );
    public static final Block ABYSS_PIE = register("abyss_pie", new BlockAbyssPie(), TSDBlocks::singleStackItemBlock);
    public static final BlockTwilightBoarKnuckle TWILIGHT_BOAR_KNUCKLE = register(
        "twilight_boar_knuckle",
        new BlockTwilightBoarKnuckle(),
        xy177.twilightsparksdelight.common.item.TwilightBoarKnuckleItemBlock::new
    );
    public static final BlockTwilightBoarKnucklePart TWILIGHT_BOAR_KNUCKLE_PART = registerBlockOnly(
        "twilight_boar_knuckle_part",
        new BlockTwilightBoarKnucklePart(TWILIGHT_BOAR_KNUCKLE)
    );
    public static final BlockGiantStove GIANT_STOVE = register("giant_stove", new BlockGiantStove(), GiantStoveItemBlock::new);
    public static final BlockGiantStructurePart GIANT_STOVE_PART = registerBlockOnly(
        "giant_stove_part",
        new BlockGiantStructurePart(GIANT_STOVE, BlockGiantStructurePart.Kind.STOVE)
    );
    public static final BlockGiantCookingPot GIANT_COOKING_POT = register(
        "giant_cooking_pot",
        new BlockGiantCookingPot(),
        GiantCookingPotItemBlock::new
    );
    public static final BlockGiantStructurePart GIANT_COOKING_POT_PART = registerBlockOnly(
        "giant_cooking_pot_part",
        new BlockGiantStructurePart(GIANT_COOKING_POT, BlockGiantStructurePart.Kind.COOKING_POT)
    );
    public static final BlockGiantsStove GIANTS_STOVE = register("giants_stove", new BlockGiantsStove(), GiantStoveItemBlock::new);
    public static final BlockGiantStructurePart GIANTS_STOVE_PART = registerBlockOnly(
        "giants_stove_part",
        new BlockGiantStructurePart(GIANTS_STOVE, BlockGiantStructurePart.Kind.STOVE)
    );
    public static final BlockGiantsCookingPot GIANTS_COOKING_POT = register(
        "giants_cooking_pot",
        new BlockGiantsCookingPot(),
        GiantsCookingPotItemBlock::new
    );
    public static final BlockGiantStructurePart GIANTS_COOKING_POT_PART = registerBlockOnly(
        "giants_cooking_pot_part",
        new BlockGiantStructurePart(GIANTS_COOKING_POT, BlockGiantStructurePart.Kind.COOKING_POT)
    );

    static {
        GIANT_STOVE.setStructurePartBlock(GIANT_STOVE_PART);
        GIANT_COOKING_POT.setStructurePartBlock(GIANT_COOKING_POT_PART);
        GIANTS_STOVE.setStructurePartBlock(GIANTS_STOVE_PART);
        GIANTS_COOKING_POT.setStructurePartBlock(GIANTS_COOKING_POT_PART);
        NAGA_MIXED_RICE.setStructurePartBlock(NAGA_MIXED_RICE_PART);
        TWILIGHT_BOAR_KNUCKLE.setStructurePartBlock(TWILIGHT_BOAR_KNUCKLE_PART);
    }

    private TSDBlocks()
    {
    }

    public static void register(IForgeRegistry<Block> registry)
    {
        for (Block block : BLOCKS) {
            registry.register(block);
        }
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry)
    {
        for (Item item : BLOCK_ITEMS) {
            registry.register(item);
        }
    }

    private static <T extends Block> T register(String name, T block, Function<Block, Item> itemFactory)
    {
        block.setRegistryName(new ResourceLocation(TwilightSparksDelight.MODID, name));
        block.setUnlocalizedName(TwilightSparksDelight.MODID + "." + name);
        block.setCreativeTab(TwilightSparksDelight.CREATIVE_TAB);
        BLOCKS.add(block);

        Item item = itemFactory.apply(block);
        item.setRegistryName(block.getRegistryName());
        item.setUnlocalizedName(block.getUnlocalizedName());
        item.setCreativeTab(TwilightSparksDelight.CREATIVE_TAB);
        BLOCK_ITEMS.add(item);
        return block;
    }

    private static <T extends Block> T registerBlockOnly(String name, T block)
    {
        block.setRegistryName(new ResourceLocation(TwilightSparksDelight.MODID, name));
        block.setUnlocalizedName(TwilightSparksDelight.MODID + "." + name);
        BLOCKS.add(block);
        return block;
    }

    private static Item singleStackItemBlock(Block block)
    {
        ItemBlock item = new ItemBlock(block);
        item.setMaxStackSize(1);
        return item;
    }

    private static Block createCrateBlock()
    {
        return new StorageCrateBlock();
    }

    private static final class StorageCrateBlock extends Block
    {
        private StorageCrateBlock()
        {
            super(Material.WOOD);
            setHardness(2.0F);
            setResistance(5.0F);
            setSoundType(SoundType.WOOD);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGlassJar.class, new TileEntityGlassJarRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGloryCrucible.class, new TileEntityGloryCrucibleRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGiantStove.class, new TileEntityGiantStoveRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGiantsStove.class, new TileEntityGiantsKitchenRenderer<>(true));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGiantsCookingPot.class, new TileEntityGiantsKitchenRenderer<>(false));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNagaMixedRice.class, new TileEntityNagaMixedRiceRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTwilightBoarKnuckle.class, new TileEntityTwilightBoarKnuckleRenderer());
        for (Item item : BLOCK_ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TWILIGHT_CHEESE_FONDUE), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":twilight_cheese_fondue", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SALT_HELMET_CRAB), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":salt_helmet_crab_item", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(LABYRINTH_MUSHROOM_COLONY), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":labyrinth_mushroom_colony_item", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TWILIGHT_BRACKEN_COLONY), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":twilight_bracken_colony_item", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TWILIGHT_BORSCHT), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":twilight_borscht", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(UNRIPE_PICKLED_BRACKEN_JAR), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":unripe_pickled_bracken_jar", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(PICKLED_BRACKEN_JAR), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":pickled_bracken_jar", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(GLASS_JAR), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":glass_jar", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(GLASS_JAR), GlassJarItemBlock.FILLED_META,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":glass_jar", "inventory"));
        Item.getItemFromBlock(GLASS_JAR).setTileEntityItemStackRenderer(new GlassJarItemStackRenderer());
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ABYSS_PIE), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":abyss_pie", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TWILIGHT_BOAR_KNUCKLE), 0,
            new ModelResourceLocation(TwilightSparksDelight.MODID + ":twilight_boar_knuckle_item", "inventory"));
    }
}
