package xy177.twilightsparksdelight.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.util.Random;

public class BlockLabyrinthMushroomColony extends BlockBush implements IGrowable
{
    public static final int MAX_AGE = 4;
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, MAX_AGE);
    private static final AxisAlignedBB[] SHAPES = new AxisAlignedBB[] {
        new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D),
        new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D),
        new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.625D, 0.8125D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D),
        new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D)
    };

    public BlockLabyrinthMushroomColony()
    {
        super(Material.PLANTS);
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);
        setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPES[state.getValue(AGE)];
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        Block richSoil = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("farmersdelight", "rich_soil"));
        return richSoil != null && state.getBlock() == richSoil;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        int age = state.getValue(AGE);
        ItemStack held = player.getHeldItem(hand);
        if (age < 1 || held.isEmpty() || held.getItem() != Items.SHEARS) {
            return false;
        }

        if (!world.isRemote) {
            spawnAsEntity(world, pos, new ItemStack(TSDItems.LABYRINTH_MUSHROOM));
            world.setBlockState(pos, state.withProperty(AGE, age - 1), 2);
            if (!player.capabilities.isCreativeMode) {
                held.damageItem(1, player);
            }
        }
        world.playSound(player, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(world, pos, state, random);
        int age = state.getValue(AGE);
        if (age < MAX_AGE && random.nextInt(4) == 0 && canBlockStay(world, pos, state)) {
            world.setBlockState(pos, state.withProperty(AGE, age + 1), 2);
        }
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state)
    {
        int age = Math.min(MAX_AGE, state.getValue(AGE) + 1 + rand.nextInt(2));
        world.setBlockState(pos, state.withProperty(AGE, age), 2);
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient)
    {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(AGE, Math.max(0, Math.min(MAX_AGE, meta)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AGE);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tileEntity, ItemStack stack)
    {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (world.isRemote || player.capabilities.isCreativeMode) {
            return;
        }

        NonNullList<ItemStack> drops = NonNullList.create();
        if (!stack.isEmpty() && stack.getItem() == Items.SHEARS && state.getValue(AGE) == MAX_AGE) {
            drops.add(new ItemStack(Item.getItemFromBlock(this)));
        } else {
            getDrops(drops, world, pos, state, 0);
        }
        for (ItemStack drop : drops) {
            spawnAsEntity(world, pos, drop);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.add(new ItemStack(TSDItems.LABYRINTH_MUSHROOM, state.getValue(AGE) + 1));
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}
