package xy177.twilightsparksdelight.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xy177.twilightsparksdelight.common.tile.TileEntityGloryCrucible;
import xy177.twilightsparksdelight.common.registry.TSDFluids;
import xy177.twilightsparksdelight.common.util.GloryCrucibleCapacity;
import xy177.twilightsparksdelight.integration.fruitsdelight.FruitsDelightCauldronCompat;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockGloryCrucible extends Block implements ITileEntityProvider
{
    private static final ResourceLocation FIERY_BLOOD_ITEM = new ResourceLocation("twilightforest", "fiery_blood");
    private static final ResourceLocation FIERY_TEARS_ITEM = new ResourceLocation("twilightforest", "fiery_tears");
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 6);
    private static final AxisAlignedBB AABB_LEGS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    private static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

    public BlockGloryCrucible()
    {
        super(Material.IRON, MapColor.IRON);
        setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setDefaultState(blockState.getBaseState().withProperty(LEVEL, 0));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityGloryCrucible();
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
        List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_LEGS);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
        EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityGloryCrucible)) {
            return false;
        }
        TileEntityGloryCrucible crucible = (TileEntityGloryCrucible) tile;
        ItemStack held = player.getHeldItem(hand);

        if (held.isEmpty()) {
            return true;
        }
        if (held.getItem() == Items.WATER_BUCKET) {
            return handleWaterBucket(world, pos, player, hand, crucible);
        }
        if (held.getItem() == Items.BUCKET && crucible.canUseAsWaterCauldron()) {
            return handleEmptyWaterBucket(world, pos, player, hand, crucible);
        }
        if (FruitsDelightCauldronCompat.handlePlayerInteraction(world, pos, player, hand, crucible)) {
            return true;
        }

        if (crucible.canPourPotion(held)) {
            if (!world.isRemote && crucible.pourPotion(held)) {
                if (!player.capabilities.isCreativeMode) {
                    replaceOneHeldItem(player, hand, new ItemStack(Items.GLASS_BOTTLE));
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                player.addStat(StatList.CAULDRON_USED);
            }
            return true;
        }
        if (held.getItem() == Items.GLASS_BOTTLE && crucible.canBottlePotion()) {
            if (!world.isRemote) {
                ItemStack potion = crucible.bottlePotion();
                if (!potion.isEmpty()) {
                    if (!player.capabilities.isCreativeMode) {
                        replaceOneHeldItem(player, hand, potion);
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    player.addStat(StatList.CAULDRON_USED);
                }
            }
            return true;
        }
        Fluid bottledHeatingFluid = getBottledHeatingFluid(held);
        if (bottledHeatingFluid != null) {
            if (!world.isRemote && crucible.addFluidPortions(new FluidStack(bottledHeatingFluid, 1), 1)) {
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                player.addStat(StatList.CAULDRON_USED);
            }
            return true;
        }
        if (!held.isEmpty() && FluidUtil.getFluidHandler(held.copy()) != null) {
            if (!world.isRemote) {
                interactWithFluidHandler(player, hand, crucible);
            }
            return true;
        }
        if (crucible.canUseAsWaterCauldron()) {
            return handleWaterCleaning(world, pos, player, hand, crucible);
        }
        return false;
    }

    private static boolean handleWaterBucket(World world, BlockPos pos, EntityPlayer player, EnumHand hand,
        TileEntityGloryCrucible crucible)
    {
        if (!world.isRemote && crucible.addFluidPortions(
            new FluidStack(FluidRegistry.WATER, GloryCrucibleCapacity.BUCKET_MB),
            TileEntityGloryCrucible.LEVELS_PER_BUCKET
        )) {
            if (!player.capabilities.isCreativeMode) {
                player.setHeldItem(hand, new ItemStack(Items.BUCKET));
            }
            player.addStat(StatList.CAULDRON_FILLED);
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private static boolean handleEmptyWaterBucket(World world, BlockPos pos, EntityPlayer player, EnumHand hand,
        TileEntityGloryCrucible crucible)
    {
        if (!world.isRemote && crucible.drainWaterBucket()) {
            if (!player.capabilities.isCreativeMode) {
                replaceOneHeldItem(player, hand, new ItemStack(Items.WATER_BUCKET));
            }
            player.addStat(StatList.CAULDRON_USED);
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private static boolean interactWithFluidHandler(EntityPlayer player, EnumHand hand,
        TileEntityGloryCrucible crucible)
    {
        ItemStack held = player.getHeldItem(hand);
        IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory == null) {
            return false;
        }
        FluidStack contained = FluidUtil.getFluidContained(held.copy());
        FluidActionResult result = contained == null
            ? FluidUtil.tryFillContainerAndStow(held, crucible, inventory, Integer.MAX_VALUE, player, true)
            : FluidUtil.tryEmptyContainerAndStow(held, crucible, inventory, Integer.MAX_VALUE, player, true);
        if (!result.isSuccess()) {
            return false;
        }
        player.setHeldItem(hand, result.getResult());
        player.addStat(contained == null ? StatList.CAULDRON_USED : StatList.CAULDRON_FILLED);
        return true;
    }

    @Nullable
    private static Fluid getBottledHeatingFluid(ItemStack stack)
    {
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) {
            return null;
        }
        ResourceLocation itemId = stack.getItem().getRegistryName();
        if (FIERY_BLOOD_ITEM.equals(itemId)) {
            return TSDFluids.FIERY_BLOOD;
        }
        return FIERY_TEARS_ITEM.equals(itemId) ? TSDFluids.FIERY_TEARS : null;
    }

    private static boolean handleWaterCleaning(World world, BlockPos pos, EntityPlayer player, EnumHand hand,
        TileEntityGloryCrucible crucible)
    {
        ItemStack held = player.getHeldItem(hand);
        Item item = held.getItem();
        if (item instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) item;
            if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && armor.hasColor(held)) {
                if (!world.isRemote) {
                    armor.removeColor(held);
                    crucible.consumeBottleUnit();
                    player.addStat(StatList.ARMOR_CLEANED);
                }
                return true;
            }
        }
        if (item instanceof ItemBanner && TileEntityBanner.getPatterns(held) > 0) {
            if (!world.isRemote) {
                ItemStack cleaned = held.copy();
                cleaned.setCount(1);
                TileEntityBanner.removeBannerData(cleaned);
                player.addStat(StatList.BANNER_CLEANED);
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                    crucible.consumeBottleUnit();
                }
                giveOrReplace(player, hand, cleaned);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        if (world.isRemote || !entity.isBurning()) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityGloryCrucible) {
            TileEntityGloryCrucible crucible = (TileEntityGloryCrucible) tile;
            if (crucible.canUseAsWaterCauldron()
                && entity.getEntityBoundingBox().minY <= pos.getY() + crucible.getSurfaceHeight()) {
                entity.extinguish();
                crucible.consumeBottleUnit();
            }
        }
    }

    @Override
    public void fillWithRain(World world, BlockPos pos)
    {
        if (world.isRemote || world.rand.nextInt(20) != 1) {
            return;
        }
        float temperature = world.getBiome(pos).getTemperature(pos);
        if (world.getBiomeProvider().getTemperatureAtHeight(temperature, pos.getY()) < 0.15F) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityGloryCrucible) {
            ((TileEntityGloryCrucible) tile).addRainWater();
        }
    }

    private static void replaceOneHeldItem(EntityPlayer player, EnumHand hand, ItemStack replacement)
    {
        ItemStack held = player.getHeldItem(hand);
        held.shrink(1);
        if (held.isEmpty()) {
            player.setHeldItem(hand, replacement);
        } else if (!player.inventory.addItemStackToInventory(replacement)) {
            player.dropItem(replacement, false);
        } else if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
        }
    }

    private static void giveOrReplace(EntityPlayer player, EnumHand hand, ItemStack result)
    {
        if (player.getHeldItem(hand).isEmpty()) {
            player.setHeldItem(hand, result);
        } else if (!player.inventory.addItemStackToInventory(result)) {
            player.dropItem(result, false);
        } else if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune)
    {
        return Item.getItemFromBlock(this);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state)
    {
        return new ItemStack(this);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityGloryCrucible
            ? ((TileEntityGloryCrucible) tile).getLiquidLevel()
            : state.getValue(LEVEL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LEVEL, MathHelper.clamp(meta, 0, 6));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LEVEL);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty<?>[] {LEVEL});
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
    public boolean isPassable(IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        if (face == EnumFacing.UP) {
            return BlockFaceShape.BOWL;
        }
        return face == EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }
}
