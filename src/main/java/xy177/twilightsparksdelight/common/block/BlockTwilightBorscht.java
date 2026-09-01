package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
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
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.tile.TileEntityTwilightBorscht;

public class BlockTwilightBorscht extends Block implements ITileEntityProvider
{
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 5;
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", MIN_LEVEL, MAX_LEVEL);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockTwilightBorscht()
    {
        super(Material.CAKE);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState()
            .withProperty(BlockFeast.FACING, EnumFacing.NORTH)
            .withProperty(LEVEL, MIN_LEVEL));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityTwilightBorscht();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return getDefaultState().withProperty(BlockFeast.FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightBorscht) {
            ((TileEntityTwilightBorscht) tile).initializeFromBlockDefault(MIN_LEVEL);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
        EnumFacing side, float hitX, float hitY, float hitZ)
    {
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
        int level = getLevel(world, pos, state);
        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                held.shrink(cupServing ? 2 : 1);
            }
            giveOrReplace(player, hand, new ItemStack(
                cupServing ? TSDItems.TWILIGHT_BORSCHT_CUP : TSDItems.BOWL_OF_TWILIGHT_BORSCHT,
                cupServing ? 2 : 1
            ));
            if (level >= MAX_LEVEL) {
                world.setBlockState(pos, TSDBlocks.GLORY_CRUCIBLE.getDefaultState(), 3);
            } else {
                setLevel(world, pos, level + 1);
            }
            world.playSound(null, pos, ModSounds.foodTakePortion, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private static Item getCopperCup()
    {
        return Loader.isModLoaded("miners_delight_bridge")
            ? ForgeRegistries.ITEMS.getValue(new ResourceLocation("miners_delight", "copper_cup"))
            : null;
    }

    private int getLevel(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightBorscht) {
            return clampLevel(((TileEntityTwilightBorscht) tile).getServings());
        }
        return clampLevel(state.getValue(LEVEL));
    }

    private void setLevel(World world, BlockPos pos, int level)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return;
        }
        int clamped = clampLevel(level);
        world.setBlockState(pos, state.withProperty(LEVEL, clamped), 3);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightBorscht) {
            ((TileEntityTwilightBorscht) tile).setServings(clamped);
        }
    }

    public static int clampLevel(int level)
    {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
    }

    private static void giveOrReplace(EntityPlayer player, EnumHand hand, ItemStack result)
    {
        if (player.getHeldItem(hand).isEmpty()) {
            player.setHeldItem(hand, result);
        } else if (!player.inventory.addItemStackToInventory(result)) {
            player.dropItem(result, false);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(BlockFeast.FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(BlockFeast.FACING, EnumFacing.getHorizontal(meta & 3));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightBorscht) {
            return state.withProperty(LEVEL, clampLevel(((TileEntityTwilightBorscht) tile).getServings()));
        }
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {BlockFeast.FACING, LEVEL});
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        if (getLevel(world, pos, state) == MIN_LEVEL) {
            drops.add(new ItemStack(Item.getItemFromBlock(this)));
        } else {
            drops.add(new ItemStack(TSDBlocks.GLORY_CRUCIBLE));
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
    {
        return getLevel(world, pos, state);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
