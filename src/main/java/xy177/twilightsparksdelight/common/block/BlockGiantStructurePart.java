package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockStove;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
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

public class BlockGiantStructurePart extends Block
{
    public enum Kind
    {
        STOVE,
        COOKING_POT
    }

    private static final ThreadLocal<BlockPos> CREATIVE_HARVEST = new ThreadLocal<>();
    private final Block controllerBlock;
    private final Kind kind;

    public BlockGiantStructurePart(Block controllerBlock, Kind kind)
    {
        super(kind == Kind.STOVE ? Material.ROCK : Material.IRON);
        this.controllerBlock = controllerBlock;
        this.kind = kind;
        this.setHardness(2.0F);
        this.setResistance(kind == Kind.STOVE ? 3.5F : 4.0F);
        this.setSoundType(kind == Kind.STOVE ? SoundType.STONE : SoundType.METAL);
    }

    @Override
    public boolean onBlockActivated(
        World world,
        BlockPos pos,
        IBlockState state,
        EntityPlayer player,
        EnumHand hand,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ
    ) {
        BlockPos controllerPos = GiantKitchenStructure.findController(world, pos, this.controllerBlock);
        if (controllerPos == null) {
            return false;
        }
        IBlockState controllerState = world.getBlockState(controllerPos);
        return this.controllerBlock.onBlockActivated(
            world,
            controllerPos,
            controllerState,
            player,
            hand,
            facing,
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
            if (this.kind == Kind.COOKING_POT) {
                BlockPos controllerPos = GiantKitchenStructure.findController(world, pos, this.controllerBlock);
                if (controllerPos != null) {
                    IBlockState controllerState = world.getBlockState(controllerPos);
                    this.controllerBlock.onBlockHarvested(world, controllerPos, controllerState, player);
                }
            }
        }
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote && !GiantKitchenStructure.isRemoving()) {
            BlockPos creativePos = CREATIVE_HARVEST.get();
            boolean creativeHarvest = pos.equals(creativePos);
            if (creativeHarvest) {
                CREATIVE_HARVEST.remove();
            }
            GiantKitchenStructure.destroyControllerFromPart(
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
    public void onEntityWalk(World world, BlockPos pos, Entity entity)
    {
        if (this.kind == Kind.STOVE
            && entity instanceof EntityLivingBase
            && !entity.isImmuneToFire()
            && !EnchantmentHelper.hasFrostWalkerEnchantment((EntityLivingBase) entity)
            && isStoveLit(world, pos)) {
            entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.onEntityWalk(world, pos, entity);
    }

    public boolean isStoveLit(IBlockAccess world, BlockPos pos)
    {
        if (this.kind != Kind.STOVE) {
            return false;
        }
        BlockPos controllerPos = GiantKitchenStructure.findController(world, pos, this.controllerBlock);
        if (controllerPos == null) {
            return false;
        }
        IBlockState controllerState = world.getBlockState(controllerPos);
        return controllerState.getBlock() instanceof BlockStove && controllerState.getValue(BlockStove.LIT);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return isStoveLit(world, pos) ? 12 : 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        BlockPos controllerPos = GiantKitchenStructure.findController(world, pos, this.controllerBlock);
        if (controllerPos == null) {
            return NULL_AABB;
        }
        if (this.kind == Kind.STOVE) {
            return FULL_BLOCK_AABB;
        }
        IBlockState controllerState = world.getBlockState(controllerPos);
        return GiantKitchenStructure.getScaledCookingPotCollision(
            controllerPos,
            pos,
            GiantKitchenStructure.getFacing(controllerState),
            GiantKitchenStructure.getStructureSize(this.controllerBlock)
        );
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        BlockPos controllerPos = GiantKitchenStructure.findController(world, pos, this.controllerBlock);
        if (controllerPos == null) {
            return NULL_AABB;
        }
        if (this.kind == Kind.STOVE) {
            return FULL_BLOCK_AABB;
        }
        IBlockState controllerState = world.getBlockState(controllerPos);
        return GiantKitchenStructure.getScaledCookingPotCollision(
            controllerPos,
            pos,
            GiantKitchenStructure.getFacing(controllerState),
            GiantKitchenStructure.getStructureSize(this.controllerBlock)
        );
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
    public ItemStack getPickBlock(
        IBlockState state,
        RayTraceResult target,
        World world,
        BlockPos pos,
        EntityPlayer player
    ) {
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
        return this.kind == Kind.STOVE;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side)
    {
        return this.kind == Kind.STOVE ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.BLOCK;
    }
}
