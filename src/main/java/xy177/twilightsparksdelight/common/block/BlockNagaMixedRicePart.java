package xy177.twilightsparksdelight.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockNagaMixedRicePart extends Block
{
    private static final ThreadLocal<BlockPos> CREATIVE_HARVEST = new ThreadLocal<>();
    private final Block controllerBlock;

    public BlockNagaMixedRicePart(Block controllerBlock)
    {
        super(Material.CAKE);
        this.controllerBlock = controllerBlock;
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.WOOD);
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
        BlockPos controllerPos = NagaMixedRiceStructure.findController(world, pos, this.controllerBlock);
        if (controllerPos == null) {
            return false;
        }
        return this.controllerBlock.onBlockActivated(
            world,
            controllerPos,
            world.getBlockState(controllerPos),
            player,
            hand,
            side,
            hitX,
            hitY,
            hitZ
        );
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (!world.isRemote && player.capabilities.isCreativeMode) {
            CREATIVE_HARVEST.set(pos.toImmutable());
        }
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote && !NagaMixedRiceStructure.isRemoving()) {
            BlockPos creativePos = CREATIVE_HARVEST.get();
            boolean creativeHarvest = pos.equals(creativePos);
            if (creativeHarvest) {
                CREATIVE_HARVEST.remove();
            }
            NagaMixedRiceStructure.destroyControllerFromPart(
                world,
                pos,
                this.controllerBlock,
                this,
                creativeHarvest
            );
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return NagaMixedRiceStructure.findController(world, pos, this.controllerBlock) == null
            ? NULL_AABB
            : FULL_BLOCK_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return getBoundingBox(state, world, pos);
    }

    @Override
    public RayTraceResult collisionRayTrace(
        IBlockState state,
        World world,
        BlockPos pos,
        Vec3d start,
        Vec3d end
    ) {
        AxisAlignedBB boundingBox = getBoundingBox(state, world, pos);
        return boundingBox == NULL_AABB ? null : rayTrace(pos, start, end, boundingBox);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(this.controllerBlock);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
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
        return true;
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side)
    {
        return BlockFaceShape.UNDEFINED;
    }
}

