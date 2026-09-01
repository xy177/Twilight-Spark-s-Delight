package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twilightforest.block.TFBlocks;
import xy177.twilightsparksdelight.common.tile.TileEntityGlassJar;

import javax.annotation.Nullable;
import java.util.List;

public class GlassJarItemBlock extends ItemBlock
{
    public static final int EMPTY_META = 0;
    public static final int FILLED_META = 1;

    public GlassJarItemBlock(Block block)
    {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return 0;
    }

    @Override
    public ItemStack getDefaultInstance()
    {
        return createEmptyJar();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
        float hitX, float hitY, float hitZ)
    {
        ItemStack held = player.getHeldItem(hand);
        Block clicked = world.getBlockState(pos).getBlock();
        if (isEmptyJar(held) && (clicked == TFBlocks.firefly || clicked == TFBlocks.cicada)) {
            if (!world.isRemote) {
                ItemStack critter = new ItemStack(clicked);
                ItemStack lid = TileEntityGlassJar.getLid(held);
                ItemStack filledJar = TileEntityGlassJar.createJarStack(critter, lid, rotationFor(player));
                world.setBlockToAir(pos);
                giveCapturedJar(player, hand, held, filledJar);
                world.playSound(null, pos, net.minecraft.init.SoundEvents.ITEM_BOTTLE_FILL,
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
        float hitX, float hitY, float hitZ, IBlockState newState)
    {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityGlassJar) {
            TileEntityGlassJar jar = (TileEntityGlassJar) tile;
            jar.readFromJarStack(stack);
            jar.setItemRotation(rotationFor(player));
        }
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        ItemStack stored = TileEntityGlassJar.getStoredItem(stack);
        if (isCritter(stored, TFBlocks.firefly)) {
            return I18n.translateToLocal("tile.twilight_spark_delight.glass_jar.firefly.name");
        }
        if (isCritter(stored, TFBlocks.cicada)) {
            return I18n.translateToLocal("tile.twilight_spark_delight.glass_jar.cicada.name");
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        ItemStack stored = TileEntityGlassJar.getStoredItem(stack);
        if (!stored.isEmpty()) {
            tooltip.add(TextFormatting.GRAY + net.minecraft.client.resources.I18n.format(
                "twilight_spark_delight.tooltip.glass_jar.contents",
                stored.getDisplayName(),
                stored.getCount()
            ));
        }
    }

    public static boolean isEmptyJar(ItemStack stack)
    {
        return !stack.isEmpty()
            && stack.getItem() instanceof GlassJarItemBlock
            && stack.getMetadata() == EMPTY_META
            && TileEntityGlassJar.getStoredItem(stack).isEmpty();
    }

    public static ItemStack createEmptyJar()
    {
        return TileEntityGlassJar.createJarStack(
            ItemStack.EMPTY,
            TileEntityGlassJar.createDefaultLid(),
            0
        );
    }

    public static boolean isCritter(ItemStack stack, Block critter)
    {
        return !stack.isEmpty() && Block.getBlockFromItem(stack.getItem()) == critter;
    }

    private static void giveCapturedJar(EntityPlayer player, EnumHand hand, ItemStack held, ItemStack filledJar)
    {
        if (player.capabilities.isCreativeMode) {
            if (!player.inventory.addItemStackToInventory(filledJar)) {
                player.dropItem(filledJar, false);
            }
            return;
        }
        held.shrink(1);
        if (held.isEmpty()) {
            player.setHeldItem(hand, filledJar);
        } else if (!player.inventory.addItemStackToInventory(filledJar)) {
            player.dropItem(filledJar, false);
        }
    }

    private static int rotationFor(EntityPlayer player)
    {
        return MathHelper.floor((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5F) & 15;
    }
}
