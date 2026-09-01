package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import twilightforest.TFFeature;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.registry.TSDBlocks;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.tile.TileEntityUnripePickledBrackenJar;

import java.util.Random;

public class BlockUnripePickledBrackenJar extends Block implements ITileEntityProvider
{
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 8);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D);

    public BlockUnripePickledBrackenJar()
    {
        super(Material.CAKE);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.GLASS);
        setTickRandomly(true);
        setDefaultState(this.blockState.getBaseState()
            .withProperty(BlockFeast.FACING, EnumFacing.NORTH)
            .withProperty(LEVEL, 0));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityUnripePickledBrackenJar();
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
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && held.getItem() == TSDItems.RABBIT_POCKET_WATCH) {
            if (!world.isRemote) {
                ripen(world, pos, state);
            }
            return true;
        }
        if (!world.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.block.unripe_pickled_bracken_jar.not_ready"), true);
        }
        return true;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (world.isRemote) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityUnripePickledBrackenJar)) {
            return;
        }
        TileEntityUnripePickledBrackenJar jar = (TileEntityUnripePickledBrackenJar) tile;
        if (jar.getProgress() >= 5) {
            ripen(world, pos, state);
            return;
        }
        int gained = rollProgress(world, pos, random, getRipeningChance(world, pos, jar));
        if (gained <= 0) {
            return;
        }
        jar.setProgress(jar.getProgress() + gained);
        world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (jar.getProgress() >= 5) {
            ripen(world, pos, state);
        }
    }

    private int rollProgress(World world, BlockPos pos, Random random, double chance)
    {
        int progress = 0;
        while (chance > 0.0D) {
            double roll = Math.min(1.0D, chance);
            if (random.nextDouble() > roll) {
                break;
            }
            progress++;
            chance -= 1.0D;
        }
        return progress;
    }

    private double getRipeningChance(World world, BlockPos pos, TileEntityUnripePickledBrackenJar jar)
    {
        double chance = TSDConfig.pickledBrackenJarBaseRipeningChance;
        BlockPos shade = findShadeBlock(world, pos);
        if (shade != null) {
            chance += isLeaves(world, shade) ? 0.30D : 0.20D;
            if (isCleanShadeArea(world, shade)) {
                chance += 0.15D;
            }
        }
        if (hasNearbyBlock(world, pos, "twilightforest:magic_log_core", 0)) {
            chance += 0.50D;
        }
        if (hasNearbyBlock(world, pos, TSDBlocks.LABYRINTH_MUSHROOM_COLONY)) {
            chance += 0.10D;
        }
        if (isInAcceleratingFeature(world, pos)) {
            chance += 0.20D;
        }
        if (jar.wasPeacockFanUsed()) {
            chance += 0.30D;
        }
        return chance;
    }

    private boolean isInAcceleratingFeature(World world, BlockPos pos)
    {
        TFFeature feature = TFFeature.getFeatureAt(pos.getX(), pos.getZ(), world);
        return feature == TFFeature.SMALL_HILL
            || feature == TFFeature.MEDIUM_HILL
            || feature == TFFeature.LARGE_HILL
            || feature == TFFeature.MUSHROOM_TOWER
            || feature == TFFeature.LABYRINTH
            || feature == TFFeature.KNIGHT_STRONGHOLD
            || feature == TFFeature.QUEST_GROVE;
    }

    private BlockPos findShadeBlock(World world, BlockPos pos)
    {
        for (int y = 1; y <= 5; y++) {
            BlockPos check = pos.up(y);
            if (!world.isAirBlock(check)) {
                return check;
            }
        }
        return null;
    }

    private boolean isCleanShadeArea(World world, BlockPos center)
    {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos check = center.add(dx, 0, dz);
                if (world.isAirBlock(check)) {
                    continue;
                }
                if (!isAllowedNearbyCatalyst(world, check)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAllowedNearbyCatalyst(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == TSDBlocks.UNRIPE_PICKLED_BRACKEN_JAR || block == TSDBlocks.PICKLED_BRACKEN_JAR || block == TSDBlocks.LABYRINTH_MUSHROOM_COLONY) {
            return true;
        }
        ResourceLocation id = block.getRegistryName();
        int meta = block.getMetaFromState(state);
        return id != null
            && (matches(id, meta, "twilightforest:magic_log_core", 0)
            || matches(id, meta, "twilightforest:twilight_sapling", 5)
            || matches(id, meta, "twilightforest:magic_log", 0)
            || matches(id, meta, "twilightforest:magic_leaves", 0));
    }

    private boolean hasNearbyBlock(World world, BlockPos pos, String id, int meta)
    {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos check = pos.add(dx, 0, dz);
                IBlockState state = world.getBlockState(check);
                ResourceLocation found = state.getBlock().getRegistryName();
                if (found != null && matches(found, state.getBlock().getMetaFromState(state), id, meta)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasNearbyBlock(World world, BlockPos pos, Block block)
    {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (world.getBlockState(pos.add(dx, 0, dz)).getBlock() == block) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLeaves(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getBlock().isLeaves(world.getBlockState(pos), world, pos);
    }

    private static boolean matches(ResourceLocation found, int foundMeta, String id, int meta)
    {
        return id.equals(found.toString()) && foundMeta == meta;
    }

    private void ripen(World world, BlockPos pos, IBlockState state)
    {
        world.setBlockState(pos, TSDBlocks.PICKLED_BRACKEN_JAR.getDefaultState()
            .withProperty(BlockFeast.FACING, state.getValue(BlockFeast.FACING))
            .withProperty(BlockPickledBrackenJar.LEVEL, 0), 3);
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
