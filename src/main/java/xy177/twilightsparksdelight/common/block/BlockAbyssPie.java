package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockPie;
import com.wdcftgg.farmersdelightlegacy.common.item.ItemKnife;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.twilightsparksdelight.common.registry.TSDItems;

import java.util.List;
import java.util.Random;

public class BlockAbyssPie extends BlockPie
{
    private static final ResourceLocation SLICE_ID = new ResourceLocation("twilight_spark_delight", "abyss_pie_slice");
    private static final AxisAlignedBB PIE_SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

    public BlockAbyssPie()
    {
        super("abyss_pie_slice");
        setTickRandomly(true);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack held = player.getHeldItem(hand);
        if (ItemKnife.isKnife(held)) {
            if (!world.isRemote) {
                giveSlice(player);
                if (!player.capabilities.isCreativeMode) {
                    held.damageItem(1, player);
                }
                advanceBite(world, pos, state);
                world.playSound(null, pos, ModSounds.foodSlice, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }

        if (!player.canEat(false)) {
            return false;
        }
        if (!world.isRemote) {
            Item sliceItem = ForgeRegistries.ITEMS.getValue(SLICE_ID);
            if (!(sliceItem instanceof ItemFood)) {
                return false;
            }
            ItemStack slice = new ItemStack(sliceItem);
            ((ItemFood) sliceItem).onItemUseFinish(slice, world, player);
            advanceBite(world, pos, state);
            world.playSound(null, pos, ModSounds.foodTakePortion, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    private static void giveSlice(EntityPlayer player)
    {
        ItemStack slice = new ItemStack(TSDItems.ABYSS_PIE_SLICE);
        if (!player.inventory.addItemStackToInventory(slice)) {
            player.dropItem(slice, false);
        }
    }

    private static void advanceBite(World world, BlockPos pos, IBlockState state)
    {
        int bites = state.getValue(BITES);
        if (bites >= 3) {
            world.setBlockToAir(pos);
        } else {
            world.setBlockState(pos, state.withProperty(BITES, bites + 1), 3);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos)
    {
        return PIE_SHAPE;
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
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (world.isRemote || random.nextFloat() >= 0.5F) {
            return;
        }
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(8.0D));
        if (!players.isEmpty()) {
            EntityPlayer target = players.get(random.nextInt(players.size()));
            world.playSound(null, target.getPosition(), SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 1.0F, 1.0F);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
        int fortune)
    {
        if (state.getValue(BITES) == 0) {
            drops.add(new ItemStack(Item.getItemFromBlock(this)));
        }
    }
}
