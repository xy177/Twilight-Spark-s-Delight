package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockFeast;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemKnife;
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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
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
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.tile.TileEntitySaltHelmetCrab;

import java.util.Set;
import java.util.UUID;

public class BlockSaltHelmetCrab extends Block implements ITileEntityProvider
{
    public static final int SERVINGS_LEFTOVER = 0;
    public static final int SERVINGS_FULL = 8;
    public static final PropertyInteger SERVINGS = PropertyInteger.create("servings", 0, 8);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.75D, 0.9375D);

    public BlockSaltHelmetCrab()
    {
        super(Material.CAKE);
        setHardness(0.5F);
        setResistance(0.5F);
        setSoundType(SoundType.WOOD);
        setDefaultState(this.blockState.getBaseState()
            .withProperty(BlockFeast.FACING, EnumFacing.NORTH)
            .withProperty(SERVINGS, SERVINGS_FULL));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntitySaltHelmetCrab();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return getDefaultState()
            .withProperty(BlockFeast.FACING, placer.getHorizontalFacing().getOpposite())
            .withProperty(SERVINGS, SERVINGS_FULL);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySaltHelmetCrab) {
            ((TileEntitySaltHelmetCrab) tile).initializeFromBlockDefault(SERVINGS_FULL);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        int servings = getServings(world, pos, state);
        if (servings == SERVINGS_LEFTOVER) {
            if (!world.isRemote) {
                world.setBlockToAir(pos);
                spawnAsEntity(world, pos, leftoverHelmet());
                spawnAsEntity(world, pos, stack("farmersdelight:canvas", 2));
                world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        return serve(world, pos, player, servings);
    }

    private boolean serve(World world, BlockPos pos, EntityPlayer player, int servings)
    {
        if (servings == SERVINGS_FULL) {
            return serveMeat(world, pos, player, servings);
        }
        return serveKnifePortion(world, pos, player, servings);
    }

    private boolean serveMeat(World world, BlockPos pos, EntityPlayer player, int servings)
    {
        if (countHeld(player, Items.BOWL) < 3) {
            if (!world.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.block.salt_helmet_crab.need_bowls"), true);
            }
            return true;
        }
        if (!world.isRemote) {
            consumeHeld(player, Items.BOWL, 3);
            recordDiner(world, pos, player);
            give(player, new ItemStack(TSDItems.BOWL_OF_SALTED_CRAB_MEAT, 3));
            setServings(world, pos, servings - 1);
            world.playSound(null, pos, ModSounds.foodTakePortion, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private boolean serveKnifePortion(World world, BlockPos pos, EntityPlayer player, int servings)
    {
        ItemStack knife = findKnife(player);
        if (knife.isEmpty()) {
            if (!world.isRemote) {
                player.sendStatusMessage(new TextComponentTranslation("twilight_spark_delight.block.salt_helmet_crab.need_knife"), true);
            }
            return true;
        }
        if (!world.isRemote) {
            recordDiner(world, pos, player);
            if (!player.capabilities.isCreativeMode) {
                knife.damageItem(1, player);
            }
            give(player, new ItemStack(servings == 1 ? TSDItems.SALT_ROASTED_HELMET_CRAB_CLAW : TSDItems.COOKED_HERMIT_CRAB_LEG));
            int nextServings = clampServings(servings - 1);
            setServings(world, pos, nextServings);
            if (nextServings == SERVINGS_LEFTOVER) {
                finishFeast(world, pos);
            }
            world.playSound(null, pos, ModSounds.foodTakePortion, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private void recordDiner(World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySaltHelmetCrab) {
            TileEntitySaltHelmetCrab crab = (TileEntitySaltHelmetCrab) tile;
            if (crab.getChef() == null) {
                crab.setChef(player.getUniqueID());
            }
            crab.addDiner(player.getUniqueID());
        }
    }

    private void finishFeast(World world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntitySaltHelmetCrab)) {
            return;
        }
        TileEntitySaltHelmetCrab crab = (TileEntitySaltHelmetCrab) tile;
        UUID chef = crab.getChef();
        Set<UUID> diners = crab.getDiners();
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
        if (tile instanceof TileEntitySaltHelmetCrab) {
            return clampServings(((TileEntitySaltHelmetCrab) tile).getServings());
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
        if (tile instanceof TileEntitySaltHelmetCrab) {
            ((TileEntitySaltHelmetCrab) tile).setServings(clamped);
        }
    }

    public static int clampServings(int servings)
    {
        return Math.max(SERVINGS_LEFTOVER, Math.min(SERVINGS_FULL, servings));
    }

    private static int countHeld(EntityPlayer player, Item item)
    {
        int count = 0;
        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        if (!main.isEmpty() && main.getItem() == item) {
            count += main.getCount();
        }
        if (!off.isEmpty() && off.getItem() == item) {
            count += off.getCount();
        }
        return count;
    }

    private static void consumeHeld(EntityPlayer player, Item item, int amount)
    {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        amount = consume(player.getHeldItemMainhand(), item, amount);
        if (amount > 0) {
            consume(player.getHeldItemOffhand(), item, amount);
        }
    }

    private static int consume(ItemStack stack, Item item, int amount)
    {
        if (amount <= 0 || stack.isEmpty() || stack.getItem() != item) {
            return amount;
        }
        int consumed = Math.min(amount, stack.getCount());
        stack.shrink(consumed);
        return amount - consumed;
    }

    private static ItemStack findKnife(EntityPlayer player)
    {
        ItemStack main = player.getHeldItemMainhand();
        if (ItemKnife.isKnife(main)) {
            return main;
        }
        ItemStack off = player.getHeldItemOffhand();
        return ItemKnife.isKnife(off) ? off : ItemStack.EMPTY;
    }

    private static void give(EntityPlayer player, ItemStack stack)
    {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    private static ItemStack leftoverHelmet()
    {
        ItemStack stack = stack("twilightforest:knightmetal_helmet", 1);
        stack.setItemDamage(Math.max(0, stack.getMaxDamage() * 2 / 3));
        Enchantment binding = Enchantments.BINDING_CURSE;
        if (binding != null) {
            stack.addEnchantment(binding, 1);
        }
        return stack;
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
            .withProperty(SERVINGS, SERVINGS_FULL);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySaltHelmetCrab) {
            return state.withProperty(SERVINGS, clampServings(((TileEntitySaltHelmetCrab) tile).getServings()));
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
        if (tile instanceof TileEntitySaltHelmetCrab) {
            servings = clampServings(((TileEntitySaltHelmetCrab) tile).getServings());
        }
        if (servings == SERVINGS_FULL) {
            drops.add(new ItemStack(Item.getItemFromBlock(this)));
        } else {
            drops.add(leftoverHelmet());
            drops.add(stack("farmersdelight:canvas", 2));
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
