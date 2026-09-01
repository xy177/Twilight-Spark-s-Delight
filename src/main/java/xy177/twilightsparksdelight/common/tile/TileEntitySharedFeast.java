package xy177.twilightsparksdelight.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class TileEntitySharedFeast extends TileEntity
{
    private int servings;
    private UUID chef;
    private final Set<UUID> diners = new LinkedHashSet<>();

    public int getServings()
    {
        return this.servings;
    }

    public void setServings(int servings)
    {
        this.servings = servings;
        markDirty();
    }

    public void initializeFromBlockDefault(int servings)
    {
        this.servings = servings;
        markDirty();
    }

    public UUID getChef()
    {
        return this.chef;
    }

    public void setChef(UUID chef)
    {
        this.chef = chef;
        markDirty();
    }

    public void addDiner(UUID diner)
    {
        if (diner != null && this.diners.add(diner)) {
            markDirty();
        }
    }

    public Set<UUID> getDiners()
    {
        return new LinkedHashSet<>(this.diners);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("Servings", this.servings);
        if (this.chef != null) {
            compound.setString("Chef", this.chef.toString());
        }
        int index = 0;
        for (UUID diner : this.diners) {
            compound.setString("Diner" + index, diner.toString());
            index++;
        }
        compound.setInteger("DinerCount", index);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.servings = compound.getInteger("Servings");
        this.chef = compound.hasKey("Chef", 8) ? parseUuid(compound.getString("Chef")) : null;
        this.diners.clear();
        int count = compound.getInteger("DinerCount");
        for (int i = 0; i < count; i++) {
            UUID diner = parseUuid(compound.getString("Diner" + i));
            if (diner != null) {
                this.diners.add(diner);
            }
        }
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if (this.world != null && this.pos != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
            this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
        if (this.world != null && this.pos != null) {
            this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
        }
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
        if (this.world != null && this.pos != null) {
            this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
        }
    }

    @Override
    public boolean shouldRefresh(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    private static UUID parseUuid(String value)
    {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
