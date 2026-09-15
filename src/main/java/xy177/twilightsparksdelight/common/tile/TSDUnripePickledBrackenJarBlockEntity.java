package xy177.twilightsparksdelight.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xy177.twilightsparksdelight.registry.TSDBlockEntities;

public final class TSDUnripePickledBrackenJarBlockEntity extends BlockEntity {
    public static final int MAX_PROGRESS = 5;

    private int progress;
    private boolean peacockFanUsed;

    public TSDUnripePickledBrackenJarBlockEntity(BlockPos pos, BlockState state) {
        super(TSDBlockEntities.UNRIPE_PICKLED_BRACKEN_JAR.get(), pos, state);
    }

    public int getProgress() {
        return progress;
    }

    public boolean wasPeacockFanUsed() {
        return peacockFanUsed;
    }

    public void addProgress(int amount) {
        if (amount <= 0) {
            return;
        }
        progress = Math.min(MAX_PROGRESS, progress + amount);
        setChanged();
        sync();
    }

    public void markPeacockFanUsed() {
        if (!peacockFanUsed) {
            peacockFanUsed = true;
            setChanged();
            sync();
        }
    }

    private void sync() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Progress", progress);
        tag.putBoolean("PeacockFanUsed", peacockFanUsed);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = Math.max(0, Math.min(MAX_PROGRESS, tag.getInt("Progress")));
        peacockFanUsed = tag.getBoolean("PeacockFanUsed");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
