package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import twilightforest.TFSounds;
import twilightforest.enums.BossVariant;
import twilightforest.item.TFItems;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.tile.TileEntityNagaMixedRice;

public class BlockNagaMixedRice extends Block implements ITileEntityProvider
{
    public static final int MIN_STAGE = 0;
    public static final int MAX_STAGE = 28;
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", MIN_STAGE, MAX_STAGE);
    public static final net.minecraft.block.properties.PropertyDirection FACING = BlockFeast.FACING;
    private static final AxisAlignedBB SHAPE = FULL_BLOCK_AABB;
    private Block structurePartBlock;

    public BlockNagaMixedRice()
    {
        super(Material.CAKE);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(STAGE, MIN_STAGE));
    }

    public void setStructurePartBlock(Block structurePartBlock)
    {
        this.structurePartBlock = structurePartBlock;
    }

    public Block getStructurePartBlock()
    {
        return this.structurePartBlock;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityNagaMixedRice();
    }

    @Override
    public IBlockState getStateForPlacement(
        World world,
        BlockPos pos,
        EnumFacing side,
        float hitX,
        float hitY,
        float hitZ,
        int meta,
        EntityLivingBase placer,
        EnumHand hand
    ) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityNagaMixedRice) {
            ((TileEntityNagaMixedRice) tile).initializeFromBlockDefault(MIN_STAGE);
        }
    }

    @Override
    public boolean onBlockActivated(
        World world,
        BlockPos pos,
        IBlockState state,
        EntityPlayer player,
        EnumHand hand,
        EnumFacing side,
        float hitX,
        float hitY,
        float hitZ
    ) {
        int stage = getStage(world, pos, state);
        if (stage >= MAX_STAGE) {
            if (!world.isRemote) {
                spawnAsEntity(world, pos, new ItemStack(Items.SHIELD));
                spawnAsEntity(world, pos, new ItemStack(TFItems.trophy, 1, BossVariant.NAGA.ordinal()));
                world.playSound(null, pos, TFSounds.NAGA_HURT, SoundCategory.BLOCKS, 1.0F, 0.8F);
                world.setBlockToAir(pos);
            }
            return true;
        }

        if (stage % 4 == 3) {
            if (!world.isRemote) {
                give(player, createScaleServing());
                setStage(world, pos, stage + 1);
                world.playSound(null, pos, TFSounds.NAGA_HURT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }

        ItemStack held = player.getHeldItem(hand);
        Item copperCup = getCopperCup();
        boolean bowlServing = !held.isEmpty() && held.getItem() == Items.BOWL;
        boolean cupServing = copperCup != null && !held.isEmpty() && held.getItem() == copperCup;
        if (!bowlServing && !cupServing) {
            return false;
        }
        if (cupServing && held.getCount() < 2) {
            return true;
        }
        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                held.shrink(cupServing ? 2 : 1);
            }
            Item servingItem = cupServing ? TSDItems.NAGA_MIXED_RICE_CUP : TSDItems.BOWL_OF_NAGA_MIXED_RICE;
            giveOrReplace(player, hand, createServing(servingItem, getIngredient(world, pos), cupServing ? 2 : 1));
            setStage(world, pos, stage + 1);
            world.playSound(null, pos, ModSounds.foodTakePortion, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private static ItemStack createScaleServing()
    {
        if (Loader.isModLoaded("twilightdelight")) {
            Item nagaChip = ForgeRegistries.ITEMS.getValue(new ResourceLocation("twilightdelight", "naga_chip"));
            if (nagaChip != null) {
                return new ItemStack(nagaChip, 4);
            }
        }
        return new ItemStack(TFItems.naga_scale);
    }

    private static Item getCopperCup()
    {
        return Loader.isModLoaded("miners_delight_bridge")
            ? ForgeRegistries.ITEMS.getValue(new ResourceLocation("miners_delight", "copper_cup"))
            : null;
    }

    private static ItemStack createServing(Item item, String ingredient, int count)
    {
        ItemStack stack = new ItemStack(item, count);
        if (ingredient != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG, ingredient);
            stack.setTagCompound(tag);
        }
        return stack;
    }

    private static String getIngredient(IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityNagaMixedRice
            ? ((TileEntityNagaMixedRice) tile).getIngredientType()
            : null;
    }

    private int getStage(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityNagaMixedRice) {
            return clampStage(((TileEntityNagaMixedRice) tile).getStage());
        }
        return clampStage(state.getValue(STAGE));
    }

    private void setStage(World world, BlockPos pos, int stage)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return;
        }
        int clamped = clampStage(stage);
        world.setBlockState(pos, state.withProperty(STAGE, clamped), 3);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityNagaMixedRice) {
            ((TileEntityNagaMixedRice) tile).setStage(clamped);
        }
    }

    public static int clampStage(int stage)
    {
        return Math.max(MIN_STAGE, Math.min(MAX_STAGE, stage));
    }

    private static void giveOrReplace(EntityPlayer player, EnumHand hand, ItemStack result)
    {
        if (player.getHeldItem(hand).isEmpty()) {
            player.setHeldItem(hand, result);
        } else if (!player.inventory.addItemStackToInventory(result)) {
            player.dropItem(result, false);
        }
    }

    private static void give(EntityPlayer player, ItemStack result)
    {
        if (!player.inventory.addItemStackToInventory(result)) {
            player.dropItem(result, false);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote && !NagaMixedRiceStructure.isRemoving()) {
            NagaMixedRiceStructure.removePartsFromController(world, pos, state, this.structurePartBlock);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        if (getStage(world, pos, state) > MIN_STAGE) {
            addFinalDrops(drops);
            return;
        }
        ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
        String ingredient = getIngredient(world, pos);
        if (ingredient != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG, ingredient);
            stack.setTagCompound(tag);
        }
        drops.add(stack);
    }

    private static void addFinalDrops(NonNullList<ItemStack> drops)
    {
        drops.add(new ItemStack(Items.SHIELD));
        drops.add(new ItemStack(TFItems.trophy, 1, BossVariant.NAGA.ordinal()));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, STAGE});
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityNagaMixedRice
            ? state.withProperty(STAGE, clampStage(((TileEntityNagaMixedRice) tile).getStage()))
            : state;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}

