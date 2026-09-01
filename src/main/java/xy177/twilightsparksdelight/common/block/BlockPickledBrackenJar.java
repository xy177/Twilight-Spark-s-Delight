package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.item.GlassJarItemBlock;

public class BlockPickledBrackenJar extends Block
{
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 8);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D);

    public BlockPickledBrackenJar()
    {
        super(Material.CAKE);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.GLASS);
        setDefaultState(this.blockState.getBaseState()
            .withProperty(BlockFeast.FACING, EnumFacing.NORTH)
            .withProperty(LEVEL, 0));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return getDefaultState()
            .withProperty(BlockFeast.FACING, placer.getHorizontalFacing().getOpposite())
            .withProperty(LEVEL, 0);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        int level = state.getValue(LEVEL);
        if (level >= 8) {
            if (!world.isRemote) {
                world.setBlockToAir(pos);
                spawnAsEntity(world, pos, GlassJarItemBlock.createEmptyJar());
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        if (!world.isRemote) {
            give(player, new ItemStack(TSDItems.PICKLED_BRACKEN));
            world.setBlockState(pos, state.withProperty(LEVEL, Math.min(8, level + 1)), 3);
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private static void give(EntityPlayer player, ItemStack stack)
    {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    private static ItemStack stack(String id, int count)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation(id));
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(BlockFeast.FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState()
            .withProperty(BlockFeast.FACING, EnumFacing.getHorizontal(meta & 3))
            .withProperty(LEVEL, 0);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {BlockFeast.FACING, LEVEL});
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
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
