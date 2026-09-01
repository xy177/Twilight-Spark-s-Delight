package xy177.twilightsparksdelight.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityUnripePickledBrackenJar extends TileEntity
{
    private int progress;
    private boolean peacockFanUsed;

    public int getProgress()
    {
        return progress;
    }

    public void setProgress(int progress)
    {
        this.progress = Math.max(0, progress);
        markDirty();
    }

    public boolean wasPeacockFanUsed()
    {
        return peacockFanUsed;
    }

    public void markPeacockFanUsed()
    {
        peacockFanUsed = true;
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("Progress", progress);
        compound.setBoolean("PeacockFanUsed", peacockFanUsed);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        progress = Math.max(0, compound.getInteger("Progress"));
        peacockFanUsed = compound.getBoolean("PeacockFanUsed");
    }
}
