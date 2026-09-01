package xy177.twilightsparksdelight.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xy177.twilightsparksdelight.common.block.BlockNagaMixedRice;
import xy177.twilightsparksdelight.common.block.NagaMixedRiceStructure;
import xy177.twilightsparksdelight.common.registry.TSDItems;
import xy177.twilightsparksdelight.common.tile.TileEntityNagaMixedRice;

import java.util.List;

public class NagaMixedRiceItemBlock extends ItemBlock
{
    public NagaMixedRiceItemBlock(Block block)
    {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        TSDFoodItem.addNagaMixedRiceVariantTooltip(stack, tooltip);
    }

    @Override
    public boolean placeBlockAt(
        ItemStack stack,
        EntityPlayer player,
        World world,
        BlockPos pos,
        EnumFacing side,
        float hitX,
        float hitY,
        float hitZ,
        IBlockState newState
    ) {
        if (!(newState.getBlock() instanceof BlockNagaMixedRice)) {
            return false;
        }
        BlockNagaMixedRice controller = (BlockNagaMixedRice) newState.getBlock();
        if (!NagaMixedRiceStructure.canPlaceParts(
            world,
            pos,
            newState,
            controller.getStructurePartBlock(),
            side,
            player
        )) {
            return false;
        }
        List<BlockPos> placed = NagaMixedRiceStructure.placeParts(
            world,
            pos,
            newState,
            controller.getStructurePartBlock()
        );
        if (placed.size() != 8) {
            return false;
        }
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            NagaMixedRiceStructure.removePlacedParts(world, placed, controller.getStructurePartBlock());
            return false;
        }
        NagaMixedRiceStructure.finishPlacement(world, placed, controller.getStructurePartBlock());
        if (world.getTileEntity(pos) instanceof TileEntityNagaMixedRice
            && stack.hasTagCompound() && stack.getTagCompound().hasKey(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG, 8)) {
            ((TileEntityNagaMixedRice) world.getTileEntity(pos)).setIngredientType(
                stack.getTagCompound().getString(TSDItems.NAGA_MIXED_RICE_INGREDIENT_TAG)
            );
        }
        return true;
    }
}

