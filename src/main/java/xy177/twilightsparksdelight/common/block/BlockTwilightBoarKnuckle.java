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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.tile.TileEntityTwilightBoarKnuckle;

public class BlockTwilightBoarKnuckle extends Block implements ITileEntityProvider
{
    public static final int MIN_STAGE = 0;
    public static final int MAX_STAGE = 6;
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", MIN_STAGE, MAX_STAGE);
    public static final net.minecraft.block.properties.PropertyDirection FACING = BlockFeast.FACING;
    private static final double MODEL_WIDTH = 15.0D / 16.0D;
    private static final double MODEL_HEIGHT = 9.0D / 16.0D;
    private static final double MODEL_LENGTH_MARGIN = 1.0D / 16.0D;
    private Block structurePartBlock;

    public BlockTwilightBoarKnuckle()
    {
        super(Material.CAKE);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(STAGE, MIN_STAGE));
    }

    public void setStructurePartBlock(Block structurePartBlock)
    {
        this.structurePartBlock = structurePartBlock;
    }

    public Block getStructurePartBlock()
    {
        return this.structurePartBlock;
    }

    public static AxisAlignedBB getModelCellBoundingBox(EnumFacing facing, boolean leftCell)
    {
        double minX = 0.0D;
        double maxX = 1.0D;
        double minZ = 0.0D;
        double maxZ = 1.0D;

        if (facing.getAxis() == EnumFacing.Axis.X) {
            if (facing.getFrontOffsetX() < 0) {
                minX = 1.0D - MODEL_WIDTH;
                maxX = 1.0D;
            } else {
                minX = 0.0D;
                maxX = MODEL_WIDTH;
            }
        } else {
            if (facing.getFrontOffsetZ() < 0) {
                minZ = 1.0D - MODEL_WIDTH;
                maxZ = 1.0D;
            } else {
                minZ = 0.0D;
                maxZ = MODEL_WIDTH;
            }
        }

        EnumFacing right = facing.rotateYCCW();
        if (leftCell) {
            if (right.getFrontOffsetX() > 0) {
                minX = Math.max(minX, MODEL_LENGTH_MARGIN);
            } else if (right.getFrontOffsetX() < 0) {
                maxX = Math.min(maxX, 1.0D - MODEL_LENGTH_MARGIN);
            }
            if (right.getFrontOffsetZ() > 0) {
                minZ = Math.max(minZ, MODEL_LENGTH_MARGIN);
            } else if (right.getFrontOffsetZ() < 0) {
                maxZ = Math.min(maxZ, 1.0D - MODEL_LENGTH_MARGIN);
            }
        }
        return new AxisAlignedBB(minX, 0.0D, minZ, maxX, MODEL_HEIGHT, maxZ);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityTwilightBoarKnuckle();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightBoarKnuckle) {
            ((TileEntityTwilightBoarKnuckle) tile).initializeFromBlockDefault(MIN_STAGE);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
        EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() || held.getItem() != Items.BOWL) {
            return false;
        }
        int stage = getStage(world, pos, state);
        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            giveOrReplace(player, hand, new ItemStack(TSDItems.PLATE_OF_TWILIGHT_BOAR_KNUCKLE));
            if (stage >= MAX_STAGE - 1) {
                spawnAsEntity(world, pos, new ItemStack(Items.BOWL));
                spawnAsEntity(world, pos, new ItemStack(Items.DYE, 7, 15));
                TwilightBoarKnuckleStructure.removePartsFromController(world, pos, state, this.structurePartBlock);
                world.setBlockToAir(pos);
            } else {
                setStage(world, pos, stage + 1);
            }
            world.playSound(null, pos, ModSounds.foodTakePortion, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private int getStage(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightBoarKnuckle) {
            return clampStage(((TileEntityTwilightBoarKnuckle) tile).getServings());
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
        if (tile instanceof TileEntityTwilightBoarKnuckle) {
            ((TileEntityTwilightBoarKnuckle) tile).setServings(clamped);
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

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote && !TwilightBoarKnuckleStructure.isRemoving()) {
            TwilightBoarKnuckleStructure.removePartsFromController(world, pos, state, this.structurePartBlock);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
        int fortune)
    {
        if (getStage(world, pos, state) == MIN_STAGE) {
            drops.add(new ItemStack(Item.getItemFromBlock(this)));
            return;
        }
        drops.add(new ItemStack(Items.BOWL));
        drops.add(new ItemStack(Items.DYE, 7, 15));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return getModelCellBoundingBox(state.getValue(FACING), false);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return getModelCellBoundingBox(state.getValue(FACING), false);
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
        return tile instanceof TileEntityTwilightBoarKnuckle
            ? state.withProperty(STAGE, clampStage(((TileEntityTwilightBoarKnuckle) tile).getServings()))
            : state;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
