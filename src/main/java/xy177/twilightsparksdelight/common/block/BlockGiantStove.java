package xy177.twilightsparksdelight.common.block;

import com.wdcftgg.farmersdelightlegacy.common.block.BlockStove;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xy177.twilightsparksdelight.common.tile.TileEntityGiantStove;

import java.util.Random;

public class BlockGiantStove extends BlockStove implements IGiantKitchenController
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
        return new TileEntityGiantStove();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (this.structurePartBlock != null) {
            EnumFacing facing = state.getValue(FACING);
            GiantKitchenStructure.removePartsFromController(world, pos, facing, this.structurePartBlock, getStructureSize());
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random)
    {
        if (!state.getValue(LIT)) {
            return;
        }
        EnumFacing facing = state.getValue(FACING);
        int size = getStructureSize();
        double centerX = GiantKitchenStructure.getCenterX(pos, facing, size);
        double centerZ = GiantKitchenStructure.getCenterZ(pos, facing, size);
        if (random.nextInt(10) == 0) {
            world.playSound(centerX, pos.getY() + 1.0D, centerZ, ModSounds.STOVE_CRACKLE,
                SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        }

        double horizontalOffset = random.nextDouble() * size * 0.6D - size * 0.3D;
        double frontOffset = size * 0.52D;
        double xOffset = facing.getAxis() == EnumFacing.Axis.X
            ? facing.getFrontOffsetX() * frontOffset
            : horizontalOffset;
        double zOffset = facing.getAxis() == EnumFacing.Axis.Z
            ? facing.getFrontOffsetZ() * frontOffset
            : horizontalOffset;
        double y = pos.getY() + random.nextDouble() * size * 0.375D;
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, centerX + xOffset, y, centerZ + zOffset, 0.0D, 0.0D, 0.0D);
        world.spawnParticle(EnumParticleTypes.FLAME, centerX + xOffset, y, centerZ + zOffset, 0.0D, 0.0D, 0.0D);
    }
}
