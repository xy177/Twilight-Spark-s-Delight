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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.common.config.TSDConfig;
import xy177.twilightsparksdelight.common.event.TSDAdvancements;
import xy177.twilightsparksdelight.common.item.TwilightCheeseFondueCompanionItem;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.tile.TileEntityTwilightCheeseFondue;

import java.util.Set;
import java.util.UUID;

public class BlockTwilightCheeseFondue extends Block implements ITileEntityProvider
{
    public static final int SERVINGS_LEFTOVER = 0;
    public static final int SERVINGS_READY = 6;
    public static final int SERVINGS_UNLIT = 7;
    public static final PropertyInteger SERVINGS = PropertyInteger.create("servings", 0, 7);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);

    public BlockTwilightCheeseFondue()
    {
        super(Material.CAKE);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.WOOD);
        setDefaultState(this.blockState.getBaseState()
            .withProperty(BlockFeast.FACING, EnumFacing.NORTH)
            .withProperty(SERVINGS, SERVINGS_UNLIT));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityTwilightCheeseFondue();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return getDefaultState()
            .withProperty(BlockFeast.FACING, placer.getHorizontalFacing().getOpposite())
            .withProperty(SERVINGS, SERVINGS_UNLIT);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightCheeseFondue) {
            ((TileEntityTwilightCheeseFondue) tile).initializeFromBlockDefault(SERVINGS_UNLIT);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        int servings = getServings(world, pos, state);
        if (servings == SERVINGS_UNLIT) {
            return tryLight(world, pos, player);
        }
        if (servings == SERVINGS_LEFTOVER) {
            if (!world.isRemote) {
                world.setBlockToAir(pos);
                spawnAsEntity(world, pos, stack("twilightforest:carminite", 5));
                spawnAsEntity(world, pos, new ItemStack(TSDItems.FIERY_SLAG, 3));
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        return serve(world, pos, player, servings);
    }

    private boolean tryLight(World world, BlockPos pos, EntityPlayer player)
    {
        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        ItemStack fuel = isFuel(main) ? main : isFuel(off) ? off : ItemStack.EMPTY;
        if (fuel.isEmpty()) {
            if (!world.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.block.twilight_cheese_fondue.need_fuel"), true);
            }
            return true;
        }
        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                fuel.shrink(1);
            }
            setServings(world, pos, SERVINGS_READY);
            world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private boolean serve(World world, BlockPos pos, EntityPlayer player, int servings)
    {
        ItemStack companion = findHeld(player, TSDItems.TWILIGHT_CHEESE_FONDUE_COMPANION);
        if (companion.isEmpty()) {
            if (!world.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.block.twilight_cheese_fondue.need_companion"), true);
            }
            return true;
        }

        if (!world.isRemote) {
            TwilightCheeseFondueCompanionItem.addDiner(companion, player.getUniqueID());
            UUID chef = TwilightCheeseFondueCompanionItem.getChef(companion);
            if (chef == null) {
                chef = player.getUniqueID();
                TwilightCheeseFondueCompanionItem.setChef(companion, chef);
            }
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityTwilightCheeseFondue) {
                TileEntityTwilightCheeseFondue fondue = (TileEntityTwilightCheeseFondue) tile;
                if (fondue.getChef() == null) {
                    fondue.setChef(chef);
                }
                fondue.addDiner(player.getUniqueID());
            }
            TwilightCheeseFondueCompanionItem.damageCompanion(companion, player);
            give(player, new ItemStack(TSDItems.TWILIGHT_CHEESE_FONDUE_WITH_BREAD));
            int nextServings = clampServings(servings - 1);
            setServings(world, pos, nextServings);
            if (nextServings == SERVINGS_LEFTOVER) {
                finishFeast(world, pos, chef);
            }
            world.playSound(null, pos, ModSounds.foodTakePortion, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private void finishFeast(World world, BlockPos pos, UUID itemChef)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityTwilightCheeseFondue)) {
            return;
        }
        TileEntityTwilightCheeseFondue fondue = (TileEntityTwilightCheeseFondue) tile;
        UUID chef = fondue.getChef() == null ? itemChef : fondue.getChef();
        Set<UUID> diners = fondue.getDiners();
        if (diners.size() < TSDConfig.twilightCheeseFondueAdvancementDinerCount) {
            return;
        }
        for (EntityPlayer player : world.playerEntities) {
            if (!(player instanceof EntityPlayerMP)) {
                continue;
            }
            EntityPlayerMP mp = (EntityPlayerMP) player;
            UUID uuid = mp.getUniqueID();
            if (diners.contains(uuid)) {
                TSDAdvancements.GATHERED_AROUND.trigger(mp);
            }
            if (chef != null && chef.equals(uuid)) {
                TSDAdvancements.EXECUTIVE_CHEF.trigger(mp);
            }
        }
    }

    private int getServings(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightCheeseFondue) {
            return clampServings(((TileEntityTwilightCheeseFondue) tile).getServings());
        }
        return clampServings(state.getValue(SERVINGS));
    }

    private void setServings(World world, BlockPos pos, int servings)
    {
        int clamped = clampServings(servings);
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this) {
            return;
        }
        world.setBlockState(pos, state.withProperty(SERVINGS, clamped), 3);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightCheeseFondue) {
            ((TileEntityTwilightCheeseFondue) tile).setServings(clamped);
        }
    }

    public static int clampServings(int servings)
    {
        return Math.max(SERVINGS_LEFTOVER, Math.min(SERVINGS_UNLIT, servings));
    }

    private static void give(EntityPlayer player, ItemStack stack)
    {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    private static ItemStack findHeld(EntityPlayer player, Item item)
    {
        ItemStack main = player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() == item) {
            return main;
        }
        ItemStack off = player.getHeldItemOffhand();
        return !off.isEmpty() && off.getItem() == item ? off : ItemStack.EMPTY;
    }

    private static boolean isFuel(ItemStack stack)
    {
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) {
            return false;
        }
        String id = stack.getItem().getRegistryName().toString();
        return "twilightforest:fiery_blood".equals(id) || "twilightforest:fiery_tears".equals(id);
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
            .withProperty(SERVINGS, SERVINGS_UNLIT);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightCheeseFondue) {
            return state.withProperty(SERVINGS, clampServings(((TileEntityTwilightCheeseFondue) tile).getServings()));
        }
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {BlockFeast.FACING, SERVINGS});
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        int servings = clampServings(state.getValue(SERVINGS));
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityTwilightCheeseFondue) {
            servings = clampServings(((TileEntityTwilightCheeseFondue) tile).getServings());
        }
        if (servings == SERVINGS_UNLIT) {
            drops.add(new ItemStack(Item.getItemFromBlock(this)));
        } else {
            drops.add(stack("twilightforest:carminite", 5));
            drops.add(new ItemStack(TSDItems.FIERY_SLAG, 3));
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

    public BlockRenderLayer getRenderLayer()
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
        return getServings(world, pos, state);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}
