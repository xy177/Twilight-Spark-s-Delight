package xy177.twilightsparksdelight.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import xy177.twilightsparksdelight.common.block.BlockNagaMixedRice;
import xy177.twilightsparksdelight.common.block.NagaMixedRiceStructure;

import java.util.List;

public class TileEntityNagaMixedRice extends TileEntitySharedFeast
{
    private String ingredientType;
    @Override
    public void onLoad()
    {
        super.onLoad();
        if (this.world == null || this.world.isRemote) {
            return;
        }
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() instanceof BlockNagaMixedRice) {
            BlockNagaMixedRice block = (BlockNagaMixedRice) state.getBlock();
            NagaMixedRiceStructure.repairLegacyLayout(this.world, this.pos, state, block.getStructurePartBlock());
        }
    }

    public int getStage()
    {
        return getServings();
    }

    public void setStage(int stage)
    {
        setServings(stage);
    }

    public String getIngredientType()
    {
        return ingredientType;
    }

    public void setIngredientType(String ingredientType)
    {
        this.ingredientType = ingredientType;
        markDirty();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (this.world == null) {
            return super.getRenderBoundingBox();
        }
        IBlockState state = this.world.getBlockState(this.pos);
        if (!(state.getBlock() instanceof BlockNagaMixedRice)) {
            return super.getRenderBoundingBox();
        }

        EnumFacing facing = state.getValue(BlockNagaMixedRice.FACING);
        List<BlockPos> positions = NagaMixedRiceStructure.getStructurePositions(this.pos, facing);
        int minX = this.pos.getX();
        int minZ = this.pos.getZ();
        int maxX = this.pos.getX();
        int maxZ = this.pos.getZ();
        for (BlockPos position : positions) {
            minX = Math.min(minX, position.getX());
            minZ = Math.min(minZ, position.getZ());
            maxX = Math.max(maxX, position.getX());
            maxZ = Math.max(maxZ, position.getZ());
        }
        return new AxisAlignedBB(
            minX - 0.25D,
            this.pos.getY() - 0.25D,
            minZ - 0.25D,
            maxX + 1.25D,
            this.pos.getY() + 2.25D,
            maxZ + 1.25D
        );
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if (ingredientType != null) {
            compound.setString("IngredientType", ingredientType);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        ingredientType = compound.hasKey("IngredientType", 8) ? compound.getString("IngredientType") : null;
    }
}

