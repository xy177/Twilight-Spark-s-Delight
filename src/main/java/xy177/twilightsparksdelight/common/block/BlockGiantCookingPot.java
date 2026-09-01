package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockCookingPot;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCookingPot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantCookingPot;

import java.util.Random;

public class BlockGiantCookingPot extends BlockCookingPot implements IGiantKitchenController
{
    private Block structurePartBlock;

    public void setStructurePartBlock(Block structurePartBlock)
    {
        this.structurePartBlock = structurePartBlock;
    }

    @Override
    public Block getStructurePartBlock()
    {
        return this.structurePartBlock;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityGiantCookingPot();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return GiantKitchenStructure.getScaledCookingPotCollision(pos, pos, state.getValue(FACING), getStructureSize());
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return GiantKitchenStructure.getScaledCookingPotCollision(pos, pos, state.getValue(FACING), getStructureSize());
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityCookingPot)) {
            return;
        }
        TileEntityCookingPot cookingPot = (TileEntityCookingPot) tileEntity;
        if (!cookingPot.isHeated() || random.nextInt(10) != 0) {
            return;
        }

        EnumFacing facing = state.getValue(FACING);
        world.playSound(
            GiantKitchenStructure.getCenterX(pos, facing, getStructureSize()),
            pos.getY() + getStructureSize() * 0.7D,
            GiantKitchenStructure.getCenterZ(pos, facing, getStructureSize()),
            cookingPot.hasCookedMeal() ? ModSounds.COOKING_POT_BOIL_SOUP : ModSounds.COOKING_POT_BOIL,
            SoundCategory.BLOCKS,
            0.7F,
            random.nextFloat() * 0.2F + 0.9F,
            false
        );
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (this.structurePartBlock != null) {
            GiantKitchenStructure.removePartsFromController(
                world,
                pos,
                state.getValue(FACING),
                this.structurePartBlock,
                getStructureSize()
            );
        }
        super.breakBlock(world, pos, state);
    }
}
